package fi.vm.kapa.rova.virreclient.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.kapa.rova.engine.model.BodyType;
import fi.vm.kapa.rova.engine.model.Organization;
import fi.vm.kapa.rova.engine.model.OrganizationType;
import fi.vm.kapa.rova.engine.model.RoleNameType;
import fi.vm.kapa.rova.engine.model.RoleType;
import fi.vm.kapa.rova.engine.model.SigningCodeType;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.virre.VIRREClient;
import fi.vm.kapa.rova.soap.virre.model.ExtendedRoleInfo;
import fi.vm.kapa.rova.soap.virre.model.LegalRepresentation;
import fi.vm.kapa.rova.soap.virre.model.Role;
import fi.vm.kapa.rova.soap.virre.model.VIRREResponseMessage;

@Service
public class VIRREService {

    private static Logger LOG = Logger.getLogger(VIRREService.class);
    
    private static final int MIN_DATE_LENGTH = 10;
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");


    @Autowired
    private VIRREClient client;

    public List<fi.vm.kapa.rova.engine.model.OrganizationalRole> getOrganizationalRoles(String hetu) throws VIRREServiceException {
        List<fi.vm.kapa.rova.engine.model.OrganizationalRole> orgRoles = new ArrayList<fi.vm.kapa.rova.engine.model.OrganizationalRole>();

        try {
            long startTime = System.currentTimeMillis();

            VIRREResponseMessage response = getResponse(client.getResponse(hetu)); 
            LOG.info("Soap request duration=" + (System.currentTimeMillis() - startTime));

            if (response.getMessage() == null) {
                List<Role> roles = response.getRoles();         
                for (Role role : roles) {
                    Set<SigningCodeType> codes = new HashSet<SigningCodeType>();
                    for (LegalRepresentation legalRepr : role.getLegalRepresentations()) {
                            String codeString = legalRepr.getSigningcode();
                            try  {
                                SigningCodeType code = SigningCodeType.valueOf(codeString);
                                codes.add(code);
                            } catch (IllegalArgumentException | NullPointerException e) {
                                LOG.warning("Unable to create SigningCodeType: " + codeString);
                            }
                    }
                    
                    for (SigningCodeType code : codes) {
                        try {
                            fi.vm.kapa.rova.engine.model.OrganizationalRole newRole = new fi.vm.kapa.rova.engine.model.OrganizationalRole();
                            newRole.setPersonIdentifier(hetu);
                            newRole.setOrganization(createOrganization(role, code));
                            newRole.setRoles(new LinkedList<>(createRoleTypes(role)));
                            orgRoles.add(newRole);
                            LOG.debug("to roles list was added: " + newRole);
                        } catch (IllegalArgumentException | NullPointerException e) {
                            LOG.warning("Unable to create OrganizationalRole: " + e.getMessage());
                        }
                    }
                    
                }
            } else {
                LOG.warning("Got error message from service: " + response.getMessage());
            }
            
        } catch (Throwable e) {
            LOG.error("Person parsing failed reason:" + e);
            e.printStackTrace();
            throw new VIRREServiceException("Person parsing failed", e);
        }
        
        return orgRoles;
    }

    
    private VIRREResponseMessage getResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        VIRREResponseMessage responseMessage=null;
        try {
            responseMessage = mapper.readValue(response, VIRREResponseMessage.class);
            LOG.debug("VIRREResponseMessage: "+responseMessage);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseMessage;
    }

    private Organization createOrganization(Role role, SigningCodeType code) {
        Organization org = new Organization();
        org.setName(role.getCompanyName());
        org.setIdentifier(role.getBusinessId());
        org.setExceptionStatus(role.getExceptionStatus());
        org.setType(OrganizationType.find(role.getCompanyFormCode()));
        org.setSigningCode(code);
        LOG.debug("created organization: "+ org);
        return org;
    }

    private Set<RoleType> createRoleTypes(Role role) {
        Set<RoleType> roleTypes = new HashSet<>();
        for (ExtendedRoleInfo info : role.getExtendedRoleInfos()) {
            try {
                RoleType roleType = new RoleType();
                roleType.setRoleName(RoleNameType.valueOf(info.getRoleType()));
                roleType.setBodyType(BodyType.valueOf(info.getBodyType()));
                LocalDateTime startDate = LocalDateTime.parse(info.getStartDate(), formatter);
                roleType.setStartDate(startDate);
                if (info.getExpirationDate() == null || info.getExpirationDate().length() < MIN_DATE_LENGTH) {
                    roleType.setExpirationDate(null);
                } else {    
                    LocalDateTime expDate = LocalDateTime.parse(info.getExpirationDate(), formatter);
                    roleType.setExpirationDate(expDate);
                }
                roleTypes.add(roleType);
                LOG.debug("created roletype: "+ roleType);
            } catch (IllegalArgumentException | NullPointerException e) {
                LOG.warning("Unable to create RoleType: " + e.getMessage());
            }
        }
        return roleTypes;
    }
}    
