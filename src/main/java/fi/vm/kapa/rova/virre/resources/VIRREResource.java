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
@Path("/rest")
public class VIRREResource {

    @Inject
    private VIRREService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles/{hetu}/{organizationid")
    public Response getOrganizationalRoles(@PathParam("hetu") String hetu,
            @PathParam("organizationid") String organizationid,
            @QueryParam("endUserId") String endUserId,
            @QueryParam("requestId") String requestId) {
        try {
            List<OrganizationalRole> organizationalRoles = service.getOrganizationalRoles(hetu, organizationid, endUserId, requestId);
            return Response.ok().entity(organizationalRoles).build();
        } catch (VIRREServiceException e) {
            ResponseBuilder responseBuilder = Response.serverError();
            return responseBuilder.build();
        }
    }
}
