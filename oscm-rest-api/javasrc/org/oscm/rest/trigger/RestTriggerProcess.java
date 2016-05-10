/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Apr 28, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.Path;

import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.RestEndpoint;
import org.oscm.rest.common.RestResource;

/**
 * Resource class for the trigger processes.
 * 
 * @author miethaner
 */
@Path("/triggerprocesses")
public class RestTriggerProcess extends RestResource {

    /**
     * Redirects to the endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/")
    public RestResource redirectToEndpoint() {

        EndpointBackend<TriggerProcess> backend = new TriggerProcessBackend();
        return new RestEndpoint<TriggerProcess>(backend);
    }

}
