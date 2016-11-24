/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 21.11.16 10:01
 *
 ******************************************************************************/

package org.oscm.app.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.oscm.app.v1_0.data.ControllerConfigurationKey;
import org.oscm.app.v1_0.data.ControllerSettings;
import org.oscm.app.v1_0.data.InstanceDescription;
import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.InstanceStatusUsers;
import org.oscm.app.v1_0.data.LocalizedText;
import org.oscm.app.v1_0.data.OperationParameter;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.data.ServiceUser;
import org.oscm.app.v1_0.data.User;

public class APPInterfaceDataConverter {

    private final String PWD_SUFFIX = "_PWD";
    private final String PASS_SUFFIX = "_PASS";

    public org.oscm.app.v2_0.data.ControllerConfigurationKey convertToNew(
            ControllerConfigurationKey controllerConfigurationKey) {
        if (controllerConfigurationKey == null) {
            return null;
        }
        String oldName = controllerConfigurationKey.name();
        return org.oscm.app.v2_0.data.ControllerConfigurationKey
                .valueOf(oldName);
    }

    public org.oscm.app.v2_0.data.ControllerSettings convertToNew(
            ControllerSettings controllerSettings) {

        if (controllerSettings == null) {
            return null;
        }

        HashMap<String, String> configSettings = controllerSettings
                .getConfigSettings();
        PasswordAuthentication authentication = controllerSettings
                .getAuthentication();

        org.oscm.app.v2_0.data.ControllerSettings newControllerSettings = new org.oscm.app.v2_0.data.ControllerSettings(
                convertToNew(configSettings));
        newControllerSettings.setConfigSettings(convertToNew(configSettings));
        newControllerSettings.setAuthentication(convertToNew(authentication));

        return newControllerSettings;

    }

    public org.oscm.app.v2_0.data.InstanceDescription convertToNew(
            InstanceDescription instanceDescription) {

        if (instanceDescription == null) {
            return null;
        }

        org.oscm.app.v2_0.data.InstanceDescription newInstanceDescription = new org.oscm.app.v2_0.data.InstanceDescription();
        newInstanceDescription.setInstanceId(instanceDescription
                .getInstanceId());
        newInstanceDescription.setAccessInfo(instanceDescription
                .getAccessInfo());
        newInstanceDescription
                .setChangedParameters(convertToNew(instanceDescription
                        .getChangedParameters()));
        newInstanceDescription.setLoginPath(instanceDescription.getLoginPath());
        newInstanceDescription.setBaseUrl(instanceDescription.getBaseUrl());
        newInstanceDescription
                .setDescription(convertToNewLocalizedTexts(instanceDescription
                        .getDescription()));
        newInstanceDescription.setRunWithTimer(instanceDescription
                .getRunWithTimer());
        newInstanceDescription
                .setInstanceProvisioningRequired(instanceDescription
                        .isInstanceProvisioningRequested());
        newInstanceDescription.setIsReady(instanceDescription.isReady());
        return newInstanceDescription;
    }

    public org.oscm.app.v2_0.data.InstanceStatus convertToNew(
            InstanceStatus instanceStatus) {

        if (instanceStatus == null) {
            return null;
        }

        org.oscm.app.v2_0.data.InstanceStatus newInstanceStatus = new org.oscm.app.v2_0.data.InstanceStatus();

        newInstanceStatus.setAccessInfo(instanceStatus.getAccessInfo());
        newInstanceStatus.setBaseUrl(instanceStatus.getBaseUrl());
        newInstanceStatus.setChangedParameters(convertToNew(instanceStatus
                .getChangedParameters()));
        List<LocalizedText> oldDescriptions = instanceStatus.getDescription();
        List<org.oscm.app.v2_0.data.LocalizedText> newDescriptions = convertToNewLocalizedTexts(oldDescriptions);

        newInstanceStatus.setDescription(newDescriptions);
        newInstanceStatus.setInstanceProvisioningRequired(instanceStatus
                .isInstanceProvisioningRequested());
        newInstanceStatus.setIsReady(instanceStatus.isReady());
        newInstanceStatus.setLoginPath(instanceStatus.getLoginPath());
        newInstanceStatus.setRunWithTimer(instanceStatus.getRunWithTimer());
        return newInstanceStatus;
    }

