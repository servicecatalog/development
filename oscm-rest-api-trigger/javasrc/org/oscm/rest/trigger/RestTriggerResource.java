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
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.RestFrontend;
import org.oscm.rest.common.RestResource;
import org.oscm.rest.common.Since;
import org.oscm.rest.trigger.config.TriggerCommonParams;
import org.oscm.rest.trigger.data.ActionRepresentation;
import org.oscm.rest.trigger.data.DefinitionRepresentation;
import org.oscm.rest.trigger.data.ProcessRepresentation;

import com.sun.jersey.api.core.InjectParam;

/**
 * Rest root resource for trigger component
 * 
 * @author miethaner
 */
@Path(CommonParams.PATH_VERSION)
@Stateless
public class RestTriggerResource extends RestResource {

    @EJB
    private DefinitionBackend triggerBackend;

    public void setDefinitionBackend(DefinitionBackend triggerBackend) {
        this.triggerBackend = triggerBackend;
    }

    @EJB
    private ProcessBackend processBackend;

    public void setProcessBackend(ProcessBackend processBackend) {
        this.processBackend = processBackend;
    }

    /**
     * Endpoint class for trigger definition
     * 
     * @author miethaner
     */
    public class Definition implements
            RestFrontend.Crud<DefinitionRepresentation, TriggerParameters> {

        /**
         * Gets the trigger definition for the given id.
         */
        @Since(CommonParams.VERSION_1)
        @GET
        @Path(CommonParams.PATH_ID)
        @Produces(MediaType.APPLICATION_JSON)
        @Override
        public Response getItem(@Context Request request,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            return get(request, triggerBackend.getItem(), params, true);
        }

        /**
         * Gets all trigger definitions of the callers organization.
         */
        @Since(CommonParams.VERSION_1)
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        @Override
        public Response getCollection(@Context Request request,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            return get(request, triggerBackend.getCollection(), params, false);
        }

        /**
         * Creates a new trigger definition from the given data. Returns new
         * location via response header
         */
        @Since(CommonParams.VERSION_1)
        @POST
        @Consumes(MediaType.APPLICATION_JSON)
        @Override
        public Response postCollection(@Context Request request,
                DefinitionRepresentation content,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            return post(request, triggerBackend.postCollection(), content,
                    params);
        }

        /**
         * Updates the trigger definition with the given id.
         */
        @Since(CommonParams.VERSION_1)
        @PUT
        @Path(CommonParams.PATH_ID)
        @Consumes(MediaType.APPLICATION_JSON)
        @Override
        public Response putItem(@Context Request request,
                DefinitionRepresentation content,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            return put(request, triggerBackend.putItem(), content, params);
        }

        /**
         * Deletes the trigger definition with the given id.
         */
        @Since(CommonParams.VERSION_1)
        @DELETE
        @Path(CommonParams.PATH_ID)
        @Override
        public Response deleteItem(@Context Request request,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            return delete(request, triggerBackend.deleteItem(), params);
        }

    }

    /**
     * Redirects to trigger definition endpoints
     * 
     * @return the trigger definition endpoints
     */
    @Path(TriggerCommonParams.PATH_DEFINITIONS)
    public Definition redirectToTrigger() {
        return new Definition();
    }

    /**
     * Endpoint class for trigger action
     * 
     * @author miethaner
     */
    public class Action implements RestFrontend.Get<TriggerParameters> {

        @Override
        public Response getItem(@Context Request request,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            return null;
        }

        /**
         * Gets all available trigger actions
         */
        @Since(CommonParams.VERSION_1)
        @GET
        @Produces(MediaType.APPLICATION_JSON)
        @Override
        public Response getCollection(@Context Request request,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {

            RestBackend.Get<RepresentationCollection<ActionRepresentation>, TriggerParameters> backend;
            backend = new RestBackend.Get<RepresentationCollection<ActionRepresentation>, TriggerParameters>() {

                @Override
                public RepresentationCollection<ActionRepresentation> get(
                        TriggerParameters params)
                        throws WebApplicationException {

                    Collection<ActionRepresentation> col = new ArrayList<ActionRepresentation>();
                    col.add(new ActionRepresentation(null,
                            ActionRepresentation.Action.SUBSCRIBE_TO_SERVICE));
                    col.add(new ActionRepresentation(
                            null,
                            ActionRepresentation.Action.UNSUBSCRIBE_FROM_SERVICE));
                    col.add(new ActionRepresentation(null,
                            ActionRepresentation.Action.MODIFY_SUBSCRIPTION));

                    return new RepresentationCollection<ActionRepresentation>(
                            col);
                }
            };

            return get(request, backend, params, false);
        }
    }

    /**
     * Redirects to trigger action endpoints
     * 
     * @return the trigger action endpoints
     */
    @Path(TriggerCommonParams.PATH_ACTIONS)
    public Action redirectToAction() {
        return new Action();
    }

    /**
     * Endpoint class for trigger process
     * 
     * @author miethaner
     */
    public class Process {

        /**
         * Approves the process with the given id and forwards the given
         * comment.
         * 
         * @param request
         *            the request context
         * @param params
         *            the request parameters
         * @return the response without content
         * @throws WebApplicationException
         */
        @Since(CommonParams.VERSION_1)
        @PUT
        @Path(CommonParams.PATH_ID + TriggerCommonParams.PATH_TRIGGER_APPROVE)
        public Response putApprove(@Context Request request,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            ProcessRepresentation process = new ProcessRepresentation();
            process.setComment("");
            return put(request, processBackend.putApprove(), process, params);
        }

        /**
         * Rejects the process with the given id and forwards the given comment.
         * 
         * @param request
         *            the request context
         * @param content
         *            the representation with the comment
         * @param params
         *            the request parameters
         * @return the response without content
         * @throws WebApplicationException
         */
        @Since(CommonParams.VERSION_1)
        @PUT
        @Path(CommonParams.PATH_ID + TriggerCommonParams.PATH_TRIGGER_REJECT)
        @Consumes(MediaType.APPLICATION_JSON)
        public Response putReject(@Context Request request,
                ProcessRepresentation content,
                @InjectParam TriggerParameters params)
                throws WebApplicationException {
            return put(request, processBackend.putReject(), content, params);
        }

    }

    /**
     * Redirects to trigger process endpoints
     * 
     * @return the trigger process endpoints
     */
    @Path(TriggerCommonParams.PATH_PROCESSES)
    public Process redirectToProcess() {
        return new Process();
    }
}
