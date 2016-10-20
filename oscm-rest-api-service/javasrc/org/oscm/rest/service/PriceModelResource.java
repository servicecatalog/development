package org.oscm.rest.service;

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

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.service.data.PriceModelRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/services" + CommonParams.PATH_ID + "/pricemodel")
@Stateless
public class PriceModelResource extends RestResource {

    @EJB
    PriceModelBackend pmb;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context Request request, @InjectParam ServiceParameters params) throws Exception {
        return get(request, pmb.get(), params, true);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@Context Request request, PriceModelRepresentation content,
            @InjectParam ServiceParameters params) throws Exception {
        return put(request, pmb.put(), content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/customer/{orgKey}")
    public Response getForCustomer(@Context Request request, @InjectParam ServiceParameters params) throws Exception {
        return get(request, pmb.getForCustomer(), params, true);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/customer/{orgKey}")
    public Response updateForCustomer(@Context Request request, PriceModelRepresentation content,
            @InjectParam ServiceParameters params) throws Exception {
        return put(request, pmb.putForCustomer(), content, params);
    }

}
