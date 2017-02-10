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

import org.oscm.app.iaas.data.Network;

public abstract class LPlatformBase {

    private final HierarchicalConfiguration configuration;

    public LPlatformBase(HierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<Network> getNetworks() {
        List<Network> result = new LinkedList<Network>();
        if (configuration != null) {
            List<HierarchicalConfiguration> networks = configuration
                    .configurationsAt("networks.network");
            for (HierarchicalConfiguration networkEntry : networks) {
                int maxVm = 0;
                try {
                    maxVm = Integer.parseInt(networkEntry
                            .getString("numOfMaxVm"));
                } catch (NumberFormatException e) {
                    maxVm = Integer.MAX_VALUE;
                }
                Network network = new Network(networkEntry.getString("name"),
                        networkEntry.getString("networkCategory"),
                        networkEntry.getString("networkId"), maxVm);
                result.add(network);
            }
        }
        return result;
    }

    public List<LServerConfiguration> getVServers() {
        List<LServerConfiguration> result = new LinkedList<LServerConfiguration>();
        if (configuration != null) {
            List<HierarchicalConfiguration> servers = configuration
                    .configurationsAt("lservers.lserver");
            for (HierarchicalConfiguration server : servers) {
                result.add(new LServerConfiguration(server));
            }
        }
        return result;
    }
}
