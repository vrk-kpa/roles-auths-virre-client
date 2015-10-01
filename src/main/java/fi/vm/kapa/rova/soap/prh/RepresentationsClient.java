package fi.vm.kapa.rova.soap.prh;

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
import fi.vm.kapa.rova.soap.handlers.RepresentationsXroadHeaderHandler;
import fi.vrk.xml.rova.prh.representations.ObjectFactory;
import fi.vrk.xml.rova.prh.representations.ProviderGateway;
import fi.vrk.xml.rova.prh.representations.Request;
import fi.vrk.xml.rova.prh.representations.RoVaCompanyRepresentationsResponse;
import fi.vrk.xml.rova.prh.representations.XRoadPortType;

@Component
public class RepresentationsClient implements SpringPropertyNames {

    ProviderGateway service = new ProviderGateway();
    ObjectFactory factory = new ObjectFactory();

    @Autowired
    private RepresentationsXroadHeaderHandler xroadHeaderHandler;

    @Autowired
    private AttachmentHandler attachmentHandler;

    @Autowired
    private HttpServletRequest httpRequest;
    
    @Value(XROAD_ENDPOINT)
    private String xrdEndPoint;

    private static Logger LOG = Logger.getLogger(RepresentationsClient.class);
    
    @PostConstruct
    public void init() {
        HandlerResolver hs = new HandlerResolver() {
            @SuppressWarnings("rawtypes")
            @Override
            public List<Handler> getHandlerChain(PortInfo portInfo) {
                List<Handler> handlers = new ArrayList<Handler>();
                handlers.add(xroadHeaderHandler);
                handlers.add(attachmentHandler);
                return handlers;
            }
        };
        service.setHandlerResolver(hs);
    }
    
    public String getResponse(String businessId) {
        XRoadPortType port = service.getXRoadServicePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        Holder<Request> request = new Holder<Request>(factory.createRequest());
        RoVaCompanyRepresentationsResponse companiesResponse = factory.createRoVaCompanyRepresentationsResponse();

        Holder<RoVaCompanyRepresentationsResponse.Response> response = new Holder<RoVaCompanyRepresentationsResponse.Response>(companiesResponse.getResponse());

        request.value.setBusinessid(businessId);
       
        port.roVaCompanyRepresentations(request, response);
        
        String attachment = (String) httpRequest.getAttribute(AttachmentHandler.ATTACHMENT_ATTRIBUTE);

        LOG.info("Got representations listing response from PRH: " + response);

        return attachment;
        
    }
}
 