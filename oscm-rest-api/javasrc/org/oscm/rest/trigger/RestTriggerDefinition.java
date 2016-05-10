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
 * Resource class for the trigger definitions.
 * 
 * @author miethaner
 */
@Path("/triggerdefinitions")
public class RestTriggerDefinition extends RestResource {

    /**
     * Redirects to the endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/")
    public RestResource redirectToEndpoint() {

        EndpointBackend<TriggerDefinition> backend = new TriggerDefinitionBackend();
        return new RestEndpoint<TriggerDefinition>(backend);
    }

}
