package fi.vm.kapa.rova.virre.model;

import java.util.List;

public class OrganizationalRole {
    private String organizationId;
    private String organizationName;
    private List<OrganizationalRoleInfo> roleInfos;
    private List<OrganizationalRepresentation> representations;
    private String exceptionStatus;
    private String companyFormCode;
    
    public OrganizationalRole() {}
    
    public String getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
        
    public String getOrganizationName() {
        return organizationName;
    }
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<OrganizationalRoleInfo> getRoleInfos() {
        return roleInfos;
    }
    public void setRoleInfos(List<OrganizationalRoleInfo> roleInfo) {
        this.roleInfos = roleInfo;
    }

    public List<OrganizationalRepresentation> getRepresentations() {
        return representations;
    }
    public void setRepresentations(List<OrganizationalRepresentation> representations) {
        this.representations = representations;
    }

    public String getExceptionStatus() {
        return exceptionStatus;
    }

    public void setExceptionStatus(String exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public String getCompanyFormCode() {
        return companyFormCode;
    }

    public void setCompanyFormCode(String companyFormCode) {
        this.companyFormCode = companyFormCode;
    }

}
