package org.oscm.rest.account;

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

import org.oscm.rest.account.data.AccountRepresentation;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/organizations")
@Stateless
public class OrganizationResource extends RestResource {

    @EJB
    AccountBackend ab;

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrganization(@Context Request request, AccountRepresentation content,
            @InjectParam AccountParameters params) throws Exception {
        return post(request, ab.postOrganization(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Path("/{orgId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganization(@Context Request request, @InjectParam AccountParameters params) throws Exception {
        return get(request, ab.getOrganization(), params, true);
    }

}
