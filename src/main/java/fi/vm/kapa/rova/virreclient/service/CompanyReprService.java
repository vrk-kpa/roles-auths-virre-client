
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.external.model.virre.CompanyPerson;
import fi.vm.kapa.rova.external.model.virre.CompanyRepresentations;
import fi.vm.kapa.rova.external.model.virre.CompanyRoleType;
import fi.vm.kapa.rova.external.model.virre.RoleNameType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.CompanyReprClient;
import fi.vm.kapa.rova.soap.prh.RepresentationsClient;
import fi.vm.kapa.rova.soap.prh.model.Body;
import fi.vm.kapa.rova.soap.prh.model.NaturalPerson;
import fi.vm.kapa.rova.soap.prh.model.Representation;
import fi.vm.kapa.rova.soap.prh.model.RepresentationsResponseMessage;
import fi.vrk.xml.rova.prh.companyreprinfo.BodyType;
import fi.vrk.xml.rova.prh.companyreprinfo.CompanyRepresentInfoResponseType;
import fi.vrk.xml.rova.prh.companyreprinfo.LegalRepresentationType;
import fi.vrk.xml.rova.prh.companyreprinfo.NaturalPersonType;
import fi.vrk.xml.rova.prh.companyreprinfo.NaturalPersonTypeRepresentation;
import fi.vrk.xml.rova.prh.companyreprinfo.RepresentationType;
import fi.vrk.xml.rova.prh.companyreprinfo.RepresentationType.Group;

import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for loading Companies from PRH
 * @author mtom
 */
@Service
public class CompanyReprService extends ServiceLogging {

    public static final String OP = "CompanyReprService";

    private static final Logger log = Logger.getLogger(CompanyReprService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    @Autowired
    private CompanyReprClient crc;

    /**
     * Fetches person associations for given Y-tunnus from PRH's registry
     * @param businessId Y-tunnus
     * @return List of organizations for given Y-tunnus
     * @throws VIRREServiceException 
     */
    public CompanyRepresentations getRepresentations(String businessId) throws VIRREServiceException {

        CompanyRepresentations representations = null;

        try {
            long startTime = System.currentTimeMillis();
            CompanyRepresentInfoResponseType info = crc.getResponse(businessId);
            logRequest(OP + "XRoadCompanyRepresentInfo", startTime, System.currentTimeMillis());
            representations = parseRepresentations(info);
        } catch (Exception e) {
            e.printStackTrace();
            logError(OP, "Failed to parse persons: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return representations;
    }

    private CompanyRepresentations parseRepresentations(CompanyRepresentInfoResponseType info) {
        CompanyRepresentations repr = new CompanyRepresentations();
        if (info != null) {
            repr.setBusinessId(info.getBusinessId());
            repr.setCompanyName(info.getCompanyName().getName());
            repr.setCompanyFormCode(info.getForm().getType());
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
                    Group group = type.getGroup();
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
        } catch (IllegalArgumentException e) {
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
        for (Object object : persons) {
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