    public org.oscm.app.v2_0.data.InstanceStatusUsers convertToNew(
            InstanceStatusUsers instanceStatusUsers) {
        if (instanceStatusUsers == null) {
            return null;
        }
        org.oscm.app.v2_0.data.InstanceStatusUsers newInstanceStatusUsers = new org.oscm.app.v2_0.data.InstanceStatusUsers();
        List<ServiceUser> oldServiceUsers = instanceStatusUsers
                .getChangedUsers();
        List<org.oscm.app.v2_0.data.ServiceUser> newServiceUsers = convertToNewServiceUsers(oldServiceUsers);
        List<org.oscm.app.v2_0.data.LocalizedText> newDescriptions = convertToNewLocalizedTexts(instanceStatusUsers
                .getDescription());
        newInstanceStatusUsers.setChangedUsers(newServiceUsers);
        newInstanceStatusUsers.setDescription(newDescriptions);
        newInstanceStatusUsers.setAccessInfo(instanceStatusUsers
                .getAccessInfo());
        newInstanceStatusUsers.setBaseUrl(instanceStatusUsers.getAccessInfo());
        newInstanceStatusUsers
                .setChangedParameters(convertToNew(instanceStatusUsers
                        .getChangedParameters()));
        newInstanceStatusUsers
                .setInstanceProvisioningRequired(instanceStatusUsers
                        .isInstanceProvisioningRequested());
        newInstanceStatusUsers.setIsReady(instanceStatusUsers.isReady());
        newInstanceStatusUsers.setRunWithTimer(instanceStatusUsers
                .getRunWithTimer());
        newInstanceStatusUsers.setLoginPath(instanceStatusUsers.getLoginPath());
        return newInstanceStatusUsers;
    }

    public List<org.oscm.app.v2_0.data.ServiceUser> convertToNewServiceUsers(
            List<ServiceUser> oldServiceUsers) {
        if (oldServiceUsers == null) {
            return null;
        }
        List<org.oscm.app.v2_0.data.ServiceUser> newServiceUsers = new ArrayList<>(
                oldServiceUsers.size());
        for (ServiceUser serviceUser : oldServiceUsers) {
            newServiceUsers.add(convertToNew(serviceUser));
        }
        return newServiceUsers;
    }

    public List<ServiceUser> convertToOldServiceUsers(
            List<org.oscm.app.v2_0.data.ServiceUser> newServiceUsers) {
        if (newServiceUsers == null) {
            return null;
        }
        List<ServiceUser> oldServiceUsers = new ArrayList<>(
                newServiceUsers.size());
        for (org.oscm.app.v2_0.data.ServiceUser serviceUser : newServiceUsers) {
            oldServiceUsers.add(convertToOld(serviceUser));
        }
        return oldServiceUsers;
    }

    public org.oscm.app.v2_0.data.LocalizedText convertToNew(
            LocalizedText localizedText) {
        if (localizedText == null) {
            return null;
        }
        org.oscm.app.v2_0.data.LocalizedText newLocalizedText = new org.oscm.app.v2_0.data.LocalizedText();
        newLocalizedText.setLocale(localizedText.getLocale());
        newLocalizedText.setText(localizedText.getText());
        return newLocalizedText;
    }

    public org.oscm.app.v2_0.data.OperationParameter convertToNew(
            OperationParameter operationParameter) {
        if (operationParameter == null) {
            return null;
        }
        org.oscm.app.v2_0.data.OperationParameter newOperationParameter = new org.oscm.app.v2_0.data.OperationParameter();
        newOperationParameter.setName(operationParameter.getName());
        newOperationParameter.setValue(operationParameter.getValue());
        return newOperationParameter;
    }

    public org.oscm.app.v2_0.data.PasswordAuthentication convertToNew(
            PasswordAuthentication passwordAuthentication) {
        if (passwordAuthentication == null) {
            return null;
        }
        String login = passwordAuthentication.getUserName();
        String password = passwordAuthentication.getPassword();
        org.oscm.app.v2_0.data.PasswordAuthentication newPasswordAuthentication = new org.oscm.app.v2_0.data.PasswordAuthentication(
                login, password);
        return newPasswordAuthentication;
    }

