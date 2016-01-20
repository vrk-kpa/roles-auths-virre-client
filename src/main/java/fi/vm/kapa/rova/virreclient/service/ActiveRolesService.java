package fi.vm.kapa.rova.virreclient.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.vm.kapa.rova.external.model.virre.Company;
import fi.vm.kapa.rova.external.model.virre.CompanyPerson;
import fi.vm.kapa.rova.external.model.virre.CompanyRoleType;
import fi.vm.kapa.rova.external.model.virre.RoleNameType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.ActiveRolesClient;
import fi.vrk.xml.rova.prh.activeroles.PersonActiveRoleInfoResponseType;
import fi.vrk.xml.rova.prh.activeroles.RoleInCompanyType;
import fi.vrk.xml.rova.prh.activeroles.RoleInCompanyType.RoleBasicInfo;

/**
 * Created by Juha Korkalainen on 15.1.2016.
 */
@Service

public class ActiveRolesService extends ServiceLogging {
    public static final String OP = "ActiveRolesService";

    private static final Logger LOG = Logger.getLogger(CompaniesService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
    
    @Autowired
    private ActiveRolesClient client;
    
    @SuppressWarnings("unchecked")
    public List<Company> getCompanies(String hetu) throws VIRREServiceException {
        Map<String, List<?>> response = new HashMap<>();

        try {
            long startTime = System.currentTimeMillis();
            PersonActiveRoleInfoResponseType result = client.getResponse(hetu);
            LOG.debug(result.toString());
            response = parseCompanies(result);
            logRequest(OP + ":RoVaListCompanies", startTime, System.currentTimeMillis());
        } catch (Exception e) {
            logError(OP, "Failed to parse companies: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return (List<Company>) response.get(Company.TYPE);
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
            
            List<CompanyRoleType> roles = new ArrayList<>();
            for (RoleBasicInfo roleInfo : roleInCompany.getRoleBasicInfo()) {
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
        
        Map<String, List<?>> rolesMap = new HashMap<>(); // return value
        rolesMap.put(CompanyPerson.TYPE, persons);
        rolesMap.put(Company.TYPE, companies);
        
        LOG.debug(rolesMap+"");
        
        return rolesMap;
    }

}
