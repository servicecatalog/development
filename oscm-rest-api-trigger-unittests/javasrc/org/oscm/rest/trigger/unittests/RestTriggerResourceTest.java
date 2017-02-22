/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.internal.intf.TriggerDefinitionService;
import org.oscm.internal.intf.TriggerService;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.trigger.DefinitionBackend;
import org.oscm.rest.trigger.ProcessBackend;
import org.oscm.rest.trigger.RestTriggerResource;
import org.oscm.rest.trigger.TriggerParameters;
import org.oscm.rest.trigger.data.DefinitionRepresentation;
import org.oscm.rest.trigger.data.ProcessRepresentation;

/**
 * Unit test for RestTriggerResource
 * 
 * @author miethaner
 */
public class RestTriggerResourceTest {

    @Test
    public void testAction() {
        RestTriggerResource.Action action = new RestTriggerResource()
                .redirectToAction();

        TriggerParameters params = new TriggerParameters();
        params.setId(new Long(1L));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperty(Mockito.anyString()))
                .thenReturn(new Integer(CommonParams.VERSION_1));

        Response response = action.getCollection(request, params);
        assertThat(response.getEntity(),
                IsInstanceOf.instanceOf(RepresentationCollection.class));

        assertNull(action.getItem(request, params));
    }

    @Test
    public void testDefinition() {


        RestTriggerResource resource = new RestTriggerResource();

        RestTriggerResource.Definition definition = resource
                .redirectToTrigger();

        DefinitionBackend backend = new DefinitionBackend();
        TriggerDefinitionService service = Mockito
                .mock(TriggerDefinitionService.class);
        backend.setService(service);
        resource.setDefinitionBackend(backend);

        TriggerParameters params = new TriggerParameters();
        params.setId(new Long(1L));
        params.setMatch("1");

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperty(Mockito.anyString()))
                .thenReturn(new Integer(CommonParams.VERSION_1));
        ExtendedUriInfo uri = Mockito.mock(ExtendedUriInfo.class);
        Mockito.when(request.getUriInfo()).thenReturn(uri);
        Mockito.when(uri.getAbsolutePathBuilder())
                .thenReturn(UriBuilder.fromPath(""));

        Response response = definition.getCollection(request, params);
        assertThat(response.getEntity(),
                IsInstanceOf.instanceOf(RepresentationCollection.class));

        response = definition.getItem(request, params);
        assertThat(response.getEntity(),
                IsInstanceOf.instanceOf(DefinitionRepresentation.class));

        DefinitionRepresentation content = new DefinitionRepresentation();
        content.setId(new Long(1L));
        content.setETag(new Long(1L));
        content.setDescription("abc");
        content.setSuspending(Boolean.TRUE);
        content.setType("REST_SERVICE");
        content.setTargetURL("http://abc.de/asdf");
        content.setAction("SUBSCRIBE_TO_SERVICE");

        response = definition.postCollection(request, content, params);
        assertEquals(Response.Status.CREATED.getStatusCode(),
                response.getStatus());

        response = definition.putItem(request, content, params);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(),
                response.getStatus());
    }

    @Test
    public void testProcess() {
        RestTriggerResource resource = new RestTriggerResource();
        RestTriggerResource.Process process = resource.redirectToProcess();

        ProcessBackend backend = new ProcessBackend();
        TriggerService service = Mockito.mock(TriggerService.class);
        backend.setService(service);
        resource.setProcessBackend(backend);

        TriggerParameters params = new TriggerParameters();
        params.setId(new Long(1L));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperty(Mockito.anyString()))
                .thenReturn(new Integer(CommonParams.VERSION_1));

        ProcessRepresentation content = new ProcessRepresentation();
        content.setComment("abc");

        Response response = process.putApprove(request, params);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(),
                response.getStatus());

        response = process.putReject(request, content, params);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(),
                response.getStatus());
    }

}
