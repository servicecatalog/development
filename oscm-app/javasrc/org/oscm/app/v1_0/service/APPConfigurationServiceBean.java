/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 17.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v1_0.service;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.string.Strings;
import org.oscm.app.business.exceptions.BadResultException;
import org.oscm.app.domain.ConfigurationSetting;
import org.oscm.app.domain.InstanceParameter;
import org.oscm.app.domain.PlatformConfigurationKey;
import org.oscm.app.domain.ServiceInstance;
import org.oscm.app.i18n.Messages;
import org.oscm.app.security.AESEncrypter;
import org.oscm.app.v1_0.data.ControllerConfigurationKey;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.data.ServiceUser;
import org.oscm.app.v1_0.exceptions.ConfigurationException;
import org.oscm.vo.VOUserDetails;

/**
 * Implementation of the configuration service.
 * 
 * @author Mike J&auml;ger
 * 
 */
@Stateless
@LocalBean
public class APPConfigurationServiceBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(APPConfigurationServiceBean.class);

    private static final String PROXY_ID = "PROXY";
    private static final String APP_SUSPEND = "APP_SUSPEND";

    /**
     * When value is prefixed with this, encryption is applied to the value and
     * the encryption is written back into the database.
     */
    public static final String CRYPT_PREFIX = "_crypt:";

    /**
     * Setting keys ending with this suffix will have their values stored
     * encrypted.
     */
    public static final String CRYPT_KEY_SUFFIX = "_PWD";

    public static final String CRYPT_KEY_SUFFIX_PASS = "_PASS";

    /**
     * EntityManager to be used for all persistence operations
     */
    @PersistenceContext(name = "persistence/em", unitName = "oscm-app")
    protected EntityManager em;

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public String getProxyConfigurationSetting(PlatformConfigurationKey key)
            throws ConfigurationException {

        LOGGER.debug("Retrieving proxy configuration setting for key {}",
                key.name());
        Query query = em
                .createNamedQuery("ConfigurationSetting.getForProxyKey");
        query.setParameter("key", key.name());
        try {
            ConfigurationSetting result = (ConfigurationSetting) query
                    .getSingleResult();
            return handleDecryption(result);
        } catch (NoResultException e) {
            if (key.name().equals(APP_SUSPEND)) {
                return "";
            } else {
                LOGGER.warn("No entry found for key {}", key.name());
                throw new ConfigurationException(String.format(
                        "No configuration setting found for key '%s'",
                        key.name()), key.name());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public HashMap<String, String> getControllerOrganizations() {

        LOGGER.debug("Retrieving configured controllers");
        HashMap<String, String> result = new HashMap<String, String>();
        Query query = em
                .createNamedQuery("ConfigurationSetting.getControllersForKey");
        query.setParameter("key",
                ControllerConfigurationKey.BSS_ORGANIZATION_ID.name());
        List<?> resultList = query.getResultList();
        for (Object entry : resultList) {
            ConfigurationSetting currentCs = (ConfigurationSetting) entry;
            result.put(currentCs.getControllerId(), currentCs.getSettingValue());
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeControllerOrganizations(
            HashMap<String, String> controllerOrganizations) {

        LOGGER.debug("Storing configured controllers");
        Query query = em
                .createNamedQuery("ConfigurationSetting.getControllersForKey");
        query.setParameter("key",
                ControllerConfigurationKey.BSS_ORGANIZATION_ID.name());
        List<?> resultList = query.getResultList();
        for (Object entry : resultList) {
            ConfigurationSetting currentCs = (ConfigurationSetting) entry;
            String cId = currentCs.getControllerId();
            if (controllerOrganizations.containsKey(cId)) {
                String value = controllerOrganizations.get(cId);
                if (value == null || value.trim().length() == 0) {
                    em.remove(currentCs);
                } else {
                    currentCs.setSettingValue(value);
                    em.persist(currentCs);
                }
                controllerOrganizations.remove(cId);
            }
        }
        for (String key : controllerOrganizations.keySet()) {
            if (controllerOrganizations.get(key) != null
                    && controllerOrganizations.get(key).trim().length() > 0) {
                ConfigurationSetting newSetting = new ConfigurationSetting();
                newSetting.setControllerId(key);
                newSetting
                        .setSettingKey(ControllerConfigurationKey.BSS_ORGANIZATION_ID
                                .name());
                newSetting.setSettingValue(controllerOrganizations.get(key));
                em.persist(newSetting);
            }
        }
    }

    private String handleDecryption(ConfigurationSetting setting)
            throws ConfigurationException {
        try {
            String value = setting.getSettingValue();
            if (value != null
                    && (setting.getSettingKey().endsWith(CRYPT_KEY_SUFFIX) || setting
                            .getSettingKey().endsWith(CRYPT_KEY_SUFFIX_PASS))) {
                if (value.startsWith(CRYPT_PREFIX)) {
                    value = value.substring(CRYPT_PREFIX.length());
                    setting.setSettingValue(AESEncrypter.encrypt(value));
                    em.persist(setting);
                } else {
                    value = AESEncrypter.decrypt(value);
                }
            }
            return value;
        } catch (GeneralSecurityException e) {
            LOGGER.warn("Error while decrypting setting with key {}",
                    setting.getSettingKey());
            throw new ConfigurationException(String.format(
                    "Error while decrypting setting with key  '%s'",
                    setting.getSettingKey()), setting.getSettingKey());
        }
    }

    private void handleEncryption(ConfigurationSetting setting)
            throws ConfigurationException {
        try {
            String value = setting.getSettingValue();
            if (value != null
                    && (setting.getSettingKey().endsWith(CRYPT_KEY_SUFFIX) || setting
                            .getSettingKey().endsWith(CRYPT_KEY_SUFFIX_PASS))) {
                setting.setSettingValue(AESEncrypter.encrypt(value));
            }
        } catch (GeneralSecurityException e) {
            LOGGER.warn("Error while decrypting setting with key {}",
                    setting.getSettingKey());
            throw new ConfigurationException(String.format(
                    "Error while encrypting setting with key  '%s'",
                    setting.getSettingKey()), setting.getSettingKey());
        }
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public HashMap<String, String> getAllProxyConfigurationSettings()
            throws ConfigurationException {
        LOGGER.debug("Retrieving all configuration settings for proxy");
        HashMap<String, String> result = new HashMap<String, String>();
        Query query = em.createNamedQuery("ConfigurationSetting.getAllProxy");
        List<?> resultList = query.getResultList();
        for (Object entry : resultList) {
            ConfigurationSetting currentCs = (ConfigurationSetting) entry;
            result.put(currentCs.getSettingKey(), handleDecryption(currentCs));
        }
        PlatformConfigurationKey[] keys = PlatformConfigurationKey.values();
        StringBuffer missing = new StringBuffer();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].isMandatory() && !result.containsKey(keys[i].name())) {
                if (missing.length() > 0) {
                    missing.append(", ");
                }
                missing.append(keys[i].name());
            }
        }
        if (missing.length() > 0) {
            throw new ConfigurationException(
                    "The configuration is missing the following parameter(s): "
                            + missing.toString(), missing.toString());
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public VOUserDetails getAPPAdministrator() throws ConfigurationException {
        VOUserDetails adminuser = new VOUserDetails();
        String adminemail = getProxyConfigurationSetting(PlatformConfigurationKey.APP_ADMIN_MAIL_ADDRESS);
        if (!Strings.isEmpty(adminemail)) {
            adminuser.setEMail(adminemail);
            adminuser.setLocale(Messages.DEFAULT_LOCALE);
        }
        return adminuser;
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public HashMap<String, String> getControllerConfigurationSettings(
            String controllerId) throws ConfigurationException {

        LOGGER.debug("Retrieving configuration settings for controller '{}'",
                controllerId);
        HashMap<String, String> result = new HashMap<String, String>();
        if (controllerId != null) {
            Query query = em
                    .createNamedQuery("ConfigurationSetting.getForController");
            query.setParameter("controllerId", controllerId);
            List<?> resultList = query.getResultList();
            for (Object entry : resultList) {
                ConfigurationSetting currentCs = (ConfigurationSetting) entry;
                result.put(currentCs.getSettingKey(),
                        handleDecryption(currentCs));
            }
        }
        ControllerConfigurationKey[] keys = ControllerConfigurationKey.values();
        StringBuffer missing = new StringBuffer();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].isMandatory() && !result.containsKey(keys[i].name())) {
                if (missing.length() > 0) {
                    missing.append(", ");
                }
                missing.append(keys[i].name());
            }
        }
        if (missing.length() > 0) {
            throw new ConfigurationException(
                    "The controller configuration is missing the following parameter(s): "
                            + missing.toString(), missing.toString());
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void setAPPSuspend(String isSupsend) {
        LOGGER.debug("Storing suspend setting for APP");
        HashMap<String, String> setting = new HashMap<String, String>();
        setting.put(APP_SUSPEND, isSupsend);
        try {
            storeControllerConfigurationSettings(PROXY_ID, setting);
        } catch (ConfigurationException exception) {
            // this exception should not happen due to no encryption needed for
            // APP_SUSPEND, no handle needed
        }
    }

    public boolean isAPPSuspend() {
        String isSuspend = "";
        try {
            isSuspend = getProxyConfigurationSetting(PlatformConfigurationKey.APP_SUSPEND);
        } catch (ConfigurationException exception) {
            // this exception should not happen due to no decryption needed for
            // APP_SUSPEND, no handle needed
        }
        if (!Strings.isEmpty(isSuspend)) {
            return Boolean.valueOf(isSuspend).booleanValue();
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeControllerConfigurationSettings(String controllerId,
            HashMap<String, String> settings) throws ConfigurationException {

        LOGGER.debug("Storing configuration settings for controller '{}'",
                controllerId);
        if (controllerId == null || settings == null) {
            throw new IllegalArgumentException("All parameters must be set");
        }
        Query query = em
                .createNamedQuery("ConfigurationSetting.getForController");
        query.setParameter("controllerId", controllerId);
        List<?> resultList = query.getResultList();
        for (Object entry : resultList) {
            ConfigurationSetting setting = (ConfigurationSetting) entry;
            String key = setting.getSettingKey();
            if (settings.containsKey(key)) {
                if (settings.get(key) == null) {
                    em.remove(setting);
                } else {
                    setting.setSettingValue(settings.get(key));
                    handleEncryption(setting);
                    em.persist(setting);
                }
                settings.remove(key);
            }
        }
        for (String newKey : settings.keySet()) {
            ConfigurationSetting newSetting = new ConfigurationSetting();
            newSetting.setControllerId(controllerId);
            newSetting.setSettingKey(newKey);
            newSetting.setSettingValue(settings.get(newKey));
            handleEncryption(newSetting);
            em.persist(newSetting);
        }
    }

    /**
     * Creates settings instance from given service instance.
     */
    public ProvisioningSettings getProvisioningSettings(
            final ServiceInstance instance, final ServiceUser requestingUser)
            throws BadResultException, ConfigurationException {
        final HashMap<String, String> controllerSettings = getControllerConfigurationSettings(instance
                .getControllerId());
        final ProvisioningSettings settings = new ProvisioningSettings(
                instance.getParameterMap(), controllerSettings,
                instance.getDefaultLocale());

        settings.setOrganizationId(instance.getOrganizationId());
        settings.setOrganizationName(instance.getOrganizationName());
        settings.setSubscriptionId(instance.getSubscriptionId());
        settings.setBesLoginUrl(instance.getBesLoginURL());
        settings.setRequestingUser(requestingUser);
        settings.setAuthentication(getAuthenticationForBESTechnologyManager(
                null, instance, null));
        copyCredentialsFromControllerSettings(settings, controllerSettings);
        return settings;
    }

    /**
     * For backwards compatibility the technology manager credentials from the
     * controller configuration are applied to the instance parameters.
     * 
     * @param settings
     * @param controllerSettings
     */
    public void copyCredentialsFromControllerSettings(
            ProvisioningSettings settings,
            HashMap<String, String> controllerSettings) {
        String userKey = controllerSettings
                .get(ControllerConfigurationKey.BSS_USER_KEY.name());
        String userPwd = controllerSettings
                .get(ControllerConfigurationKey.BSS_USER_PWD.name());
        if (!Strings.isEmpty(userKey) && !Strings.isEmpty(userPwd)) {
            // override technology manager user credentials in parameters
            // (for backwards compatibility)
            settings.getParameters().put(InstanceParameter.BSS_USER, userKey);
            settings.getParameters().put(InstanceParameter.BSS_USER_PWD,
                    userPwd);
        }
    }

    public PasswordAuthentication getWebServiceAuthentication(
            ServiceInstance serviceInstance, Map<String, String> proxySettings)
            throws ConfigurationException {
        if (serviceInstance != null) {
            return getAuthenticationForBESTechnologyManager(null,
                    serviceInstance, proxySettings);
        }
        return getAuthenticationForAPPAdmin(proxySettings);
    }

    public PasswordAuthentication getAuthenticationForBESTechnologyManager(
            String controllerId, ServiceInstance serviceInstance,
            Map<String, String> proxySettings) throws ConfigurationException {
        if (proxySettings == null) {
            proxySettings = getAllProxyConfigurationSettings();
        }
        boolean isSso = isSsoMode(proxySettings);
        if (serviceInstance != null) {
            controllerId = serviceInstance.getControllerId();
        }
        HashMap<String, String> controllerSettings = getControllerConfigurationSettings(controllerId);

        String usernameKey = isSso ? ControllerConfigurationKey.BSS_USER_ID
                .name() : ControllerConfigurationKey.BSS_USER_KEY.name();
        String user = controllerSettings.get(usernameKey);
        String userPwd = controllerSettings
                .get(ControllerConfigurationKey.BSS_USER_PWD.name());

        if (!Strings.isEmpty(user) ^ !Strings.isEmpty(userPwd)) {
            LOGGER.warn(
                    "The controller settings for controller '{}' define incomplete technology manager credentials. Please define values for both {} and {}.",
                    new String[] { controllerId, usernameKey,
                            ControllerConfigurationKey.BSS_USER_PWD.name() });
        }
        String ws_username = null;
        String ws_password = null;

        if (!Strings.isEmpty(user) && !Strings.isEmpty(userPwd)) {
            ws_username = user;
            ws_password = userPwd;
        } else {
            if (serviceInstance != null) {
                InstanceParameter userParam = serviceInstance
                        .getParameterForKey(InstanceParameter.BSS_USER);
                InstanceParameter userPwdParam = serviceInstance
                        .getParameterForKey(InstanceParameter.BSS_USER_PWD);
                try {
                    ws_username = userParam == null ? null : userParam
                            .getDecryptedValue();
                    ws_password = userPwdParam == null ? null : userPwdParam
                            .getDecryptedValue();
                } catch (BadResultException e) {
                    throw new ConfigurationException(e.getMessage(),
                            InstanceParameter.BSS_USER_PWD);
                }
            }
            if (Strings.isEmpty(ws_username) || ws_password == null) {
                LOGGER.error(
                        "Request context for web service call is incomplete due to missing credentials. Please check controller settings [{}].",
                        new String[] { controllerId });
                throw new ConfigurationException(
                        "The controller settings for controller '"
                                + controllerId
                                + "' are missing complete technology manager credentials. Please define values for both "
                                + usernameKey
                                + " and "
                                + ControllerConfigurationKey.BSS_USER_PWD
                                        .name(),
                        usernameKey);

            }
        }
        return new PasswordAuthentication(ws_username, ws_password);
    }

    public PasswordAuthentication getAuthenticationForAPPAdmin(
            Map<String, String> proxySettings) throws ConfigurationException {
        if (proxySettings == null) {
            proxySettings = getAllProxyConfigurationSettings();
        }
        boolean isSso = isSsoMode(proxySettings);
        String usernameKey = isSso ? PlatformConfigurationKey.BSS_USER_ID
                .name() : PlatformConfigurationKey.BSS_USER_KEY.name();
        String ws_username = proxySettings.get(usernameKey);
        String ws_password = proxySettings
                .get(PlatformConfigurationKey.BSS_USER_PWD.name());
        if (Strings.isEmpty(ws_username) || ws_password == null) {
            LOGGER.error("Request context for web service call is incomplete due to missing credentials. Please check platform settings.");
            throw new ConfigurationException(
                    "The APP configuration settings define incomplete admin credentials. Please define values for both "
                            + usernameKey
                            + " and "
                            + PlatformConfigurationKey.BSS_USER_PWD.name(),
                    usernameKey);
        }
        return new PasswordAuthentication(ws_username, ws_password);
    }

    private boolean isSsoMode(Map<String, String> settings) {
        return "SAML_SP".equals(settings
                .get(PlatformConfigurationKey.BSS_AUTH_MODE.name()));
    }

}
