package fi.vm.kapa.rova.soap.prh;

import eu.x_road.xsd.identifiers.ObjectFactory;
import eu.x_road.xsd.identifiers.XRoadClientIdentifierType;
import eu.x_road.xsd.identifiers.XRoadServiceIdentifierType;
import fi.prh.virre.xroad.producer.activeroleinfo.XRoadPersonActiveRoleInfo;
import fi.prh.virre.xroad.producer.activeroleinfo.XRoadPersonActiveRoleInfoPortType;
import fi.prh.virre.xroad.producer.activeroleinfo.XRoadPersonActiveRoleInfoResponse;
import fi.vm.kapa.rova.logging.Logger;
import https.ws_prh_fi.novus.ids.services._2008._08._22.PersonActiveRoleInfoResponseType;
import https.ws_prh_fi.novus.ids.services._2008._08._22.PersonActiveRoleInfoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.ws.Holder;

@Component
public class ActiveRolesClient extends AbstractPrhClient {
    private static final Logger LOG = Logger.getLogger(ActiveRolesClient.class);

    @Value(SERVICE_COMPANIES_SERVICE_CODE)
    private String serviceCode;

    @Autowired
    private XRoadPersonActiveRoleInfoPortType personActiveRolesClient;

    ObjectFactory identifierFactory = new ObjectFactory();
    fi.prh.virre.xroad.producer.activeroleinfo.ObjectFactory producerFactory
            = new fi.prh.virre.xroad.producer.activeroleinfo.ObjectFactory();
    https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory novusFactory
            = new https.ws_prh_fi.novus.ids.services._2008._08._22.ObjectFactory();

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

    public PersonActiveRoleInfoResponseType getResponse(String personId) {
        XRoadPersonActiveRoleInfo request = producerFactory.createXRoadPersonActiveRoleInfo();
        PersonActiveRoleInfoType value = novusFactory.createPersonActiveRoleInfoType();
        value.setSocialSecurityNumber(personId);
        request.setRequest(value);

        XRoadPersonActiveRoleInfoResponse response = personActiveRolesClient.xRoadPersonActiveRoleInfo(request,
                getClientHeader(identifierFactory), getServiceHeader(identifierFactory),
                getUserIdHeader(), getIdHeader(), getIssueHeader(), getProtocolVersionHeader());
        LOG.debug("soap for active role info succeeded");
        
        return response.getResponse();
   }
}
 