package fi.vm.kapa.rova.virre.model;

import java.time.LocalDateTime;

public class OrganizationalRoleInfo {
    private String roleName;
    private String bodyType;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
        
    public String getRoleName() {
        return roleName;
    }
    public String getBodyType() {
        return bodyType;
    }
    
    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

}
