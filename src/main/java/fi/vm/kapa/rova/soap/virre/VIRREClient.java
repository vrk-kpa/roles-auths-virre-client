package fi.vm.kapa.rova.soap.virre;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.kapa.rova.config.SpringPropertyNames;
import fi.vm.kapa.rova.engine.model.BodyType;
import fi.vm.kapa.rova.engine.model.Organization;
import fi.vm.kapa.rova.engine.model.OrganizationalRole;
import fi.vm.kapa.rova.engine.model.RoleNameType;
import fi.vm.kapa.rova.engine.model.RoleType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.virre.model.VIRREResponseMessage;

@Component
public class VIRREClient implements SpringPropertyNames {

    @Value(VIRRE_USERNAME)
    private String vtjUsername;
    @Value(VIRRE_PASSWORD)
    private String vtjPassword;
    @Value(XROAD_ENDPOINT)
    private String xrdEndPoint;

    private static Logger LOG = Logger.getLogger(VIRREClient.class, Logger.VIRRE_CLIENT);
    
    
    public VIRREResponseMessage getResponse(String personId) {
        
        ObjectMapper mapper = new ObjectMapper();
        VIRREResponseMessage responseMessage=null;
        try {
            responseMessage = mapper.readValue(new File("virre_response.json"), VIRREResponseMessage.class);
            System.out.println(responseMessage);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return responseMessage;
          
    }
 
}
