
package fi.vm.kapa.rova.virreclient.service;

import fi.vm.kapa.rova.external.model.virre.RepresentationRight;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.RightReprClient;
import fi.vrk.xml.rova.prh.companyright.RightToRepresentResponseType;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for loading Companies from PRH
 * @author mtom
 */
@Service
public class RightReprService extends ServiceLogging {

    public static final String OP = "RightsService";

    private static final Logger log = Logger.getLogger(RightReprService.class);

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

    @Autowired
    private RightReprClient rrc;

    /**
     * Fetches person associations for given Y-tunnus from PRH's registry
     * @param socialsec hetu
     * @param businessId Y-tunnus
     * @param level OI/P
     * @return RepresentationRight 
     * @throws VIRREServiceException 
     */
    public RepresentationRight getRights(String socialsec, String businessId, String level) throws VIRREServiceException {
        log.debug("service received: "+ socialsec +" - "+ businessId +" - "+ level);

        RepresentationRight right = null;

        try {
            long startTime = System.currentTimeMillis();
            RightToRepresentResponseType result = rrc.getRights(socialsec, businessId, level);
            logRequest(OP + ":RoVaCompanyRepresentations", startTime, System.currentTimeMillis());
            right = parseRight(result);
        } catch (Exception e) {
            e.printStackTrace();
            logError(OP, "Failed to parse rights: " + e.getMessage());
            throw new VIRREServiceException(e.getMessage(), e);
        }

        return right;
    }

    private RepresentationRight parseRight(RightToRepresentResponseType result) {
        RepresentationRight right = new RepresentationRight();
        String code = result.getCode(); //msg.getCode();
        int num = Integer.parseInt(code);
        right.setCode(num);
        return right;
    }

}
