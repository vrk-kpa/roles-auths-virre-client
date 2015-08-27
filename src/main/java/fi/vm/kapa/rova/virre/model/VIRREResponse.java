package fi.vm.kapa.rova.virre.model;

public class VIRREResponse {
    private OrganizationalPerson organizationalPerson;
    private boolean success;
    private String error;
   
    public OrganizationalPerson getOrganizationalPerson() {
        return organizationalPerson;
    }
    public void setOrganizationalPerson(OrganizationalPerson organizationalPerson) {
        this.organizationalPerson = organizationalPerson;
    }
        
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    
}
