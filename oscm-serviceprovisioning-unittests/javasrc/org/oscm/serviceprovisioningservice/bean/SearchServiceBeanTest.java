/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: groch                                                      
 *                                                                              
 *  Creation Date: 18.07.2011                                                      
 *                                                                              
 *  Completion Time: 20.07.2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.serviceprovisioningservice.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.oscm.dataservice.bean.IndexMQSender;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.index.IndexReinitRequestMessage;
import org.oscm.usergroupservice.bean.UserGroupServiceLocalBean;

public class SearchServiceBeanTest {

    private SearchServiceBean searchServiceBean;
    private IndexMQSender indexMQSender;

    @Before
    public void setUp() throws Exception {
        searchServiceBean = spy(new SearchServiceBean());
        indexMQSender = mock(IndexMQSender.class);
        searchServiceBean.setDm(mock(DataService.class));
        searchServiceBean.userGroupService = mock(UserGroupServiceLocalBean.class);
        doReturn(indexMQSender).when(searchServiceBean).getMQSender();
    }

    @Test
    public void initIndexForFulltextSearch_notForcingCreation()
            throws Exception {
        ArgumentCaptor<Serializable> captor = ArgumentCaptor
                .forClass(Serializable.class);
        searchServiceBean.initIndexForFulltextSearch(false);
        verify(indexMQSender, times(1)).sendMessage(captor.capture());
        assertFalse(((IndexReinitRequestMessage) captor.getValue())
                .isForceIndexCreation());
    }

    @Test
    public void initIndexForFulltextSearch_forcingCreation() throws Exception {
        ArgumentCaptor<Serializable> captor = ArgumentCaptor
                .forClass(Serializable.class);
        searchServiceBean.initIndexForFulltextSearch(true);
        verify(indexMQSender, times(1)).sendMessage(captor.capture());
        assertTrue(((IndexReinitRequestMessage) captor.getValue())
                .isForceIndexCreation());
    }

    @Test
    public void testReadDelayTime_Default() throws Exception {
        // ON_MESSAGE_DELAY not set
        System.clearProperty("ON_MESSAGE_DELAY");
        assertEquals(50, IndexRequestListener.readDelayTime());
    }

    @Test
    public void testReadDelayTime_negativNumber() throws Exception {
        System.setProperty("ON_MESSAGE_DELAY", "-1");
        assertEquals(50, IndexRequestListener.readDelayTime());
    }

    @Test
    public void testReadDelayTime_NoNumber() throws Exception {
        System.setProperty("ON_MESSAGE_DELAY", "aaaa");
        assertEquals(50, IndexRequestListener.readDelayTime());
    }

    @Test
    public void testReadDelayTime_0() throws Exception {
        System.setProperty("ON_MESSAGE_DELAY", "0");
        assertEquals(0, IndexRequestListener.readDelayTime());
    }

    @Test
    public void testReadDelayTime_22() throws Exception {
        System.setProperty("ON_MESSAGE_DELAY", "22");
        assertEquals(22, IndexRequestListener.readDelayTime());
    }
}
