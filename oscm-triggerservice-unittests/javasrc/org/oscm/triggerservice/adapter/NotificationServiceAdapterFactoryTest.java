/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: weiser                                                      
 *                                                                              
 *  Creation Date: 11.10.2011                                                      
 *                                                                              
 *  Completion Time: 11.10.2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.triggerservice.adapter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.dataservice.local.DataService;
import org.oscm.ws.WSPortConnector;
import org.oscm.ws.WSPortDescription;

/**
 * @author weiser
 * 
 */
public class NotificationServiceAdapterFactoryTest {

    private WSPortConnector pcMock;

    @Before
    public void setup() throws Exception {
        pcMock = mock(WSPortConnector.class);
    }

    @Test
    public void initAdapter() throws Exception {
        // given
        INotificationServiceAdapter adapter = mock(INotificationServiceAdapter.class);
        ConfigurationServiceLocal cs = mock(ConfigurationServiceLocal.class);
        DataService ds = mock(DataService.class);
        Object port = new Object();

        // when
        NotificationServiceAdapterFactory.initAdapter(cs, ds, adapter, port);

        // then
        verify(adapter, times(1)).setConfigurationService(eq(cs));
        verify(adapter, times(1)).setDataService(eq(ds));
        verify(adapter, times(1)).setNotificationService(eq(port));
    }

    void givenVersion(String version) {
        WSPortDescription pdMock = mock(WSPortDescription.class);
        when(pdMock.getVersion()).thenReturn(version);
        when(pcMock.getPortDescription()).thenReturn(pdMock);
    }

}
