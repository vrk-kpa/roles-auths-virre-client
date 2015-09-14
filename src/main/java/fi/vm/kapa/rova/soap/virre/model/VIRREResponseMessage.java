package fi.vm.kapa.rova.soap.virre.model;

import java.util.List;

public class VIRREResponseMessage {

    private String socialSec;
    private List<Role> roles;
    private String message;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
