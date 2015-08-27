package fi.vm.kapa.rova.virre.model;

import java.util.List;

import fi.vm.kapa.rova.virre.model.OrganizationalRole;

public class OrganizationalPerson {
    private String personId;
    private List<OrganizationalRole> orgRoles;
    
    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }
    
    public List<OrganizationalRole> getOrgRoles() {
        return orgRoles;
    }
    public void setOrgRoles(List<OrganizationalRole> orgRoles) {
        this.orgRoles = orgRoles;
    }
}
