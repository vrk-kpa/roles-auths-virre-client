package fi.vm.kapa.rova.soap.prh;

import javax.xml.bind.JAXBException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.logging.Logger;
import fi.vrk.xml.rova.prh.companyreprinfo.CompanyBasicInfoType;
import fi.vrk.xml.rova.prh.companyreprinfo.CompanyRepresentInfoResponseType;
import fi.vrk.xml.rova.prh.companyreprinfo.ObjectFactory;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadClientIdentifierType;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfo;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfoPortType;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfoPortTypeService;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadCompanyRepresentInfoResponse;
import fi.vrk.xml.rova.prh.companyreprinfo.XRoadServiceIdentifierType;

@Component
public class CompanyReprClient extends AbstractPrhClient {
    private static Logger LOG = Logger.getLogger(CompanyReprClient.class);

    @Value(SERVICE_REPRESENTATIONS_SERVICE_CODE)
    private String serviceCode;
    
    XRoadCompanyRepresentInfoPortTypeService service = new XRoadCompanyRepresentInfoPortTypeService();
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

    public CompanyRepresentInfoResponseType getResponse(String businessId) throws JAXBException {

        XRoadCompanyRepresentInfoPortType port = service.getXRoadCompanyRepresentInfoPortTypePort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xrdEndPoint);

        XRoadCompanyRepresentInfo request = factory.createXRoadCompanyRepresentInfo();
        CompanyBasicInfoType value = factory.createCompanyBasicInfoType();

        value.setBusinessId(businessId);
        request.setRequest(value);

        Holder<XRoadCompanyRepresentInfoResponse> response = new Holder<>();
        response.value = factory.createXRoadCompanyRepresentInfoResponse();

        CompanyRepresentInfoResponseType result = null;

        try {

            port.xRoadCompanyRepresentInfo(request, getClientHeader(factory),
                    getServiceHeader(factory), getUserIdHeader(), getIdHeader(), getIssueHeader(),
                    getProtocolVersionHeader(), response);

            LOG.debug("Soap request succeeded.");
            
            result = response.value.getResponse();

        } catch (Exception e) {
            LOG.error("Failed to fetch company representation data: " + e.getMessage());
        }

        return result;
        
    }
}
 