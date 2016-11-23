/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2012-11-08                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import java.util.Iterator;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.data.FlowState;
import org.oscm.app.iaas.data.Network;
import org.oscm.app.iaas.data.VServerStatus;
import org.oscm.app.iaas.data.VSystemConfiguration;
import org.oscm.app.iaas.i18n.Messages;
import org.oscm.app.iaas.intf.VServerCommunication;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.LServerClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LPlatformStatus;
import org.oscm.app.ror.data.LServerConfiguration;
import org.oscm.app.ror.data.LServerStatus;
import org.oscm.app.ror.exceptions.RORException;
import org.oscm.app.v2_0.exceptions.InstanceExistsException;
import org.oscm.app.v2_0.exceptions.SuspendException;

@Stateless
@Local(VServerCommunication.class)
public class RORVServerCommunication extends RORCommonInfo implements
        VServerCommunication {

    static final Logger logger = LoggerFactory
            .getLogger(RORVServerCommunication.class);

    /**
     * Creates a VSERVER for the given VSYS with the given name.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return the VServer ID
     * @throws Exception
     */
    @Override
    public String createVServer(PropertyHandler paramHandler) throws Exception {
        LPlatformClient vsysClient = getLPlatformClient(paramHandler);
        // Create virtual machine

        String vserverId = "";
        List<LServerConfiguration> lServers = vsysClient.getConfiguration()
                .getVServers();
        Iterator<LServerConfiguration> lServerIter = lServers.iterator();

        while (lServerIter.hasNext()) {
            LServerConfiguration tempLServer = lServerIter.next();
            String serverName = tempLServer.getServerName();
            if (serverName.equals(paramHandler.getInstanceName())) {
                throw new InstanceExistsException(Messages.getAll(
                        "error_instance_exists", new Object[] { serverName }));
            }
        }

        vserverId = vsysClient.createLServer(paramHandler.getInstanceName(),
                paramHandler.getVserverType(), paramHandler.getDiskImageId(),
                resolveValidNetworkId(paramHandler), paramHandler.getVMPool(),
                paramHandler.getStoragePool(), paramHandler.getCountCPU());

        paramHandler.setVserverId(vserverId);
        paramHandler.getIaasContext().clear();
        return vserverId;
    }

    /**
     * Destroys the VSERVER with the given ID - by doing that all data is lost.
     * 
     * 
     * @param paramHandler
     *            the parameter handler
     * @throws Exception
     */
    @Override
    public void destroyVServer(PropertyHandler paramHandler) throws Exception {
        LServerClient vserverClient = getLServerClient(paramHandler);
        vserverClient.destroy();
        paramHandler.getIaasContext().clear();
    }

    /**
     * Modifies a VSERVER for the given ID with the given values.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return
     * 
     * @throws Exception
     */
    @Override
    public FlowState modifyVServerAttributes(PropertyHandler paramHandler)
            throws Exception {
        LServerClient vserverClient = getLServerClient(paramHandler);
        LServerConfiguration configuration = vserverClient.getConfiguration();
        if (configuration == null) {
            throw new SuspendException(
                    "Error while reading server configuration");
        }
        boolean toBeModified = paramHandler.getCountCPU() != null
                && !paramHandler.getCountCPU().equals(
                        configuration.getNumOfCPU());

        if (toBeModified) {
            String status = vserverClient.getStatus();
            if (LServerStatus.RUNNING.equals(status)) {
                logger.debug(
                        "Stopping LServer {} to update changed attributes",
                        paramHandler.getVserverId());
                paramHandler.addVserverToBeStarted(paramHandler.getVserverId());
                vserverClient.stop();
                return FlowState.VSERVER_STOPPING_FOR_MODIFICATION;
            } else if (LServerStatus.STOPPING.equals(status)) {
                return FlowState.VSERVER_STOPPING_FOR_MODIFICATION;
            }
            logger.debug(
                    "Updating VServer {}: CPU_COUNT => {}, STATUS = {}",
                    new String[] { paramHandler.getVserverId(),
                            paramHandler.getCountCPU(), status });
            vserverClient.updateConfiguration(paramHandler.getCountCPU(), null);
            paramHandler.getIaasContext().clear();
            return FlowState.VSERVER_UPDATING;
        }
        logger.debug(
                "All attributes of LServer {} are up top date => nothing to do",
                paramHandler.getVserverId());
        return FlowState.FINISHED;
    }

    /**
     * Convert given oviss exception into a well designed suspend exception.
     * 
     * @param ex
     *            the exception
     * @return the converted platform exception
     */
    SuspendException getSuspendException(Throwable ex, String messageType) {
        if (ex instanceof RORException) { // Get real error cause
            if (ex.getCause() != null) {
                ex = ex.getCause();
            } else {
                String causeMessage = (ex.getMessage() != null) ? ex
                        .getMessage() : ex.getClass().getName();
                return new SuspendException(Messages.getAll(messageType,
                        causeMessage));
            }
        }
        // Map to platform exception
        String causeMessage = (ex.getMessage() != null) ? ex.getMessage() : ex
                .getClass().getName();
        return new SuspendException(Messages.getAll(messageType, causeMessage),
                ex);
    }

    /**
     * Starts the VServer associated with the VSYS with the given ID.
     * 
     * 
     * @param paramHandler
     *            the parameter handler
     * @throws Exception
     */

    @Override
    public boolean startVServer(PropertyHandler paramHandler) throws Exception {
        LServerClient vserverClient = getLServerClient(paramHandler);
        boolean starting = false;
        if (LServerStatus.STOPPED.equals(vserverClient.getStatus())) {
            logger.debug("startVServer(PropertyHandler) starting");
            vserverClient.start();
            starting = true;
        }
        return starting;
    }

    /**
     * Retrieves the current VServer status.
     * 
     * @param ph
     *            The parameter handler
     * @return the string representation of the current status
     * @throws Exception
     */
    @Override
    public String getVServerStatus(PropertyHandler ph) throws Exception {
        LServerClient vserverClient = getLServerClient(ph);
        String result = vserverClient.getStatus();
        return result;
    }

    /**
     * Stops the VServer associated with the VSYS with the given ID.
     * 
     * @param paramHandler
     *            the parameter handler
     * @throws Exception
     */

    @Override
    public void stopVServer(PropertyHandler paramHandler) throws Exception {
        LServerClient vserverClient = getLServerClient(paramHandler);
        if (LServerStatus.RUNNING.equals(vserverClient.getStatus())) {
            vserverClient.stop();
        }
    }

    /**
     * Checks if the VSYS is not changed/moved in the Fujitsu global cloud
     * platform.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return <code>true</code> if the VSYS ID is available; otherwise
     *         <code>false</code>
     * @throws Exception
     */

    @Override
    public boolean isVSysIdValid(PropertyHandler paramHandler) throws Exception {
        boolean valid = false;
        try {
            LPlatformClient vsysClient = getLPlatformClient(paramHandler);
            if (LPlatformStatus.ERROR.equals(vsysClient.getStatus())) {
                throw new IllegalStateException(); // TODO add localized text
            }
            valid = true;
        } catch (Exception e) {
            logger.error("Error while checking VSYS id status", e);
            SuspendException exception = getSuspendException(e,
                    "error_invalid_sysid");
            throw exception;
        }
        return valid;
    }

    /**
     * Checks if the configured network ID is present.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return <code>true</code> if the network ID is available otherwise
     *         <code>false</code>
     * @throws Exception
     */
    @Override
    public boolean isNetworkIdValid(PropertyHandler paramHandler)
            throws Exception {
        return resolveValidNetworkId(paramHandler) != null;
    }

    /**
     * Checks if the configured network ID or name can be resolved to a
     * deterministic network ID.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return the Id of the resolved network
     * @throws Exception
     */

    public String resolveValidNetworkId(PropertyHandler paramHandler)
            throws Exception {

        String id = paramHandler.getNetworkId();
        if (id != null && id.trim().length() == 0) {
            id = null;
        }

        try {
            LPlatformClient lPlatformClient = getLPlatformClient(paramHandler);
            VSystemConfiguration config = paramHandler.getIaasContext()
                    .getVSystemConfiguration();
            if (config == null) { // nothing cached
                config = lPlatformClient.getConfiguration();
                paramHandler.getIaasContext().add(config);
            }

            List<Network> networks = config.getNetworks();
            if (id == null) {
                if (networks != null && networks.size() == 1) {
                    // the only existing network is always deterministic
                    return networks.iterator().next().getId();
                }
                throw new SuspendException(Messages.getAll(
                        "error_invalid_networkid", new Object[] { id }));
            }
            String nameToId = null;
            boolean nameDeterministic = true;
            if (networks != null) {
                for (Network net : networks) {
                    if (id.equals(net.getId())) {
                        return id;
                    }
                    if (id.equals(net.getName())) {
                        if (nameToId == null) {
                            nameToId = net.getId();
                        } else {
                            nameDeterministic = false;
                        }
                    }
                }
            }
            if (nameToId != null) {
                if (nameDeterministic) {
                    return nameToId;
                } else {
                    throw new SuspendException(Messages.getAll(
                            "error_invalid_networkid", new Object[] { id }));
                }
            }
        } catch (Exception e) {
            logger.error("Error while validating configured networkId", e);
            SuspendException exception = getSuspendException(e,
                    "error_invalid_networkid");
            throw exception;
        }
        return null;
    }

    /**
     * Checks if the virtual ServerType exists for the disk image ID in the
     * Fujitsu global cloud platform.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return <code>true</code> if the ServerType is available; otherwise
     *         <code>false</code>
     * @throws Exception
     */
    @Override
    public boolean isServerTypeValid(PropertyHandler paramHandler)
            throws Exception {
        try {
            RORClient rorClient = getVdcClient(paramHandler);
            List<String> serverTypes = rorClient.listServerTypes();
            if (serverTypes != null
                    && serverTypes.contains(paramHandler.getVserverType())) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Error while checking VServer Type status", e);
            SuspendException exception = getSuspendException(e,
                    "error_invalid_servertype");
            throw exception;
        }
        return false;
    }

    @Override
    public boolean isVServerDestroyed(PropertyHandler paramHandler)
            throws Exception {
        // FIXME
        return true;
    }

    /**
     * Returns the network ID of the VServer.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return the network ID of the VServer
     * @throws Exception
     */

    public String getNetworkId(PropertyHandler paramHandler) throws Exception {
        // FIXME??
        // LPlatformClient vsysClient = getLPlatformClient(paramHandler);
        // String networkId =
        // vsysClient.getConfiguration().getVnets()[0].getNetworkId();
        throw new RuntimeException("Not implemented: getNetworkId()");
    }

    /**
     * Checks if the virtual server internal private IP exists in the Fujitsu
     * global cloud platform.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return the internal private IP if the IP is available; otherwise throws
     *         exception
     * @throws Exception
     */
    @Override
    public String getInternalIp(PropertyHandler paramHandler) throws Exception {
        LServerClient lserverClient = getLServerClient(paramHandler);
        String privateIP = lserverClient.getConfiguration().getPrivateIP();
        return privateIP;
    }

    /**
     * Find if the virtual server initial password exists in the Fujitsu global
     * cloud platform.
     * 
     * @param paramHandler
     *            the parameter handler
     * @return the initial password; otherwise throws exception
     * @throws Exception
     */
    @Override
    public String getVServerInitialPassword(PropertyHandler paramHandler)
            throws Exception {
        LServerClient vserverClient = getLServerClient(paramHandler);
        String password = vserverClient.getInitialPassword();
        return password;
    }

    /**
     * Retrieves the current VServer status and throws a SuspendException if the
     * VServer is in an error state.
     * 
     * @param ph
     *            The parameter handler
     * @return the string representation of the current status
     * @throws Exception
     */
    @Override
    public String getNonErrorVServerStatus(PropertyHandler paramHandler)
            throws Exception {
        final String status = this.getVServerStatus(paramHandler);
        boolean isErrorState = false;
        String errorCode = "";
        if (status != null) {
            switch (status) {
            case VServerStatus.ERROR:
                isErrorState = true;
                errorCode = "error_state_vserver";
                break;
            case VServerStatus.START_ERROR:
                isErrorState = true;
                errorCode = "error_failed_to_start_vserver";
                break;
            case VServerStatus.STOP_ERROR:
                isErrorState = true;
                errorCode = "error_failed_to_stop_vserver";
                break;
            case VServerStatus.UNEXPECTED_STOP:
                isErrorState = true;
                errorCode = "error_unexpected_stop_vserver";
                break;
            }
        }

        if (isErrorState) {
            throw new SuspendException(Messages.getAll(errorCode,
                    new Object[] { paramHandler.getVserverId() }));
        }

        return status;
    }
}
