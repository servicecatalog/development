/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: May 19, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.oscm.rest.common.CommonParams.PARAM_VERSION;
import static org.oscm.rest.common.CommonParams.VERSION_1;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.impl.RequestImpl;
import org.apache.cxf.jaxrs.impl.UriBuilderImpl;
import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.rest.common.*;

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
        public boolean put(MockRepresentation content, MockRequestParameters params) {

            assertNotNull(content);
            assertNull(content.getETag());
            assertNotNull(params);
            return true;
        }
    };

    private RestBackend.Put<MockRepresentation, MockRequestParameters> backendPutETag = new RestBackend.Put<MockRepresentation, MockRequestParameters>() {

        @Override
        public boolean put(MockRepresentation content, MockRequestParameters params) {

            assertNotNull(content);
            assertNotNull(content.getETag());
            assertNotNull(params);
            return true;
        }
    };

    private RestBackend.Delete<MockRequestParameters> backendDelete = new RestBackend.Delete<MockRequestParameters>() {

        @Override
        public boolean delete(MockRequestParameters params) {

            assertNotNull(params);
            return true;
        }
    };

    @Test
    public void testGet() throws Exception {

        MockRequestParameters params = new MockRequestParameters();
        params.setId(1L);

        UriInfo uriinfo = mock(UriInfo.class);
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(CommonParams.PARAM_VERSION, Integer.valueOf(CommonParams.VERSION_1).toString());
        Mockito.when(uriinfo.getQueryParameters()).thenReturn(map);

        Response response = get(uriinfo, backendGet, params, true);

        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertThat(response.getEntity(), instanceOf(MockRepresentation.class));
    }

    @Test
    public void testPost() throws Exception {

        MockRepresentation content = new MockRepresentation();
        content.setId(1L);

        MockRequestParameters params = new MockRequestParameters();
        params.setId(1L);

        UriInfo uriinfo = mock(UriInfo.class);
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(CommonParams.PARAM_VERSION, Integer.valueOf(CommonParams.VERSION_1).toString());
        Mockito.when(uriinfo.getQueryParameters()).thenReturn(map);
        Response response = post(uriinfo, backendPost, content, params);

        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testPut() throws Exception {

        MockRepresentation content = new MockRepresentation();
        content.setId(1L);
        content.setETag(1L);

        MockRequestParameters params = new MockRequestParameters();
        params.setId(1L);

        UriInfo uriInfo = mock(UriInfo.class);
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(CommonParams.PARAM_VERSION, Integer.valueOf(CommonParams.VERSION_1).toString());
        Mockito.when(uriInfo.getQueryParameters()).thenReturn(map);

        Response response = put(uriInfo, backendPut, content, params);

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testPutWithETag() throws Exception {

        MockRepresentation content = new MockRepresentation();
        content.setId(1L);
        content.setETag(1L);

        MockRequestParameters params = new MockRequestParameters();
        params.setId(1L);
        params.setMatch("1");

        UriInfo uriinfo = mock(UriInfo.class);
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(CommonParams.PARAM_VERSION, Integer.valueOf(CommonParams.VERSION_1).toString());
        Mockito.when(uriinfo.getQueryParameters()).thenReturn(map);

        Response response = put(uriinfo, backendPutETag, content, params);

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDelete() throws Exception {

        MockRequestParameters params = new MockRequestParameters();
        params.setId(1L);
        UriInfo uriinfo = mock(UriInfo.class);
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(CommonParams.PARAM_VERSION, Integer.valueOf(CommonParams.VERSION_1).toString());
        Mockito.when(uriinfo.getQueryParameters()).thenReturn(map);

        Response response = delete(uriinfo, backendDelete, params);

        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testVersionAndID() throws Exception {

        UriInfo uriInfoWith = mock(UriInfo.class);
        UriInfo uriInfoWithout = mock(UriInfo.class);
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        map.putSingle(CommonParams.PARAM_VERSION, Integer.valueOf(CommonParams.VERSION_1).toString());
        Mockito.when(uriInfoWith.getQueryParameters()).thenReturn(map);
        Mockito.when(uriInfoWithout.getQueryParameters()).thenReturn(new MultivaluedHashMap<>());

        MockRequestParameters params = new MockRequestParameters();

        try {
            get(uriInfoWithout, backendGet, params, false);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            post(uriInfoWithout, backendPost, null, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            put(uriInfoWithout, backendPut, null, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            delete(uriInfoWithout, backendDelete, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            get(uriInfoWith, backendGet, params, true);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            put(uriInfoWith, backendPut, null, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }

        try {
            delete(uriInfoWith, backendDelete, params);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }
    }
}
