/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 19, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.EndpointBackend;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RequestParameters;
import org.oscm.rest.common.RestEndpoint;

import com.sun.jersey.api.uri.UriBuilderImpl;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Unit test for RestEndpoint
 * 
 * @author miethaner
 */
public class RestEndpointTest {

    private class MockRepresentation extends Representation {
        @Override
        public void validateContent() throws WebApplicationException {
        }

        @Override
        public void update() {
        }

        @Override
        public void convert() {
        }
    }

    private class MockRequestParameters extends RequestParameters {

        @Override
        public void validateParameters() throws WebApplicationException {
        }

        @Override
        public void update() {
        }
    }

    private class LocalEndpoint extends
            RestEndpoint<MockRepresentation, MockRequestParameters> {

        public LocalEndpoint(
                EndpointBackend<MockRepresentation, MockRequestParameters> backend) {
            super(backend);
        }

    }

    private interface LocalBackend extends
            EndpointBackend<MockRepresentation, MockRequestParameters> {
    }

    @Test
    public void testGet() {

        MockRepresentation rep = new MockRepresentation();
        rep.setId(UUID.fromString("14269f7a-2184-11e6-b67b-9e71128cae77"));

        LocalBackend backend = Mockito.mock(LocalBackend.class);
        LocalEndpoint endpoint = new LocalEndpoint(backend);

        MockRequestParameters params = new MockRequestParameters();
        params.setId(UUID.randomUUID());

        Collection<MockRepresentation> collection = new ArrayList<MockRepresentation>();
        collection.add(rep);

        Mockito.when(backend.getItem(params)).thenReturn(rep);
        Mockito.when(backend.getCollection(params)).thenReturn(collection);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        Response response = endpoint.getItem(request, params);

        assertEquals(response.getStatus(), CommonParams.STATUS_SUCCESS);
        assertNotNull(response.getEntity());
        Mockito.verify(backend, Mockito.times(1)).getItem(params);

        response = endpoint.getCollection(request, params);

        assertEquals(response.getStatus(), CommonParams.STATUS_SUCCESS);
        assertNotNull(response.getEntity());
        Mockito.verify(backend, Mockito.times(1)).getCollection(params);
    }

    @Test
    public void testPost() {

        MockRepresentation rep = new MockRepresentation();
        rep.setId(UUID.fromString("14269f7a-2184-11e6-b67b-9e71128cae77"));

        LocalBackend backend = Mockito.mock(LocalBackend.class);
        LocalEndpoint endpoint = new LocalEndpoint(backend);

        MockRequestParameters params = new MockRequestParameters();
        params.setId(UUID.randomUUID());

        Mockito.when(backend.postCollection(params, rep)).thenReturn(
                UUID.randomUUID());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        UriInfo info = Mockito.mock(UriInfo.class);
        UriBuilder builder = new UriBuilderImpl();
        Mockito.when(info.getAbsolutePathBuilder()).thenReturn(builder);

        Response response = endpoint.postCollection(request, info, params, rep);

        assertEquals(response.getStatus(), CommonParams.STATUS_CREATED);
        Mockito.verify(backend, Mockito.times(1)).postCollection(params, rep);
    }

    @Test
    public void testPut() {

        MockRepresentation rep = new MockRepresentation();
        rep.setId(UUID.fromString("14269f7a-2184-11e6-b67b-9e71128cae77"));

        LocalBackend backend = Mockito.mock(LocalBackend.class);
        LocalEndpoint endpoint = new LocalEndpoint(backend);

        MockRequestParameters params = new MockRequestParameters();
        params.setId(UUID.randomUUID());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        Response response = endpoint.putItem(request, params, rep);

        assertEquals(response.getStatus(), CommonParams.STATUS_NO_CONTENT);
        Mockito.verify(backend, Mockito.times(1)).putItem(params, rep);
    }

    @Test
    public void testDelete() {

        MockRepresentation rep = new MockRepresentation();
        rep.setId(UUID.fromString("14269f7a-2184-11e6-b67b-9e71128cae77"));

        LocalBackend backend = Mockito.mock(LocalBackend.class);
        LocalEndpoint endpoint = new LocalEndpoint(backend);

        MockRequestParameters params = new MockRequestParameters();
        params.setId(UUID.randomUUID());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        Response response = endpoint.deleteItem(request, params);

        assertEquals(response.getStatus(), CommonParams.STATUS_NO_CONTENT);
        Mockito.verify(backend, Mockito.times(1)).deleteItem(params);

    }

    @Test
    public void testVersionAndID() {

        LocalBackend backend = Mockito.mock(LocalBackend.class);
        LocalEndpoint endpoint = new LocalEndpoint(backend);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest requestWith = Mockito.mock(ContainerRequest.class);
        ContainerRequest requestWithout = Mockito.mock(ContainerRequest.class);
        Mockito.when(requestWith.getProperties()).thenReturn(map);

        MockRequestParameters params = new MockRequestParameters();

        try {
            endpoint.getItem(requestWithout, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }

        try {
            endpoint.getCollection(requestWithout, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }

        try {
            endpoint.postCollection(requestWithout, null, params, null);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }

        try {
            endpoint.putItem(requestWithout, params, null);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }

        try {
            endpoint.deleteItem(requestWithout, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }

        try {
            endpoint.getItem(requestWith, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }

        try {
            endpoint.putItem(requestWith, params, null);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }

        try {
            endpoint.deleteItem(requestWith, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(CommonParams.STATUS_NOT_FOUND, e.getResponse()
                    .getStatus());
        }
    }
}
