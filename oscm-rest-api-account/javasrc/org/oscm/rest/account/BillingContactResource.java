package org.oscm.rest.account;

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

import org.oscm.rest.account.data.BillingContactRepresentation;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/billingcontacts")
@Stateless
public class BillingContactResource extends RestResource {

    @EJB
    AccountBackend ab;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBillingContacts(@Context Request request, @InjectParam AccountParameters params)
            throws Exception {
        return getCollection(request, ab.getBillingContactCollection(), params);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBillingContact(@Context Request request, BillingContactRepresentation content,
            @InjectParam AccountParameters params) throws Exception {
        return post(request, ab.postBillingContact(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response getBillingContact(@Context Request request, @InjectParam AccountParameters params) throws Exception {
        return get(request, ab.getBillingContact(), params, true);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response updateBillingContact(@Context Request request, BillingContactRepresentation content,
            @InjectParam AccountParameters params) throws Exception {
        return put(request, ab.putBillingContact(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response deleteBillingContact(@Context Request request, @InjectParam AccountParameters params)
            throws Exception {
        return delete(request, ab.deleteBillingContact(), params);
    }

}
