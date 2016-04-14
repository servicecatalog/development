package org.oscm.app.vmware.business.statemachine;

import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.vmware.business.VM;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.business.statemachine.api.StateMachineAction;
import org.oscm.app.vmware.i18n.Messages;
import org.oscm.app.vmware.remote.vmware.VMClientPool;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.oscm.app.vmware.remote.vmware.VMwareClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.TaskInfo;

public class DeleteActions extends Actions {

    private static final Logger logger = LoggerFactory
            .getLogger(DeleteActions.class);

    @StateMachineAction
    public String deleteVM(String instanceId, ProvisioningSettings settings,
            InstanceStatus result) {
        logger.debug("instanceId: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
        
//        try (VMwareClient vmClient = vmwFactory.getInstance(ph);) {
//            vmClient.connect();
            VM vm = new VM(vmClient, ph.getInstanceName());
            TaskInfo tInfo = vm.delete();
            ph.setTask(tInfo.getKey());
            eventId = "deleting";
        } catch (Exception e) {
            logger.error("Failed to delete VM for instance " + instanceId, e);
            String message = Messages.get(ph.getLocale(), "error_delete_vm",
                    new Object[] { instanceId });
            ph.setSetting(VMPropertyHandler.SM_ERROR_MESSAGE, message);
        }
        finally{
        	if( vmClient != null ){
        	try {
				VMClientPool.getInstance().getPool().returnObject(vcenter, vmClient);
			} catch (Exception e) {
	            logger.error("Failed to return VMware client into pool", e);
			}
        	}
        }

        return eventId;
    }
}
