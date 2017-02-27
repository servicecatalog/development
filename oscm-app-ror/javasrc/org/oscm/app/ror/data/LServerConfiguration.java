/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-11-12                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.data;

import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.HierarchicalConfiguration;

import org.oscm.app.iaas.data.VNIC;
import org.oscm.app.iaas.data.VServerConfiguration;

public class LServerConfiguration implements VServerConfiguration {

    private HierarchicalConfiguration configuration;

    public LServerConfiguration(HierarchicalConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getPrivateIP() {
        // no index returns first element (add index when other elements are
        // required)
        return configuration.getString("nics.nic.privateIp");
    }

    @Override
    public String getNetworkId() {
        // no index returns first element (add index when other elements are
        // required)
        // FIXME change to get ControlNetworkId
        return configuration.getString("nics.nic.networkId");
    }

    @Override
    public String getDiskImageId() {
        return configuration.getString("diskimageId");
    }

    public String getLServerType() {
        return configuration.getString("lserverType");
    }

    public String getVmType() {
        return configuration.getString("vmType");
    }

    public String getServerType() {
        return configuration.getString("serverType");
    }

    @Override
    public String getServerId() {
        return configuration.getString("lserverId");
    }

    @Override
    public String getServerName() {
        return configuration.getString("lserverName");
    }

    public String getLServerStatus() {
        return configuration.getString("lserverStatus");
    }

    public String getNumOfCPU() {
        return configuration.getString("numOfCpu");
    }

    public String getMemorySize() {
        return configuration.getString("memorySize");
    }

    public String getPool() {
        return configuration.getString("pool");
    }

    public String getStoragePool() {
        return configuration.getString("storagePool");
    }

    @Override
    public String getHostName() {
        return configuration.getString("hostName");
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("============ Server Configuration =========\n");
        sb.append("ServerId:\t" + getServerId() + "\n");
        sb.append("ServerName:\t" + getServerName() + "\n");
        sb.append("ServerType:\t" + getServerType() + "\n");
        sb.append("DiskImgId:\t" + getDiskImageId() + "\n");
        sb.append("VMHostPool:\t" + getPool() + "\n");
        sb.append("StoragePool:\t" + getStoragePool() + "\n");
        sb.append("NetworkID:\t" + getNetworkId() + " [");
        List<HierarchicalConfiguration> nics = configuration
                .configurationsAt("nics.nic");
        for (HierarchicalConfiguration nic : nics) {
            sb.append("networkID=" + nic.getString("networkId") + ", ");
            sb.append("nicNo=" + nic.getString("nicNo") + ", ");
            sb.append("management=" + nic.getString("management") + "]\n");
        }
        sb.append("===========================================\n");
        return sb.toString();
    }

    @Override
    public Set<VNIC> getVirtualNICs() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isEFM() {
        // TODO Auto-generated method stub
        return false;
    }
}
