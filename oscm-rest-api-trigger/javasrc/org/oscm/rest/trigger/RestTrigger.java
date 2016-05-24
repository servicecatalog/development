/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 12, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.RestEndpoint;
import org.oscm.rest.common.Since;

import com.sun.jersey.api.core.InjectParam;

/**
 * @author miethaner
 *
 */
@Path(CommonParams.PATH_VERSION)
public class RestTrigger {

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerDefinitionEndpoint extends
            RestEndpoint<TriggerDefinition, TriggerRequestParameters> {

        public TriggerDefinitionEndpoint(
                EndpointBackend<TriggerDefinition, TriggerRequestParameters> backend) {
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

        EndpointBackend<TriggerDefinition, TriggerRequestParameters> backend = new TriggerDefinitionBackend();
        return new TriggerDefinitionEndpoint(backend);
    }

    /**
     * Sub class to prevent type erasure for the generic endpoint. Extended with
     * status methods for trigger process
     * 
     * @author miethaner
     */
    private class TriggerProcessEndpoint extends
            RestEndpoint<TriggerProcess, TriggerRequestParameters> {

        private TriggerProcessEndpointBackend backend;

        public TriggerProcessEndpoint(TriggerProcessEndpointBackend backend) {
            super(backend);
            this.backend = backend;
        }

        @Since(CommonParams.VERSION_1)
        @PUT
        @Path("/{id}/approve")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response putApprove(@Context Request request,
                @InjectParam TriggerRequestParameters params,
                TriggerProcess content) {

            int version = getVersion(request);

            params.validateResourceId();
            params.validateParameters();
            content.validateContent();

            params.setVersion(version);
            params.update();
            content.setVersion(version);
            content.update();

            backend.putApprove(params, content);

            return Response.noContent().build();
        }

        @Since(CommonParams.VERSION_1)
        @PUT
        @Path("/{id}/reject")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response putReject(@Context Request request,
                @InjectParam TriggerRequestParameters params,
                TriggerProcess content) {

            int version = getVersion(request);

            params.validateResourceId();
            params.validateParameters();
            content.validateContent();

            params.setVersion(version);
            params.update();
            content.setVersion(version);
            content.update();

            backend.putReject(params, content);

            return Response.noContent().build();
        }

        @Since(CommonParams.VERSION_1)
        @PUT
        @Path("/{id}/cancel")
        @Consumes(MediaType.APPLICATION_JSON)
        public Response putCancel(@Context Request request,
                @InjectParam TriggerRequestParameters params,
                TriggerProcess content) {

            int version = getVersion(request);

            params.validateResourceId();
            params.validateParameters();
            content.validateContent();

            params.setVersion(version);
            params.update();
            content.setVersion(version);
            content.update();

            backend.putCancel(params, content);

            return Response.noContent().build();
        }
    }

    /**
     * Redirects to the /triggerprocesses endpoint
     * 
     * @return the endpoint resource
     */
    @Path("/triggerprocesses")
    public TriggerProcessEndpoint redirectToProcesses() {

        TriggerProcessEndpointBackend backend = new TriggerProcessBackend();
        return new TriggerProcessEndpoint(backend);
    }

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerProcessIdentifierEndpoint extends
            RestEndpoint<TriggerAction, TriggerRequestParameters> {

        public TriggerProcessIdentifierEndpoint(
                EndpointBackend<TriggerAction, TriggerRequestParameters> backend) {
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

        EndpointBackend<TriggerAction, TriggerRequestParameters> backend = new TriggerActionBackend();
        return new TriggerProcessIdentifierEndpoint(backend);
    }

    /**
     * Sub class to prevent type erasure for the generic endpoint
     * 
     * @author miethaner
     */
    private class TriggerProcessParameterEndpoint extends
            RestEndpoint<TriggerProcessParameter, TriggerRequestParameters> {

        public TriggerProcessParameterEndpoint(
                EndpointBackend<TriggerProcessParameter, TriggerRequestParameters> backend) {
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

        EndpointBackend<TriggerProcessParameter, TriggerRequestParameters> backend = new TriggerProcessParameterBackend();
        return new TriggerProcessParameterEndpoint(backend);
    }
}
