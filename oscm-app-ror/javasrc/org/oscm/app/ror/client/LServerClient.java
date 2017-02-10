/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
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
import org.oscm.app.ror.data.LOperation;
import org.oscm.app.ror.data.LParameter;
import org.oscm.app.ror.data.LServerConfiguration;
import org.oscm.app.ror.exceptions.RORException;

public class LServerClient {

    private final LPlatformClient lplatformClient;
    private HashMap<String, String> values = new HashMap<String, String>();

    /**
     * @param lplatformClient
     * @param lserverId
     */
    public LServerClient(LPlatformClient lplatformClient, String lserverId) {
        this.lplatformClient = lplatformClient;
        this.values.putAll(lplatformClient.getRequestParameters());
        this.values.put(LParameter.LSERVER_ID, lserverId);
    }

    public HashMap<String, String> getRequestParameters() {
        return new HashMap<String, String>(values);
    }

    /**
     * @throws RORException
     * 
     */
    public void destroy() throws IaasException {
        executeLServerAction(LOperation.DESTROY_LSERVER);
        // FIXME error handling
    }

    /**
     * @return the password
     * @throws RORException
     */
    public String getInitialPassword() throws IaasException {
        XMLConfiguration result = executeLServerAction(LOperation.GET_LSERVER_INIT_PASSWD);
        return result.getString("initialPassword");
    }

    /**
     * @return the status
     * @throws RORException
     */
    public String getStatus() throws IaasException {
        XMLConfiguration result = executeLServerAction(LOperation.GET_LSERVER_STATUS);
        return result.getString("lserverStatus");
    }

    /**
     * @return the configuration
     * @throws RORException
     */
    public LServerConfiguration getConfiguration() throws IaasException {
        XMLConfiguration result = executeLServerAction(LOperation.GET_LSERVER_CONFIG);
        return new LServerConfiguration(result.configurationAt("lserver"));
    }

    public void updateConfiguration(String countCPU, String memorySize)
            throws IaasException {
        HashMap<String, String> request = this.getRequestParameters();
        request.put(LParameter.ACTION, LOperation.UPDATE_LSERVER_CONFIG);
        boolean execute = false;
        if (countCPU != null) {
            request.put(LParameter.CPU_NUMBER, countCPU);
            execute = true;
        }
        if (memorySize != null) {
            request.put(LParameter.MEMORY_SIZE, memorySize);
            execute = true;
        }
        if (!execute) {
            throw new RORException("Nothing to modify");
        }
        XMLConfiguration result = lplatformClient.getVdcClient().execute(
                request);
        System.out.println(result.toString());
        // FIXME error handling
    }

    /**
     * @throws RORException
     * 
     */
    public void start() throws IaasException {
        executeLServerAction(LOperation.START_LSERVER);
    }

    /**
     * @throws RORException
     */
    public void stop() throws IaasException {
        executeLServerAction(LOperation.STOP_LSERVER);
    }

    /**
     * @param name
     * @param imagePool
     * @param comment
     * @throws RORException
     */
    public void createImage(String name, String imagePool, String comment)
            throws IaasException {
        HashMap<String, String> request = this.getRequestParameters();
        request.put(LParameter.NAME, name);
        request.put(LParameter.IMAGE_POOL, imagePool);
        request.put(LParameter.COMMENT, comment);
        request.put(LParameter.ACTION, LOperation.CREATE_IMAGE);
        lplatformClient.getVdcClient().execute(request);
    }

    XMLConfiguration executeLServerAction(String action) throws IaasException {
        List<String> emptyParams = new ArrayList<String>();
        if (isEmpty(this.getRequestParameters().get(LParameter.LPLATFORM_ID))) {
            emptyParams.add(LParameter.LPLATFORM_ID);
        }
        if (isEmpty(this.getRequestParameters().get(LParameter.LSERVER_ID))) {
            emptyParams.add(LParameter.LSERVER_ID);
        }
        if (!emptyParams.isEmpty()) {
            throw new MissingParameterException(action, emptyParams);
        }

        HashMap<String, String> request = this.getRequestParameters();
        request.put(LParameter.ACTION, action);
        XMLConfiguration result = lplatformClient.getVdcClient().execute(
                request);
        return result;
    }

    boolean isEmpty(String s) {
        return (s == null || s.trim().length() == 0);
    }
}
