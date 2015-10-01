
package fi.vm.kapa.rova.virreclient.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.kapa.rova.logging.Logger;
import java.io.IOException;

/**
 *
 * @author mtom
 */
public abstract class MessageParser {

    private static final Logger log = Logger.getLogger(MessageParser.class);

    @JsonIgnore
    private String sourceString = null;

    public static <T extends Object> T parseResponseMessage(String response, Class<T> targetClass) {
        ObjectMapper mapper = new ObjectMapper();
        T responseMessage = null;
        try {
            responseMessage = mapper.readValue(response, targetClass); 
            log.debug("Response message: " + responseMessage);
        } catch (JsonGenerationException | JsonMappingException e) {
            log.warning(e.getMessage());
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        return responseMessage;
    }  

    protected MessageParser() {
    }

}
