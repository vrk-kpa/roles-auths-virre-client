
package fi.vm.kapa.rova.soap.prh.model;

import java.util.List;

/**
 *
 * @author mtom
 */
public class CompaniesResponseMessage {

    public CompaniesResponseMessage() {
    }

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
