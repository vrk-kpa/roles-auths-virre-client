package fi.vm.kapa.rova.soap.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import net.logstash.logback.encoder.org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.logging.Logger;

@Component
public class AttachmentHandler implements SOAPHandler<SOAPMessageContext>, SpringPropertyNames {

        private static Logger LOG = Logger.getLogger(AttachmentHandler.class);

        public static final String ATTACHMENT_ATTRIBUTE = "virre-attachment-attribute";
        
        @Autowired
        private HttpServletRequest request;

        @Override
        public boolean handleMessage(SOAPMessageContext context) {
            Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (!outboundProperty) {
                Iterator<AttachmentPart> it =  context.getMessage().getAttachments();
                while (it.hasNext()) {
                    AttachmentPart ap = it.next();
                    try {
                        InputStream inputStream = (InputStream)ap.getContent();
                        
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(inputStream, writer, "UTF-8");
                        String json = writer.toString();

                        request.setAttribute(ATTACHMENT_ATTRIBUTE, json);
                    } catch (SOAPException e) {
                        LOG.error("Virre attachment handling error: " +  e);
                    } catch (IOException e) {
                        LOG.error("Virre attachment handling error: " +  e);
                    }
                }
            }
        return true;
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
