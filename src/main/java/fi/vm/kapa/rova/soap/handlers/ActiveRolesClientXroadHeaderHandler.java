package fi.vm.kapa.rova.soap.handlers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Juha Korkalainen on 15.1.2016.
 */
@Component
public class ActiveRolesClientXroadHeaderHandler extends AbstractXroadHeaderHandler {

    //@Value(SERVICE_COMPANIES_SERVICE_CODE)
    private String serviceServiceCode = "XRoadPersonActiveRoleInfo";

    //@Value(SERVICE_COMPANIES_SERVICE_VERSION)
    private String serviceVersion = null;

    @Override
    protected String getServiceCode() {
        return serviceServiceCode;
    }

    @Override
    protected String getServiceVersion() {
        return serviceVersion;
    }
}
