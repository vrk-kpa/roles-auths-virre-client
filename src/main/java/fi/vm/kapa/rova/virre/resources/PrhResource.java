package fi.vm.kapa.rova.virre.resources;

import fi.vm.kapa.rova.external.model.virre.Company;
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

import org.springframework.stereotype.Service;

import fi.vm.kapa.rova.virreclient.service.CompaniesService;
import fi.vm.kapa.rova.virreclient.service.RepresentationsService;
import fi.vm.kapa.rova.virreclient.service.RightsService;
import fi.vm.kapa.rova.virreclient.service.VIRREServiceException;
import javax.ws.rs.QueryParam;

@Service
@Path("/rest")
public class PrhResource {

    private static final Logger log = Logger.getLogger(PrhResource.class);

    @Inject
    private CompaniesService cs;

    @Inject
    private RepresentationsService reps;

    @Inject
    private RightsService rs;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prh/companies/{socialsec}")
    public Response getCompanies(@PathParam("socialsec") String socialsec) {
        log.info("Companies request received.");
        try {
            List<Company> companies = cs.getCompanies(socialsec);
            return Response.ok().entity(companies).build();
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
        log.info("Representations request received.");
        try {
            CompanyRepresentations reprs = reps.getRepresentations(businessid);
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
        log.info("Rights request received.");
        try {
            RepresentationRight right = rs.getRights(socialSec, businessId, rightLevel);
            return Response.ok().entity(right).build();
        } catch (VIRREServiceException e) {
            log.error("Returning error. Failed to get rights: " + e.getMessage());
            ResponseBuilder responseBuilder = Response.serverError();
            return responseBuilder.build();
        }
    }

}
