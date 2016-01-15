package fi.vm.kapa.rova.soap.prh;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.handlers.ActiveRolesClientXroadHeaderHandler;
import fi.vm.kapa.rova.soap.handlers.AttachmentHandler;
import fi.vm.kapa.rova.soap.handlers.CompaniesXroadHeaderHandler;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoResponse;
import fi.vrk.xml.rova.prh.activeroles.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
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
    
    public String getResponse(String personId) {
        XRoadPortType port = service.getXRoadServicePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        Holder<Request> request = new Holder<Request>(factory.createRequest());
        XRoadPersonActiveRoleInfoResponse XRoadResponse = factory.createXRoadPersonActiveRoleInfoResponse();


        Holder<XRoadPersonActiveRoleInfoResponse.Response> response = new Holder<XRoadPersonActiveRoleInfoResponse.Response>(XRoadResponse.getResponse());

        request.value.setSocialSecurityNumber(personId);
       
        port.xRoadPersonActiveRoleInfo(request, response);
        
        String attachment = (String) httpRequest.getAttribute(AttachmentHandler.ATTACHMENT_ATTRIBUTE);

        LOG.info("Got company listing response from PRH: " + response);

        return attachment;
        
    }
}
 