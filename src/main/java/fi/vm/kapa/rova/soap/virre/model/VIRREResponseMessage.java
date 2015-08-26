package fi.vm.kapa.rova.soap.virre.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnore
public class VIRREResponseMessage {

    private String socialSec;
    private List<Role> roles;
    
    public String getSocialSec() {
        return socialSec;
    }
    public void setSocialSec(String socialSec) {
        this.socialSec = socialSec;
    }
    public List<Role> getRoles() {
        return roles;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
  
}
