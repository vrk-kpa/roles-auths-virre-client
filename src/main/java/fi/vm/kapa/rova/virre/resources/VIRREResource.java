package fi.vm.kapa.rova.virre.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.stereotype.Service;

import fi.vm.kapa.rova.engine.model.OrganizationalRole;
import fi.vm.kapa.rova.virre.model.OrganizationalPerson;
import fi.vm.kapa.rova.virreclient.service.VIRREService;
import fi.vm.kapa.rova.virreclient.service.VIRREServiceException;

@Service
@Path("/virre")
public class VIRREResource {

    @Inject
    private VIRREService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/person/{hetu}/{organizationid")
    public Response getOrganizationalPerson(@PathParam("hetu") String hetu,
            @PathParam("organizationid") String organizationid,
            @QueryParam("endUserId") String endUserId,
            @QueryParam("requestId") String requestId) {
        try {
            OrganizationalPerson organizationalPerson = service.getOrganizationalPerson(hetu, organizationid, endUserId, requestId);
            return Response.ok().entity(organizationalPerson).build();
        } catch (VIRREServiceException e) {
            ResponseBuilder responseBuilder = Response.serverError();
            return responseBuilder.build();
        }
    }
}