    public org.oscm.app.v2_0.data.ProvisioningSettings convertToNew(
            ProvisioningSettings provisioningSettings) {
        if (provisioningSettings == null) {
            return null;
        }
        HashMap<String, String> parameters = provisioningSettings
                .getParameters();
        HashMap<String, String> configSettings = provisioningSettings
                .getConfigSettings();
        HashMap<String, org.oscm.app.v2_0.data.Setting> attributes = new HashMap<String, org.oscm.app.v2_0.data.Setting>();
        String locale = provisioningSettings.getLocale();
        org.oscm.app.v2_0.data.ProvisioningSettings newProvisioningSettings = new org.oscm.app.v2_0.data.ProvisioningSettings(
                convertToNew(parameters), attributes, attributes,
                convertToNew(configSettings), locale);
        newProvisioningSettings.setOrganizationId(provisioningSettings
                .getOrganizationId());
        newProvisioningSettings.setOrganizationName(provisioningSettings
                .getOrganizationName());
        newProvisioningSettings.setBesLoginUrl(provisioningSettings
                .getBesLoginURL());
        newProvisioningSettings
                .setRequestingUser(convertToNew(provisioningSettings
                        .getRequestingUser()));
        newProvisioningSettings.setSubscriptionId(provisioningSettings
                .getSubscriptionId());
        newProvisioningSettings
                .setAuthentication(convertToNew(provisioningSettings
                        .getAuthentication()));
        return newProvisioningSettings;
    }

    public org.oscm.app.v2_0.data.ServiceUser convertToNew(
            ServiceUser serviceUser) {
        if (serviceUser == null) {
            return null;
        }
        org.oscm.app.v2_0.data.ServiceUser newServiceUser = new org.oscm.app.v2_0.data.ServiceUser();
        newServiceUser.setLocale(serviceUser.getLocale());
        newServiceUser.setApplicationUserId(serviceUser.getApplicationUserId());
        newServiceUser.setEmail(serviceUser.getEmail());
        newServiceUser.setFirstName(serviceUser.getFirstName());
        newServiceUser.setLastName(serviceUser.getLastName());
        newServiceUser.setRoleIdentifier(serviceUser.getRoleIdentifier());
        newServiceUser.setUserId(serviceUser.getUserId());
        return newServiceUser;
    }

    public org.oscm.app.v2_0.data.User convertToNew(User user) {
        if (user == null) {
            return null;
        }
        org.oscm.app.v2_0.data.User newUser = new org.oscm.app.v2_0.data.User();
        newUser.setUserId(user.getUserId());
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setLocale(user.getLocale());
        newUser.setEmail(user.getEmail());
        newUser.setUserKey(user.getUserKey());
        return newUser;
    }

    public ControllerConfigurationKey convertToOld(
            org.oscm.app.v2_0.data.ControllerConfigurationKey controllerConfigurationKey) {
        if (controllerConfigurationKey == null) {
            return null;
        }
        String oldName = controllerConfigurationKey.name();
        return ControllerConfigurationKey.valueOf(oldName);
    }

    public ControllerSettings convertToOld(
            org.oscm.app.v2_0.data.ControllerSettings controllerSettings) {
        if (controllerSettings == null) {
            return null;
        }
        HashMap<String, String> configSettings = convertToOld(controllerSettings
                .getConfigSettings());
        org.oscm.app.v2_0.data.PasswordAuthentication authentication = controllerSettings
                .getAuthentication();

        ControllerSettings oldControllerSettings = new ControllerSettings(
                configSettings);
        oldControllerSettings.setConfigSettings(configSettings);
        oldControllerSettings.setAuthentication(convertToOld(authentication));
        return oldControllerSettings;
    }

    public InstanceDescription convertToOld(
            org.oscm.app.v2_0.data.InstanceDescription instanceDescription) {
        if (instanceDescription == null) {
            return null;
        }
        InstanceDescription oldInstanceDescription = new InstanceDescription();
        oldInstanceDescription.setInstanceId(instanceDescription
                .getInstanceId());
        oldInstanceDescription.setAccessInfo(instanceDescription
                .getAccessInfo());
        oldInstanceDescription
                .setChangedParameters(convertToOld(instanceDescription
                        .getChangedParameters()));
        oldInstanceDescription.setLoginPath(instanceDescription.getLoginPath());
        oldInstanceDescription.setBaseUrl(instanceDescription.getBaseUrl());
        oldInstanceDescription
                .setDescription(convertToOldLocalizedTexts(instanceDescription
                        .getDescription()));
        oldInstanceDescription.setRunWithTimer(instanceDescription
                .getRunWithTimer());
        oldInstanceDescription
                .setInstanceProvisioningRequired(instanceDescription
                        .isInstanceProvisioningRequested());
        oldInstanceDescription.setIsReady(instanceDescription.isReady());
        return oldInstanceDescription;
    }

