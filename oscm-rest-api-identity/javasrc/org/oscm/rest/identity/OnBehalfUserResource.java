package org.oscm.rest.identity;

import java.net.URI;

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
import javax.ws.rs.core.UriBuilder;

import org.oscm.internal.intf.IdentityService;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.identity.data.OnBehalfUserRepresentation;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;

@Path(CommonParams.PATH_VERSION + "/onbehalfusers")
@Stateless
public class OnBehalfUserResource extends RestResource {

    // private static final String PATH_USERID = "/{userid}";

    @EJB
    IdentityService is;

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOnBehalfUser(@Context Request request, OnBehalfUserRepresentation content,
            @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, content, true);

        String newId = is.createOnBehalfUser(content.getOrganizationId(), content.getPassword()).getUserId();

        ContainerRequest cr = (ContainerRequest) request;
        UriBuilder builder = cr.getAbsolutePathBuilder();
        URI uri = builder.path(UserResource.class, "getUser").build(newId);

        return Response.created(uri).build();
    }

    // @Since(CommonParams.VERSION_1)
    // @GET
    // @Produces(MediaType.APPLICATION_JSON)
    // @Path(PATH_USERID)
    // public Response getUser(@Context Request request, @InjectParam
    // UserParameters params) throws Exception {
    // int version = getVersion(request);
    // prepareData(version, params, false, null, false);
    //
    // VOUser vo = new VOUser();
    // vo.setUserId(params.getUserId());
    // OnBehalfUserRepresentation item = new
    // OnBehalfUserRepresentation(is.getUserDetails(vo));
    //
    // reviseData(version, item);
    // return Response.ok(item).tag(item.getTag()).build();
    // }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOnBehalfUser(@Context Request request, @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, null, false);

        // TODO: either this or delete with user id in path but check if its the
        // calling users one
        is.cleanUpCurrentUser();

        return Response.noContent().build();
    }

}
