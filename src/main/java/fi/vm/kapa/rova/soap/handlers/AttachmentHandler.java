package fi.vm.kapa.rova.soap.handlers;

import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;

@Component
public class AttachmentHandler implements SOAPHandler<SOAPMessageContext>, SpringPropertyNames {

        private static Logger LOG = Logger.getLogger(XroadHeaderHandler.class, Logger.VIRRE_CLIENT);

        public static final String ATTACHMENT_ATTRIBUTE = "virre-attachment-attribute";
        
        @Autowired
        private HttpServletRequest request;

        @Override
        public boolean handleMessage(SOAPMessageContext context) {
            Iterator<AttachmentPart> it =  context.getMessage().getAttachments();
            while (it.hasNext()) {
                AttachmentPart ap = it.next();
                try {
                    request.setAttribute(ATTACHMENT_ATTRIBUTE, (String)ap.getContent());
                    System.out.println((String)ap.getContent());
                } catch (SOAPException e) {
                    LOG.error("Virre attachment handling error: " +  e);
                }
            }
        return false;
        }

        @Override
        public boolean handleFault(SOAPMessageContext context) {
            return true;
        }

        @Override
        public void close(MessageContext context) {
            
        }

        @Override
        public Set<QName> getHeaders() {
            return null;
        }

}
