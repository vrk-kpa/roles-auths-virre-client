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

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Holder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.rest.identification.RequestIdentificationFilter;

public abstract class AbstractPrhClient implements SpringPropertyNames {

    public static final String PROTOCOL_VERSION = "4.0";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    protected HttpServletRequest httpRequest;

    @Value(SERVICE_OBJECT_TYPE)
    protected String serviceObjectType;

    @Value(CLIENT_OBJECT_TYPE)
    protected String clientObjectType;

    @Value(CLIENT_SDSB_INSTANCE)
    protected String clientSdsbInstance;

    @Value(CLIENT_MEMBER_CLASS)
    protected String clientMemberClass;

    @Value(CLIENT_MEMBER_CODE)
    protected String clientMemberCode;

    @Value(CLIENT_SUBSYSTEM_CODE)
    protected String clientSubsystemCode;

    @Value(SERVICE_SDSB_INSTANCE)
    protected String serviceSdsbInstance;

    @Value(SERVICE_MEMBER_CLASS)
    protected String serviceMemberClass;

    @Value(SERVICE_MEMBER_CODE)
    protected String serviceMemberCode;

    @Value(SERVICE_SUBSYSTEM_CODE)
    protected String serviceSubsystemCode;

    @Value(XROAD_ENDPOINT)
    protected String xrdEndPoint;

    protected String serviceCode;

    public Holder<String> getUserIdHeader() {
        Holder<String> result = new Holder<>();
        String origUserId = httpRequest.getHeader(RequestIdentificationFilter.ORIG_END_USER);
        if (origUserId == null) {
            origUserId = "rova-end-user-unknown";
        }
        result.value = origUserId;
        return result;
    }
    
    public Holder<String> getIdHeader() {
        Holder<String> result = new Holder<>();
        result.value = UUID.randomUUID().toString();
        return result;
    }

    public  Holder<String> getProtocolVersionHeader() {
        Holder<String> result = new Holder<>();
        result.value = PROTOCOL_VERSION;
        return result;
    }

    public Holder<String> getIssueHeader() {
        Holder<String> result = new Holder<>();

        String origRequestId = request.getHeader(RequestIdentificationFilter.ORIG_REQUEST_IDENTIFIER);
        if (origRequestId == null) {
            origRequestId = "";
        }

        result.value = origRequestId;
        return result;
    }

}
