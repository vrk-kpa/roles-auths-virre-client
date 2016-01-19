package fi.vm.kapa.rova.soap.prh;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;
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

@Component
public class ActiveRolesClient implements SpringPropertyNames {

    ProviderGateway service = new ProviderGateway();
    ObjectFactory factory = new ObjectFactory();

    @Autowired
    private ActiveRolesClientXroadHeaderHandler xroadHeaderHandler;

    @Autowired
    private HttpServletRequest httpRequest;
    
    @Value(XROAD_ENDPOINT)
    private String xrdEndPoint;

    private static Logger LOG = Logger.getLogger(ActiveRolesClient.class);
    
    @PostConstruct
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
    
    @SuppressWarnings("restriction")
    public CompaniesResponseMessage getResponse(String personId) throws JAXBException {
        XRoadPortType port = service.getXRoadServicePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        Holder<Request> request = new Holder<Request>(factory.createRequest());
        XRoadPersonActiveRoleInfoResponse xRoadResponse = factory.createXRoadPersonActiveRoleInfoResponse();

        Holder<XRoadPersonActiveRoleInfoResponse.Response> responseHolder = new Holder<XRoadPersonActiveRoleInfoResponse.Response>(xRoadResponse.getResponse());

        request.value.setSocialSecurityNumber(personId);
       
        port.xRoadPersonActiveRoleInfo(request, responseHolder);
        
        XRoadPersonActiveRoleInfoResponse.Response response = responseHolder.value; // soap response
        LOG.info("Got company listing response from PRH: " + response);

        CompaniesResponseMessage responseMsg = new CompaniesResponseMessage(); // return value
        
        ElementNSImpl socSecElement = (ElementNSImpl) response.getSocialSecurityNumber();
        responseMsg.setSocialSec(socSecElement.getTextContent());
        
        ElementNSImpl statusElement = (ElementNSImpl) response.getStatus();
        responseMsg.setStatus(statusElement.getTextContent());
        
        List<Role> roles = new ArrayList<>(); // to be set into responseMsg

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<ElementNSImpl> roleElements = (List) response.getRoleInCompany();
        JAXBContext context = JAXBContext.newInstance(RoleInCompany.class);
        Unmarshaller um = context.createUnmarshaller();
        um.setEventHandler(new CustomValidationEventHandler());
        for (ElementNSImpl roleElement : roleElements) {
            RoleInCompany roleInCompany = (RoleInCompany) um.unmarshal((Node) roleElement);
            LOG.info("role = "+ roleInCompany);

            Role role = new Role();
            role.setBusinessId(roleInCompany.getBusinessId());
            role.setCompanyName(roleInCompany.getCompanyName());
            
            RoleInfo roleInfo = roleInCompany.getRoleInfo();
            ExtendedRoleInfo extendedRoleInfo = new ExtendedRoleInfo();
            extendedRoleInfo.setRoleType(roleInfo.getName());
            extendedRoleInfo.setBodyType(roleInfo.getBodyType());
            extendedRoleInfo.setStartDate(roleInfo.getStartDate());
            
            List<ExtendedRoleInfo> extendedRoleInfos = new ArrayList<>();
            extendedRoleInfos.add(extendedRoleInfo);
            role.setExtendedRoleInfos(extendedRoleInfos);
            
            roles.add(role);
        }
        
        responseMsg.setRoles(roles);

        return responseMsg;
    }
}
 