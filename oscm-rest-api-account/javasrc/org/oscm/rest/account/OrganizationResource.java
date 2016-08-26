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

import org.oscm.internal.intf.AccountService;
import org.oscm.internal.intf.OperatorService;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.rest.account.data.AccountRepresentation;
import org.oscm.rest.account.data.OrganizationRepresentation;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/organizations")
@Stateless
public class OrganizationResource extends RestResource {

    @EJB
    AccountService as;

    @EJB
    OperatorService os;

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrganization(@Context Request request, AccountRepresentation content,
            @InjectParam AccountParameters params) throws Exception {
        return post(request, new RestBackend.Post<AccountRepresentation, AccountParameters>() {

            @Override
            public Object post(AccountRepresentation content, AccountParameters params) throws Exception {
                VOOrganization org;
                if (content.isSelfRegistration()) {
                    // TODO: this is available public
                    org = as.registerCustomer(content.getOrganization().getVO(), content.getUser().getVO(),
                            content.getPassword(), content.getServiceKey(), params.getMarketplaceId(),
                            content.getSellerId());
                } else if (content.isCustomerRegistration()) {
                    org = as.registerKnownCustomer(content.getOrganization().getVO(), content.getUser().getVO(),
                            content.getProps(), params.getMarketplaceId());
                } else {
                    org = os.registerOrganization(content.getOrganization().getVO(), null, content.getUser().getVO(),
                            content.getProps(), params.getMarketplaceId(), content.getOrganizationRoles());
                }
                return org.getOrganizationId();
            }
        }, content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Path("/{orgId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrganization(@Context Request request, @InjectParam AccountParameters params) throws Exception {
        return get(request, new RestBackend.Get<OrganizationRepresentation, AccountParameters>() {

            @Override
            public OrganizationRepresentation get(AccountParameters params) throws Exception {
                VOOrganization org = as.getOrganizationData();
                OrganizationRepresentation item;
                if (org.getOrganizationId().equals(params.getOrgId())) {
                    item = new OrganizationRepresentation(org);
                } else {
                    item = new OrganizationRepresentation(os.getOrganization(params.getOrgId()));
                }
                return item;
            }

        }, params, true);
    }

}
