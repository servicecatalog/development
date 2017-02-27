/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                                                                                 
 *  Creation Date: Mar 4, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;

import org.oscm.app.iaas.PropertyHandler;

/**
 * Unit tests for RORVDiskCommunication
 * 
 */
public class RORVDiskCommunicationTest {
    private RORVDiskCommunication rorVDiskCommunication;
    private PropertyHandler ph;

    @Before
    public void setup() throws Exception {
        rorVDiskCommunication = spy(new RORVDiskCommunication());
        ph = mock(PropertyHandler.class);
    }

    @Test
    public void createVDisk() throws Exception {
        // when
        String result = rorVDiskCommunication.createVDisk(ph);
        // then
        assertNull(result);
    }

    @Test
    public void isVDiskDeployed() throws Exception {
        // when
        boolean result = rorVDiskCommunication.isVDiskDeployed(ph);
        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void attachVDisk() throws Exception {
        // when
        rorVDiskCommunication.attachVDisk(ph);
    }

    @Test
    public void isVDiskAttached() throws Exception {
        // when
        boolean result = rorVDiskCommunication.isVDiskAttached(ph);
        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void isAdditionalDiskSelected() throws Exception {
        // when
        boolean result = rorVDiskCommunication.isAdditionalDiskSelected(ph);
        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void areVDisksDetached() throws Exception {
        // when
        boolean result = rorVDiskCommunication.areVDisksDetached(ph);
        // then
        assertEquals(Boolean.TRUE, Boolean.valueOf(result));
    }

    @Test
    public void isAttachedVDisksFound() throws Exception {
        // when
        boolean result = rorVDiskCommunication.isAttachedVDisksFound(ph);
        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void detachVDisks() throws Exception {
        // when
        rorVDiskCommunication.detachVDisks(ph);
    }

    @Test
    public void areVDisksDestroyed() throws Exception {
        // when
        boolean result = rorVDiskCommunication.areVDisksDestroyed(ph);
        // then
        assertEquals(Boolean.TRUE, Boolean.valueOf(result));
    }

    @Test
    public void destroyVDisks() throws Exception {
        // when
        rorVDiskCommunication.destroyVDisks(ph);
    }
}
