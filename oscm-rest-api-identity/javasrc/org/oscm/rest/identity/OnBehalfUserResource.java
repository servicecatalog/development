package org.oscm.rest.identity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
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
import org.oscm.rest.identity.data.OnBehalfUserRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/onbehalfusers")
@Stateless
public class OnBehalfUserResource extends RestResource {

    @EJB
    UserBackend ub;

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOnBehalfUser(@Context Request request, OnBehalfUserRepresentation content,
            @InjectParam UserParameters params) throws Exception {
        return post(request, ub.postOnBehalfUser(), content, params, UserResource.class, "getUser");
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOnBehalfUser(@Context Request request, @InjectParam UserParameters params) throws Exception {
        return delete(request, ub.deleteOBehalfUser(), params);
    }

}
