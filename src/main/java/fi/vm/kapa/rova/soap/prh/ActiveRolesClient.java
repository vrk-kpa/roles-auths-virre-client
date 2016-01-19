package fi.vm.kapa.rova.soap.prh;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.handlers.ActiveRolesClientXroadHeaderHandler;
import fi.vm.kapa.rova.soap.handlers.AttachmentHandler;
import fi.vm.kapa.rova.soap.handlers.CompaniesXroadHeaderHandler;
import fi.vm.kapa.rova.soap.virre.CustomValidationEventHandler;
import fi.vm.kapa.rova.soap.virre.model.RoleInCompany;
import fi.vm.kapa.rova.soap.virre.model.VirreResponseMessage;
import fi.vrk.xml.rova.prh.activeroles.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

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
    
    public VirreResponseMessage getResponse(String personId) throws JAXBException { // String
        XRoadPortType port = service.getXRoadServicePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        Holder<Request> request = new Holder<Request>(factory.createRequest());
        XRoadPersonActiveRoleInfoResponse xRoadResponse = factory.createXRoadPersonActiveRoleInfoResponse();

        Holder<XRoadPersonActiveRoleInfoResponse.Response> responseHolder = new Holder<XRoadPersonActiveRoleInfoResponse.Response>(xRoadResponse.getResponse());

        request.value.setSocialSecurityNumber(personId);
       
        port.xRoadPersonActiveRoleInfo(request, responseHolder);
        
//        String attachment = (String) httpRequest.getAttribute(AttachmentHandler.ATTACHMENT_ATTRIBUTE);

        VirreResponseMessage result = null;
        
        XRoadPersonActiveRoleInfoResponse.Response response = responseHolder.value;

        LOG.info("Got company listing response from PRH: " + response);

        Object object = response.getAny();
        if (object != null) {
            JAXBContext context = JAXBContext.newInstance(VirreResponseMessage.class);
            Unmarshaller um = context.createUnmarshaller();
            um.setEventHandler(new CustomValidationEventHandler());
            LOG.info("Ready for unmarshalling");
            try {
                JAXBElement<VirreResponseMessage> root = um.unmarshal((Node) object, VirreResponseMessage.class);
                result = root.getValue();
//                result = (VirreResponseMessage) um.unmarshal((Node) object);
                LOG.info("Unmarshalling done: "+ result);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            LOG.info("getAny() gave null");
        }
        return result;
        
    }
}
 