package fi.vm.kapa.rova.virreclient.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        
        OrganizationalPerson person = new OrganizationalPerson();
        
        try {
            long startTime = System.currentTimeMillis();
            VIRREResponseMessage response=client.getResponse(hetu);
            LOG.info("duration=" + (System.currentTimeMillis() - startTime));
           
            
            person.setPersonId(response.getSocialSec());
            
            List<OrganizationalRole> orgRoles=new ArrayList<OrganizationalRole>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            List<Role> roles= response.getRoles();         
            for (Role role : roles) {
                OrganizationalRole orgRole=new OrganizationalRole();
                orgRole.setOrganizationId(role.getBusinessId());
                orgRole.setOrganizationName(role.getCompanyName());
                
                for (ExtendedRoleInfo e : role.getExtendedRoleInfos()) {
                    OrganizationalRoleInfo roleInfo=new OrganizationalRoleInfo();
                    roleInfo.setBodyType(e.getBodyType());
                    LocalDateTime startDate = LocalDateTime.parse(e.getStartDate(), formatter);
                    roleInfo.setStartDate(startDate);
                    LocalDateTime expDate = LocalDateTime.parse(e.getExpirationDate(), formatter);
                    roleInfo.setExpirationDate(expDate);
                    roleInfo.setRoleName(e.getRoleName());
                    orgRole.getRoleInfos().add(roleInfo);
                }
                
                for (LegalRepresentation l : role.getLegalRepresentations()) {
                    OrganizationalRepresentation lRepr=new OrganizationalRepresentation();
                    lRepr.setSigningcode(l.getSigningcode());
                    orgRole.getRepresentations().add(lRepr);
                } 
                orgRoles.add(orgRole);
            }
           person.setOrgRoles(orgRoles);
      
        } catch (Throwable e) {
            LOG.error("Person parsing failed reason:" + e);
            throw new VIRREServiceException("Person parsing failed", e);
        }
        return person;
    }
}
