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

import org.oscm.app.ror.data.LPlatformDescriptorConfiguration;
import org.oscm.app.ror.data.LServerConfiguration;

/**
 * @author zhaohang
 * 
 */
public class LPlatformDescriptorConfigurationTest {

    private LPlatformDescriptorConfiguration lPlatformDescriptorConfiguration;
    private static final String LSERVERTYPE = "lserverType";

    @Test
    public void getVSystemTemplateId() {
        // given
        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        List<HierarchicalConfiguration> servers = new ArrayList<HierarchicalConfiguration>();
        servers.add(configuration);
        when(configuration.getString(eq(LSERVERTYPE))).thenReturn(LSERVERTYPE);
        when(configuration.configurationsAt(eq("lservers.lserver")))
                .thenReturn(servers);

        // when
        lPlatformDescriptorConfiguration = new LPlatformDescriptorConfiguration(
                configuration);
        List<LServerConfiguration> result = lPlatformDescriptorConfiguration
                .getVServers();

        // then
        assertEquals(1, result.size());
        assertEquals(LSERVERTYPE, result.get(0).getLServerType());
    }

    @Test
    public void getVSystemTemplateId_ConfigurationIsNull() {
        // when
        lPlatformDescriptorConfiguration = new LPlatformDescriptorConfiguration(
                null);
        List<LServerConfiguration> result = lPlatformDescriptorConfiguration
                .getVServers();

        // then
        assertEquals(0, result.size());
    }
}
