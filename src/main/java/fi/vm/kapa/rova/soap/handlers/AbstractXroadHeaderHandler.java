package fi.vm.kapa.rova.soap.handlers;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.rest.identification.RequestIdentificationFilter;
import fi.vrk.xml.rova.virre.ObjectFactory;
import fi.vrk.xml.rova.virre.XRoadClientIdentifierType;
import fi.vrk.xml.rova.virre.XRoadObjectType;
import fi.vrk.xml.rova.virre.XRoadServiceIdentifierType;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public abstract class AbstractXroadHeaderHandler implements SOAPHandler<SOAPMessageContext>,
        SpringPropertyNames {

    private ObjectFactory factory = new ObjectFactory();

    @Autowired
    private HttpServletRequest request;

    @Value(CLIENT_OBJECT_TYPE)
    private String clientObjectType;

    @Value(CLIENT_SDSB_INSTANCE)
    private String clientSdsbInstance;

    @Value(CLIENT_MEMBER_CLASS)
    private String clientMemberClass;

    @Value(CLIENT_MEMBER_CODE)
    private String clientMemberCode;

    @Value(CLIENT_SUBSYSTEM_CODE)
    private String clientSubsystemCode;

    @Value(SERVICE_OBJECT_TYPE)
    private String serviceObjectType;

    @Value(SERVICE_SDSB_INSTANCE)
    private String serviceSdsbInstance;

    @Value(SERVICE_MEMBER_CLASS)
    private String serviceMemberClass;

    @Value(SERVICE_MEMBER_CODE)
    private String serviceMemberCode;

    @Value(SERVICE_SUBSYSTEM_CODE)
    private String serviceSubsystemCode;

    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    @PostConstruct
    public void init() {

    }

    public boolean handleMessage(SOAPMessageContext messageContext) {
        Boolean outboundProperty = (Boolean) messageContext
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        SOAPMessage soapMsg = messageContext.getMessage();
        SOAPEnvelope soapEnv;

        if (outboundProperty.booleanValue()) {
            try {
                soapEnv = soapMsg.getSOAPPart().getEnvelope();
                SOAPHeader header = soapEnv.getHeader();
                if (header == null) {
                    header = soapEnv.addHeader();
                }

                String id = UUID.randomUUID().toString();

                JAXBElement<String> idElement = factory.createId(id);
                SOAPHeaderElement idHeaderElement = header
                        .addHeaderElement(idElement.getName());
                idHeaderElement.addTextNode(idElement.getValue());

                String origUserId = request.getHeader(RequestIdentificationFilter.XROAD_END_USER);
                if (origUserId == null) {
                    origUserId = "rova-end-user-unknown";
                }

                JAXBElement<String> userIdElement = factory.createUserId(origUserId);
                SOAPHeaderElement uidHeaderElement = header.addHeaderElement(userIdElement.getName());
                uidHeaderElement.addTextNode(userIdElement.getValue());

                JAXBElement<String> protocolversion = factory.createProtocolVersion("4.0");
                SOAPHeaderElement pvHeaderElement = header.addHeaderElement(protocolversion.getName());
                pvHeaderElement.addTextNode(protocolversion.getValue());

                String origRequestId = request.getHeader(RequestIdentificationFilter.XROAD_REQUEST_IDENTIFIER);
                if (origRequestId == null) {
                    origRequestId = "";
                }

                JAXBElement<String> issueElement = factory.createIssue(origRequestId);
                SOAPHeaderElement issueHeaderElement = header.addHeaderElement(issueElement.getName());
                issueHeaderElement.addTextNode(issueElement.getValue());

                XRoadClientIdentifierType clientHeaderType = factory.createXRoadClientIdentifierType();
                JAXBElement<XRoadClientIdentifierType> clientElement = factory.createClient(clientHeaderType);
                clientHeaderType.setObjectType(XRoadObjectType.SUBSYSTEM);
                clientHeaderType.setXRoadInstance(clientSdsbInstance);
                clientHeaderType.setMemberClass(clientMemberClass);
                clientHeaderType.setMemberCode(clientMemberCode);
                clientHeaderType.setSubsystemCode(clientSubsystemCode);

                Marshaller marshaller;
                marshaller = JAXBContext.newInstance(
                        XRoadClientIdentifierType.class).createMarshaller();
                marshaller.marshal(clientElement, header);

                XRoadServiceIdentifierType serviceHeaderType = factory
                        .createXRoadServiceIdentifierType();
                JAXBElement<XRoadServiceIdentifierType> serviceElement = factory
                        .createService(serviceHeaderType);
                serviceHeaderType.setObjectType(XRoadObjectType.SERVICE);
                serviceHeaderType.setXRoadInstance(serviceSdsbInstance);
                serviceHeaderType.setMemberClass(serviceMemberClass);
                serviceHeaderType.setMemberCode(serviceMemberCode);
                serviceHeaderType.setSubsystemCode(serviceSubsystemCode);
                serviceHeaderType.setServiceCode(getServiceCode());
                serviceHeaderType.setServiceVersion(getServiceVersion());

                marshaller = JAXBContext.newInstance(
                        XRoadServiceIdentifierType.class).createMarshaller();
                marshaller.marshal(serviceElement, header);

                soapMsg.saveChanges();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext messageContext) {
        return true;
    }

    public void close(MessageContext messageContext) {
    }

    abstract protected String getServiceCode();

    abstract protected String getServiceVersion();
}
