/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2013-02-15                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.data.AccessInformation;
import org.oscm.app.iaas.data.DiskImage;
import org.oscm.app.iaas.data.VServerConfiguration;
import org.oscm.app.iaas.data.VServerStatus;
import org.oscm.app.iaas.data.VSystemConfiguration;
import org.oscm.app.iaas.data.VSystemTemplate;
import org.oscm.app.iaas.data.VSystemTemplateConfiguration;
import org.oscm.app.iaas.intf.VSystemCommunication;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.LServerClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.ror.data.LPlatformDescriptor;
import org.oscm.app.ror.data.LPlatformDescriptorConfiguration;
import org.oscm.app.ror.data.LServerConfiguration;
import org.oscm.app.ror.exceptions.RORException;

@Stateless
@Local(VSystemCommunication.class)
public class RORVSystemCommunication extends RORCommonInfo implements
        VSystemCommunication {

    @Override
    public String createVSystem(PropertyHandler properties) throws Exception {
        RORClient rorClient = getVdcClient(properties);
        VSystemConfiguration vSysConfig = getVSystemConfigurationByInstanceName(properties);
        String lplatformId;
        if (vSysConfig != null) {
            lplatformId = vSysConfig.getVSystemId();
        } else {

            lplatformId = rorClient.createLPlatform(
                    properties.getInstanceName(),
                    properties.getSystemTemplateId());
        }
        properties.setVsysId(lplatformId);
        properties.getIaasContext().clear();
        return lplatformId;
    }

    @Override
    public void destroyVSystem(PropertyHandler properties) throws Exception {
        LPlatformClient platformClient = getLPlatformClient(properties);
        platformClient.destroy();
        properties.getIaasContext().clear();
    }

    @Override
    public String getVSystemState(PropertyHandler properties) throws Exception {
        LPlatformClient platformClient = getLPlatformClient(properties);
        return platformClient.getStatus();
    }

    @Override
    public boolean getCombinedVServerState(PropertyHandler properties,
            String targetState) throws Exception {
        String vsysId = properties.getVsysId();
        if (vsysId == null) {
            throw new IllegalArgumentException(
                    "Virtual system ID not defined but required to retrive status of virtual servers");
        }
        if (targetState == null) {
            throw new IllegalArgumentException(
                    "Target state not defined but required to compare status of virtual servers");
        }
        RORClient vdcClient = getVdcClient(properties);
        List<LPlatformConfiguration> platforms = vdcClient.listLPlatforms(true);
        for (LPlatformConfiguration platform : platforms) {
            if (vsysId.equals(platform.getVSystemId())) {
                for (String status : platform.getServerStatus()) {
                    if (!targetState.equals(status)) {
                        return false;
                    }
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void startAllVServers(PropertyHandler properties) throws Exception {
        LPlatformClient platformClient = getLPlatformClient(properties);
        try {
            platformClient.startAllServers();
        } catch (RORException e) {
            if (e.isStateAlreadyPresent()) {
                return; // desired state was already there
            }
            throw e;
        }
    }

    @Override
    public void startVServers(PropertyHandler properties) throws Exception {
        String oldVserverId = properties.getVserverIdIfPresent();
        try {
            for (String serverId : properties.getVserversToBeStarted()) {
                if (serverId.trim().length() > 0) {
                    properties.setVserverId(serverId);
                    LServerClient serverClient = getLServerClient(properties);
                    try {
                        serverClient.start();
                    } catch (RORException e) {
                        if (!e.isStateAlreadyPresent()) {
                            throw e;
                        }
                    }
                }
            }
        } finally {
            properties.setVserverId(oldVserverId);
        }
    }

    @Override
    public List<String> stopAllVServers(PropertyHandler properties)
            throws Exception {
        List<String> result = new ArrayList<String>();
        String oldVserverId = properties.getVserverIdIfPresent();
        LPlatformClient platformClient = getLPlatformClient(properties);
        try {
            for (VServerConfiguration server : getConfiguration(properties)
                    .getVServers()) {
                properties.setVserverId(server.getServerId());
                LServerClient serverClient = getLServerClient(properties);
                String status = serverClient.getStatus();
                if (VServerStatus.RUNNING.equals(status)
                        || VServerStatus.STARTING.equals(status)) {
                    result.add(server.getServerId());
                }
            }
            try {
                platformClient.stopAllServers();
            } catch (RORException e) {
                if (e.isStateAlreadyPresent()) {
                    return result; // desired state was already there
                }
                throw e;
            }
        } finally {
            properties.setVserverId(oldVserverId);
        }
        return result;
    }

    @Override
    public List<String> getVServersForTemplate(String serverTemplateId,
            PropertyHandler properties) throws Exception {
        List<String> result = new ArrayList<String>();
        if (serverTemplateId != null && serverTemplateId.trim().length() > 0) {
            List<? extends VServerConfiguration> servers = getConfiguration(
                    properties).getVServers();
            for (VServerConfiguration lsc : servers) {
                if (serverTemplateId.equals(lsc.getDiskImageId())) {
                    result.add(lsc.getServerId());
                }
            }
        }
        return result;
    }

    @Override
    public String scaleUp(String masterTemplateId, String slaveTemplateId,
            PropertyHandler properties) throws Exception {

        LPlatformClient platformClient = getLPlatformClient(properties);
        LServerConfiguration master = null;
        List<LServerConfiguration> servers = platformClient.getConfiguration()
                .getVServers();
        for (LServerConfiguration lsc : servers) {
            if (masterTemplateId.equals(lsc.getDiskImageId())) {
                master = lsc;
                break;
            }
        }
        if (master == null) {
            throw new RuntimeException("Master does not exist");
        }
        properties.getIaasContext().clear();
        return platformClient.createLServer("Node" + servers.size(),
                master.getServerType(), slaveTemplateId, master.getNetworkId(),
                master.getPool(), master.getStoragePool(),
                properties.getCountCPU());

    }

    @Override
    public List<String> getPublicIps(PropertyHandler properties)
            throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        HashSet<String> serverIds = new HashSet<String>();
        serverIds.addAll(getVServersForTemplate(
                properties.getMasterTemplateId(), properties));
        if (properties.getSlaveTemplateId() != null
                && !properties.getSlaveTemplateId().equals(
                        properties.getMasterTemplateId())) {
            serverIds.addAll(getVServersForTemplate(
                    properties.getSlaveTemplateId(), properties));
        }
        String oldServerId = properties.getVserverIdIfPresent();
        try {
            for (String serverId : serverIds) {
                // temporarily set different server id
                properties.setVserverId(serverId);
                LServerClient lserverClient = getLServerClient(properties);
                String privateIP = lserverClient.getConfiguration()
                        .getPrivateIP();
                if (privateIP != null) {
                    result.add(privateIP);
                }
            }
        } finally {
            // make sure to reset old value
            properties.setVserverId(oldServerId);
        }
        return result;
    }

    @Override
    public List<AccessInformation> getAccessInfo(PropertyHandler properties)
            throws Exception {
        List<AccessInformation> result = new ArrayList<>();
        List<? extends VServerConfiguration> servers = getConfiguration(
                properties).getVServers();
        String oldServerId = properties.getVserverIdIfPresent();
        try {
            for (VServerConfiguration server : servers) {
                // temporarily set different server id
                properties.setVserverId(server.getServerId());
                LServerClient lserverClient = getLServerClient(properties);
                AccessInformation info = new AccessInformation(
                        ((LServerConfiguration) server).getPrivateIP(),
                        lserverClient.getInitialPassword());
                result.add(info);
            }
        } finally {
            // make sure to reset old value
            properties.setVserverId(oldServerId);
        }
        return result;
    }

    @Override
    public VSystemConfiguration getConfiguration(PropertyHandler properties)
            throws Exception {
        VSystemConfiguration result = properties.getIaasContext()
                .getVSystemConfiguration();
        if (result == null) { // nothing cached
            LPlatformClient platformClient = getLPlatformClient(properties);
            result = platformClient.getConfiguration();
            properties.getIaasContext().add(result);
        }
        return result;
    }

    @Override
    public List<VSystemTemplate> getVSystemTemplates(PropertyHandler properties)
            throws Exception {
        List<LPlatformDescriptor> listLPlatformDescriptors = getVdcClient(
                properties).listLPlatformDescriptors();
        List<VSystemTemplate> result = new ArrayList<VSystemTemplate>();
        for (LPlatformDescriptor ds : listLPlatformDescriptors) {
            result.add(ds);
        }
        return result;
    }

    @Override
    public List<DiskImage> getDiskImages(PropertyHandler properties)
            throws Exception {
        return getVdcClient(properties).listDiskImages();
    }

    @Override
    public VSystemTemplateConfiguration getVSystemTemplateConfiguration(
            PropertyHandler properties) throws Exception {
        LPlatformDescriptorConfiguration details = getVdcClient(properties)
                .getLPlatformDescriptorConfiguration(
                        properties.getSystemTemplateId());
        return details;
    }

    @Override
    public VSystemConfiguration getVSystemConfigurationByInstanceName(
            PropertyHandler properties) throws Exception {
        String instanceName = properties.getInstanceName();

        RORClient vdcClient = getVdcClient(properties);
        List<LPlatformConfiguration> platforms = vdcClient
                .listLPlatforms(false);

        for (LPlatformConfiguration platform : platforms) {
            if (instanceName.equals(platform.getVSystemName())) {
                return platform;
            }
        }

        return null;
    }

    @Override
    public String freePublicIPs(PropertyHandler properties,
            Set<String> externalIPs) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void activatePublicIPs(PropertyHandler properties,
            Set<String> externalIPs) throws Exception {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void allocatePublicIP(PropertyHandler properties) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean startAllEFMs(PropertyHandler properties) throws Exception {
        // TODO Auto-generated method stub
        return true;
    }

}
