/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Creation Date: 2012-11-08                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;

import org.oscm.app.iaas.exceptions.IaasException;
import org.oscm.app.iaas.exceptions.MissingParameterException;
import org.oscm.app.iaas.i18n.Messages;
import org.oscm.app.ror.data.LOperation;
import org.oscm.app.ror.data.LParameter;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.ror.exceptions.RORException;
import org.oscm.app.v2_0.exceptions.SuspendException;

public class LPlatformClient {

    private final RORClient vdcClient;
    private final String lplatformId;
    private final HashMap<String, String> values = new HashMap<String, String>();

    /**
     * @param vdcClient
     * @param lplatformId
     */
    public LPlatformClient(RORClient vdcClient, String lplatformId) {
        this.vdcClient = vdcClient;
        this.lplatformId = lplatformId;
        this.values.putAll(vdcClient.getBasicParameters());
        this.values.put(LParameter.LPLATFORM_ID, lplatformId);
    }

    public HashMap<String, String> getRequestParameters() {
        return new HashMap<String, String>(values);
    }

    /**
     * @param instanceName
     * @param serverType
     * @param diskImageId
     * @param controlNetworkId
     * @param vmPool
     * @param storagePool
     * @param countCPU
     * @return the id for the created LServer
     * @throws RORException
     */
    public String createLServer(String instanceName, String serverType,
            String diskImageId, String controlNetworkId, String vmPool,
            String storagePool, String countCPU) throws IaasException {
        List<String> emptyParams = new ArrayList<String>();
        if (isEmpty(lplatformId)) {
            emptyParams.add(LParameter.LPLATFORM_ID);
        }
        if (isEmpty(instanceName)) {
            emptyParams.add(LParameter.LSERVER_NAME);
        }
        // TODO other checks?
        if (!emptyParams.isEmpty()) {
            throw new MissingParameterException(LOperation.CREATE_LSERVER,
                    emptyParams);
        }
        HashMap<String, String> request = this.getRequestParameters();
        request.put(LParameter.ACTION, LOperation.CREATE_LSERVER);
        request.put(LParameter.LSERVER_NAME, instanceName);
        request.put(LParameter.LSERVER_TYPE, serverType);
        request.put(LParameter.DISKIMAGE_ID, diskImageId);
        request.put(LParameter.CONTROL_NETWORK_ID, controlNetworkId);
        if (vmPool != null) {
            request.put(LParameter.POOL, vmPool);
        } else {
            request.put(LParameter.POOL, "VMHostPool");
        }
        if (storagePool != null && storagePool.trim().length() > 0) {
            request.put(LParameter.STORAGE_POOL, storagePool);
        } else {
            request.put(LParameter.STORAGE_POOL, "StoragePool");
        }
        if (countCPU != null) {
            request.put(LParameter.CPU_NUMBER, countCPU);
        }
        XMLConfiguration result = vdcClient.execute(request);
        return result.getString(LParameter.LSERVER_ID);
    }

    /**
     * @return the status of the LPlatform
     * @throws SuspendException
     * @throws RORException
     */
    public String getStatus() throws IaasException, SuspendException {
        XMLConfiguration result = executeLPlatformAction(LOperation.GET_LPLATFORM_STATUS);
        return result.getString("lplatformStatus");
    }

    public LPlatformConfiguration getConfiguration() throws IaasException,
            SuspendException {
        XMLConfiguration result = executeLPlatformAction(LOperation.GET_LPLATFORM_CONFIG);
        return new LPlatformConfiguration(result.configurationAt("lplatform"));
    }

    public void destroy() throws IaasException, SuspendException {
        executeLPlatformAction(LOperation.DESTROY_LPLATFORM);
        // FIXME error handling
    }

    public void stopAllServers() throws IaasException, SuspendException {
        executeLPlatformAction(LOperation.STOP_LPLATFORM);
        // FIXME error handling
    }

    public void startAllServers() throws IaasException, SuspendException {
        executeLPlatformAction(LOperation.START_LPLATFORM);
        // FIXME error handling
    }

    public RORClient getVdcClient() {
        return vdcClient;
    }

    boolean isEmpty(String s) {
        return (s == null || s.trim().length() == 0);
    }

    XMLConfiguration executeLPlatformAction(String action)
            throws IaasException, SuspendException {
        if (isEmpty(lplatformId)) {
            throw new SuspendException(Messages.getAll("error_missing_sysid"));
        }
        HashMap<String, String> request = this.getRequestParameters();
        request.put(LParameter.ACTION, action);
        XMLConfiguration result = vdcClient.execute(request);
        return result;
    }
}
