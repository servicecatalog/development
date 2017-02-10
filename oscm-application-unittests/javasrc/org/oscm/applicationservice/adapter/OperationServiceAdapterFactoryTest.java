/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 21.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.applicationservice.adapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import org.oscm.domobjects.TechnicalProductOperation;
import org.oscm.ws.WSPortConnector;
import org.oscm.ws.WSPortDescription;
import org.oscm.internal.types.exception.SaaSSystemException;

/**
 * @author weiser
 * 
 */
public class OperationServiceAdapterFactoryTest {

    private TechnicalProductOperation tpo;

    @Before
    public void setup() {
        tpo = new TechnicalProductOperation();
    }

    @Test(expected = SaaSSystemException.class)
    public void getNotificationServiceAdapter_TargetNull() throws Exception {
        tpo.setActionUrl(null);

        OperationServiceAdapterFactory.getOperationServiceAdapter(tpo, null,
                null, null);
    }

    @Test(expected = SaaSSystemException.class)
    public void getNotificationServiceAdapter_TargetEmpty() throws Exception {
        tpo.setActionUrl("  ");

        OperationServiceAdapterFactory.getOperationServiceAdapter(tpo, null,
                null, null);
    }

    @Test(expected = SaaSSystemException.class)
    public void getSupportedVersion_UnsupportedVersion() {
        WSPortConnector pc = mock(WSPortConnector.class);
        WSPortDescription pd = mock(WSPortDescription.class);
        when(pd.getTargetNamespace()).thenReturn("unsupported namespace");
        when(pc.getPortDescription()).thenReturn(pd);

        OperationServiceAdapterFactory.getSupportedVersion(pc);
    }
}
