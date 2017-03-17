/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-11-12                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.data;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

import org.oscm.app.iaas.data.VSystemConfiguration;

public class LPlatformConfiguration extends LPlatformBase implements
        VSystemConfiguration {

    private final HierarchicalConfiguration configuration;

    public LPlatformConfiguration(HierarchicalConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    @Override
    public String getVSystemId() {
        return configuration.getString("lplatformId");
    }

    @Override
    public String getVSystemName() {
        return configuration.getString("lplatformName");
    }

    @Override
    public List<String> getServerStatus() {
        List<String> result = new LinkedList<String>();
        if (configuration != null) {
            List<HierarchicalConfiguration> servers = configuration
                    .configurationsAt("lservers.lserver");
            for (HierarchicalConfiguration server : servers) {
                String status = server.getString("lserverStatus");
                if (status != null && status.trim().length() > 0) {
                    result.add(status);
                }
            }
        }
        return result;
    }

    @Override
    public List<String> getNetworkIds(boolean fullID) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNetworkPrefix() {
        // TODO Auto-generated method stub
        return null;
    }
}
