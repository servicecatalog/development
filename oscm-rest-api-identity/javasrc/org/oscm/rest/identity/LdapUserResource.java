package org.oscm.rest.identity;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;

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
import javax.ws.rs.core.UriBuilder;

import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.identity.data.UserRepresentation;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;

@Path(CommonParams.PATH_VERSION + "/ldapusers")
@Stateless
public class LdapUserResource extends RestResource {

    @EJB
    IdentityService is;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLdapUsers(@Context Request request, @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, null, false);

        Collection<UserRepresentation> list = UserRepresentation.convert(is.searchLdapUsers(params.getPattern()));
        RepresentationCollection<UserRepresentation> item = new RepresentationCollection<UserRepresentation>(list);

        reviseData(version, item);
        return Response.ok(item).tag(item.getTag()).build();
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLdapUser(@Context Request request, UserRepresentation content,
            @InjectParam UserParameters params) throws Exception {
        int version = getVersion(request);
        prepareData(version, params, false, content, true);

        VOUserDetails vo = content.getVO();
        is.importLdapUsers(Collections.singletonList(vo), params.getMarketplaceId());

        ContainerRequest cr = (ContainerRequest) request;
        UriBuilder builder = cr.getAbsolutePathBuilder();
        URI uri = builder.path(UserResource.class, "getUser").build(vo.getUserId());

        return Response.created(uri).build();
    }

}
