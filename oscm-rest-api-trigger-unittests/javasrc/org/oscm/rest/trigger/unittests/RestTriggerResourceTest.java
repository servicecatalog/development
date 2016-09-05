/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.unittests;

import org.junit.Test;

/**
 * Unit test for RestTriggerResource
 * 
 * @author miethaner
 */
public class RestTriggerResourceTest {

    @Test
    public void testAction() {
        //TODO glassfish upgrade
        /*RestTriggerResource.Action action = new RestTriggerResource()
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
                */
    }

}
