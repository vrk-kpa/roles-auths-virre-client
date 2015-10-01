package fi.vm.kapa.rova.soap.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CompaniesXroadHeaderHandler extends AbstractXroadHeaderHandler {

    @Value(SERVICE_COMPANIES_SERVICE_CODE)
    private String serviceServiceCode;

    @Value(SERVICE_COMPANIES_SERVICE_VERSION)
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
