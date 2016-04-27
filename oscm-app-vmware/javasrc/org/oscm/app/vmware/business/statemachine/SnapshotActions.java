/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2016 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 26.04.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.business.statemachine;

import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.business.statemachine.api.StateMachineAction;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.oscm.app.vmware.remote.vmware.VMwareClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.TaskInfo;

/**
 * @author kulle
 *
 */
public class SnapshotActions extends Actions {

    private static final Logger logger = LoggerFactory
            .getLogger(SnapshotActions.class);

    private static final String SNAPSHOT_NAME = "backup";

    private static final String EVENT_ERROR = "error";
    private static final String EVENT_RUN = "run";

    @StateMachineAction
    public String createSnapshot(String instanceId,
            ProvisioningSettings settings,
            @SuppressWarnings("unused") InstanceStatus result) {

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        try (VMwareClient client = new VMwareClientFactory("en")
                .getInstance(ph);) {

            ManagedObjectReference vm = getVirtualMachine(ph, client);
            String description = "Instance ID: " + instanceId;
            boolean dumpMemory = true;
            boolean quiesceFileSystem = true;
            ManagedObjectReference task = client.getService()
                    .createSnapshotTask(vm, SNAPSHOT_NAME, description,
                            dumpMemory, quiesceFileSystem);
            ph.setTask(retrieveTaskInfoKey(client, task));
            return EVENT_RUN;
        } catch (Exception e) {
            logger.error("Failed to create snapshot for instance " + instanceId,
                    e);
            ph.setSetting(VMPropertyHandler.SM_ERROR_MESSAGE,
                    "Failed to create snapshot for instance " + instanceId);
            return EVENT_ERROR;
        }
    }

    private ManagedObjectReference getVirtualMachine(VMPropertyHandler ph,
            VMwareClient client) throws InvalidPropertyFaultMsg,
            RuntimeFaultFaultMsg, APPlatformException {

        ManagedObjectReference vm = client.getServiceUtil().getDecendentMoRef(
                null, "VirtualMachine", ph.getInstanceName());
        return vm;
    }

    private String retrieveTaskInfoKey(VMwareClient client,
            ManagedObjectReference task) throws Exception {

        return ((TaskInfo) client.getServiceUtil().getDynamicProperty(task,
                "info")).getKey();
    }

}
