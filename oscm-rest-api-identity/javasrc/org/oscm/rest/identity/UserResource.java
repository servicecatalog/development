package org.oscm.rest.identity;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.identity.data.UserRepresentation;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;

@Path(CommonParams.PATH_VERSION + "/users")
@Stateless
public class UserResource extends RestResource {

    private static final String PATH_USERID = "/{userid}";

    @EJB
    IdentityService is;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(@Context Request request, @InjectParam UserParameters params) {
        int version = getVersion(request);
        prepareData(version, params, false, null, false);

        Collection<UserRepresentation> list = UserRepresentation.convert(is.getUsersForOrganization());
        RepresentationCollection<UserRepresentation> item = new RepresentationCollection<UserRepresentation>(list);

        reviseData(version, item);
        return Response.ok(item).tag(item.getTag()).build();
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@Context Request request, UserRepresentation content, @InjectParam UserParameters params)
            throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, content, true);

        VOUserDetails vo = content.getVO();
        String newId = is.createUser(vo, new ArrayList<UserRoleType>(vo.getUserRoles()), params.getMarketplaceId())
                .getUserId();

        ContainerRequest cr = (ContainerRequest) request;
        UriBuilder builder = cr.getAbsolutePathBuilder();
        URI uri = builder.path(newId).build();

        return Response.created(uri).build();
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(PATH_USERID)
    public Response getUser(@Context Request request, @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, null, false);

        VOUser vo = new VOUser();
        vo.setUserId(params.getUserId());
        UserRepresentation item = new UserRepresentation(is.getUserDetails(vo));

        reviseData(version, item);
        return Response.ok(item).tag(item.getTag()).build();
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path(PATH_USERID)
    public Response updateUser(@Context Request request, UserRepresentation content, @InjectParam UserParameters params)
            throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, content, true);
        prepareTag(params, content, true);

        String newId = is.updateUser(content.getVO()).getUserId();
        if (!newId.equals(params.getUserId())) {
            // if the user id has been changed, point to new location
            ContainerRequest cr = (ContainerRequest) request;
            UriBuilder builder = cr.getAbsolutePathBuilder();
            URI uri = builder.path(newId).build();
            // TODO: this or created?
            return Response.status(Status.MOVED_PERMANENTLY).contentLocation(uri).build();
        }

        return Response.noContent().build();
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(PATH_USERID)
    public Response deleteUser(@Context Request request, @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, null, false);

        VOUser vo = new VOUser();
        vo.setUserId(params.getUserId());
        // we have to read first as delete checks key and version
        vo = is.getUser(vo);
        is.deleteUser(vo, params.getMarketplaceId());

        return Response.noContent().build();
    }

}
