package fi.vm.kapa.rova.soap.prh;

import eu.x_road.xsd.identifiers.ObjectFactory;
import eu.x_road.xsd.identifiers.XRoadClientIdentifierType;
import eu.x_road.xsd.identifiers.XRoadServiceIdentifierType;
import fi.prh.virre.xroad.producer.righttorepresent.XRoadRightToRepresent;
import fi.prh.virre.xroad.producer.righttorepresent.XRoadRightToRepresentPortType;
import fi.prh.virre.xroad.producer.righttorepresent.XRoadRightToRepresentResponse;
import fi.vm.kapa.rova.logging.Logger;
import https.ws_prh_fi.novus.ids.services._2008._08._22.RightToRepresentParametersType;
import https.ws_prh_fi.novus.ids.services._2008._08._22.RightToRepresentResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.ws.Holder;

@Component
public class RightReprClient extends AbstractPrhClient {
    private static Logger LOG = Logger.getLogger(RightReprClient.class);

    @Value(SERVICE_RIGHTS_SERVICE_CODE)
    private String serviceCode;

    @Autowired
    private XRoadRightToRepresentPortType rightToRepresentClient;

    ObjectFactory identifierFactory = new ObjectFactory();
    fi.prh.virre.xroad.producer.righttorepresent.ObjectFactory producerFactory = new fi.prh.virre.xroad.producer.righttorepresent.ObjectFactory();
    https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory novusFactory = new https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory();

    public Holder<XRoadClientIdentifierType> getClientHeader() {
        Holder<XRoadClientIdentifierType> result = new Holder<>();
        result.value = identifierFactory.createXRoadClientIdentifierType();
        result.value.setObjectType(clientObjectType);
        result.value.setXRoadInstance(clientSdsbInstance);
        result.value.setMemberClass(clientMemberClass);
        result.value.setMemberCode(clientMemberCode);
        result.value.setSubsystemCode(clientSubsystemCode);
        return result;
    }

    public Holder<XRoadServiceIdentifierType> getServiceHeader() {
        Holder<XRoadServiceIdentifierType> result = new Holder<>();
        result.value = identifierFactory.createXRoadServiceIdentifierType();
        result.value.setObjectType(serviceObjectType);
        result.value.setXRoadInstance(serviceSdsbInstance);
        result.value.setMemberClass(serviceMemberClass);
        result.value.setMemberCode(serviceMemberCode);
        result.value.setSubsystemCode(serviceSubsystemCode);
        result.value.setServiceCode(serviceCode);
        return result;
    }

    public RightToRepresentResponseType getRights(String socialSec, String businessId, String rightLevel) {
        XRoadRightToRepresent request = producerFactory.createXRoadRightToRepresent();
        RightToRepresentParametersType parametersType = novusFactory.createRightToRepresentParametersType();
        parametersType.setBusinessId(businessId);
        parametersType.setPersonId(socialSec);
        parametersType.setLevel(rightLevel);
        request.setRequest(parametersType);

        XRoadRightToRepresentResponse response = rightToRepresentClient.xRoadRightToRepresent(request, getClientHeader(),
                getServiceHeader(), getUserIdHeader(), getIdHeader(), getIssueHeader(), getProtocolVersionHeader());

        LOG.debug("soap for right to represent succeeded");
        return response.getResponse();
    }
}