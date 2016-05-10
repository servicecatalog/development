/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 3, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.Path;

import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.RestEndpoint;
import org.oscm.rest.common.RestResource;

/**
 * Resource class for the trigger process parameters.
 * 
 * @author miethaner
 */
@Path("/triggerprocessparameters")
public class RestTriggerProcessParameter extends RestResource {

    /**
     * Redirects to the endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/")
    public RestResource redirectToEndpoint() {

        EndpointBackend<TriggerProcessParameter> backend = new TriggerProcessParameterBackend();
        return new RestEndpoint<TriggerProcessParameter>(backend);
    }
}
