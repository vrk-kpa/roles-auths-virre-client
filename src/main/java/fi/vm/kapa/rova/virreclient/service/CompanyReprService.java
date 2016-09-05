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

import fi.vm.kapa.rova.external.model.virre.CompanyPerson;
import fi.vm.kapa.rova.external.model.virre.CompanyRepresentations;
import fi.vm.kapa.rova.external.model.virre.CompanyRoleType;
import fi.vm.kapa.rova.external.model.virre.RoleNameType;
import fi.vm.kapa.rova.soap.prh.CompanyReprClient;
import fi.vm.kapa.rova.soap.prh.VirreException;
import https.ws_prh_fi.novus.ids.services._2008._08._22.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Service for loading Companies from PRH
 * @author mtom
 */
@Service
public class CompanyReprService extends AbstractCompanyService {

    public static final String OP = "CompanyReprService";

    @Autowired
    private CompanyReprClient crc;

    /**
     * Fetches person associations for given Y-tunnus from PRH's registry
     * @param businessId Y-tunnus
     * @return List of organizations for given Y-tunnus
     * @throws VIRREServiceException 
     */
    public CompanyRepresentations getRepresentations(String businessId) throws VirreException {

        long startTime = System.currentTimeMillis();
        CompanyRepresentInfoResponse info = crc.getResponse(businessId);
        logRequest(OP + "XRoadCompanyRepresentInfo", startTime, System.currentTimeMillis());
        return parseRepresentations(info.getCompanyRepresentInfoResponseTypeDetails());
    }

    private CompanyRepresentations parseRepresentations(CompanyRepresentInfoResponseType info) {
        CompanyRepresentations repr = new CompanyRepresentations();
        if (info != null) {
            repr.setBusinessId(info.getBusinessId());
            repr.setCompanyName(info.getCompanyName().getName());
            repr.setCompanyFormCode(info.getForm().getType());
            repr.setPhases(parseActiveCompanyPhases(info.getPhase()));

            List<CompanyPerson> persons = new LinkedList<>();

            List<BodyType> bodyTypes = (List) info.getBody();
            if (bodyTypes != null) {
                for (BodyType type : bodyTypes) {
                    List<Object> objects = type.getNaturalPersonOrJuristicPerson();
                    addPersonData(persons, objects);
                }
            }

            List<RepresentationType> reprTypes = (List) info.getRepresentation();
            if (reprTypes != null) {
                for (RepresentationType type : reprTypes) {
                    RepresentationType.Group group = type.getGroup();
                    if (group != null) {
                        List<NaturalPersonTypeRepresentation> reprs = group.getNaturalPerson();
                        addRepresentationData(persons, reprs);
                    }
                }
            }

        
            repr.setCompanyPersons(persons);
        }
        return repr;
    }

    private CompanyRoleType parseRoleType(String r) {
        CompanyRoleType role = new CompanyRoleType();
        RoleNameType rnt = null;
        try {
            rnt = RoleNameType.valueOf(r);
        } catch (IllegalArgumentException e) { //NOSONAR Ainoastaan tunnetut kiinnostaa
            logWarning(OP, "Unable to parse role: " + r);
        }
        role.setType(rnt);
        return role;
    }

    private void addPersonData(List<CompanyPerson> persons, List<Object> objects) {
        if (objects == null) {
            logWarning(OP, "Person data was null.");
            return;
        }
        for (Object object : objects) {
            if (object instanceof NaturalPersonType) {
                NaturalPersonType np = (NaturalPersonType) object;
                CompanyPerson person = new CompanyPerson(); 
                person.setFirstName(np.getFirstname());
                person.setLastName(np.getSurname());
                person.setSocialSec(np.getSocialSecurityNumber());
                person.setCompanyRole(parseRoleType(np.getRole()));
                person.setStatus(np.getStatus().getValue());
                persons.add(person);
            }
        }
    }

    private void addRepresentationData(List<CompanyPerson> persons, List<NaturalPersonTypeRepresentation> nps) {
        if (nps == null) {
            logWarning(OP, "Representation data was null.");
            return;
        }
        for (NaturalPersonTypeRepresentation np : nps) {
            CompanyPerson person = new CompanyPerson(); 
            person.setFirstName(np.getFirstname());
            person.setLastName(np.getSurname());
            person.setSocialSec(np.getSocialSecurityNumber());
            person.setCompanyRole(parseRoleType(np.getRole()));
            person.setStatus(np.getStatus().getValue());
            persons.add(person);
        }
    }

}
