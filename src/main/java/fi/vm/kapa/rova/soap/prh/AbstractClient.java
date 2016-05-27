package fi.vm.kapa.rova.soap.prh;

import fi.vm.kapa.rova.rest.identification.RequestIdentificationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Juha Korkalainen on 27.5.2016.
 */
abstract class AbstractClient {

    @Autowired
    HttpServletRequest request;

    public String getEnduser() {
        String origUserId = request.getHeader(RequestIdentificationFilter.ORIG_END_USER);
        if (origUserId == null || origUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("User header missing");
        }
        return origUserId;
    }
}
