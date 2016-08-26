package org.oscm.rest.operation;

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

import org.oscm.internal.intf.OperatorService;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.operation.data.SettingRepresentation;

import com.sun.jersey.api.core.InjectParam;

@Path(CommonParams.PATH_VERSION + "/settings")
@Stateless
public class SettingsResource extends RestResource {

    @EJB
    OperatorService os;

    @EJB
    SettingsBackend sb;

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSettings(@Context Request request, @InjectParam OperationParameters params) throws Exception {
        return get(request,
                new RestBackend.Get<RepresentationCollection<SettingRepresentation>, OperationParameters>() {

                    @Override
                    public RepresentationCollection<SettingRepresentation> get(OperationParameters params)
                            throws Exception {
                        List<VOConfigurationSetting> settings = os.getConfigurationSettings();
                        return SettingRepresentation.toCollection(settings);
                    }
                }, params, false);
    }

    @Since(CommonParams.VERSION_1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSetting(@Context Request request, SettingRepresentation content,
            @InjectParam OperationParameters params) throws Exception {
        return post(request, sb, content, params);
    }

    @Since(CommonParams.VERSION_1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response getSetting(@Context Request request, @InjectParam OperationParameters params) throws Exception {
        return get(request, sb, params, true);
    }

    @Since(CommonParams.VERSION_1)
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response updateUser(@Context Request request, SettingRepresentation content,
            @InjectParam OperationParameters params) throws Exception {
        return put(request, sb, content, params);
    }

    @Since(CommonParams.VERSION_1)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(CommonParams.PATH_ID)
    public Response deleteSetting(@Context Request request, @InjectParam OperationParameters params) throws Exception {
        return delete(request, sb, params);
    }

}
