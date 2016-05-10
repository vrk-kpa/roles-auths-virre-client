/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.virreclient.service;

import https.ws_prh_fi.novus.ids.services._2008._08._22.PersonActiveRoleInfoResponseType;
import https.ws_prh_fi.novus.ids.services._2008._08._22.PhaseType;
import https.ws_prh_fi.novus.ids.services._2008._08._22.RoleInCompanyType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.kapa.rova.external.model.virre.Company;
import fi.vm.kapa.rova.external.model.virre.CompanyPerson;
import fi.vm.kapa.rova.external.model.virre.CompanyRoleType;
import fi.vm.kapa.rova.external.model.virre.PhaseNameType;
import fi.vm.kapa.rova.external.model.virre.RoleNameType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.ActiveRolesClient;

/**
 * Created by Juha Korkalainen on 15.1.2016.
 */
@Service
public class ActiveRolesService extends ServiceLogging {
    public static final String OP = "ActiveRolesService";

    private static final Logger LOG = Logger.getLogger(ActiveRolesService.class);

    @Autowired
    private ActiveRolesClient client;

    @SuppressWarnings("unchecked")
	public CompanyPerson getCompanyPerson(String hetu) throws VIRREServiceException {
        try {
            long startTime = System.currentTimeMillis();
            PersonActiveRoleInfoResponseType result = client.getResponse(hetu);
            LOG.debug(result.toString());
            Map<String, List<?>> response = parseCompanies(result);
            logRequest(OP + ":XRoadPersonActiveRoleInfo", startTime, System.currentTimeMillis());
            // return first
            return ((List<CompanyPerson>) response.get(CompanyPerson.TYPE)).get(0);
        } catch (Exception e) {
            logError(OP, "Failed to parse companyPerson: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Company> getCompanies(String hetu) throws VIRREServiceException {
        try {
            long startTime = System.currentTimeMillis();
            PersonActiveRoleInfoResponseType result = client.getResponse(hetu);
            LOG.debug(result.toString());
            Map<String, List<?>> response = parseCompanies(result);
            logRequest(OP + ":RoVaListCompanies", startTime, System.currentTimeMillis());
            return (List<Company>) response.get(Company.TYPE);
        } catch (Exception e) {
            logError(OP, "Failed to parse companies: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

    }

    private Map<String, List<?>> parseCompanies(PersonActiveRoleInfoResponseType result) {
        if (result == null) {
            throw new ServiceException("Parsing failed because of null result (PersonActiveRoleInfoResponseType)");
        }
        
        CompanyPerson person = new CompanyPerson();
        person.setFirstName(result.getFirstname());
        person.setLastName(result.getSurname());
        person.setSocialSec(result.getSocialSecurityNumber());
        person.setStatus(result.getStatus());

        List<CompanyPerson> persons = new ArrayList<>(); // list of one person into return map
        persons.add(person);
        
        List<Company> companies = new ArrayList<>(); // list of companies into return map
        for (RoleInCompanyType roleInCompany : result.getRoleInCompany()) {
            Company company = new Company();
            company.setBusinessId(roleInCompany.getBusinessId());
            company.setCompanyName(roleInCompany.getCompanyName());
            company.setState(roleInCompany.getState());
            company.setPhases(parseActiveCompanyPhases(roleInCompany.getPhase()));
            
            List<CompanyRoleType> roles = new ArrayList<>();
            for (RoleInCompanyType.RoleBasicInfo roleInfo : roleInCompany.getRoleBasicInfo()) {
                CompanyRoleType role = new CompanyRoleType();
                
                RoleNameType type = null;
                String roleName = roleInfo.getName();
                try {
                    type = RoleNameType.valueOf(roleName);
                } catch (IllegalArgumentException e) {
                    logWarning(OP, "Unable to parse role: " + roleName);
                }
                role.setType(type);
                
                roles.add(role);
            }
            company.setRoles(roles);
            
            companies.add(company);
        }

        person.setCompanies(companies);

        Map<String, List<?>> rolesMap = new HashMap<>(); // return value
        rolesMap.put(CompanyPerson.TYPE, persons);
        rolesMap.put(Company.TYPE, companies);
        
        LOG.debug(rolesMap+"");
        
        return rolesMap;
    }

    private List<PhaseNameType> parseActiveCompanyPhases(List<PhaseType> phases) {
        List<PhaseNameType> activePhases = new LinkedList<>();
        if (phases != null) {
            ZonedDateTime now = ZonedDateTime.now();
            for (PhaseType phaseType : phases) {
                if (phaseType != null) {
                    String phaseName = phaseType.getName();
                    PhaseNameType phase = PhaseNameType.parseType(phaseName);
                    if (phase != PhaseNameType.NONE && isPhaseActive(phaseType, now)) {
                        activePhases.add(phase);
                    }
                }
            }
        }
        return activePhases;
    }

    private boolean isPhaseActive(PhaseType phaseType, ZonedDateTime now) {

        boolean active = false;

        XMLGregorianCalendar startCal = phaseType.getRegistrationDate();
        XMLGregorianCalendar endCal = phaseType.getExpirationDate();

        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime start = ZonedDateTime.of(LocalDateTime.MIN, zoneId); 
        ZonedDateTime end = ZonedDateTime.of(LocalDateTime.MAX, zoneId);

        if (startCal != null) {
            start = startCal.toGregorianCalendar().toZonedDateTime();
        }
        if (endCal != null) {
            end = endCal.toGregorianCalendar().toZonedDateTime();
        }

        if ((now.isEqual(start) || now.isAfter(start)) && now.isBefore(end)) {
            active = true;
        }

        return active;
    }

}
