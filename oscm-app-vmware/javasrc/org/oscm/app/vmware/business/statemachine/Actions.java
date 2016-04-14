package org.oscm.app.vmware.business.statemachine;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.oscm.app.v1_0.APPlatformServiceFactory;
import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.intf.APPlatformService;
import org.oscm.app.vmware.business.VM;
import org.oscm.app.vmware.business.VM.VMwareGuestSystemStatus;
import org.oscm.app.vmware.business.statemachine.api.StateMachineAction;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.i18n.Messages;
import org.oscm.app.vmware.remote.vmware.VMClientPool;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.LocalizableMessage;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.TaskFilterSpec;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.TaskReason;
import com.vmware.vim25.TaskReasonAlarm;
import com.vmware.vim25.TaskReasonSchedule;
import com.vmware.vim25.TaskReasonSystem;
import com.vmware.vim25.TaskReasonUser;

public class Actions {

    private static final Logger logger = LoggerFactory.getLogger(Actions.class);

    protected static final String FAILED = "failed";
    protected static final String SUCCESS = "success";
    protected static final String ERROR = "error";

    protected APPlatformService platformService;

    public Actions() {
        try {
            platformService = APPlatformServiceFactory.getInstance();
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    @StateMachineAction
    public String configureVM(String instanceId, ProvisioningSettings settings,
            InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            VM vm = new VM(vmClient, ph.getInstanceName());
            TaskInfo tInfo = vm.reconfigureVirtualMachine(ph);
            ph.setTask(tInfo.getKey());
            eventId = "configuring";
        } catch (Exception e) {
            logger.error("Failed to configure VM of instance " + instanceId, e);
            String message = Messages.get(ph.getLocale(), "error_configure_vm",
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

    
    
    /**
     * Synchronous activity. This is like selecting the shutdown option in the
     * operating system.
     * 
     * @return eventId
     */
    @StateMachineAction
    public String shutdownVM(String instanceId, ProvisioningSettings settings,
            InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            VM vm = new VM(vmClient, ph.getInstanceName());
            vm.stop(false);
            eventId = "stopped";
        } catch (Exception e) {
            logger.error("Failed to shutdown VM of instance " + instanceId, e);
            String message = Messages.get(ph.getLocale(), "error_shutdown_vm",
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

    /**
     * Asynchronous activity. This is like pressing the power button to turn off
     * the machine.
     * 
     * @return eventId
     */
    @StateMachineAction
    public String powerOffVM(String instanceId, ProvisioningSettings settings,
            InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            VM vm = new VM(vmClient, ph.getInstanceName());
            TaskInfo tInfo = vm.stop(true);
            ph.setTask(tInfo.getKey());
            eventId = "stopping";
        } catch (Exception e) {
            logger.error("Failed to power off VM of instance " + instanceId, e);
            String message = Messages.get(ph.getLocale(), "error_poweroff_vm",
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

    @StateMachineAction
    public String startVM(String instanceId, ProvisioningSettings settings,
            InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            VM vm = new VM(vmClient, ph.getInstanceName());
            TaskInfo tInfo = vm.start();
            ph.setTask(tInfo.getKey());
            eventId = "starting";
        } catch (Exception e) {
            logger.error("Failed to start VM of instance " + instanceId, e);
            String message = Messages.get(ph.getLocale(), "error_start_vm",
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

    
    @StateMachineAction
    public String checkVMRunning(String instanceId,
            ProvisioningSettings settings, InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;
        String instanceName = "";

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            instanceName = ph.getInstanceName();
            VM vm = new VM(vmClient, instanceName);
            VMwareGuestSystemStatus guestStatus = vm.getState();
            eventId = guestStatus == VMwareGuestSystemStatus.GUEST_READY
                    ? "running" : "not running";
        } catch (Exception e) {
            logger.error("Failed to check VM running state of instance "
                    + instanceId, e);
            String message = Messages.get(ph.getLocale(),
                    "error_check_vm_running",
                    new Object[] { instanceName, instanceId });
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

    @StateMachineAction
    public String finalizeProvisioning(String instanceId,
            ProvisioningSettings settings, InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            VM vm = new VM(vmClient, ph.getInstanceName());
            String accessInfo = vm.generateAccessInfo(ph);
            ph.setAccessInfo(accessInfo);
            result.setAccessInfo(accessInfo);
            result.setIsReady(true);
            eventId = SUCCESS;
        } catch (Exception e) {
            logger.error("Failed to set access info for instance " + instanceId,
                    e);
            String message = Messages.get(ph.getLocale(),
                    "error_finalize_provisioning", new Object[] { instanceId });
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

    @StateMachineAction
    public String finish(String instanceId, ProvisioningSettings settings,
            InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        result.setIsReady(true);
        return SUCCESS;
    }

    @StateMachineAction
    public String throwSuspendException(String instanceId,
            ProvisioningSettings settings, InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        return FAILED;
    }

    @StateMachineAction
    public String inspectTaskResult(String instanceId,
            ProvisioningSettings settings, InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = "";

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            TaskInfo taskInfo = getTaskInfo(vmClient, ph);
            TaskInfoState taskState = (taskInfo != null) ? taskInfo.getState()
                    : TaskInfoState.SUCCESS;

            switch (taskState) {
            case SUCCESS:
                eventId = SUCCESS;
                break;
            case ERROR:
                eventId = ERROR;
                String errorMessage = "";
                if (taskInfo != null && taskInfo.getError() != null) {
                    errorMessage = taskInfo.getError().getLocalizedMessage();
                    logger.error(errorMessage);

                    if (taskInfo.getError().getFault() != null) {
                        List<LocalizableMessage> errorMessageList2 = taskInfo
                                .getError().getFault().getFaultMessage();
                        for (LocalizableMessage msg : errorMessageList2) {
                            logger.error(msg.getMessage());
                            errorMessage += msg.getMessage();
                        }

                        if (taskInfo.getError().getFault()
                                .getFaultCause() != null) {
                            String errorMsg = taskInfo.getError().getFault()
                                    .getFaultCause().getLocalizedMessage();
                            logger.error(errorMsg);
                            errorMessage += errorMsg;
                        }
                    }
                }
                ph.setSetting(VMPropertyHandler.SM_ERROR_MESSAGE, errorMessage);
                break;
            case QUEUED:
                eventId = "queued";
                break;
            case RUNNING:
                eventId = "running";
                break;
            }
        } catch (Exception e) {
            logger.error("Failed to check task execution result for instance "
                    + instanceId, e);

            String taskKey = ph.getServiceSetting(VMPropertyHandler.TASK_KEY);

            String message = Messages.get(ph.getLocale(),
                    "error_check_task_result",
                    new Object[] { instanceId, taskKey });
            ph.setSetting(VMPropertyHandler.SM_ERROR_MESSAGE, message);
            eventId = ERROR;
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

    protected TaskInfo getTaskInfo(VMwareClient vmw,
            VMPropertyHandler paramHandler) throws Exception {
        String instanceId = paramHandler.getInstanceName();
        String taskKey = paramHandler
                .getServiceSetting(VMPropertyHandler.TASK_KEY);
        logger.debug("VM: " + instanceId + " taskId: " + taskKey);

        if (taskKey == null || "".equals(taskKey)) {
            return null;
        }

        ManagedObjectReference taskManagerRef = vmw.getConnection()
                .getServiceContent().getTaskManager();
        TaskFilterSpec taskfilter = new TaskFilterSpec();
        ManagedObjectReference taskHistoryCollector = vmw.getConnection()
                .getService()
                .createCollectorForTasks(taskManagerRef, taskfilter);
        vmw.getConnection().getService().resetCollector(taskHistoryCollector);
        vmw.getConnection().getService().readNextTasks(taskHistoryCollector,
                100);
        List<TaskInfo> taskList = vmw.getConnection().getService()
                .readPreviousTasks(taskHistoryCollector, 100);

        if (taskList != null) {
            for (TaskInfo taskInfo : taskList) {
                if (taskInfo != null && taskKey.equals(taskInfo.getKey())) {
                    logTaskInfo(taskInfo);
                    return taskInfo;
                }
            }
        }

        logger.error(
                "Task not found. VM: " + instanceId + " taskId: " + taskKey);
        return null;
    }

    private void logTaskInfo(TaskInfo info) {
        String key = info.getKey();
        String name = info.getName();
        String target = info.getEntityName();
        TaskInfoState state = info.getState();
        Integer progress = info.getProgress();
        if (state == TaskInfoState.SUCCESS) {
            progress = Integer.valueOf(100);
        } else if (progress == null) {
            progress = Integer.valueOf(0);
        }
        LocalizableMessage desc = info.getDescription();
        String description = desc != null ? desc.getMessage() : "";
        TaskReason reason = info.getReason();
        String initiatedBy = "";
        if (reason != null) {
            if (reason instanceof TaskReasonUser) {
                initiatedBy = ((TaskReasonUser) reason).getUserName();
            } else if (reason instanceof TaskReasonSystem) {
                initiatedBy = "System";
            } else if (reason instanceof TaskReasonSchedule) {
                initiatedBy = ((TaskReasonSchedule) reason).getName();
            } else if (reason instanceof TaskReasonAlarm) {
                initiatedBy = ((TaskReasonAlarm) reason).getAlarmName();
            }
        }

        XMLGregorianCalendar queueT = info.getQueueTime();
        String queueTime = queueT != null
                ? queueT.toGregorianCalendar().getTime().toString() : "";
        XMLGregorianCalendar startT = info.getStartTime();
        String startTime = startT != null
                ? startT.toGregorianCalendar().getTime().toString() : "";
        XMLGregorianCalendar completeT = info.getCompleteTime();
        String completeTime = completeT != null
                ? completeT.toGregorianCalendar().getTime().toString() : "";
        logger.debug(key + " name: " + name + " target: " + target + " state: "
                + state + " progress: " + progress + "% description: "
                + description + " initiated: " + initiatedBy + " queue-time: "
                + queueTime + " start-time: " + startTime + " complete-time: "
                + completeTime);
    }

}
