package fi.vm.kapa.rova.soap.virre.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response", namespace = "http://xroad.virre.prh.fi/producer") // XRoadPersonActiveRoleInfoResponse.
@XmlAccessorType(XmlAccessType.FIELD)
public class VirreResponseMessage {
    @XmlElement(name = "firstname")
    private String firstName;

    @XmlElement(name = "surname")
    private String lastName;

    @XmlElement(name = "socialSecurityNumber")
    private String socialSecurityNumber;

    @XmlElement(name = "status")
    private String status;

    @XmlElementWrapper(name="roleInCompany")
    @XmlElement(name = "role")
    private List<RoleInCompany> roles;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RoleInCompany> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInCompany> roles) {
        this.roles = roles;
    }

}
