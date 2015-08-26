package fi.vm.kapa.rova.virreclient.service;

public class VIRREServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public VIRREServiceException(String msg, Throwable t) {
        super(msg, t);
    }
}
