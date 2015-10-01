package fi.vm.kapa.rova.soap.prh.model;

public class NaturalPerson {

    private String socialSec;
    private String firstName;
    private String lastName;
    private String status;
    private String role;
    private String body;

    public String getSocialSec() {
        return socialSec;
    }
    public void setSocialSec(String socialSec) {
        this.socialSec = socialSec;
    }
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
     
}