package fi.vm.kapa.rova.soap.virre;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.rest.identification.RequestIdentificationFilter;

import fi.vm.kapa.rova.soap.handlers.XroadHeaderHandler;
import fi.vm.kapa.rova.soap.virre.model.VIRREResponseMessage;

import fi.vrk.xml.rova.virre.ObjectFactory;
import fi.vrk.xml.rova.virre.ProviderGateway;
import fi.vrk.xml.rova.virre.RoVaRoles;
import fi.vrk.xml.rova.virre.RoVaRolesResponse;
import fi.vrk.xml.rova.virre.XRoadPortType;
import fi.vrk.xml.rova.virre.Request;


import javax.xml.ws.Holder;

@Component
public class VIRREClient implements SpringPropertyNames {

    ProviderGateway service = new ProviderGateway();
    ObjectFactory factory = new ObjectFactory();

    @Autowired
    private XroadHeaderHandler xroadHeaderHandler;

    @Autowired
    private HttpServletRequest request;
    
    @Value(VIRRE_USERNAME)
    private String vtjUsername;
    @Value(VIRRE_PASSWORD)
    private String vtjPassword;
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
    
    public VIRREResponseMessage getResponse(String personId) {
        XRoadPortType port = service.getXRoadServicePort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        Holder<Request> request = new Holder<Request>(factory.createRequest());
        RoVaRolesResponse roleResponse = factory.createRoVaRolesResponse();
        Holder<RoVaRolesResponse.Response> response = new Holder<RoVaRolesResponse.Response>(roleResponse.getResponse());
        request.value.setHetu(personId);
      
        port.roVaRoles(request, response);
        System.out.println(response);
        return null;
        
    }
}
 

