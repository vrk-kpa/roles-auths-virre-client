package fi.vm.kapa.rova.soap.virre.model;

public class ExtendedRoleInfo {
    private String roleType;
    private String bodyType;
    private String startDate;
    private String expirationDate;
    private String roleDescription;
    private String BodyDescription;
    
    public String getBodyDescription() {
        return BodyDescription;
    }
    public void setBodyDescription(String bodyDescription) {
        BodyDescription = bodyDescription;
    }
    public String getRoleDescription() {
        return roleDescription;
    }
    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
    public String getRoleType() {
        return roleType;
    }
    public void setRoleType(String roleName) {
        this.roleType = roleName;
    }
    
    public String getBodyType() {
        return bodyType;
    }
    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }
    
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    
}
