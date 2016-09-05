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

import fi.vm.kapa.rova.external.model.virre.RepresentationRight;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.RightReprClient;
import https.ws_prh_fi.novus.ids.services._2008._08._22.RightToRepresentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Service for loading Companies from PRH
 * @author mtom
 */
@Service
public class RightReprService extends ServiceLogging {

    public static final String OP = "RightsService";

    private static final Logger log = Logger.getLogger(RightReprService.class);

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
    public RepresentationRight getRights(String socialsec, String businessId, String level) {
        log.debug("service received: "+ socialsec +" - "+ businessId +" - "+ level);

        RepresentationRight right;

        long startTime = System.currentTimeMillis();
        RightToRepresentResponse result = rrc.getRights(socialsec, businessId, level);
        logRequest(OP + ":XRoadRightToRepresent", startTime, System.currentTimeMillis());
        right = parseRight(result);

        return right;
    }

    private RepresentationRight parseRight(RightToRepresentResponse result) {
        RepresentationRight right = new RepresentationRight();
        String code = result.getRepresentation().getCode();
        int num = Integer.parseInt(code);
        right.setCode(num);
        return right;
    }

}
