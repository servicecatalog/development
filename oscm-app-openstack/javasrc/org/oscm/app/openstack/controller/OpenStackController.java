/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  OpenStack controller implementation for the 
 *  Asynchronous Provisioning Platform (APP)
 *       
 *  Creation Date: 2013-11-29                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.openstack.controller;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.oscm.app.openstack.NovaProcessor;
import org.oscm.app.openstack.data.FlowState;
import org.oscm.app.openstack.i18n.Messages;
import org.oscm.app.v2_0.APPlatformServiceFactory;
import org.oscm.app.v2_0.data.Context;
import org.oscm.app.v2_0.data.ControllerSettings;
import org.oscm.app.v2_0.data.InstanceDescription;
import org.oscm.app.v2_0.data.InstanceStatus;
import org.oscm.app.v2_0.data.InstanceStatusUsers;
import org.oscm.app.v2_0.data.LocalizedText;
import org.oscm.app.v2_0.data.OperationParameter;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.oscm.app.v2_0.data.ServiceUser;
import org.oscm.app.v2_0.exceptions.*;
import org.oscm.app.v2_0.intf.APPlatformController;
import org.oscm.app.v2_0.intf.APPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an OpenStack service controller based on the Asynchronous
 * Provisioning Platform (APP).
 * <p>
 * Whenever an application instance is to be created, updated, or deleted, the
 * corresponding method of the controller is called by APP. As long as the
 * provisioning operation is not finished, the controller returns the overall
 * instance status as "not ready". APP thus continues to poll the status at
 * regular intervals (<code>getInstanceStatus</code> method) until the instance
 * is reported as "ready".
 * <p>
 * The controller methods for creating, updating, and deleting instances set
 * their own, internal status. This status is evaluated and handled by a
 * dispatcher, which is invoked at regular intervals by APP through the
 * <code>getInstanceStatus</code> method. The dispatcher sets the next internal
 * status and returns the corresponding overall instance status to APP.
 */