    public InstanceStatus convertToOld(
            org.oscm.app.v2_0.data.InstanceStatus instanceStatus) {
        if (instanceStatus == null) {
            return null;
        }
        InstanceStatus oldInstanceStatus = new InstanceStatus();

        oldInstanceStatus.setAccessInfo(instanceStatus.getAccessInfo());
        oldInstanceStatus.setBaseUrl(instanceStatus.getBaseUrl());
        oldInstanceStatus.setChangedParameters(convertToOld(instanceStatus
                .getChangedParameters()));
        oldInstanceStatus
                .setDescription(convertToOldLocalizedTexts(instanceStatus
                        .getDescription()));
        oldInstanceStatus.setInstanceProvisioningRequired(instanceStatus
                .isInstanceProvisioningRequested());
        oldInstanceStatus.setIsReady(instanceStatus.isReady());
        oldInstanceStatus.setLoginPath(instanceStatus.getLoginPath());
        oldInstanceStatus.setRunWithTimer(instanceStatus.getRunWithTimer());
        return oldInstanceStatus;
    }

    public InstanceStatusUsers convertToOld(
            org.oscm.app.v2_0.data.InstanceStatusUsers instanceStatusUsers) {
        if (instanceStatusUsers == null) {
            return null;
        }
        InstanceStatusUsers oldInstanceStatusUsers = new InstanceStatusUsers();
        List<org.oscm.app.v2_0.data.ServiceUser> newServiceUsers = instanceStatusUsers
                .getChangedUsers();
        List<ServiceUser> oldServiceUsers = convertToOldServiceUsers(newServiceUsers);
        List<LocalizedText> oldDescriptions = convertToOldLocalizedTexts(instanceStatusUsers
                .getDescription());
        oldInstanceStatusUsers.setChangedUsers(oldServiceUsers);
        oldInstanceStatusUsers.setDescription(oldDescriptions);
        oldInstanceStatusUsers.setAccessInfo(instanceStatusUsers
                .getAccessInfo());
        oldInstanceStatusUsers.setBaseUrl(instanceStatusUsers.getBaseUrl());
        oldInstanceStatusUsers
                .setInstanceProvisioningRequired(instanceStatusUsers
                        .isInstanceProvisioningRequested());
        oldInstanceStatusUsers.setIsReady(instanceStatusUsers.isReady());
        oldInstanceStatusUsers.setLoginPath(instanceStatusUsers.getLoginPath());
        oldInstanceStatusUsers.setRunWithTimer(instanceStatusUsers
                .getRunWithTimer());
        oldInstanceStatusUsers
                .setChangedParameters(convertToOld(instanceStatusUsers
                        .getChangedParameters()));
        return oldInstanceStatusUsers;
    }

    public LocalizedText convertToOld(
            org.oscm.app.v2_0.data.LocalizedText localizedText) {
        if (localizedText == null) {
            return null;
        }
        LocalizedText oldLocalizedText = new LocalizedText();
        oldLocalizedText.setLocale(localizedText.getLocale());
        oldLocalizedText.setText(localizedText.getText());
        return oldLocalizedText;
    }

    public OperationParameter convertToOld(
            org.oscm.app.v2_0.data.OperationParameter operationParameter) {
        if (operationParameter == null) {
            return null;
        }
        OperationParameter oldOperationParameter = new OperationParameter();
        oldOperationParameter.setName(operationParameter.getName());
        oldOperationParameter.setValue(operationParameter.getValue());
        return oldOperationParameter;
    }

    public PasswordAuthentication convertToOld(
            org.oscm.app.v2_0.data.PasswordAuthentication passwordAuthentication) {
        if (passwordAuthentication == null) {
            return null;
        }
        String login = passwordAuthentication.getUserName();
        String password = passwordAuthentication.getPassword();
        PasswordAuthentication oldPasswordAuthentication = new PasswordAuthentication(
                login, password);
        return oldPasswordAuthentication;
    }

    public ProvisioningSettings convertToOld(
            org.oscm.app.v2_0.data.ProvisioningSettings provisioningSettings) {
        if (provisioningSettings == null) {
            return null;
        }
        HashMap<String, String> parameters = convertToOld(provisioningSettings
                .getParameters());
        HashMap<String, String> configSettings = convertToOld(provisioningSettings
                .getConfigSettings());
        String locale = provisioningSettings.getLocale();
        ProvisioningSettings oldProvisioningSettings = new ProvisioningSettings(
                parameters, configSettings, locale);
        oldProvisioningSettings.setSubscriptionId(provisioningSettings
                .getSubscriptionId());
        oldProvisioningSettings.setOrganizationId(provisioningSettings
                .getOrganizationId());
        oldProvisioningSettings.setOrganizationName(provisioningSettings
                .getOrganizationName());
        oldProvisioningSettings
                .setRequestingUser(convertToOld(provisioningSettings
                        .getRequestingUser()));
        oldProvisioningSettings.setBesLoginUrl(provisioningSettings
                .getBesLoginURL());
        oldProvisioningSettings
                .setAuthentication(convertToOld(provisioningSettings
                        .getAuthentication()));
        return oldProvisioningSettings;
    }

