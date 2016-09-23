package org.oscm.rest.identity;

import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import org.oscm.rest.identity.data.UserRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/ldapusers")
@Stateless
public class LdapUserResource extends RestResource {

    @EJB
    UserBackend ub;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLdapUsers(@Context Request request, @InjectParam UserParameters params) throws Exception {
        return getCollection(request, ub.getLdapUsers(), params);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLdapUser(@Context Request request, UserRepresentation content,
            @InjectParam UserParameters params) throws Exception {
        return post(request, ub.postLdapUser(), content, params, UserResource.class, "getUser");
    }

}
