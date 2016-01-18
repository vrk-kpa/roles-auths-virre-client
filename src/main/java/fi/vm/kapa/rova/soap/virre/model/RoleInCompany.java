package fi.vm.kapa.rova.soap.virre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "roleInCompany")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleInCompany {
    @XmlElement(name = "companyName")
    private String companyName;

    @XmlElement(name = "registrationDate")
    private String registrationDate;

    @XmlElement(name = "businessId")
    private String businessId;

    @XmlElement(name = "state")
    private String state;

    @XmlElement(name = "roleBasicInfo")
    private RoleInfo roleInfo;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public RoleInfo getRoleInfo() {
        return roleInfo;
    }

    public void setRoleInfo(RoleInfo roleInfo) {
        this.roleInfo = roleInfo;
    }

    @Override
    public String toString() {
        return "RoleInCompany [companyName=" + companyName
                + ", registrationDate=" + registrationDate + ", businessId="
                + businessId + ", state=" + state + ", roleInfo=" + roleInfo
                + "]";
    }
    
}
