package org.oscm.rest.service;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.service.data.OrganizationRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/technicalservices" + CommonParams.PATH_ID + "/suppliers")
@Stateless
public class TSSupplierResource extends RestResource {

    @EJB
    TSSupplierBackend sb;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSuppliers(@Context Request request, @InjectParam ServiceParameters params) throws Exception {
        return getCollection(request, sb.getCollection(), params);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addSupplier(@Context Request request, OrganizationRepresentation content,
            @InjectParam ServiceParameters params) throws Exception {
        return post(request, sb.post(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{orgId}")
    public Response removeSupplier(@Context Request request, @InjectParam ServiceParameters params) throws Exception {
        return delete(request, sb.delete(), params);
    }

}
