package org.oscm.rest.account;

import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
import org.oscm.internal.vo.VOPaymentInfo;
import org.oscm.rest.account.data.PaymentInfoRepresentation;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/paymentinfos")
@Stateless
public class PaymentInfoResource extends RestResource {

    @EJB
    AccountService as;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaymentInfos(@Context Request request, @InjectParam AccountParameters params) throws Exception {
        return get(request,
                new RestBackend.Get<RepresentationCollection<PaymentInfoRepresentation>, AccountParameters>() {

                    @Override
                    public RepresentationCollection<PaymentInfoRepresentation> get(AccountParameters params)
                            throws Exception {
                        Collection<PaymentInfoRepresentation> list = PaymentInfoRepresentation.convert(as
                                .getPaymentInfos());
                        RepresentationCollection<PaymentInfoRepresentation> item = new RepresentationCollection<PaymentInfoRepresentation>(
                                list);
                        return item;
                    }
                }, params, false);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response getPaymentInfo(@Context Request request, @InjectParam AccountParameters params) throws Exception {
        return get(request, new RestBackend.Get<PaymentInfoRepresentation, AccountParameters>() {

            @Override
            public PaymentInfoRepresentation get(AccountParameters params) throws Exception {
                PaymentInfoRepresentation item = null;
                List<VOPaymentInfo> list = as.getPaymentInfos();
                for (VOPaymentInfo pi : list) {
                    if (pi.getKey() == params.getId().longValue()) {
                        item = new PaymentInfoRepresentation(pi);
                        break;
                    }
                }
                if (item == null) {
                    throw new ObjectNotFoundException(ClassEnum.PAYMENT_INFO, String
                            .valueOf(params.getId().longValue()));
                }
                return item;
            }
        }, params, true);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response updatePaymentInfo(@Context Request request, PaymentInfoRepresentation content,
            @InjectParam AccountParameters params) throws Exception {
        return put(request, new RestBackend.Put<PaymentInfoRepresentation, AccountParameters>() {

            @Override
            public void put(PaymentInfoRepresentation content, AccountParameters params) throws Exception {
                as.savePaymentInfo(content.getVO());
            }
        }, content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response deletePaymentInfo(@Context Request request, @InjectParam AccountParameters params) throws Exception {
        return delete(request, new RestBackend.Delete<AccountParameters>() {

            @Override
            public void delete(AccountParameters params) throws Exception {
                VOPaymentInfo pi = new VOPaymentInfo();
                pi.setKey(params.getId().longValue());
                as.deletePaymentInfo(pi);
            }
        }, params);
    }

}
