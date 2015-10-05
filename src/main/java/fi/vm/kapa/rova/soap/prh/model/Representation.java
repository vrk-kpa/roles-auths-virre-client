package fi.vm.kapa.rova.soap.prh.model;

import java.util.List;

public class Representation {
    private String rule;
    private String Description;
    private List<NaturalPerson> persons; 

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public List<NaturalPerson> getPersons() {
        return persons;
    }

    public void setPersons(List<NaturalPerson> persons) {
        this.persons = persons;
    }

}
