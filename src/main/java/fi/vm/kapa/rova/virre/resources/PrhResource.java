/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.virre.resources;

import fi.vm.kapa.rova.external.model.virre.CompanyPerson;
import fi.vm.kapa.rova.external.model.virre.CompanyRepresentations;
import fi.vm.kapa.rova.external.model.virre.RepresentationRight;
import fi.vm.kapa.rova.logging.Logger;
import fi.vm.kapa.rova.soap.prh.VirreException;
import fi.vm.kapa.rova.virreclient.service.ActiveRolesService;
import fi.vm.kapa.rova.virreclient.service.CompanyReprService;
import fi.vm.kapa.rova.virreclient.service.RightReprService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

@Service
@Path("/")
public class PrhResource {

    private static final Logger log = Logger.getLogger(PrhResource.class);

    @Inject
    private ActiveRolesService arc;

    @Inject
    private CompanyReprService crs;

    @Inject
    private RightReprService rrs;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prh/companies/{socialsec}")
    public CompanyPerson getCompanyPerson(@PathParam("socialsec") String socialsec) throws VirreException {
        log.debug("CompanyPerson request received.");
        return arc.getCompanyPerson(socialsec)
                .orElseThrow(() -> new WebApplicationException(Status.NOT_FOUND));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prh/representations/{businessid}")
    public CompanyRepresentations getRepresentations(@PathParam("businessid") String businessid) throws VirreException {
        log.debug("Representations request received.");
        return crs.getRepresentations(businessid);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prh/rights/{rightlevel}/{socialsec}/{businessid}")
    public RepresentationRight getRights(@PathParam("socialsec") String socialSec,
                                         @PathParam("businessid") String businessId,
                                         @PathParam("rightlevel") String rightLevel) {
        log.debug("Rights request received.");
        return rrs.getRights(socialSec, businessId, rightLevel);
    }

}
