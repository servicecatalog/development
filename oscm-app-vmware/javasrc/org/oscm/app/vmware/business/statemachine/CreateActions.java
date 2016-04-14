package org.oscm.app.vmware.business.statemachine;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.vmware.business.Controller;
import org.oscm.app.vmware.business.VM;
import org.oscm.app.vmware.business.VMPropertyHandler;
import org.oscm.app.vmware.business.statemachine.api.StateMachineAction;
import org.oscm.app.vmware.i18n.Messages;
import org.oscm.app.vmware.remote.vmware.VMClientPool;
import org.oscm.app.vmware.remote.vmware.VMwareClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vim25.TaskInfo;

public class CreateActions extends Actions {

    private static final Logger logger = LoggerFactory
            .getLogger(CreateActions.class);

    @StateMachineAction
    public String createInstanceName(String instanceId,
            ProvisioningSettings settings, InstanceStatus result)
                    throws Exception {
        logger.debug("instance: " + instanceId);

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String instanceName = ph.getInstanceName();
        String regex = ph
                .getServiceSetting(VMPropertyHandler.TS_INSTANCENAME_PATTERN);
        if (regex != null) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(instanceName);
            if (!m.matches()) {
                logger.error("Validation error on instance name: ["
                        + instanceName + "/" + regex + "]");
                throw new APPlatformException(
                        Messages.getAll("error_invalid_name",
                                new Object[] { instanceName, regex }));
            }
        }

        return SUCCESS;
    }

    @StateMachineAction
    public String createVM(String instanceId, ProvisioningSettings settings,
            InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;
        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            VM template = new VM(vmClient, ph.getTemplateName());
            TaskInfo tInfo = template.cloneVM(ph);
            ph.setTask(tInfo.getKey());
            eventId = "creating";
        } catch (Exception e) {
            logger.error("Failed to create VM of instance " + instanceId, e);
            String message = Messages.get(ph.getLocale(), "error_create_vm",
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
    public String executeScript(String instanceId,
            ProvisioningSettings settings, InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String vcenter = ph.getServiceSetting(VMPropertyHandler.TS_TARGET_VCENTER_SERVER);
        VMwareClient vmClient = null;
        try{
        	vmClient = VMClientPool.getInstance().getPool().borrowObject(vcenter);
            if (ph.getServiceSetting(VMPropertyHandler.TS_SCRIPT_URL) != null) {
                VM vm = new VM(vmClient, ph.getInstanceName());
                vm.runScript(ph);
            }
            eventId = SUCCESS;
        } catch (Exception e) {
            logger.error("Failed to execute script of instance " + instanceId,
                    e);
            String message = Messages.get(ph.getLocale(),
                    "error_execute_script", new Object[] { instanceId });
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
    public String suspendAfterCreation(String instanceId,
            ProvisioningSettings settings, InstanceStatus result) {
        logger.debug("instance: " + instanceId);
        String eventId = FAILED;

        VMPropertyHandler ph = new VMPropertyHandler(settings);
        String mailRecipient = ph
                .getServiceSetting(VMPropertyHandler.TS_MAIL_FOR_COMPLETION);
        if (mailRecipient == null) {
            logger.debug("mailRecipient is not defined.");
            return SUCCESS;
        }

        try {
            sendEmail(ph, instanceId, mailRecipient);
            result.setRunWithTimer(false);
            eventId = SUCCESS;
        } catch (Exception e) {
            logger.error("Failed to pause after creating the VM instance "
                    + instanceId, e);
            String message = Messages.get(ph.getLocale(),
                    "error_pause_after_creation", new Object[] { instanceId });
            ph.setSetting(VMPropertyHandler.SM_ERROR_MESSAGE, message);
        }

        return eventId;
    }

    private void sendEmail(VMPropertyHandler paramHandler, String instanceId,
            String mailRecipient) throws Exception {
        logger.debug("instanceId: " + instanceId + " mailRecipient: "
                + mailRecipient);
        StringBuffer eventLink = new StringBuffer(
                platformService.getEventServiceUrl());
        eventLink.append("?sid=")
                .append(URLEncoder.encode(instanceId, "UTF-8"));
        eventLink.append("&cid=").append(Controller.ID);
        eventLink.append("&command=finish");
        String subject = Messages.get(paramHandler.getSettings().getLocale(),
                "mail_pause_after_creation.subject",
                new Object[] { paramHandler.getInstanceName() });
        String details = paramHandler.getConfigurationAsString(
                paramHandler.getSettings().getLocale());
        details += paramHandler.getResponsibleUserAsString(
                paramHandler.getSettings().getLocale());
        String text = Messages.get(paramHandler.getSettings().getLocale(),
                "mail_pause_after_creation.text",
                new Object[] { paramHandler.getInstanceName(),
                        paramHandler.getServiceSetting(
                                VMPropertyHandler.REQUESTING_USER),
                        details, eventLink.toString() });

        platformService.sendMail(Collections.singletonList(mailRecipient),
                subject, text);
    }
}
