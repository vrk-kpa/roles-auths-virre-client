package fi.vm.kapa.rova.soap.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RepresentationsXroadHeaderHandler extends AbstractXroadHeaderHandler {

    @Value(SERVICE_REPRESENTATIONS_SERVICE_CODE)
    private String serviceServiceCode;

    @Value(SERVICE_REPRESENTATIONS_SERVICE_VERSION)
    private String serviceVersion;

    @Override
    protected String getServiceCode() {
        return serviceServiceCode;
    }

    @Override
    protected String getServiceVersion() {
        return serviceVersion;
    }
}
