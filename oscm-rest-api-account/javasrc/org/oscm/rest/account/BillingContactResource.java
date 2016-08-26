package org.oscm.rest.account;

import java.util.Collection;
import java.util.List;

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

import org.oscm.internal.intf.AccountService;
import org.oscm.internal.types.exception.DomainObjectException.ClassEnum;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOBillingContact;
import org.oscm.rest.account.data.BillingContactRepresentation;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/billingcontacts")
@Stateless
public class BillingContactResource extends RestResource {

    @EJB
    AccountService as;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBillingContacts(@Context Request request, @InjectParam AccountParameters params)
            throws Exception {
        return get(request,
                new RestBackend.Get<RepresentationCollection<BillingContactRepresentation>, AccountParameters>() {

                    @Override
                    public RepresentationCollection<BillingContactRepresentation> get(AccountParameters params)
                            throws Exception {
                        Collection<BillingContactRepresentation> list = BillingContactRepresentation.convert(as
                                .getBillingContacts());
                        RepresentationCollection<BillingContactRepresentation> item = new RepresentationCollection<BillingContactRepresentation>(
                                list);
                        return item;
                    }
                }, params, false);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBillingContact(@Context Request request, BillingContactRepresentation content,
            @InjectParam AccountParameters params) throws Exception {
        return post(request, new RestBackend.Post<BillingContactRepresentation, AccountParameters>() {

            @Override
            public Object post(BillingContactRepresentation content, AccountParameters params) throws Exception {
                VOBillingContact vo = content.getVO();
                return String.valueOf(as.saveBillingContact(vo).getKey());
            }
        }, content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response getBillingContact(@Context Request request, @InjectParam AccountParameters params) throws Exception {
        return get(request, new RestBackend.Get<BillingContactRepresentation, AccountParameters>() {

            @Override
            public BillingContactRepresentation get(AccountParameters params) throws Exception {
                BillingContactRepresentation item = null;
                List<VOBillingContact> list = as.getBillingContacts();
                for (VOBillingContact bc : list) {
                    if (bc.getKey() == params.getId().longValue()) {
                        item = new BillingContactRepresentation(bc);
                        break;
                    }
                }
                if (item == null) {
                    throw new ObjectNotFoundException(ClassEnum.BILLING_CONTACT, String.valueOf(params.getId()
                            .longValue()));
                }
                return item;
            }
        }, params, true);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response updateBillingContact(@Context Request request, BillingContactRepresentation content,
            @InjectParam AccountParameters params) throws Exception {
        return put(request, new RestBackend.Put<BillingContactRepresentation, AccountParameters>() {

            @Override
            public void put(BillingContactRepresentation content, AccountParameters params) throws Exception {
                as.saveBillingContact(content.getVO());
            }

        }, content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response deleteBillingContact(@Context Request request, @InjectParam AccountParameters params)
            throws Exception {
        return delete(request, new RestBackend.Delete<AccountParameters>() {

            @Override
            public void delete(AccountParameters params) throws Exception {
                VOBillingContact bc = new VOBillingContact();
                bc.setKey(params.getId().longValue());
                as.deleteBillingContact(bc);
            }
        }, params);
    }

}
