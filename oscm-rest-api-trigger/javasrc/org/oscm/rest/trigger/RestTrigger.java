/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestEndpoint;
import org.oscm.rest.common.Since;
import org.oscm.rest.trigger.TriggerActionRepresentation.Action;

import com.sun.jersey.api.core.InjectParam;

/**
 * Rest root resource for trigger component
 * 
 * @author miethaner
 */
@Path(CommonParams.PATH_VERSION)
public class RestTrigger {

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerDefinitionEndpoint
            extends
            RestEndpoint<TriggerDefinitionRepresentation, TriggerRequestParameters> {

        public TriggerDefinitionEndpoint(
                EndpointBackend<TriggerDefinitionRepresentation, TriggerRequestParameters> backend) {
            super(backend);
        }
    }

    @EJB
    private TriggerDefinitionBackend triggerDefinitionBackend;

    /**
     * Redirects to the /triggerdefinitions endpoint
     * 
     * @return the endpoint resource
     */
    @Path(TriggerParams.PATH_DEFINITIONS)
    public TriggerDefinitionEndpoint redirectToDefinitions() {

        return new TriggerDefinitionEndpoint(triggerDefinitionBackend);
    }

    /**
     * Gets all available trigger actions
     * 
     * @return the endpoint resource
     */
    @GET
    @Path(TriggerParams.PATH_ACTIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTriggerActions() {

        Collection<TriggerActionRepresentation> list = new ArrayList<TriggerActionRepresentation>();
        list.add(new TriggerActionRepresentation(null,
                Action.SUBSCRIBE_TO_SERVICE));
        list.add(new TriggerActionRepresentation(null,
                Action.UNSUBSCRIBE_FROM_SERVICE));
        list.add(new TriggerActionRepresentation(null,
                Action.MODIFY_SUBSCRIPTION));

        return Response
                .ok(new RepresentationCollection<TriggerActionRepresentation>(
                        list)).build();
    }

    /**
     * Sub class to prevent type erasure for the generic endpoint. Extended with
     * status methods for trigger process
     * 
     * @author miethaner
     */
    protected class TriggerProcessEndpoint
            extends
            RestEndpoint<TriggerProcessRepresentation, TriggerRequestParameters> {

        private TriggerProcessEndpointBackend backend;

        public TriggerProcessEndpoint(TriggerProcessEndpointBackend backend) {
            super(backend);
            this.backend = backend;
        }

        @Since(CommonParams.VERSION_1)
        @PUT
        @Path(CommonParams.PATH_ID + TriggerParams.PATH_TRIGGER_APPROVE)
        @Consumes(MediaType.APPLICATION_JSON)
        public Response putApprove(@Context Request request,
                @InjectParam TriggerRequestParameters params,
                TriggerProcessRepresentation content) {

            int version = getVersion(request);

            prepareData(version, params, true, content);

            backend.putApprove(params, content);

            return Response.noContent().build();
        }

        @Since(CommonParams.VERSION_1)
        @PUT
        @Path(CommonParams.PATH_ID + TriggerParams.PATH_TRIGGER_REJECT)
        @Consumes(MediaType.APPLICATION_JSON)
        public Response putReject(@Context Request request,
                @InjectParam TriggerRequestParameters params,
                TriggerProcessRepresentation content) {

            int version = getVersion(request);

            prepareData(version, params, true, content);

            backend.putReject(params, content);

            return Response.noContent().build();
        }

        @Since(CommonParams.VERSION_1)
        @PUT
        @Path(CommonParams.PATH_ID + TriggerParams.PATH_TRIGGER_CANCEL)
        @Consumes(MediaType.APPLICATION_JSON)
        public Response putCancel(@Context Request request,
                @InjectParam TriggerRequestParameters params,
                TriggerProcessRepresentation content) {

            int version = getVersion(request);

            prepareData(version, params, true, content);

            backend.putCancel(params, content);

            return Response.noContent().build();
        }

    }

    @EJB
    private TriggerProcessBackend triggerProcessBackend;

    /**
     * Redirects to the /triggerprocesses endpoint
     * 
     * @return the endpoint resource
     */
    @Path(TriggerParams.PATH_PROCESSES)
    public TriggerProcessEndpoint redirectToProcesses() {

        return new TriggerProcessEndpoint(triggerProcessBackend);
    }

}
