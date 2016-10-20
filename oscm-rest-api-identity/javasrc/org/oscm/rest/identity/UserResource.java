package org.oscm.rest.identity;

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
import org.oscm.rest.identity.data.UserRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/users")
@Stateless
public class UserResource extends RestResource {

    private static final String PATH_USERID = "/{userId}";

    @EJB
    UserBackend ub;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@Context Request request, @InjectParam UserParameters params) throws Exception {
        return getCollection(request, ub.getUsers(), params);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Context Request request, UserRepresentation content, @InjectParam UserParameters params)
            throws Exception {
        return post(request, ub.postUser(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(PATH_USERID)
    public Response getUser(@Context Request request, @InjectParam UserParameters params) throws Exception {
        return get(request, ub.getUser(), params, false);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path(PATH_USERID)
    public Response updateUser(@Context Request request, UserRepresentation content, @InjectParam UserParameters params)
            throws Exception {
        return put(request, ub.putUser(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(PATH_USERID)
    public Response deleteUser(@Context Request request, @InjectParam UserParameters params) throws Exception {
        return delete(request, ub.deleteUser(), params);
    }

}