    public ServiceUser convertToOld(
            org.oscm.app.v2_0.data.ServiceUser serviceUser) {
        if (serviceUser == null) {
            return null;
        }
        ServiceUser oldServiceUser = new ServiceUser();
        oldServiceUser.setLocale(serviceUser.getLocale());
        oldServiceUser.setApplicationUserId(serviceUser.getApplicationUserId());
        oldServiceUser.setEmail(serviceUser.getEmail());
        oldServiceUser.setFirstName(serviceUser.getFirstName());
        oldServiceUser.setLastName(serviceUser.getLastName());
        oldServiceUser.setRoleIdentifier(serviceUser.getRoleIdentifier());
        oldServiceUser.setUserId(serviceUser.getUserId());
        return oldServiceUser;
    }

    public User convertToOld(org.oscm.app.v2_0.data.User user) {
        if (user == null) {
            return null;
        }
        User oldUser = new User();
        oldUser.setUserId(user.getUserId());
        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());
        oldUser.setLocale(user.getLocale());
        oldUser.setEmail(user.getEmail());
        oldUser.setUserKey(user.getUserKey());
        return oldUser;
    }

    public List<org.oscm.app.v2_0.data.LocalizedText> convertToNewLocalizedTexts(
            List<LocalizedText> oldDescriptions) {
        if (oldDescriptions == null) {
            return null;
        }
        List<org.oscm.app.v2_0.data.LocalizedText> newDescriptions = new ArrayList<>(
                oldDescriptions.size());
        for (LocalizedText localizedText : oldDescriptions) {
            newDescriptions.add(convertToNew(localizedText));
        }

        return newDescriptions;
    }

    public List<LocalizedText> convertToOldLocalizedTexts(
            List<org.oscm.app.v2_0.data.LocalizedText> newDescriptions) {
        if (newDescriptions == null) {
            return null;
        }
        List<LocalizedText> oldDescriptions = new ArrayList<>(
                newDescriptions.size());
        for (org.oscm.app.v2_0.data.LocalizedText localizedText : newDescriptions) {
            oldDescriptions.add(convertToOld(localizedText));
        }
        return oldDescriptions;
    }

    public List<OperationParameter> convertToOldOperationParametersList(
            List<org.oscm.app.v2_0.data.OperationParameter> parameters) {
        if (parameters == null) {
            return null;
        }
        List<OperationParameter> oldParameters = new ArrayList<>(
                parameters.size());
        for (org.oscm.app.v2_0.data.OperationParameter operationParameter : parameters) {
            oldParameters.add(convertToOld(operationParameter));
        }
        return oldParameters;
    }

    public List<org.oscm.app.v2_0.data.OperationParameter> convertToNew(
            List<OperationParameter> operationParameters) {
        if (operationParameters == null) {
            return null;
        }
        List<org.oscm.app.v2_0.data.OperationParameter> parameters = new ArrayList<>(
                operationParameters.size());
        for (OperationParameter operationParameter : operationParameters) {
            parameters.add(convertToNew(operationParameter));

        }
        return parameters;
    }

    public String convertToOld(org.oscm.app.v2_0.data.Setting setting) {
        if (setting == null) {
            return null;
        }

        return setting.getValue();
    }

    public HashMap<String, org.oscm.app.v2_0.data.Setting> convertToNew(
            HashMap<String, String> map) {
        if (map == null) {
            return null;
        }

        HashMap<String, org.oscm.app.v2_0.data.Setting> newMap = new HashMap<>();
        for (String key : map.keySet()) {
            org.oscm.app.v2_0.data.Setting setting = new org.oscm.app.v2_0.data.Setting(
                    key, map.get(key));
            if (key.endsWith(PASS_SUFFIX) || key.endsWith(PWD_SUFFIX)) {
                setting.setEncrypted(true);
            }
            newMap.put(key, setting);
            if (key.endsWith(PASS_SUFFIX) || key.endsWith(PWD_SUFFIX)) {
                setting.setEncrypted(true);
            }
        }
        return newMap;
    }

    public HashMap<String, String> convertToOld(
            HashMap<String, org.oscm.app.v2_0.data.Setting> map) {
        if (map == null) {
            return null;
        }

        HashMap<String, String> newMap = new HashMap<>();
        for (String key : map.keySet()) {
            newMap.put(key, map.get(key).getValue());
        }
        return newMap;
    }
}
