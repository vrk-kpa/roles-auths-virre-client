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

import fi.vm.kapa.rova.external.model.virre.Company;
import fi.vm.kapa.rova.external.model.virre.CompanyPerson;
import fi.vm.kapa.rova.external.model.virre.CompanyRepresentations;
import fi.vm.kapa.rova.external.model.virre.RepresentationRight;
import fi.vm.kapa.rova.logging.Logger;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import fi.vm.kapa.rova.virreclient.service.*;
import org.springframework.stereotype.Service;

import javax.ws.rs.QueryParam;

@Service
@Path("/rest")
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
    public Response getCompanyPerson(@PathParam("socialsec") String socialsec) {
        log.debug("CompanyPerson request received.");
        try {
            CompanyPerson person = arc.getCompanyPerson(socialsec);
            return Response.ok().entity(person).build();
        } catch (VIRREServiceException e) {
            log.error("Returning error. Failed to get companies: " + e.getMessage());
            ResponseBuilder responseBuilder = Response.serverError();
            return responseBuilder.build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prh/representations/{businessid}")
    public Response getRepresentations(@PathParam("businessid") String businessid) {
        log.debug("Representations request received.");
        try {
            CompanyRepresentations reprs = crs.getRepresentations(businessid);
            return Response.ok().entity(reprs).build();
        } catch (VIRREServiceException e) {
            log.error("Returning error. Failed to get persons: " + e.getMessage());
            ResponseBuilder responseBuilder = Response.serverError();
            return responseBuilder.build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prh/rights")
    public Response getRights(@QueryParam("socialsec") String socialSec,
                        @QueryParam("businessid") String businessId,
                        @QueryParam("rightlevel") String rightLevel) {
        log.debug("Rights request received.");
        try {
            RepresentationRight right = rrs.getRights(socialSec, businessId, rightLevel);
            return Response.ok().entity(right).build();
        } catch (VIRREServiceException e) {
            log.error("Returning error. Failed to get rights: " + e.getMessage());
            ResponseBuilder responseBuilder = Response.serverError();
            return responseBuilder.build();
        }
    }

}
