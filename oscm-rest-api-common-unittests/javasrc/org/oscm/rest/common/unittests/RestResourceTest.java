/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 19, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RequestParameters;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.common.RestResource;

/**
 * Unit test for RestEndpoint
 * 
 * @author miethaner
 */
public class RestResourceTest extends RestResource {

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

    private RestBackend.Get<MockRepresentation, MockRequestParameters> backendGet = new RestBackend.Get<MockRepresentation, MockRequestParameters>() {

        @Override
        public MockRepresentation get(MockRequestParameters params) {

            assertNotNull(params);

            return new MockRepresentation();
        }
    };

    private RestBackend.Post<MockRepresentation, MockRequestParameters> backendPost = new RestBackend.Post<MockRepresentation, MockRequestParameters>() {

        @Override
        public Object post(MockRepresentation content,
                MockRequestParameters params) {

            assertNotNull(content);
            assertNotNull(params);

            return UUID.randomUUID();
        }
    };

    private RestBackend.Put<MockRepresentation, MockRequestParameters> backendPut = new RestBackend.Put<MockRepresentation, MockRequestParameters>() {

        @Override
        public void put(MockRepresentation content, MockRequestParameters params) {

            assertNotNull(content);
            assertNotNull(params);
        }
    };

    private RestBackend.Delete<MockRequestParameters> backendDelete = new RestBackend.Delete<MockRequestParameters>() {

        @Override
        public void delete(MockRequestParameters params) {

            assertNotNull(params);
        }
    };

    //TODO glassfish upgrade
    @Test
    public void testGet() {

        /*MockRequestParameters params = new MockRequestParameters();
        params.setId(new Long(1L));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperty(CommonParams.PARAM_VERSION)).thenReturn(map);

        Response response = get(request, backendGet, params, true);

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertThat(response.getEntity(), instanceOf(MockRepresentation.class));*/
    }

    @Test
    public void testPost() {

        /*MockRepresentation content = new MockRepresentation();
        content.setId(new Long(1L));

        MockRequestParameters params = new MockRequestParameters();
        params.setId(new Long(1L));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        UriBuilder builder = new UriBuilderImpl();
        Mockito.when(request.getAbsolutePathBuilder()).thenReturn(builder);

        Response response = post(request, backendPost, content, params);

        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());*/
    }

    /*@Test
    public void testPut() {

        MockRepresentation content = new MockRepresentation();
        content.setId(new Long(1L));
        content.setTag(new Long(1L).toString());

        MockRequestParameters params = new MockRequestParameters();
        params.setId(new Long(1L));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        Response response = put(request, backendPut, content, params);

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDelete() {

        MockRequestParameters params = new MockRequestParameters();
        params.setId(new Long(1L));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        Response response = delete(request, backendDelete, params);

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testVersionAndID() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest requestWith = Mockito.mock(ContainerRequest.class);
        ContainerRequest requestWithout = Mockito.mock(ContainerRequest.class);
        Mockito.when(requestWith.getProperties()).thenReturn(map);

        MockRequestParameters params = new MockRequestParameters();

        try {
            get(requestWithout, backendGet, params, false);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            post(requestWithout, backendPost, null, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            put(requestWithout, backendPut, null, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            delete(requestWithout, backendDelete, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            get(requestWith, backendGet, params, true);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            put(requestWith, backendPut, null, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            delete(requestWith, backendDelete, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }
    }*/
}
