package fi.vm.kapa.rova.soap.prh;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.logging.Logger;
import fi.vrk.xml.rova.prh.companyright.ObjectFactory;
import fi.vrk.xml.rova.prh.companyright.RightToRepresentParametersType;
import fi.vrk.xml.rova.prh.companyright.RightToRepresentResponseType;
import fi.vrk.xml.rova.prh.companyright.XRoadClientIdentifierType;
import fi.vrk.xml.rova.prh.companyright.XRoadRightToRepresent;
import fi.vrk.xml.rova.prh.companyright.XRoadRightToRepresentPortType;
import fi.vrk.xml.rova.prh.companyright.XRoadRightToRepresentPortTypeService;
import fi.vrk.xml.rova.prh.companyright.XRoadRightToRepresentResponse;
import fi.vrk.xml.rova.prh.companyright.XRoadServiceIdentifierType;

@Component
public class RightReprClient extends AbstractPrhClient {
    private static Logger LOG = Logger.getLogger(RightReprClient.class);
    
    @Value(SERVICE_RIGHTS_SERVICE_CODE)
    private String serviceCode;

    XRoadRightToRepresentPortTypeService service = new XRoadRightToRepresentPortTypeService(); 
    ObjectFactory factory = new ObjectFactory();
    
    public Holder<XRoadClientIdentifierType> getClientHeader() {
        Holder<XRoadClientIdentifierType> result = new Holder<>();
        result.value = factory.createXRoadClientIdentifierType();
        result.value.setObjectType(clientObjectType);
        result.value.setXRoadInstance(clientSdsbInstance);
        result.value.setMemberClass(clientMemberClass);
        result.value.setMemberCode(clientMemberCode);
        result.value.setSubsystemCode(clientSubsystemCode);
        return result;
    }

    public Holder<XRoadServiceIdentifierType> getServiceHeader() {
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

    public RightToRepresentResponseType getRights(String socialSec, String businessId, String rightLevel) {
        XRoadRightToRepresentPortType port = service.getXRoadRightToRepresentPortTypePort();
        BindingProvider bp = (BindingProvider) port;
        
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);
        
        XRoadRightToRepresent request = factory.createXRoadRightToRepresent();
        RightToRepresentParametersType parametersType = factory.createRightToRepresentParametersType();
        parametersType.setBusinessId(businessId);
        parametersType.setPersonId(socialSec);
        parametersType.setLevel(rightLevel);
        request.setRequest(parametersType);
        
        Holder<XRoadRightToRepresentResponse> response = new Holder<>();
        response.value = factory.createXRoadRightToRepresentResponse();

        port.xRoadRightToRepresent(request, getClientHeader(), getServiceHeader(), 
                getUserIdHeader(), getIdHeader(), getIssueHeader(), getProtocolVersionHeader(), response);
        LOG.debug("soap for right to represent succeeded");

        return response.value.getResponse();
        
    }
}
 