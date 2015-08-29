package fi.vm.kapa.rova.soap.virre;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.handlers.AttachmentHandler;
import fi.vm.kapa.rova.soap.handlers.XroadHeaderHandler;
import fi.vrk.xml.rova.virre.ObjectFactory;
import fi.vrk.xml.rova.virre.ProviderGateway;
import fi.vrk.xml.rova.virre.Request;
import fi.vrk.xml.rova.virre.RoVaRolesResponse;
import fi.vrk.xml.rova.virre.XRoadPortType;

@Component
public class VIRREClient implements SpringPropertyNames {

    ProviderGateway service = new ProviderGateway();
    ObjectFactory factory = new ObjectFactory();

    @Autowired
    private XroadHeaderHandler xroadHeaderHandler;

    @Autowired
    private HttpServletRequest httpRequest;
    
    @Value(XROAD_ENDPOINT)
    private String xrdEndPoint;

    private static Logger LOG = Logger.getLogger(VIRREClient.class, Logger.VIRRE_CLIENT);
    
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
        RoVaRolesResponse roleResponse = factory.createRoVaRolesResponse();
        Holder<RoVaRolesResponse.Response> response = new Holder<RoVaRolesResponse.Response>(roleResponse.getResponse());
        request.value.setHetu(personId);
        
        port.roVaRoles(request, response);
        
        String attachment = (String) httpRequest.getAttribute(AttachmentHandler.ATTACHMENT_ATTRIBUTE);
        LOG.debug("Virre got response: "+ response);
        return attachment;
        
    }
} //TODO:handler ottaa attackmentin ja muuntaa sen stringiksi
 

