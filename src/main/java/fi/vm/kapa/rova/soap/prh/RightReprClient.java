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
package fi.vm.kapa.rova.soap.prh;

import fi.prh.virre.xroad.producer.righttorepresent.XRoadRightToRepresentPortType;
import fi.prh.virre.xroad.producer.righttorepresent.XRoadRightToRepresentRequestType;
import fi.prh.virre.xroad.producer.righttorepresent.XRoadRightToRepresentResponseType;
import fi.vm.kapa.rova.logging.Logger;
import https.ws_prh_fi.novus.ids.services._2008._08._22.RightToRepresent;
import https.ws_prh_fi.novus.ids.services._2008._08._22.RightToRepresentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.Holder;

@Component
public class RightReprClient extends AbstractClient {
    private static Logger LOG = Logger.getLogger(RightReprClient.class);

    @Autowired
    private XRoadRightToRepresentPortType rightToRepresentClient;

    fi.prh.virre.xroad.producer.righttorepresent.ObjectFactory producerFactory = new fi.prh.virre.xroad.producer.righttorepresent.ObjectFactory();
    https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory novusFactory = new https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory();

    public RightToRepresentResponse getRights(String socialSec, String businessId, String rightLevel) {
        Holder<XRoadRightToRepresentRequestType> requestHolder = new Holder<XRoadRightToRepresentRequestType>();
        XRoadRightToRepresentRequestType req = producerFactory.createXRoadRightToRepresentRequestType();
        RightToRepresent rr = novusFactory.createRightToRepresent();
        rr.setPersonId(socialSec);
        rr.setBusinessId(businessId);
        rr.setLevel(rightLevel);
        rr.setUserId(getEnduser());
        req.setRightToRepresent(rr);
        requestHolder.value = req;

        Holder<XRoadRightToRepresentResponseType> responseHolder = new Holder<XRoadRightToRepresentResponseType>();

        rightToRepresentClient.xRoadRightToRepresent(requestHolder, responseHolder);
        RightToRepresentResponse response = responseHolder.value.getRightToRepresentResponse();

        LOG.debug("soap for right to represent succeeded");
        return response;
    }
}