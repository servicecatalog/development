package org.oscm.rest.service;

import java.util.Collections;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.oscm.internal.intf.ServiceProvisioningService;
import org.oscm.internal.vo.VOTechnicalService;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.service.data.TechnicalServiceRepresentation;
import org.oscm.string.Strings;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/technicalservices")
@Stateless
public class TechnicalServiceResource extends RestResource {

    @EJB
    TechnicalServiceBackend tsb;

    @EJB
    ServiceProvisioningService sps;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTechnicalServices(@Context Request request, @InjectParam ServiceParameters params)
            throws Exception {
        return getCollection(request, tsb.getCollection(), params);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTechnicalService(@Context Request request, TechnicalServiceRepresentation content,
            @InjectParam ServiceParameters params) throws Exception {
        return post(request, tsb.post(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response deleteTechnicalService(@Context Request request, @InjectParam ServiceParameters params)
            throws Exception {
        return delete(request, tsb.delete(), params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path(CommonParams.PATH_ID)
    public Response exportTechnicalService(@Context Request request, @InjectParam ServiceParameters params)
            throws Exception {
        // key needed
        VOTechnicalService ts = new VOTechnicalService();
        ts.setKey(params.getId().longValue());
        byte[] export = sps.exportTechnicalServices(Collections.singletonList(ts));
        return Response.ok(export, MediaType.APPLICATION_XML_TYPE).build();
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public Response importTechnicalServices(@Context Request request, byte[] input,
            @InjectParam ServiceParameters params) throws Exception {
        String msg = sps.importTechnicalServices(input);
        if (Strings.isEmpty(msg)) {
            return Response.noContent().build();
        }
        return Response.status(Status.BAD_REQUEST).entity(msg).build();
    }

}
