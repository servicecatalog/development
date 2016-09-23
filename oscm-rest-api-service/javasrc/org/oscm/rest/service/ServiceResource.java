package org.oscm.rest.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.service.data.ServiceDetailsRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/services")
@Stateless
public class ServiceResource extends RestResource {

    @EJB
    ServiceBackend sb;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServices(@Context Request request, @InjectParam ServiceParameters params) throws Exception {
        return getCollection(request, sb.getCollection(), params);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createService(@Context Request request, ServiceDetailsRepresentation content,
            @InjectParam ServiceParameters params) throws Exception {
        return post(request, sb.post(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response getService(@Context Request request, @InjectParam ServiceParameters params) throws Exception {
        return get(request, sb.get(), params, true);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response updateService(@Context Request request, ServiceDetailsRepresentation content,
            @InjectParam ServiceParameters params) throws Exception {
        return put(request, sb.put(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response deleteService(@Context Request request, @InjectParam ServiceParameters params) throws Exception {
        return delete(request, sb.delete(), params);
    }

}
