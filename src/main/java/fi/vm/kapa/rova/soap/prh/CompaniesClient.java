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
import fi.vm.kapa.rova.soap.handlers.CompaniesXroadHeaderHandler;
import fi.vrk.xml.rova.prh.companies.ObjectFactory;
import fi.vrk.xml.rova.prh.companies.ProviderGateway;
import fi.vrk.xml.rova.prh.companies.Request;
import fi.vrk.xml.rova.prh.companies.RoVaListCompaniesResponse;
import fi.vrk.xml.rova.prh.companies.XRoadPortType;

@Component
public class CompaniesClient implements SpringPropertyNames {

    ProviderGateway service = new ProviderGateway();
    ObjectFactory factory = new ObjectFactory();

    @Autowired
    private CompaniesXroadHeaderHandler xroadHeaderHandler;

    @Autowired
    private AttachmentHandler attachmentHandler;

    @Autowired
    private HttpServletRequest httpRequest;
    
    @Value(XROAD_ENDPOINT)
    private String xrdEndPoint;

    private static Logger LOG = Logger.getLogger(CompaniesClient.class);
    
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
    
    public String getResponse(String personId) {
        XRoadPortType port = service.getXRoadServicePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        Holder<Request> request = new Holder<Request>(factory.createRequest());
        RoVaListCompaniesResponse companiesResponse = factory.createRoVaListCompaniesResponse();

        Holder<RoVaListCompaniesResponse.Response> response = new Holder<RoVaListCompaniesResponse.Response>(companiesResponse.getResponse());

        request.value.setSocialsecuritynumber(personId);
       
        port.roVaListCompanies(request, response);
        
        String attachment = (String) httpRequest.getAttribute(AttachmentHandler.ATTACHMENT_ATTRIBUTE);

        LOG.info("Got company listing response from PRH: " + response);

        return attachment;
        
    }
}
 