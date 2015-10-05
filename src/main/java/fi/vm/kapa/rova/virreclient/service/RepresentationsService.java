
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.engine.model.prh.CompanyPerson;
import fi.vm.kapa.rova.engine.model.prh.CompanyRepresentations;
import fi.vm.kapa.rova.engine.model.prh.CompanyRole;
import fi.vm.kapa.rova.engine.model.RoleNameType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.RepresentationsClient;
import fi.vm.kapa.rova.soap.prh.model.Body;
import fi.vm.kapa.rova.soap.prh.model.NaturalPerson;
import fi.vm.kapa.rova.soap.prh.model.Representation;
import fi.vm.kapa.rova.soap.prh.model.RepresentationsResponseMessage;
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
public class RepresentationsService {

    private static final Logger log = Logger.getLogger(RepresentationsService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    @Autowired
    private RepresentationsClient rc;

    /**
     * Fetches person associations for given Y-tunnus from PRH's registry
     * @param businessId Y-tunnus
     * @return List of organizations for given Y-tunnus
     * @throws VIRREServiceException 
     */
    public CompanyRepresentations getRepresentations(String businessId) throws VIRREServiceException {

        CompanyRepresentations representations = null;

        try {
            String responseString = rc.getResponse(businessId);
            RepresentationsResponseMessage msg = MessageParser.parseResponseMessage(responseString, RepresentationsResponseMessage.class);
            representations = parseRepresentations(msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to parse persons: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return representations;
    }

    private CompanyRepresentations parseRepresentations(RepresentationsResponseMessage msg) {

        CompanyRepresentations repr = new CompanyRepresentations();
        if (msg != null) {
            repr.setCompanyFormCode(msg.getCompanyFormCode());
            List<CompanyPerson> persons = new LinkedList<>();

            for (Representation r : msg.getRepresentations()) {
                addPersonData(persons, r.getPersons());
            }

            for (Body body : msg.getBodies()) {
                addPersonData(persons, body.getNaturalPersons());
            }

            repr.setCompanyPersons(persons);
        } else {
            log.warning("Got null message.");
        }
        return repr;
    }


    private void addPersonData(List<CompanyPerson> list, List<NaturalPerson> persons) {

        for (NaturalPerson np : persons) {
            CompanyPerson person = new CompanyPerson(); 
            person.setFirstName(np.getFirstName());
            person.setLastName(np.getLastName());
            person.setSocialSec(np.getSocialSec());

            CompanyRole role = new CompanyRole();
            String r = np.getRole();
            RoleNameType rnt = null;
            try {
                rnt = RoleNameType.valueOf(r);
            } catch (IllegalArgumentException e) {
                log.warning("Unable to parse role: " + r);
            }
            role.setType(rnt);
            person.setCompanyRole(role);

            list.add(person);
        }

    }


}
