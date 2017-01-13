package org.oscm.rest.subscription;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/subscriptions")
@Stateless
public class SubscriptionResource extends RestResource {

    @EJB
    SubscriptionBackend sb;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriptions(@Context Request request, @InjectParam SubscriptionParameters params)
            throws Exception {
        return getCollection(request, sb.getCollection(), params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response deleteSubscription(@Context Request request, @InjectParam SubscriptionParameters params)
            throws Exception {
        return delete(request, sb.delete(), params);
    }

}
