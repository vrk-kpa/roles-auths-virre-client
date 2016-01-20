package fi.vm.kapa.rova.soap.prh;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.logging.Logger;
import fi.vrk.xml.rova.prh.activeroles.ObjectFactory;
import fi.vrk.xml.rova.prh.activeroles.PersonActiveRoleInfoResponseType;
import fi.vrk.xml.rova.prh.activeroles.PersonActiveRoleInfoType;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfo;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoPortType;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoPortTypeService;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoResponse;

@Component
public class ActiveRolesClient extends AbstractPrhClient {
    private static Logger LOG = Logger.getLogger(ActiveRolesClient.class);
    
    XRoadPersonActiveRoleInfoPortTypeService service = new XRoadPersonActiveRoleInfoPortTypeService();
    ObjectFactory factory = new ObjectFactory();

    public PersonActiveRoleInfoResponseType getResponse(String personId) throws JAXBException {
        XRoadPersonActiveRoleInfoPortType port = service.getXRoadPersonActiveRoleInfoPortTypePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);
        XRoadPersonActiveRoleInfo request = factory.createXRoadPersonActiveRoleInfo();
        PersonActiveRoleInfoType value = factory.createPersonActiveRoleInfoType();
        value.setSocialSecurityNumber(personId);
        request.setRequest(value);

        Holder<XRoadPersonActiveRoleInfoResponse> response = new Holder<XRoadPersonActiveRoleInfoResponse>();
        response.value = factory.createXRoadPersonActiveRoleInfoResponse();

        port.xRoadPersonActiveRoleInfo(request, getClientHeader(factory), getServiceHeader(factory), 
                getUserIdHeader(), getIdHeader(), getProtocolVersionHeader(), response);
        LOG.debug("soap succeeded");
        
        return response.value.getResponse();
   }
}
 