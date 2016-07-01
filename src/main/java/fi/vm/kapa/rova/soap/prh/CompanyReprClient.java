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

import fi.prh.virre.xroad.producer.companyrepresent.XRoadCompanyRepresentInfoPortType;
import fi.prh.virre.xroad.producer.companyrepresent.XRoadCompanyRepresentInfoRequestType;
import fi.prh.virre.xroad.producer.companyrepresent.XRoadCompanyRepresentInfoResponseType;
import fi.vm.kapa.rova.logging.Logger;
import https.ws_prh_fi.novus.ids.services._2008._08._22.CompanyBasicInfoType;
import https.ws_prh_fi.novus.ids.services._2008._08._22.CompanyRepresentInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.Holder;

@Component
public class CompanyReprClient extends AbstractClient {
    private static Logger LOG = Logger.getLogger(CompanyReprClient.class);

    @Autowired
    private XRoadCompanyRepresentInfoPortType companyRepresentClient;

    fi.prh.virre.xroad.producer.companyrepresent.ObjectFactory producerFactory = new fi.prh.virre.xroad.producer.companyrepresent.ObjectFactory();
    https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory novusFactory = new https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory();

    public CompanyRepresentInfoResponse getResponse(String businessId) throws VirreException {
        javax.xml.ws.Holder<XRoadCompanyRepresentInfoRequestType> request = new Holder<>();
        javax.xml.ws.Holder<XRoadCompanyRepresentInfoResponseType> response = new Holder<>();

        request.value = producerFactory.createXRoadCompanyRepresentInfoRequestType();

        CompanyBasicInfoType req = novusFactory.createCompanyBasicInfoType();
        req.setBusinessId(businessId);
        req.setUserId(getEnduser());
        request.value.setCompanyRepresentInfo(req);

        companyRepresentClient.xRoadCompanyRepresentInfo(request, response);

        if (response.value.getFaultCode() != null) {
            throw new VirreException(response.value.getFaultCode() + " " + response.value.getFaultString());
        }

        CompanyRepresentInfoResponse result = response.value.getCompanyRepresentInfoResponse();

        LOG.debug("Soap request succeeded.");
        return result;
    }
}