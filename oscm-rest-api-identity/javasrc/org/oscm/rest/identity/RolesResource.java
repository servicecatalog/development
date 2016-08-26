package org.oscm.rest.identity;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.identity.data.RolesRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/users/{userid}/userroles")
@Stateless
public class RolesResource extends RestResource {

    @EJB
    IdentityService is;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRoles(@Context Request request, @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, null, false);

        VOUser vo = new VOUser();
        vo.setUserId(params.getUserId());
        RolesRepresentation item = new RolesRepresentation(is.getUserDetails(vo));

        reviseData(version, item);
        return Response.ok(item).tag(item.getTag()).build();
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response setUserRoles(@Context Request request, RolesRepresentation content,
            @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, content, true);
        prepareTag(params, content, true);

        VOUserDetails vo = content.getVO();
        vo.setUserId(params.getUserId());
        is.setUserRoles(vo, new ArrayList<UserRoleType>(content.getUserRoles()));

        return Response.noContent().build();
    }

}
