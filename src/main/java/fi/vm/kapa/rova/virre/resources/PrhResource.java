package fi.vm.kapa.rova.virre.resources;

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
import fi.vm.kapa.rova.virreclient.service.VIRREServiceException;

@Service
@Path("/rest")
public class PrhResource {

    private static final Logger log = Logger.getLogger(PrhResource.class);

    @Inject
    private CompaniesService cs;

    @Inject
    private RepresentationsService rs;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/prh/companies/{socialsecuritynumber}")
    public Response getCompanies(@PathParam("socialsecuritynumber") String socialsecuritynumber) {
        try {
            List<String> roles = cs.getCompanies(socialsecuritynumber);
            return Response.ok().entity(roles).build();
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
        try {
            List<String> roles = rs.getRepresentations(businessid);
            return Response.ok().entity(roles).build();
        } catch (VIRREServiceException e) {
            log.error("Returning error. Failed to get persons: " + e.getMessage());
            ResponseBuilder responseBuilder = Response.serverError();
            return responseBuilder.build();
        }
    }

}
