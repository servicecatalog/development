/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.trigger.RestTriggerResource;
import org.oscm.rest.trigger.TriggerParameters;

import com.sun.jersey.spi.container.ContainerRequest;

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

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(CommonParams.PARAM_VERSION, new Integer(CommonParams.VERSION_1));

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Mockito.when(request.getProperties()).thenReturn(map);

        Response response = action.getCollection(request, params);
        assertThat(response.getEntity(),
                instanceOf(RepresentationCollection.class));
    }

}
