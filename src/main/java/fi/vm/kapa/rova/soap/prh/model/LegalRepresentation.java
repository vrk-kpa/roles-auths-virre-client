package fi.vm.kapa.rova.soap.prh.model;

public class LegalRepresentation {
    private String header;
    private String text;
    private String signingcode;
    
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    
    public String getSigningcode() {
        return signingcode;
    }
    public void setSigningcode(String signingcode) {
        this.signingcode = signingcode;
    }
    
}
