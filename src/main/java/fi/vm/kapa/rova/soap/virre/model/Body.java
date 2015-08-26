package fi.vm.kapa.rova.soap.virre.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Body {
    private String bodyType;
    private String bodyDescription;
    private List<NaturalPerson> naturalPersons;
    
    @JsonIgnore
    private List<JuristicPerson> juristicPersons;
    
    public String getBodyType() {
        return bodyType;
    }
    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }
    public String getBodyDescription() {
        return bodyDescription;
    }
    public void setBodyDescription(String bodyDescription) {
        this.bodyDescription = bodyDescription;
    }
    public List<NaturalPerson> getNaturalPersons() {
        return naturalPersons;
    }
    public void setNaturalPersons(List<NaturalPerson> naturalPersons) {
        this.naturalPersons = naturalPersons;
    }
    public List<JuristicPerson> getJuristicPersons() {
        return juristicPersons;
    }
    public void setJuristicPersons(List<JuristicPerson> juristicPersons) {
        this.juristicPersons = juristicPersons;
    }
    
    
}
