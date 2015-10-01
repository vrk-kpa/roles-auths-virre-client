
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.CompaniesClient;
import fi.vm.kapa.rova.soap.prh.model.CompaniesResponseMessage;
import fi.vm.kapa.rova.soap.prh.model.Role;
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
public class CompaniesService {

    private static final Logger log = Logger.getLogger(CompaniesService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    @Autowired
    private CompaniesClient cc;

    /**
     * Fetches company associations for given hetu from PRH's registry
     * @param hetu Social security number
     * @return List of organizations for given hetu
     * @throws VIRREServiceException 
     */
    public List<String> getCompanies(String hetu) throws VIRREServiceException {

        List<String> orgs = null;

        try {
            String responseString = cc.getResponse(hetu);
            CompaniesResponseMessage msg = MessageParser.parseResponseMessage(responseString, CompaniesResponseMessage.class);
            orgs = listBusinessIds(msg);
            log.info("Found " + orgs.size() + " companies for person.");
        } catch (Exception e) {
            log.error("Failed to parse companies: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return orgs;
    }

    private List<String> listBusinessIds(CompaniesResponseMessage msg) {
        List<String> orgs = new LinkedList<>();
        if (msg != null) {
            for (Role role : msg.getRoles()) {
                String businessId = role.getBusinessId();
                orgs.add(businessId);
                log.debug("Business id: " + businessId);
            }
        } else {
            log.warning("Got null message.");
        }
        return orgs;
    }

}
