/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 21.11.16 15:51
 *
 *******************************************************************************/

package org.oscm.app.adapter;

import java.util.List;
import java.util.Properties;

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
import org.oscm.app.converter.APPInterfaceDataConverter;
import org.oscm.app.converter.APPInterfaceExceptionConverter;

public class APPlatformControllerLegacyAdapter implements APPlatformController {

    private APPInterfaceDataConverter converterData;
    private APPInterfaceExceptionConverter converterException;
    private org.oscm.app.v1_0.intf.APPlatformController delegate;

    public APPlatformControllerLegacyAdapter(
            APPInterfaceDataConverter converterData,
            APPInterfaceExceptionConverter converterException,
            org.oscm.app.v1_0.intf.APPlatformController controllerInterface) {
        this(converterData, converterException);
        this.delegate = controllerInterface;
    }

    public APPlatformControllerLegacyAdapter(
            APPInterfaceDataConverter converterData,
            APPInterfaceExceptionConverter converterException) {
        this.converterData = converterData;
        this.converterException = converterException;
    }

    private APPlatformControllerLegacyAdapter() {
        this(new APPInterfaceDataConverter(),
                new APPInterfaceExceptionConverter());
    }

    public APPlatformControllerLegacyAdapter(
            org.oscm.app.v1_0.intf.APPlatformController delegate) {
        this();
        this.delegate = delegate;
    }

    public APPInterfaceDataConverter getConverterData() {
        return converterData;
    }

    public void setConverterData(APPInterfaceDataConverter converterData) {
        this.converterData = converterData;
    }

    public APPInterfaceExceptionConverter getConverterException() {
        return converterException;
    }

    public void setConverterException(
            APPInterfaceExceptionConverter converterException) {
        this.converterException = converterException;
    }

    @Override
    public InstanceDescription createInstance(ProvisioningSettings settings)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        org.oscm.app.v1_0.data.InstanceDescription instance;
        try {
            instance = delegate.createInstance(oldSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instance);
    }

    @Override
    public InstanceStatus modifyInstance(String instanceId,
            ProvisioningSettings currentSettings,
            ProvisioningSettings newSettings) throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldCurrentSettings = converterData
                .convertToOld(currentSettings);
        org.oscm.app.v1_0.data.ProvisioningSettings oldNewSettings = converterData
                .convertToOld(newSettings);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.modifyInstance(instanceId,
                    oldCurrentSettings, oldNewSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatus deleteInstance(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.deleteInstance(instanceId, oldSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatus getInstanceStatus(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate
                    .getInstanceStatus(instanceId, oldSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatus notifyInstance(String instanceId,
            ProvisioningSettings settings, Properties properties)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.notifyInstance(instanceId, oldSettings,
                    properties);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatus activateInstance(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.activateInstance(instanceId, oldSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatus deactivateInstance(String instanceId,
            ProvisioningSettings settings) throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.deactivateInstance(instanceId,
                    oldSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatusUsers createUsers(String instanceId,
            ProvisioningSettings settings, List<ServiceUser> users)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        List<org.oscm.app.v1_0.data.ServiceUser> oldServiceUsers = converterData
                .convertToOldServiceUsers(users);
        org.oscm.app.v1_0.data.InstanceStatusUsers instanceStatus;
        try {
            instanceStatus = delegate.createUsers(instanceId, oldSettings,
                    oldServiceUsers);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatus deleteUsers(String instanceId,
            ProvisioningSettings settings, List<ServiceUser> users)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        List<org.oscm.app.v1_0.data.ServiceUser> oldUsers = converterData
                .convertToOldServiceUsers(users);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.deleteUsers(instanceId, oldSettings,
                    oldUsers);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public InstanceStatus updateUsers(String instanceId,
            ProvisioningSettings settings, List<ServiceUser> users)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        List<org.oscm.app.v1_0.data.ServiceUser> oldUsers = converterData
                .convertToOldServiceUsers(users);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.updateUsers(instanceId, oldSettings,
                    oldUsers);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public List<LocalizedText> getControllerStatus(ControllerSettings settings)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ControllerSettings oldSettings = converterData
                .convertToOld(settings);
        List<org.oscm.app.v1_0.data.LocalizedText> controllerStatus;
        try {
            controllerStatus = delegate.getControllerStatus(oldSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNewLocalizedTexts(controllerStatus);
    }

    @Override
    public List<OperationParameter> getOperationParameters(String userId,
            String instanceId, String operationId, ProvisioningSettings settings)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldSettings = converterData
                .convertToOld(settings);
        List<org.oscm.app.v1_0.data.OperationParameter> operationParameters;
        try {
            operationParameters = delegate.getOperationParameters(userId,
                    instanceId, operationId, oldSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(operationParameters);
    }

    @Override
    public InstanceStatus executeServiceOperation(String userId,
            String instanceId, String transactionId, String operationId,
            List<OperationParameter> parameters, ProvisioningSettings settings)
            throws APPlatformException {
        org.oscm.app.v1_0.data.ProvisioningSettings oldCurrentSettings = converterData
                .convertToOld(settings);
        List<org.oscm.app.v1_0.data.OperationParameter> oldParametersList = converterData
                .convertToOldOperationParametersList(parameters);
        org.oscm.app.v1_0.data.InstanceStatus instanceStatus;
        try {
            instanceStatus = delegate.executeServiceOperation(userId,
                    instanceId, transactionId, operationId, oldParametersList,
                    oldCurrentSettings);
        } catch (org.oscm.app.v1_0.exceptions.APPlatformException e) {
            throw converterException.convertToNew(e);
        }
        return converterData.convertToNew(instanceStatus);
    }

    @Override
    public void setControllerSettings(ControllerSettings settings) {
        org.oscm.app.v1_0.data.ControllerSettings controllerSettings = converterData
                .convertToOld(settings);
        delegate.setControllerSettings(controllerSettings);
    }

    public org.oscm.app.v1_0.intf.APPlatformController getDelegate() {
        return delegate;
    }

    public void setDelegate(org.oscm.app.v1_0.intf.APPlatformController delegate) {
        this.delegate = delegate;
    }
}