@Stateless(mappedName = "bss/app/controller/" + OpenStackController.ID)
@Remote(APPlatformController.class)
public class OpenStackController extends ProvisioningValidator
        implements APPlatformController {

    public static final String ID = "ess.openstack";
    private static final int SERVERS_NUMBER_CANNOT_BE_CHECKED = 0;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OpenStackController.class);

    // Reference to an APPlatformService instance
    private APPlatformService platformService;

    /**
     * Retrieves an <code>APPlatformService</code> instance.
     * <p>
     * The <code>APPlatformService</code> provides helper methods by which the
     * service controller can access common APP utilities, for example, send
     * emails or lock application instances.
     */
    @PostConstruct
    public void initialize() {
        platformService = APPlatformServiceFactory.getInstance();
    }

    /**
     * Starts the creation of an application instance and returns the instance
     * ID.
     * <p>
     * The internal status <code>CREATION_REQUESTED</code> is stored as a
     * controller configuration setting. It is evaluated and handled by the
     * status dispatcher, which is invoked at regular intervals by APP through
     * the <code>getInstanceStatus</code> method.
     * 
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @return an <code>InstanceDescription</code> instance describing the
     *         application instance
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceDescription createInstance(ProvisioningSettings settings)
            throws APPlatformException {

        try {
            PropertyHandler ph = new PropertyHandler(settings);
            validateStackName(ph);
            ph.setState(FlowState.CREATION_REQUESTED);

            // Return generated instance information
            InstanceDescription id = new InstanceDescription();
            id.setInstanceId("stack-" + UUID.randomUUID().toString());
            id.setBaseUrl("baseurl");
            id.setChangedParameters(settings.getParameters());
            id.setChangedAttributes(settings.getAttributes());
            LOGGER.info("createInstance({})", LogAndExceptionConverter
                    .getLogText(id.getInstanceId(), settings));
            return id;
        } catch (Exception t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.CREATION);
        }
    }

    /**
     * Starts the deletion of an application instance.
     * <p>
     * The internal status <code>DELETION_REQUESTED</code> is stored as a
     * controller configuration setting. It is evaluated and handled by the
     * status dispatcher, which is invoked at regular intervals by APP through
     * the <code>getInstanceStatus</code> method.
     * 
     * @param instanceId
     *            the ID of the application instance to be deleted
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @return an <code>InstanceStatus</code> instance with the overall status
     *         of the application instance
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus deleteInstance(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        LOGGER.info("deleteInstance({})",
                LogAndExceptionConverter.getLogText(instanceId, settings));
        try {
            PropertyHandler ph = new PropertyHandler(settings);
            ph.setState(FlowState.DELETION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(settings.getParameters());
            result.setChangedAttributes(settings.getAttributes());
            return result;
        } catch (Exception t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.DELETION);
        }
    }

    /**
     * Starts the modification of an application instance.
     * <p>
     * The internal status <code>MODIFICATION_REQUESTED</code> is stored as a
     * controller configuration setting. It is evaluated and handled by the
     * status dispatcher, which is invoked at regular intervals by APP through
     * the <code>getInstanceStatus</code> method.
     * 
     * @param instanceId
     *            the ID of the application instance to be modified
     * @param currentSettings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            current service parameters and configuration settings
     * @param newSettings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            modified service parameters and configuration settings
     * @return an <code>InstanceStatus</code> instance with the overall status
     *         of the application instance
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus modifyInstance(String instanceId,
            ProvisioningSettings currentSettings,
            ProvisioningSettings newSettings) throws APPlatformException {
        LOGGER.info("modifyInstance({})", LogAndExceptionConverter
                .getLogText(instanceId, currentSettings));
        try {
            newSettings.getParameters().put(PropertyHandler.STACK_NAME,
                    currentSettings.getParameters()
                            .get(PropertyHandler.STACK_NAME));
            PropertyHandler ph = new PropertyHandler(newSettings);
            ph.setState(FlowState.MODIFICATION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(newSettings.getParameters());
            result.setChangedAttributes(newSettings.getAttributes());
            return result;
        } catch (Exception t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.MODIFICATION);
        }
    }

    /**
     * Returns the current overall status of the application instance.
     * <p>
     * For retrieving the status, the method calls the status dispatcher with
     * the currently stored controller configuration settings. These settings
     * include the internal status set by the controller or the dispatcher
     * itself. The overall status of the instance depends on this internal
     * status.
     * 
     * @param instanceId
     *            the ID of the application instance to be checked
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @return an <code>InstanceStatus</code> instance with the overall status
     *         of the application instance
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus getInstanceStatus(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        LOGGER.debug("getInstanceStatus({})",
                LogAndExceptionConverter.getLogText(instanceId, settings));
        try {
            PropertyHandler ph = new PropertyHandler(settings);
            ProvisioningValidator.validateTimeout(instanceId, ph,
                    platformService);
            Dispatcher dp = new Dispatcher(platformService, instanceId, ph);
            InstanceStatus status = dp.dispatch();
            return status;
        } catch (Exception t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.STATUS);
        }
    }

    /**
     * Does not carry out specific actions in this implementation and always
     * returns <code>null</code>.
     * 
     * @param instanceId
     *            the ID of the application instance
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @param properties
     *            the events as properties consisting of a key and a value each
     * @return <code>null</code>
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus notifyInstance(String instanceId,
            ProvisioningSettings settings, Properties properties)
            throws APPlatformException {
        LOGGER.info("notifyInstance({})",
                LogAndExceptionConverter.getLogText(instanceId, settings));
        InstanceStatus status = null;
        if (instanceId == null || settings == null || properties == null) {
            return status;
        }
        PropertyHandler propertyHandler = new PropertyHandler(settings);

        if ("finish".equals(properties.get("command"))) {
            if (FlowState.MANUAL.equals(propertyHandler.getState())) {
                propertyHandler.setState(FlowState.FINISHED);
                status = setNotificationStatus(settings, propertyHandler);
                LOGGER.debug(
                        "Got finish event => changing instance status to finished");
            } else {
                APPlatformException pe = new APPlatformException(
                        "Got finish event but instance is in state "
                                + propertyHandler.getState()
                                + " => nothing changed");
                throw pe;
            }
        }
        return status;
    }

    /**
     * Starts the activation of an application instance.
     * <p>
     * The internal status <code>ACTIVATION_REQUESTED</code> is stored as a
     * controller configuration setting. It is evaluated and handled by the
     * status dispatcher, which is invoked at regular intervals by APP through
     * the <code>getInstanceStatus</code> method.
     * 
     * @param instanceId
     *            the ID of the application instance to be activated
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @return an <code>InstanceStatus</code> instance with the overall status
     *         of the application instance
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus activateInstance(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        LOGGER.info("activateInstance({})",
                LogAndExceptionConverter.getLogText(instanceId, settings));
        try {
            // Set status to store for application instance
            PropertyHandler ph = new PropertyHandler(settings);
            ph.setState(FlowState.ACTIVATION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(settings.getParameters());
            result.setChangedAttributes(settings.getAttributes());
            return result;
        } catch (Exception t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.ACTIVATION);
        }
    }
    /**
     * Starts the deactivation of an application instance.
     * <p>
     * The internal status <code>DEACTIVATION_REQUESTED</code> is stored as a
     * controller configuration setting. It is evaluated and handled by the
     * status dispatcher, which is invoked at regular intervals by APP through
     * the <code>getInstanceStatus</code> method.
     * 
     * @param instanceId
     *            the ID of the application instance to be activated
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @return an <code>InstanceStatus</code> instance with the overall status
     *         of the application instance
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus deactivateInstance(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        LOGGER.info("deactivateInstance({})",
                LogAndExceptionConverter.getLogText(instanceId, settings));
        try {
            // Set status to store for application instance
            PropertyHandler ph = new PropertyHandler(settings);
            ph.setState(FlowState.DEACTIVATION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(settings.getParameters());
            result.setChangedAttributes(settings.getAttributes());
            return result;
        } catch (Exception t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.DEACTIVATION);
        }
    }

    /**
     * Does not carry out specific actions in this implementation and always
     * returns <code>null</code>.
     * 
     * @param instanceId
     *            the ID of the application instance
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @param users
     *            a list of users
     * @return <code>null</code>
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatusUsers createUsers(String instanceId,
            ProvisioningSettings settings, List<ServiceUser> users)
            throws APPlatformException {
        return null;
    }

    /**
     * Does not carry out specific actions in this implementation and always
     * returns <code>null</code>.
     * 
     * @param instanceId
     *            the ID of the application instance
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @param users
     *            a list of users
     * @return <code>null</code>
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus deleteUsers(String instanceId,
            ProvisioningSettings settings, List<ServiceUser> users)
            throws APPlatformException {
        return null;
    }

    /**
     * Does not carry out specific actions in this implementation and always
     * returns <code>null</code>.
     * 
     * @param instanceId
     *            the ID of the application instance
     * @param settings
     *            a <code>ProvisioningSettings</code> object specifying the
     *            service parameters and configuration settings
     * @param users
     *            a list of users
     * @return <code>null</code>
     * @throws APPlatformException
     */

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public InstanceStatus updateUsers(String instanceId,
            ProvisioningSettings settings, List<ServiceUser> users)
            throws APPlatformException {
        return null;
    }

    @Override
    public List<LocalizedText> getControllerStatus(ControllerSettings settings)
            throws APPlatformException {
        return null; // not yet implemented by APP => nothing to do
    }

    @Override
    public List<OperationParameter> getOperationParameters(String userId,
            String instanceId, String operationId,
            ProvisioningSettings settings) throws APPlatformException {
        return null; // not applicable
    }

    @Override
    public InstanceStatus executeServiceOperation(String userId,
            String instanceId, String transactionId, String operationId,
            List<OperationParameter> parameters, ProvisioningSettings settings)
            throws APPlatformException {
        LOGGER.info("executeServiceOperation({})",
                LogAndExceptionConverter.getLogText(instanceId, settings)
                        + " | OperationIdID: " + operationId);
        InstanceStatus status = null;
        if (instanceId == null || operationId == null || settings == null) {
            return status;
        }

        try {
            PropertyHandler ph = new PropertyHandler(settings);
            boolean operationAccepted = false;
            switch (operationId) {
            case "START_VIRTUAL_SYSTEM":
                ph.setState(FlowState.START_REQUESTED);
                ph.setStartTime(String.valueOf(System.currentTimeMillis()));
                operationAccepted = true;
                break;

            case "STOP_VIRTUAL_SYSTEM":
                ph.setState(FlowState.STOP_REQUESTED);
                ph.setStartTime(String.valueOf(System.currentTimeMillis()));
                operationAccepted = true;
                break;

            case "RESUME_VIRTUAL_SYSTEM":
                // FIXME decide whether activation process is sufficient or
                // dedicated start/stop need to be used
                ph.setState(FlowState.ACTIVATION_REQUESTED);
                operationAccepted = true;
                break;

            case "SUSPEND_VIRTUAL_SYSTEM":
                ph.setState(FlowState.DEACTIVATION_REQUESTED);
                operationAccepted = true;
                break;

            }
            if (operationAccepted) {
                // when a valid operation has been requested, let the timer
                // handle the instance afterwards
                status = new InstanceStatus();
                status.setRunWithTimer(true);
                status.setIsReady(false);
                status.setChangedParameters(settings.getParameters());
                status.setChangedAttributes(settings.getAttributes());
            }
            return status;
        } catch (Exception t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.OPERATION);
        }
    }

    @Override
    public void setControllerSettings(ControllerSettings settings) {
        // not applicable
    }

    private InstanceStatus setNotificationStatus(ProvisioningSettings settings,
            PropertyHandler propertyHandler) {
        InstanceStatus status;
        status = new InstanceStatus();
        status.setIsReady(true);
        status.setRunWithTimer(true);
        status.setDescription(getProvisioningStatusText(propertyHandler));
        status.setChangedParameters(settings.getParameters());
        status.setChangedAttributes(settings.getAttributes());
        return status;
    }

    private List<LocalizedText> getProvisioningStatusText(
            PropertyHandler paramHandler) {
        List<LocalizedText> messages = Messages
                .getAll("status_" + paramHandler.getState());
        for (LocalizedText message : messages) {
            if (message.getText() == null || (message.getText().startsWith("!")
                    && message.getText().endsWith("!"))) {
                message.setText(Messages.get(message.getLocale(),
                        "status_INSTANCE_OVERALL"));
            }
        }
        return messages;
    }

    @Override
    public int getServersNumber(String instanceId,
        String subscriptionId, String organizationId)
        throws APPlatformException {

        ProvisioningSettings settings = platformService
            .getServiceInstanceDetails(OpenStackController.ID, instanceId,
                subscriptionId, organizationId);
        PropertyHandler ph = new PropertyHandler(settings);

        try {
            return new NovaProcessor().getServersDetails(ph, false).size();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return SERVERS_NUMBER_CANNOT_BE_CHECKED;
    }
}
