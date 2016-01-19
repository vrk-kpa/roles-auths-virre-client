package fi.vm.kapa.rova.soap.prh;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.rest.identification.RequestIdentificationFilter;
import fi.vm.kapa.rova.soap.handlers.ActiveRolesClientXroadHeaderHandler;
import fi.vm.kapa.rova.soap.handlers.AttachmentHandler;
import fi.vm.kapa.rova.soap.handlers.CompaniesXroadHeaderHandler;
import fi.vm.kapa.rova.soap.prh.model.CompaniesResponseMessage;
import fi.vm.kapa.rova.soap.prh.model.ExtendedRoleInfo;
import fi.vm.kapa.rova.soap.prh.model.Role;
import fi.vm.kapa.rova.soap.virre.CustomValidationEventHandler;
import fi.vm.kapa.rova.soap.virre.model.RoleInCompany;
import fi.vm.kapa.rova.soap.virre.model.RoleInfo;
import fi.vm.kapa.rova.soap.virre.model.VirreResponseMsg;
import fi.vrk.xml.rova.prh.activeroles.*;
import fi.vrk.xml.rova.prh.activeroles.ObjectFactory;
import fi.vrk.xml.rova.prh.activeroles.RoleInCompanyType.RoleBasicInfo;
import fi.vrk.xml.rova.prh.activeroles.XRoadClientIdentifierType;
import fi.vrk.xml.rova.prh.activeroles.XRoadObjectType;
import fi.vrk.xml.rova.prh.activeroles.XRoadServiceIdentifierType;
import fi.vrk.xml.rova.virre.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ActiveRolesClient implements SpringPropertyNames {

    XRoadPersonActiveRoleInfoPortTypeService service = new XRoadPersonActiveRoleInfoPortTypeService();
    ObjectFactory factory = new ObjectFactory();

    @Autowired
    private HttpServletRequest request;

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


    @Autowired
    private ActiveRolesClientXroadHeaderHandler xroadHeaderHandler;

    @Autowired
    private HttpServletRequest httpRequest;
    
    @Value(XROAD_ENDPOINT)
    private String xrdEndPoint;

    private static Logger LOG = Logger.getLogger(ActiveRolesClient.class);
    
/*    @PostConstruct
    public void init() {
        HandlerResolver hs = new HandlerResolver() {
            @SuppressWarnings("rawtypes")
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlers = new ArrayList<Handler>();
                handlers.add(xroadHeaderHandler);
                return handlers;
            }
        };
        service.setHandlerResolver(hs);
    }
*/
    private Holder<XRoadClientIdentifierType> getClientHeader() {
        Holder<XRoadClientIdentifierType> result = new Holder<>();
        result.value = factory.createXRoadClientIdentifierType();
        result.value.setObjectType("SUBSYSTEM");
        result.value.setXRoadInstance(clientSdsbInstance);
        result.value.setMemberClass(clientMemberClass);
        result.value.setMemberCode(clientMemberCode);
        result.value.setSubsystemCode(clientSubsystemCode);
        return result;
    }

    private Holder<XRoadServiceIdentifierType> getServiceHeader() {
        Holder<XRoadServiceIdentifierType> result = new Holder<>();
        result.value = factory.createXRoadServiceIdentifierType();
        result.value.setObjectType("SERVICE");
        result.value.setXRoadInstance(serviceSdsbInstance);
        result.value.setMemberClass(serviceMemberClass);
        result.value.setMemberCode(serviceMemberCode);
        result.value.setSubsystemCode(serviceSubsystemCode);
        result.value.setServiceCode("XRoadPersonActiveRoleInfo");
        return result;

    }

    private Holder<String> getUserIdHeader() {
        Holder<String> result = new Holder<>();
        String origUserId = request.getHeader(RequestIdentificationFilter.XROAD_END_USER);
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
        result.value = "4.0";
        return result;
    }

    public CompaniesResponseMessage getResponse(String personId) throws JAXBException {
        XRoadPersonActiveRoleInfoPortType port = service.getXRoadPersonActiveRoleInfoPortTypePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);
        XRoadPersonActiveRoleInfo request = factory.createXRoadPersonActiveRoleInfo();
        PersonActiveRoleInfoType value = factory.createPersonActiveRoleInfoType();
        value.setSocialSecurityNumber(personId);
        request.setRequest(value);

        Holder<XRoadPersonActiveRoleInfoResponse> response = new Holder<XRoadPersonActiveRoleInfoResponse>();
        response.value = factory.createXRoadPersonActiveRoleInfoResponse();

        CompaniesResponseMessage result = new CompaniesResponseMessage();
        try {
            port.xRoadPersonActiveRoleInfo(request, getClientHeader(), getServiceHeader(), getUserIdHeader(), getIdHeader(), getProtocolVersionHeader(), response);
            LOG.info("soap succeeded");

            result.setSocialSec(response.value.getResponse().getSocialSecurityNumber());
            result.setStatus(response.value.getResponse().getStatus());


            List<Role> roles = new ArrayList<>(); // to be set into responseMsg

            for (RoleInCompanyType roleInCompany : response.value.getResponse().getRoleInCompany()) {

                Role role = new Role();
                role.setBusinessId(roleInCompany.getBusinessId());
                role.setCompanyName(roleInCompany.getCompanyName());

                List<ExtendedRoleInfo> extendedRoleInfos = new ArrayList<>();
                
                for (RoleBasicInfo roleInfo : roleInCompany.getRoleBasicInfo()) {
                    ExtendedRoleInfo extendedRoleInfo = new ExtendedRoleInfo();
                    extendedRoleInfo.setRoleType(roleInfo.getName());
                    extendedRoleInfo.setBodyType(roleInfo.getBodyType());
                    extendedRoleInfo.setStartDate(roleInfo.getStartDate().toString());

                    extendedRoleInfos.add(extendedRoleInfo);
                    
                }
                role.setExtendedRoleInfos(extendedRoleInfos);

                roles.add(role);
            }
            
            result.setRoles(roles);
        } catch (Exception e) {
            LOG.warning("exception: "+ e.getMessage());
            e.printStackTrace();
        }
        
        return result;
   }
}
 