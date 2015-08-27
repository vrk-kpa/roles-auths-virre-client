package fi.vm.kapa.rova.soap.handlers;

import java.util .Collections;
import java.util.Set;
import java.util.UUID;

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
import fi.vm.kapa.rova.logging.Logger;
import fi.vrk.xml.rova.virre.ObjectFactory;
import fi.vrk.xml.rova.virre.XRoadClientIdentifierType;
import fi.vrk.xml.rova.virre.XRoadObjectType;
import fi.vrk.xml.rova.virre.XRoadServiceIdentifierType;


@Component
public class XroadHeaderHandler implements SOAPHandler<SOAPMessageContext>, SpringPropertyNames {

    private static Logger LOG = Logger.getLogger(XroadHeaderHandler.class, Logger.VTJ_CLIENT);

    private ObjectFactory factory = new ObjectFactory();

    public static final String ORIG_USERID_HEADER = "origUserId";
    public static final String ORIG_REQUEST_ID_HEADER = "origRequestId";

    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

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

    @Value(SERVICE_SERVICE_CODE)
    private String serviceServiceCode;
    
    

    public boolean handleMessage(SOAPMessageContext messageContext) {
        Boolean outboundProperty = (Boolean) messageContext
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outboundProperty.booleanValue()) {
            SOAPMessage soapMsg = messageContext.getMessage();
            SOAPEnvelope soapEnv;
            try {
                soapEnv = soapMsg.getSOAPPart().getEnvelope();
                SOAPHeader header = soapEnv.getHeader();
                if (header == null) {
                    header = soapEnv.addHeader();
                }

                JAXBElement<String> idElement = factory.createId(UUID.randomUUID().toString());
                SOAPHeaderElement idHeaderElement = header.addHeaderElement(idElement.getName());
                idHeaderElement.addTextNode((String) idElement.getValue());

                String origUserId = (String) messageContext.get(ORIG_USERID_HEADER);
                if (origUserId == null) {
                    origUserId = "rova-end-user-unknown";
                }
                JAXBElement<String> userIdElement = factory.createUserId(origUserId);
                SOAPHeaderElement uidHeaderElement = header.addHeaderElement(userIdElement.getName());
                uidHeaderElement.addTextNode((String) userIdElement.getValue());

                String origRequestId = (String) messageContext.get(ORIG_REQUEST_ID_HEADER);
                if (origRequestId == null) {
                    origRequestId = "";
                }

                JAXBElement<String> protocolversion = factory.createProtocolVersion("4.0");
                SOAPHeaderElement pvHeaderElement = header.addHeaderElement(protocolversion.getName());
                pvHeaderElement.addTextNode((String) protocolversion.getValue());

                
                JAXBElement<String> issueElement = factory.createIssue(origRequestId);
                SOAPHeaderElement issueHeaderElement = header.addHeaderElement(issueElement.getName());
                issueHeaderElement.addTextNode((String) issueElement.getValue());

                
                
                XRoadClientIdentifierType client = factory.createXRoadClientIdentifierType();
                JAXBElement<XRoadClientIdentifierType> clientElement = factory.createClient(client);
                client.setObjectType(XRoadObjectType.fromValue(this.clientObjectType));
                client.setXRoadInstance(this.clientSdsbInstance);
                client.setMemberClass(this.clientMemberClass);
                client.setMemberCode(this.clientMemberCode);
                client.setSubsystemCode(this.clientSubsystemCode);

                Marshaller marshaller;
                marshaller = JAXBContext.newInstance(XRoadClientIdentifierType.class).createMarshaller();
                marshaller.marshal(clientElement, header);

                XRoadServiceIdentifierType service = factory.createXRoadServiceIdentifierType();
                JAXBElement<XRoadServiceIdentifierType> serviceElement = factory.createService(service);
                service.setObjectType(XRoadObjectType.fromValue(this.serviceObjectType));
                service.setXRoadInstance(this.serviceSdsbInstance);
                service.setMemberClass(this.serviceMemberClass);
                service.setMemberCode(this.serviceMemberCode);
                service.setSubsystemCode(this.serviceSubsystemCode);
                service.setServiceCode(this.serviceServiceCode);
                service.setServiceVersion("v1");
                

                marshaller = JAXBContext.newInstance(XRoadServiceIdentifierType.class).createMarshaller();
                marshaller.marshal(serviceElement, header);

                soapMsg.saveChanges();

            } catch (Exception e) {
                LOG.error("Xroad header handler exception occured " + e);
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
}
