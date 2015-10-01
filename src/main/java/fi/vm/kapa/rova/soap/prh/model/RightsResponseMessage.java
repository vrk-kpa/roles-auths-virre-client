
package fi.vm.kapa.rova.soap.prh.model;

/**
 *
 * @author mtom
 */
public class RightsResponseMessage {

    private String code;
    private String description;
    private String timestamp;

    public RightsResponseMessage() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
