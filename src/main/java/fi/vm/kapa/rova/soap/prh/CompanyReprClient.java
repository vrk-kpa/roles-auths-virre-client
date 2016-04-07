package fi.vm.kapa.rova.soap.prh;

import eu.x_road.xsd.identifiers.ObjectFactory;
import eu.x_road.xsd.identifiers.XRoadClientIdentifierType;
import eu.x_road.xsd.identifiers.XRoadServiceIdentifierType;
import fi.prh.virre.xroad.producer.companyrepresent.XRoadCompanyRepresentInfo;
import fi.prh.virre.xroad.producer.companyrepresent.XRoadCompanyRepresentInfoPortType;
import fi.prh.virre.xroad.producer.companyrepresent.XRoadCompanyRepresentInfoResponse;
import fi.vm.kapa.rova.logging.Logger;
import https.ws_prh_fi.novus.ids.services._2008._08._22.CompanyBasicInfoType;
import https.ws_prh_fi.novus.ids.services._2008._08._22.CompanyRepresentInfoResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import javax.xml.ws.Holder;

@Component
public class CompanyReprClient extends AbstractPrhClient {
    private static Logger LOG = Logger.getLogger(CompanyReprClient.class);

    @Value(SERVICE_REPRESENTATIONS_SERVICE_CODE)
    private String serviceCode;

    @Autowired
    private XRoadCompanyRepresentInfoPortType companyRepresentClient;

    ObjectFactory identifierFactory = new ObjectFactory();
    fi.prh.virre.xroad.producer.companyrepresent.ObjectFactory producerFactory = new fi.prh.virre.xroad.producer.companyrepresent.ObjectFactory();
    https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory novusFactory = new https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory();


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

        XRoadCompanyRepresentInfo request = producerFactory.createXRoadCompanyRepresentInfo();
        CompanyBasicInfoType value = novusFactory.createCompanyBasicInfoType();

        value.setBusinessId(businessId);
        request.setRequest(value);

        CompanyRepresentInfoResponseType result = null;

        try {
            XRoadCompanyRepresentInfoResponse response = companyRepresentClient.xRoadCompanyRepresentInfo(request, getClientHeader(identifierFactory), getServiceHeader(identifierFactory), getUserIdHeader(), getIdHeader(), getIssueHeader(), getProtocolVersionHeader());
            LOG.debug("Soap request succeeded.");
            result = response.getResponse();

        } catch (Exception e) {
            LOG.error("Failed to fetch company representation data: " + e.getMessage());
        }

        return result;
    }
}
 