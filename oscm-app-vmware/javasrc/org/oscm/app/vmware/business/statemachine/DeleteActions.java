package org.oscm.app.vmware.business.statemachine;

import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.vmware.business.VM;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.business.statemachine.api.StateMachineAction;
import org.oscm.app.vmware.i18n.Messages;
import org.oscm.app.vmware.remote.vmware.VMClientPool;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.TaskInfo;

public class DeleteActions extends Actions {

    private static final Logger logger = LoggerFactory
            .getLogger(DeleteActions.class);

    private static final String EVENT_DELETING = "deleting";

    @StateMachineAction
    public String deleteVM(String instanceId, ProvisioningSettings settings,
            @SuppressWarnings("unused") InstanceStatus result) {

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph
                .getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        try (VMwareClient vmClient = VMClientPool.getInstance().getPool()
                .borrowObject(vcenter);) {
            VM vm = new VM(vmClient, ph.getInstanceName());
            TaskInfo taskInfo = vm.delete();
            ph.setTask(taskInfo.getKey());
            return EVENT_DELETING;
        } catch (Exception e) {
            logger.error("Failed to delete VM for instance " + instanceId, e);
            String message = Messages.get(ph.getLocale(), "error_delete_vm",
                    new Object[] { instanceId });
            ph.setSetting(VMPropertyHandler.SM_ERROR_MESSAGE, message);
            return EVENT_FAILED;
        }
    }
}
