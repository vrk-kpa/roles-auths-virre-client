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

import fi.prh.virre.xroad.producer.activeroleinfo.XRoadPersonActiveRoleInfoPortType;
import fi.prh.virre.xroad.producer.activeroleinfo.XRoadPersonActiveRoleInfoRequestType;
import fi.prh.virre.xroad.producer.activeroleinfo.XRoadPersonActiveRoleInfoResponseType;
import fi.vm.kapa.rova.logging.Logger;
import https.ws_prh_fi.novus.ids.services._2008._08._22.PersonActiveRoleInfoResponse;
import https.ws_prh_fi.novus.ids.services._2008._08._22.PersonActiveRoleInfoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.Holder;

@Component
public class ActiveRolesClient extends AbstractClient {
    private static final Logger LOG = Logger.getLogger(ActiveRolesClient.class);

    @Autowired
    private XRoadPersonActiveRoleInfoPortType personActiveRolesClient;

    fi.prh.virre.xroad.producer.activeroleinfo.ObjectFactory producerFactory
            = new fi.prh.virre.xroad.producer.activeroleinfo.ObjectFactory();
    https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory novusFactory
            = new https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory();

    public PersonActiveRoleInfoResponse getResponse(String personId) throws VirreException {
        PersonActiveRoleInfoType value = novusFactory.createPersonActiveRoleInfoType();
        value.setSocialSecurityNumber(personId);
        value.setUserId(getEnduser());
        Holder<XRoadPersonActiveRoleInfoRequestType> request = new Holder();
        request.value = producerFactory.createXRoadPersonActiveRoleInfoRequestType();
        request.value.setPersonActiveRoleInfo(value);
        Holder<XRoadPersonActiveRoleInfoResponseType> response = new Holder();
        response.value = producerFactory.createXRoadPersonActiveRoleInfoResponseType();
        response.value.setPersonActiveRoleInfoResponse(novusFactory.createPersonActiveRoleInfoResponse());

        personActiveRolesClient.xRoadPersonActiveRoleInfo(request, response);

        PersonActiveRoleInfoResponse result = response.value.getPersonActiveRoleInfoResponse();
        if (result.getError() != null) {
            throw new VirreException(result.getError().getMessage());
        }
        LOG.debug("soap for active role info succeeded");
        return result;
   }
}
 