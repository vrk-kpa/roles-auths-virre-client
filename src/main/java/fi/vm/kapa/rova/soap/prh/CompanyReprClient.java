package fi.vm.kapa.rova.soap.prh;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.rest.identification.RequestIdentificationFilter;
import fi.vm.kapa.rova.soap.prh.model.LegalRepresentation;
import fi.vm.kapa.rova.soap.prh.model.Representation;
import fi.vm.kapa.rova.soap.prh.model.RepresentationsResponseMessage;
import fi.vm.kapa.rova.soap.virre.CustomValidationEventHandler;
import fi.vm.kapa.rova.soap.prh.model.Body;
import fi.vrk.xml.rova.prh.companyreprinfo.BodyType;
import fi.vrk.xml.rova.prh.companyreprinfo.CompanyBasicInfoType;
import fi.vrk.xml.rova.prh.companyreprinfo.CompanyRepresentInfoResponseType;
import fi.vrk.xml.rova.prh.companyreprinfo.ObjectFactory;
import fi.vrk.xml.rova.prh.companyreprinfo.StateType;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadClientIdentifierType;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfo;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfoPortType;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfoPortTypeService;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfoResponse;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadServiceIdentifierType;
import java.util.LinkedList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import java.util.List;
import java.util.UUID;

@Component
public class CompanyReprClient implements SpringPropertyNames {

    public static final String SERVICE_CODE = "XRoadCompanyRepresentInfo";
    public static final String PROTOCOL_VERSION = "4.0";

    XRoadCompanyRepresentInfoPortTypeService service = new XRoadCompanyRepresentInfoPortTypeService();
    ObjectFactory factory = new ObjectFactory();

    @Autowired
    private HttpServletRequest httpRequest;

    @Value(CLIENT_OBJECT_TYPE)
    private String clientObjectType;

    @Value(CLIENT_SDSB_INSTANCE)
    private String clientSdsbInstance;

    @Value(CLIENT_MEMBER_CLASS)
    private String clientMemberClass;

    @Value(CLIENT_MEMBER_CODE)
    private String clientMemberCode;

    @Value(CLIENT_SUBSYSTEM_CODE)
    private String clientSubsystemCode;

    @Value(SERVICE_OBJECT_TYPE)
    private String serviceObjectType;

    @Value(SERVICE_SDSB_INSTANCE)
    private String serviceSdsbInstance;

    @Value(SERVICE_MEMBER_CLASS)
    private String serviceMemberClass;

    @Value(SERVICE_MEMBER_CODE)
    private String serviceMemberCode;

    @Value(SERVICE_SUBSYSTEM_CODE)
    private String serviceSubsystemCode;

    @Value(XROAD_ENDPOINT)
    private String xrdEndPoint;

    private static Logger LOG = Logger.getLogger(CompanyReprClient.class);

    private Holder<XRoadClientIdentifierType> getClientHeader() {
        Holder<XRoadClientIdentifierType> result = new Holder<>();
        result.value = factory.createXRoadClientIdentifierType();
        result.value.setObjectType(clientObjectType);
        result.value.setXRoadInstance(clientSdsbInstance);
        result.value.setMemberClass(clientMemberClass);
        result.value.setMemberCode(clientMemberCode);
        result.value.setSubsystemCode(clientSubsystemCode);
        return result;
    }

    private Holder<XRoadServiceIdentifierType> getServiceHeader() {
        Holder<XRoadServiceIdentifierType> result = new Holder<>();
        result.value = factory.createXRoadServiceIdentifierType();
        result.value.setObjectType(serviceObjectType);
        result.value.setXRoadInstance(serviceSdsbInstance);
        result.value.setMemberClass(serviceMemberClass);
        result.value.setMemberCode(serviceMemberCode);
        result.value.setSubsystemCode(serviceSubsystemCode);
        result.value.setServiceCode(SERVICE_CODE);
        return result;

    }

    private Holder<String> getUserIdHeader() {
        Holder<String> result = new Holder<>();
        String origUserId = httpRequest.getHeader(RequestIdentificationFilter.XROAD_END_USER);
        if (origUserId == null) {
            origUserId = "rova-end-user-unknown";
        }
        result.value = origUserId;
        return result;
    }

    private Holder<String> getIdHeader() {
        Holder<String> result = new Holder<>();
        result.value = UUID.randomUUID().toString();
        return result;
    }

    private Holder<String> getProtocolVersionHeader() {
        Holder<String> result = new Holder<>();
        result.value = PROTOCOL_VERSION;
        return result;
    }
    
    public CompanyRepresentInfoResponseType getResponse(String businessId) throws JAXBException {

        XRoadCompanyRepresentInfoPortType port = service.getXRoadCompanyRepresentInfoPortTypePort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        XRoadCompanyRepresentInfo request = factory.createXRoadCompanyRepresentInfo();
        CompanyBasicInfoType value = factory.createCompanyBasicInfoType();

        value.setBusinessId(businessId);
        request.setRequest(value);

        Holder<XRoadCompanyRepresentInfoResponse> response = new Holder<>();
        response.value = factory.createXRoadCompanyRepresentInfoResponse();

        CompanyRepresentInfoResponseType result = null;

        try {

            port.xRoadCompanyRepresentInfo(request, getClientHeader(),
                    getServiceHeader(), getUserIdHeader(), getIdHeader(),
                    getProtocolVersionHeader(), response);

            LOG.info("Soap request succeeded.");
            
            result = response.value.getResponse();

        } catch (Exception e) {
            LOG.error("Failed to fetch company representation data: " + e.getMessage());
        }

        return result;
        
    }
}
 