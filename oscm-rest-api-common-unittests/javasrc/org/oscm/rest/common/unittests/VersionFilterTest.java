/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 19, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.Since;
import org.oscm.rest.common.Until;
import org.oscm.rest.common.VersionFilter;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * Unit test for VersionFilter
 * 
 * @author miethaner
 */
public class VersionFilterTest {

    public interface MockMultivaluedMap extends MultivaluedMap<String, String> {
    }

    @SuppressWarnings("boxing")
    private Map<String, Object> testVersionFilter(String version,
            boolean since, final int sinceValue, boolean until,
            final int untilValue) throws WebApplicationException {

        UriInfo info = Mockito.mock(UriInfo.class);
        MultivaluedMap<String, String> map = Mockito
                .mock(MockMultivaluedMap.class);
        AbstractMethod method = Mockito.mock(AbstractMethod.class);
        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Since sinceAnnotation = Mockito.mock(Since.class);
        Until untilAnnotation = Mockito.mock(Until.class);

        List<String> list = new ArrayList<String>();
        list.add(version);
        Mockito.when(map.containsKey(CommonParams.PARAM_VERSION)).thenReturn(
                true);
        Mockito.when(map.get(CommonParams.PARAM_VERSION)).thenReturn(list);
        Mockito.when(info.getPathParameters()).thenReturn(map);

        Mockito.when(sinceAnnotation.value()).thenReturn(sinceValue);
        Mockito.when(method.isAnnotationPresent(Since.class)).thenReturn(since);
        Mockito.when(method.getAnnotation(Since.class)).thenReturn(
                sinceAnnotation);

        Mockito.when(untilAnnotation.value()).thenReturn(untilValue);
        Mockito.when(method.isAnnotationPresent(Until.class)).thenReturn(until);
        Mockito.when(method.getAnnotation(Until.class)).thenReturn(
                untilAnnotation);

        Map<String, Object> prop = new HashMap<String, Object>();
        Mockito.when(request.getProperties()).thenReturn(prop);

        VersionFilter filter = new VersionFilter(method, info);
        filter.filter(request);

        return prop;
    }

    @SuppressWarnings("boxing")
    @Test
    public void testVersionFilterVersionPositive() {

        String version = "v" + CommonParams.VERSION_1;

        Map<String, Object> prop = testVersionFilter(version, false, 0, false,
                0);

        assertEquals(CommonParams.VERSION_1,
                prop.get(CommonParams.PARAM_VERSION));
    }

    @Test
    public void testVersionFilterVersionNegative() {

        String version = "n42";

        try {
            testVersionFilter(version, false, 0, false, 0);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }
    }

    @SuppressWarnings("boxing")
    @Test
    public void testVersionFilterSincePositive() {

        String version = "v" + CommonParams.VERSION_1;

        Map<String, Object> prop = testVersionFilter(version, true,
                CommonParams.VERSION_1, false, 0);

        assertEquals(CommonParams.VERSION_1,
                prop.get(CommonParams.PARAM_VERSION));
    }

    @Test
    public void testVersionFilterSinceNegative() {

        String version = "v" + CommonParams.VERSION_1;

        try {
            testVersionFilter(version, true, CommonParams.VERSION_1 + 1, false,
                    0);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }
    }

    @SuppressWarnings("boxing")
    @Test
    public void testVersionFilterUntilPositive() {

        String version = "v" + CommonParams.VERSION_1;

        Map<String, Object> prop = testVersionFilter(version, false, 0, true,
                CommonParams.VERSION_1 + 1);

        assertEquals(CommonParams.VERSION_1,
                prop.get(CommonParams.PARAM_VERSION));
    }

    @Test
    public void testVersionFilterUntilNegative() {

        String version = "v" + CommonParams.VERSION_1;

        try {
            testVersionFilter(version, false, 0, true, CommonParams.VERSION_1);
            fail();
        } catch (WebApplicationException e) {
            assertEquals(Status.NOT_FOUND.getStatusCode(), e.getResponse()
                    .getStatus());
        }
    }
}
