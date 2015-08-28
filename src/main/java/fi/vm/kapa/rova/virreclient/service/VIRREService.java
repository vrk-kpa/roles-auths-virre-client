package fi.vm.kapa.rova.virreclient.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.virre.VIRREClient;
import fi.vm.kapa.rova.soap.virre.model.ExtendedRoleInfo;
import fi.vm.kapa.rova.soap.virre.model.LegalRepresentation;
import fi.vm.kapa.rova.soap.virre.model.Role;
import fi.vm.kapa.rova.soap.virre.model.VIRREResponseMessage;
import fi.vm.kapa.rova.virre.model.OrganizationalRepresentation;
import fi.vm.kapa.rova.virre.model.OrganizationalRoleInfo;
import fi.vm.kapa.rova.virre.model.OrganizationalPerson;
import fi.vm.kapa.rova.virre.model.OrganizationalRole;

@Service
public class VIRREService {

    private static Logger LOG = Logger.getLogger(VIRREService.class, Logger.VIRRE_CLIENT);

    @Autowired
    private VIRREClient client;

    public OrganizationalPerson getOrganizationalPerson(String hetu) throws VIRREServiceException {
        final int MIN_DATE_LENGTH=10;
        OrganizationalPerson person = new OrganizationalPerson();
        
        try {
            long startTime = System.currentTimeMillis();

            //TODO: must use correct VirreClient
            //VIRREResponseMessage response=client.getResponse(hetu); 
            VIRREResponseMessage response=getResponse(); 
            LOG.info("duration=" + (System.currentTimeMillis() - startTime));
                      
            person.setPersonId(response.getSocialSec());
          
            List<OrganizationalRole> orgRoles=new ArrayList<OrganizationalRole>();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy‐MM‐dd'T'HH:mm:ss.SSSZZZZZ");
            List<Role> roles= response.getRoles();         
            for (Role role : roles) {
                OrganizationalRole orgRole=new OrganizationalRole();
                orgRole.setOrganizationId(role.getBusinessId());
                orgRole.setOrganizationName(role.getCompanyName());
                List<OrganizationalRoleInfo> oRoleInfo=new ArrayList<OrganizationalRoleInfo>();
                for (ExtendedRoleInfo e : role.getExtendedRoleInfos()) {
                    OrganizationalRoleInfo roleInfo=new OrganizationalRoleInfo();
                    roleInfo.setBodyType(e.getBodyType());
                    LocalDateTime startDate = LocalDateTime.parse(e.getStartDate(), formatter);
                    roleInfo.setStartDate(startDate);
                                   
                    if ( e.getExpirationDate().length() < MIN_DATE_LENGTH ) {
                        e.setExpirationDate(null);
                    }
                    else {    
                        LocalDateTime expDate = LocalDateTime.parse(e.getExpirationDate(), formatter);
                        roleInfo.setExpirationDate(expDate);
                    }
                    roleInfo.setRoleName(e.getRoleName());
                    oRoleInfo.add(roleInfo);
                 }
                orgRole.setRoleInfos(oRoleInfo);
                List<OrganizationalRepresentation> oRepr=new ArrayList<OrganizationalRepresentation>();
                for (LegalRepresentation l : role.getLegalRepresentations()) {
                    OrganizationalRepresentation lRepr=new OrganizationalRepresentation();
                    lRepr.setSigningcode(l.getSigningcode());
                    oRepr.add(lRepr);
                } 
                orgRole.setRepresentations(oRepr);
                
                orgRoles.add(orgRole);
            }
           person.setOrgRoles(orgRoles);
      
        } catch (Throwable e) {
           LOG.error("Person parsing failed reason:" + e);
           throw new VIRREServiceException("Person parsing failed", e);
        }
        return person;
    }
    
    private VIRREResponseMessage getResponse() {
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
