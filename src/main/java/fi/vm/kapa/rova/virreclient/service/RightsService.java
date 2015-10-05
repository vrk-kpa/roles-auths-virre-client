
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.engine.model.AuthorizationType;
import fi.vm.kapa.rova.engine.model.prh.RepresentationRight;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.RightsClient;
import fi.vm.kapa.rova.soap.prh.model.RightsResponseMessage;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for loading Companies from PRH
 * @author mtom
 */
@Service
public class RightsService {

    private static final Logger log = Logger.getLogger(RightsService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    @Autowired
    private RightsClient rc;

    /**
     * Fetches person associations for given Y-tunnus from PRH's registry
     * @param socialsec hetu
     * @param businessId Y-tunnus
     * @param level OI/P
     * @return AUTHORIZED or UNAUTHORIZED
     * @throws VIRREServiceException 
     */
    public RepresentationRight getRights(String socialsec, String businessId, String level) throws VIRREServiceException {

        RepresentationRight right = null;

        try {
            String responseString = rc.getRights(socialsec, businessId, level);
            log.info("Response string: " + responseString);
            RightsResponseMessage msg = MessageParser.parseResponseMessage(responseString, RightsResponseMessage.class);
            right = parseRight(msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Failed to parse rights: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return right;
    }

    private RepresentationRight parseRight(RightsResponseMessage msg) {
        RepresentationRight right = new RepresentationRight();
        String code = msg.getCode();
        int num = Integer.parseInt(code);
        right.setCode(num);
        return right;
    }

}
