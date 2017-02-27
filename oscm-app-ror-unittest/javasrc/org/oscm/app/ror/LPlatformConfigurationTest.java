/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
 *                                                                                                                                 
 *  Creation Date: Mar 5, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;

import org.oscm.app.iaas.data.Network;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.ror.data.LServerConfiguration;

/**
 * @author zhaohang
 * 
 */
public class LPlatformConfigurationTest {

    private LPlatformConfiguration lPlatformConfiguration;
    private static final String NETWORKID = "networkId";
    private static final String LPLATFORMID = "lplatformId";
    private static final String LPLATFORMNAME = "lplatformName";
    private static final String LSERVERSTATUS = "lserverStatus";
    private static final String LSERVER = "lservers.lserver";
    private static final String NETWORKS = "networks.network";

    @Test
    public void getVSystemId() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LPLATFORMID, LPLATFORMID);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        String result = lPlatformConfiguration.getVSystemId();

        // then
        assertEquals(result, LPLATFORMID);
    }

    @Test
    public void getVSystemName() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LPLATFORMNAME, LPLATFORMNAME);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        String result = lPlatformConfiguration.getVSystemName();

        // then
        assertEquals(result, LPLATFORMNAME);
    }

    @Test
    public void getNetworkIds_ConfigurationIsNull() {
        // when
        lPlatformConfiguration = new LPlatformConfiguration(null);
        List<Network> result = lPlatformConfiguration.getNetworks();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getNetworkIds_NetworkIdIsNull() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                NETWORKID, null);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        List<Network> result = lPlatformConfiguration.getNetworks();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getNetworkIds_NetworkIdTrimLengthIsZero() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                NETWORKID, "   ");

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        List<Network> result = lPlatformConfiguration.getNetworks();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getNetworkIds() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                NETWORKID, NETWORKID);
        prepareNetWorks(configuration, NETWORKS);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        List<Network> result = lPlatformConfiguration.getNetworks();

        // then
        assertEquals(1, result.size());
        assertEquals(NETWORKID, result.get(0).getId());
    }

    @Test
    public void getServerStatus_ConfigurationIsNull() {
        // when
        lPlatformConfiguration = new LPlatformConfiguration(null);
        List<String> result = lPlatformConfiguration.getServerStatus();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getServerStatus_ServerStatusIsNull() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERSTATUS, null);
        prepareNetWorks(configuration, NETWORKS);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        List<String> result = lPlatformConfiguration.getServerStatus();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getServerStatus_ServerStatusTrimLengthIsZero() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERSTATUS, "     ");
        prepareNetWorks(configuration, LSERVER);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        List<String> result = lPlatformConfiguration.getServerStatus();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getServerStatus() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERSTATUS, LSERVERSTATUS);
        prepareNetWorks(configuration, LSERVER);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        List<String> result = lPlatformConfiguration.getServerStatus();

        // then
        assertEquals(1, result.size());
        assertEquals(LSERVERSTATUS, result.get(0));
    }

    @Test
    public void getVServers_ConfigurationIsNull() {
        // when
        lPlatformConfiguration = new LPlatformConfiguration(null);
        List<LServerConfiguration> result = lPlatformConfiguration
                .getVServers();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getVServers() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERSTATUS, LSERVERSTATUS);
        prepareNetWorks(configuration, LSERVER);

        // when
        lPlatformConfiguration = new LPlatformConfiguration(configuration);
        List<LServerConfiguration> result = lPlatformConfiguration
                .getVServers();

        // then
        assertEquals(1, result.size());
        assertEquals(LSERVERSTATUS, result.get(0).getLServerStatus());
    }

    private HierarchicalConfiguration prepareConfiguration(String entryString,
            String returnString) {
        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        when(configuration.getString(eq(entryString))).thenReturn(returnString);

        return configuration;
    }

    private void prepareNetWorks(HierarchicalConfiguration configuration,
            String entryString) {
        List<HierarchicalConfiguration> networks = new ArrayList<HierarchicalConfiguration>();
        networks.add(configuration);
        when(configuration.configurationsAt(entryString)).thenReturn(networks);
    }
}
