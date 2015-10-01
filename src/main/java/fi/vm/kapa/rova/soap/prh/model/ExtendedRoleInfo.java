package fi.vm.kapa.rova.soap.prh.model;

public class ExtendedRoleInfo {
    private String roleType;
    private String bodyType;
    private String roleDescription;
    private String BodyDescription;
    private String startDate;
    
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
    
}
