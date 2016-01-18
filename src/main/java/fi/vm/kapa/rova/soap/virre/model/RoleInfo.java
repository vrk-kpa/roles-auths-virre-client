package fi.vm.kapa.rova.soap.virre.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "roleBasicInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleInfo {
    @XmlElement(name = "bodyType")
    private String bodyType;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "startDate")
    private String startDate;

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "RoleInfo [bodyType=" + bodyType + ", name=" + name
                + ", startDate=" + startDate + "]";
    }
}
