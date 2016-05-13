/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.Path;

import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.RestEndpoint;

/**
 * @author miethaner
 *
 */
@Path("/{version}")
public class RestTrigger {

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerDefinitionEndpoint extends
            RestEndpoint<TriggerDefinition> {

        public TriggerDefinitionEndpoint(
                EndpointBackend<TriggerDefinition> backend) {
            super(backend);
        }
    }

    /**
     * Redirects to the /triggerdefinitions endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/triggerdefinitions")
    public TriggerDefinitionEndpoint redirectToDefinitions() {

        EndpointBackend<TriggerDefinition> backend = new TriggerDefinitionBackend();
        return new TriggerDefinitionEndpoint(backend);
    }

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerProcessEndpoint extends RestEndpoint<TriggerProcess> {

        public TriggerProcessEndpoint(EndpointBackend<TriggerProcess> backend) {
            super(backend);
        }
    }

    /**
     * Redirects to the /triggerprocesses endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/triggerprocesses")
    public TriggerProcessEndpoint redirectToProcesses() {

        EndpointBackend<TriggerProcess> backend = new TriggerProcessBackend();
        return new TriggerProcessEndpoint(backend);
    }

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerProcessIdentifierEndpoint extends
            RestEndpoint<TriggerProcessIdentifier> {

        public TriggerProcessIdentifierEndpoint(
                EndpointBackend<TriggerProcessIdentifier> backend) {
            super(backend);
        }
    }

    /**
     * Redirects to the /triggerprocessidentifiers endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/triggerprocessidentifiers")
    public TriggerProcessIdentifierEndpoint redirectToProcessIdentifiers() {

        EndpointBackend<TriggerProcessIdentifier> backend = new TriggerProcessIdentifierBackend();
        return new TriggerProcessIdentifierEndpoint(backend);
    }

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerProcessParameterEndpoint extends
            RestEndpoint<TriggerProcessParameter> {

        public TriggerProcessParameterEndpoint(
                EndpointBackend<TriggerProcessParameter> backend) {
            super(backend);
        }
    }

    /**
     * Redirects to the /triggerprocessparameters endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/triggerprocessparameters")
    public TriggerProcessParameterEndpoint redirectToProcessParameters() {

        EndpointBackend<TriggerProcessParameter> backend = new TriggerProcessParameterBackend();
        return new TriggerProcessParameterEndpoint(backend);
    }
}
