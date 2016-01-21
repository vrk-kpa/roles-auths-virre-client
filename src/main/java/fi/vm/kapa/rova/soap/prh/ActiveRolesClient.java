package fi.vm.kapa.rova.soap.prh;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.logging.Logger;
import fi.vrk.xml.rova.prh.activeroles.ObjectFactory;
import fi.vrk.xml.rova.prh.activeroles.PersonActiveRoleInfoResponseType;
import fi.vrk.xml.rova.prh.activeroles.PersonActiveRoleInfoType;
import fi.vrk.xml.rova.prh.activeroles.XRoadClientIdentifierType;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfo;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoPortType;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoPortTypeService;
import fi.vrk.xml.rova.prh.activeroles.XRoadPersonActiveRoleInfoResponse;
import fi.vrk.xml.rova.prh.activeroles.XRoadServiceIdentifierType;

@Component
public class ActiveRolesClient extends AbstractPrhClient {
    private static final Logger LOG = Logger.getLogger(ActiveRolesClient.class);
    
    @Value(SERVICE_COMPANIES_SERVICE_CODE)
    private String serviceCode;
    
    XRoadPersonActiveRoleInfoPortTypeService service = new XRoadPersonActiveRoleInfoPortTypeService();
    ObjectFactory factory = new ObjectFactory();

    public Holder<XRoadClientIdentifierType> getClientHeader(ObjectFactory factory) {
        Holder<XRoadClientIdentifierType> result = new Holder<>();
        result.value = factory.createXRoadClientIdentifierType();
        result.value.setObjectType(clientObjectType);
        result.value.setXRoadInstance(clientSdsbInstance);
        result.value.setMemberClass(clientMemberClass);
        result.value.setMemberCode(clientMemberCode);
        result.value.setSubsystemCode(clientSubsystemCode);
        return result;
    }

    public Holder<XRoadServiceIdentifierType> getServiceHeader(ObjectFactory factory) {
        Holder<XRoadServiceIdentifierType> result = new Holder<>();
        result.value = factory.createXRoadServiceIdentifierType();
        result.value.setObjectType(serviceObjectType);
        result.value.setXRoadInstance(serviceSdsbInstance);
        result.value.setMemberClass(serviceMemberClass);
        result.value.setMemberCode(serviceMemberCode);
        result.value.setSubsystemCode(serviceSubsystemCode);
        result.value.setServiceCode(serviceCode);
        return result;
    }

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
        LOG.debug("soap for active role info succeeded");
        
        return response.value.getResponse();
   }
}
 