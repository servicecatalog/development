/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  AWS controller implementation supporting EC2 provisioning for the 
 *  Asynchronous Provisioning Platform (APP)
 *       
 *  Creation Date: 2012-09-06                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.aws.controller;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.oscm.app.aws.data.FlowState;
import org.oscm.app.aws.data.Operation;
import org.oscm.app.aws.i18n.Messages;
import org.oscm.app.common.controller.LogAndExceptionConverter;
import org.oscm.app.common.data.Context;
import org.oscm.app.v2_0.data.ControllerSettings;
import org.oscm.app.v2_0.data.InstanceDescription;
import org.oscm.app.v2_0.data.InstanceStatus;
import org.oscm.app.v2_0.data.InstanceStatusUsers;
import org.oscm.app.v2_0.data.LocalizedText;
import org.oscm.app.v2_0.data.OperationParameter;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.oscm.app.v2_0.data.ServiceUser;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.intf.APPlatformController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS implementation of a service controller based on the Asynchronous
 * Provisioning Platform (APP).
 */
@Stateless(mappedName = "bss/app/controller/" + AWSController.ID)
@Remote(APPlatformController.class)
public class AWSController implements APPlatformController {

    public static final String ID = "ess.aws";

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AWSController.class);

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
            PropertyHandler ph = PropertyHandler.withSettings(settings);
            validateParameters(ph);
            ph.setOperation(Operation.EC2_CREATION);
            ph.setState(FlowState.CREATION_REQUESTED);

            // Return generated instance information
            InstanceDescription id = new InstanceDescription();
            id.setInstanceId("aws-" + UUID.randomUUID().toString());
            id.setChangedParameters(settings.getParameters());
            id.setChangedAttributes(settings.getAttributes());
            LOGGER.info("createInstance({})", LogAndExceptionConverter
                    .getLogText(id.getInstanceId(), settings));
            return id;
        } catch (Throwable t) {
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
            PropertyHandler ph = PropertyHandler.withSettings(settings);
            ph.setOperation(Operation.EC2_DELETION);
            ph.setState(FlowState.DELETION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(settings.getParameters());
            result.setChangedAttributes(settings.getAttributes());
            return result;
        } catch (Throwable t) {
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
        LOGGER.info("modifyInstance({})", LogAndExceptionConverter.getLogText(
                instanceId, currentSettings));
        try {
            PropertyHandler ph = PropertyHandler.withSettings(newSettings);
            ph.setOperation(Operation.EC2_MODIFICATION);
            ph.setState(FlowState.MODIFICATION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(newSettings.getParameters());
            result.setChangedAttributes(newSettings.getAttributes());
            return result;
        } catch (Throwable t) {
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
            PropertyHandler ph = PropertyHandler.withSettings(settings);
            EC2Processor ec2processor = new EC2Processor(ph, instanceId);
            InstanceStatus status = ec2processor.process();

            return status;
        } catch (Throwable t) {
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
                LOGGER.debug("Got finish event => changing instance status to finished");
            } else {
                APPlatformException pe = new APPlatformException(
                        "Got finish event but instance is in state "
                                + propertyHandler.getState()
                                + " => nothing changed");
                LOGGER.debug(pe.getMessage());
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
            PropertyHandler ph = PropertyHandler.withSettings(settings);
            ph.setOperation(Operation.EC2_ACTIVATION);
            ph.setState(FlowState.ACTIVATION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(settings.getParameters());
            result.setChangedAttributes(settings.getAttributes());
            return result;
        } catch (Throwable t) {
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
            PropertyHandler ph = PropertyHandler.withSettings(settings);
            ph.setOperation(Operation.EC2_ACTIVATION);
            ph.setState(FlowState.DEACTIVATION_REQUESTED);

            InstanceStatus result = new InstanceStatus();
            result.setChangedParameters(settings.getParameters());
            result.setChangedAttributes(settings.getAttributes());
            return result;
        } catch (Throwable t) {
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
    public InstanceStatus executeServiceOperation(String userId,
            String instanceId, String transactionId, String operationId,
            List<OperationParameter> parameters, ProvisioningSettings settings)
            throws APPlatformException {
        LOGGER.info(
                "executeServiceOperation("
                        + LogAndExceptionConverter.getLogText(instanceId,
                                settings) + " | OperationIdID: {})",
                operationId);
        InstanceStatus status = null;
        if (instanceId == null || operationId == null || settings == null) {
            return status;
        }

        try {
            PropertyHandler ph = PropertyHandler.withSettings(settings);
            boolean operationAccepted = false;
            if ("START_VIRTUAL_SYSTEM".equals(operationId)) {
                ph.setState(FlowState.START_REQUESTED);
                ph.setOperation(Operation.EC2_OPERATION);
                operationAccepted = true;
            } else if ("STOP_VIRTUAL_SYSTEM".equals(operationId)) {
                ph.setState(FlowState.STOP_REQUESTED);
                ph.setOperation(Operation.EC2_OPERATION);
                operationAccepted = true;
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
        } catch (Throwable t) {
            throw LogAndExceptionConverter.createAndLogPlatformException(t,
                    Context.OPERATION);
        }
    }

    @Override
    public List<LocalizedText> getControllerStatus(ControllerSettings settings)
            throws APPlatformException {
        return null; // not yet implemented
    }

    @Override
    public List<OperationParameter> getOperationParameters(String userId,
            String instanceId, String operationId, ProvisioningSettings settings)
            throws APPlatformException {
        return null; // not applicable
    }

    @Override
    public void setControllerSettings(ControllerSettings settings) {
        // not applicable
    }

    private void validateParameters(PropertyHandler ph)
            throws APPlatformException {

        String instanceName = ph.getInstanceName();
        if (isNullOrEmpty(instanceName)) {
            LOGGER.error(Messages.get(Messages.DEFAULT_LOCALE,
                    "error_missing_name"));
            throw new APPlatformException(Messages.getAll("error_missing_name"));
        }
        String regex = ph.getInstanceNamePattern();
        if (!isNullOrEmpty(regex)) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(instanceName);
            if (!m.matches()) {
                LOGGER.error(Messages.get(Messages.DEFAULT_LOCALE,
                        "error_missing_name", new Object[] { instanceName }));
                throw new APPlatformException(Messages.getAll(
                        "error_invalid_name", new Object[] { instanceName }));
            }
        }

        if (isNullOrEmpty(ph.getKeyPairName())) {
            LOGGER.error(Messages.get(Messages.DEFAULT_LOCALE,
                    "error_missing_keypair"));
            throw new APPlatformException(
                    Messages.getAll("error_missing_keypair"));
        }

        if (isNullOrEmpty(ph.getImageName())) {
            LOGGER.error(Messages.get(Messages.DEFAULT_LOCALE,
                    "error_missing_imagename"));
            throw new APPlatformException(
                    Messages.getAll("error_missing_imagename"));
        }

        if (isNullOrEmpty(ph.getInstanceType())) {
            LOGGER.error(Messages.get(Messages.DEFAULT_LOCALE,
                    "error_missing_instancetype"));
            throw new APPlatformException(
                    Messages.getAll("error_missing_instancetype"));
        }

    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
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

    /**
     * Returns a small status text for the current provisioning step.
     * 
     * @param paramHandler
     *            property handler containing the current status
     * @return short status text describing the current status
     */
    private List<LocalizedText> getProvisioningStatusText(
            PropertyHandler paramHandler) {
        List<LocalizedText> messages = Messages.getAll("status_"
                + paramHandler.getState());
        for (LocalizedText message : messages) {
            if (message.getText() == null
                    || (message.getText().startsWith("!") && message.getText()
                            .endsWith("!"))) {
                message.setText(Messages.get(message.getLocale(),
                        "status_INSTANCE_OVERALL"));
            }
        }
        return messages;
    }
}
