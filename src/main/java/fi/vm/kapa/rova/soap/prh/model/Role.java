package fi.vm.kapa.rova.soap.prh.model;

import java.util.List;

public class Role {
    private String businessId;
    private String companyName;
    private List<ExtendedRoleInfo> extendedRoleInfos;
    
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
    
    public List<ExtendedRoleInfo> getExtendedRoleInfos() {
        return extendedRoleInfos;
    }
    public void setExtendedRoleInfos(List<ExtendedRoleInfo> extendedRoleInfos) {
        this.extendedRoleInfos = extendedRoleInfos;
    }

}
