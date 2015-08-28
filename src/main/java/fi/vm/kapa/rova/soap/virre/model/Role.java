package fi.vm.kapa.rova.soap.virre.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Role {
    private String businessId;
    private String companyName;
    private String exceptionStatus;
    private String companyFormCode;
    private List<ExtendedRoleInfo> extendedRoleInfos;
    
    @JsonIgnore
    private List<Representation> representations;
    private List<LegalRepresentation> legalRepresentations;
    private List<Body> bodies;
    
    public String getBusinessId() {
        return businessId;
    }
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getExceptionStatus() {
        return exceptionStatus;
    }
    public void setExceptionStatus(String exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }
    
    public List<ExtendedRoleInfo> getExtendedRoleInfos() {
        return extendedRoleInfos;
    }
    public void setExtendedRoleInfos(List<ExtendedRoleInfo> extendedRoleInfos) {
        this.extendedRoleInfos = extendedRoleInfos;
    }
    
    public List<Representation> getRepresentations() {
        return representations;
    }
    public void setRepresentations(List<Representation> representations) {
        this.representations = representations;
    }
    
    public List<LegalRepresentation> getLegalRepresentations() {
        return legalRepresentations;
    }
    public void setLegalRepresentations(
            List<LegalRepresentation> legalRepresentations) {
        this.legalRepresentations = legalRepresentations;
    }
    
    public List<Body> getBodies() {
        return bodies;
    }
    public void setBodies(List<Body> bodies) {
        this.bodies = bodies;
    }
    public String getCompanyFormCode() {
        return companyFormCode;
    }
    public void setCompanyFormCode(String companyFormCode) {
        this.companyFormCode = companyFormCode;
    }   
}
