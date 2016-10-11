/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 2016/10/06                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.oscm.app.openstack.controller.PropertyHandler;
import org.oscm.app.openstack.data.Server;
import org.oscm.app.openstack.exceptions.HeatException;
import org.oscm.app.openstack.exceptions.NovaException;
import org.oscm.app.openstack.exceptions.OpenStackConnectionException;
import org.oscm.app.openstack.i18n.Messages;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.exceptions.InstanceNotAliveException;

/**
 * @author tateiwamext
 *
 */
public class NovaProcessor {

    private NovaClient createNovaClient(PropertyHandler ph)
            throws APPlatformException, NovaException {
        OpenStackConnection connection = new OpenStackConnection(
                ph.getKeystoneUrl());
        KeystoneClient client = new KeystoneClient(connection);
        try {
            client.authenticate(ph.getUserName(), ph.getPassword(),
                    ph.getDomainName(), ph.getTenantId());
        } catch (OpenStackConnectionException ex) {
            throw new NovaException(
                    "Failed to connect to Heat: " + ex.getMessage(),
                    ex.getResponseCode());
        }
        return new NovaClient(connection);
    }

    private HeatClient createHeatClient(PropertyHandler ph)
            throws HeatException, APPlatformException {
        OpenStackConnection connection = new OpenStackConnection(
                ph.getKeystoneUrl());
        KeystoneClient client = new KeystoneClient(connection);
        try {
            client.authenticate(ph.getUserName(), ph.getPassword(),
                    ph.getDomainName(), ph.getTenantId());
        } catch (OpenStackConnectionException ex) {
            throw new HeatException(
                    "Failed to connect to Heat: " + ex.getMessage(),
                    ex.getResponseCode());
        }
        return new HeatClient(connection);
    }

    /**
     * Start servers which are in Stack. The stack is identified by its name.
     *
     * @param ph
     * @throws HeatException,
     *             APPlatformException, NovaException
     */
    public HashMap<String, Boolean> startInstances(PropertyHandler ph)
            throws HeatException, APPlatformException, NovaException {
        List<String> serverIds = createHeatClient(ph)
                .getServerIds(ph.getStackName());
        HashMap<String, Boolean> operationStatuses = new HashMap<String, Boolean>();
        if (serverIds.size() == 0) {
            throw new InstanceNotAliveException(Messages
                    .getAll("error_starting_failed_instance_not_found"));
        }

        for (String id : serverIds) {
            Boolean result = createNovaClient(ph).startServer(ph, id);
            operationStatuses.put(id, result);
        }
        return operationStatuses;
    }

    /**
     * Stop servers which are in Stack. The stack is identified by its name.
     * 
     * @param ph
     * @return The HashMap of server ID and status of execution
     * @throws HeatException
     * @throws APPlatformException
     * @throws NovaException
     */
    public HashMap<String, Boolean> stopInstances(PropertyHandler ph)
            throws HeatException, APPlatformException, NovaException {
        List<String> serverIds = createHeatClient(ph)
                .getServerIds(ph.getStackName());
        HashMap<String, Boolean> operationStatuses = new HashMap<String, Boolean>();
        if (serverIds.size() == 0) {
            throw new InstanceNotAliveException(Messages
                    .getAll("error_stopping_failed_instance_not_found"));
        }

        for (String id : serverIds) {
            Boolean result = createNovaClient(ph).stopServer(ph, id);
            operationStatuses.put(id, result);
        }
        return operationStatuses;
    }

    public List<Server> getServersDetails(PropertyHandler ph)
            throws HeatException, APPlatformException, NovaException {
        List<String> serverIds = createHeatClient(ph)
                .getServerIds(ph.getStackName());
        List<Server> servers = new ArrayList<Server>();
        if (serverIds.size() == 0) {
            throw new InstanceNotAliveException(Messages.getAll(
                    "error_check_servers_status_failed_instance_not_found",
                    ph.getStackName()));
        }

        for (String id : serverIds) {
            Server server = createNovaClient(ph).getServerDetails(ph, id);
            servers.add(server);
        }
        return servers;
    }
}
