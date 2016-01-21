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
    protected HttpServletRequest httpRequest;

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
        String origUserId = httpRequest.getHeader(RequestIdentificationFilter.XROAD_END_USER);
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

}
