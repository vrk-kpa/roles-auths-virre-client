
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.RepresentationsClient;
import fi.vm.kapa.rova.soap.prh.model.Body;
import fi.vm.kapa.rova.soap.prh.model.NaturalPerson;
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
    public List<String> getRepresentations(String businessId) throws VIRREServiceException {

        List<String> orgs = null;

        try {
            String responseString = rc.getResponse(businessId);
            RepresentationsResponseMessage msg = MessageParser.parseResponseMessage(responseString, RepresentationsResponseMessage.class);
            orgs = listSocialSecurityNumbers(msg);
            log.info("Found " + orgs.size() + " persons for company.");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to parse persons: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return orgs;
    }

    private List<String> listSocialSecurityNumbers(RepresentationsResponseMessage msg) {
        List<String> persons = new LinkedList<>();
        if (msg != null) {
            for (Body body : msg.getBodies()) {
                for (NaturalPerson person : body.getNaturalPersons()) {
                    String hetu = person.getSocialSec();
                    persons.add(hetu);
                    log.debug("Person id: " + hetu);
                }
            }
        } else {
            log.warning("Got null message.");
        }
        return persons;
    }

}
