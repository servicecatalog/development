/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 04.07.2012                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.ui.pages.clusterconfig;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * Reflects the storage balancer configuration.
 * 
 * @author soehnges
 */
public class StorageBalancerConfig {

    private HierarchicalConfiguration xmlConfig;

    public StorageBalancerConfig(HierarchicalConfiguration xmlConfig) {
        this.xmlConfig = xmlConfig;
    }

    public String getBalancer() {
        return xmlConfig.getString("[@class]");
    }

    public void setBalancer(String value) {
        xmlConfig.setProperty("[@class]", value);
    }

    public String getStorages() {
        return xmlConfig.getString("[@storage]");
    }

    public void setStorages(String storages) {
        xmlConfig.setProperty("[@storage]", storages);
    }
}
