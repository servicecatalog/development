/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: 16.08.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v1_0.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.oscm.app.business.APPlatformControllerFactory;
import org.oscm.app.business.exceptions.BadResultException;
import org.oscm.app.business.exceptions.ServiceInstanceNotFoundException;
import org.oscm.app.dao.ServiceInstanceDAO;
import org.oscm.app.domain.PlatformConfigurationKey;
import org.oscm.app.domain.ServiceInstance;
import org.oscm.app.v1_0.data.ControllerSettings;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.data.User;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.exceptions.AuthenticationException;
import org.oscm.app.v1_0.exceptions.ConfigurationException;
import org.oscm.app.v1_0.exceptions.ControllerLookupException;
import org.oscm.app.v1_0.exceptions.ObjectNotFoundException;
import org.oscm.app.v1_0.exceptions.SuspendException;
import org.oscm.app.v1_0.intf.APPlatformController;
import org.oscm.app.v1_0.intf.APPlatformService;
import org.oscm.vo.VOUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dirk Bernsau
 * 
 */
@Stateless
@Remote(APPlatformService.class)
public class APPlatformServiceBean implements APPlatformService {

    private static final Logger logger = LoggerFactory
            .getLogger(APPlatformServiceBean.class);

    @EJB
    protected APPConfigurationServiceBean configService;

    @EJB
    protected APPConcurrencyServiceBean concurrencyService;

    @EJB
    protected APPCommunicationServiceBean mailService;

    @EJB
    protected APPAuthenticationServiceBean authService;

    @EJB
    protected ServiceInstanceDAO instanceDAO;

    @Override
    public boolean lockServiceInstance(String controllerId, String instanceId,
            PasswordAuthentication authentication) throws APPlatformException {
        authService.authenticateTMForInstance(controllerId, instanceId,
                authentication);
        return concurrencyService.lockServiceInstance(controllerId, instanceId);
    }

    @Override
    public void unlockServiceInstance(String controllerId, String instanceId,
            PasswordAuthentication authentication) throws APPlatformException {
        authService.authenticateTMForInstance(controllerId, instanceId,
                authentication);
        concurrencyService.unlockServiceInstance(controllerId, instanceId);
    }

    @Override
    public boolean exists(String controllerId, String instanceId) {
        return instanceDAO.exists(controllerId, instanceId);
    }

    @Override
    public void sendMail(List<String> mailAddresses, String subject,
            String text) throws APPlatformException {
        try {
            mailService.sendMail(mailAddresses, subject, text);
        } catch (Exception e) {
            logger.warn("Controller cannot send mail for instance processing.",
                    e);
            SuspendException se = new SuspendException(
                    "Controller cannot send mail for instance processing.", e);
            throw se;
        }
    }

    @Override
    public String getEventServiceUrl() throws ConfigurationException {
        String result = configService.getProxyConfigurationSetting(
                PlatformConfigurationKey.APP_BASE_URL);
        if (!result.endsWith("/")) {
            result += "/";
        }
        result += "notify";
        return result;
    }

    @Override
    public String getBSSWebServiceUrl() throws ConfigurationException {
        if ("SAML_SP".equals(configService.getProxyConfigurationSetting(
                PlatformConfigurationKey.BSS_AUTH_MODE))) {
            return configService.getProxyConfigurationSetting(
                    PlatformConfigurationKey.BSS_STS_WEBSERVICE_URL);
        }
        return configService.getProxyConfigurationSetting(
                PlatformConfigurationKey.BSS_WEBSERVICE_URL);
    }

    @Override
    public HashMap<String, String> getControllerSettings(String controllerId,
            PasswordAuthentication authentication) throws APPlatformException {
        authService.authenticateTMForController(controllerId, authentication);
        return configService.getControllerConfigurationSettings(controllerId);
    }

    @Override
    public void storeControllerSettings(String controllerId,
            HashMap<String, String> controllerSettings,
            PasswordAuthentication authentication) throws APPlatformException {

        authService.authenticateTMForController(controllerId, authentication);
        configService.storeControllerConfigurationSettings(controllerId,
                controllerSettings);

        // controllers expect to be notified once the settings are modified
        requestControllerSettings(controllerId);
    }

    @Override
    public User authenticate(String controllerId,
            PasswordAuthentication authentication) throws APPlatformException {
        VOUserDetails vo = authService
                .getAuthenticatedTMForController(controllerId, authentication);
        User user = new User();
        user.setUserKey(vo.getKey());
        user.setUserId(vo.getUserId());
        user.setEmail(vo.getEMail());
        user.setLocale(vo.getLocale());
        user.setLastName(vo.getLastName());
        user.setFirstName(vo.getFirstName());
        return user;
    }

    @Override
    public void requestControllerSettings(String controllerId)
            throws ConfigurationException, ControllerLookupException {
        HashMap<String, String> settings = configService
                .getControllerConfigurationSettings(controllerId);
        APPlatformController controller = APPlatformControllerFactory
                .getInstance(controllerId);
        ControllerSettings controllerSettings = new ControllerSettings(
                settings);
        controllerSettings.setAuthentication(
                configService.getAuthenticationForBESTechnologyManager(
                        controllerId, null, null));
        controller.setControllerSettings(controllerSettings);
    }

    @Override
    public Collection<String> listServiceInstances(String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {
        authService.authenticateTMForController(controllerId, authentication);
        Collection<String> result = new ArrayList<String>();
        List<ServiceInstance> instances = instanceDAO
                .getInstancesForController(controllerId);
        for (ServiceInstance instance : instances) {
            result.add(instance.getInstanceId());
        }
        return result;
    }

    @Override
    public ProvisioningSettings getServiceInstanceDetails(String controllerId,
            String instanceId, PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {
        authService.authenticateTMForController(controllerId, authentication);
        try {
            ServiceInstance instance = instanceDAO.getInstanceById(instanceId);
            return configService.getProvisioningSettings(instance, null);
        } catch (ServiceInstanceNotFoundException e) {
            throw new ObjectNotFoundException(e.getMessage());
        } catch (BadResultException e) {
            throw new APPlatformException(e.getMessage());
        }
    }

    @Override
    public String getBSSWebServiceWSDLUrl() throws ConfigurationException {
        if ("SAML_SP".equals(configService.getProxyConfigurationSetting(
                PlatformConfigurationKey.BSS_AUTH_MODE))) {
            return configService.getProxyConfigurationSetting(
                    PlatformConfigurationKey.BSS_STS_WEBSERVICE_WSDL_URL);
        }
        return configService.getProxyConfigurationSetting(
                PlatformConfigurationKey.BSS_WEBSERVICE_WSDL_URL);
    }

    @Override
    public void storeServiceInstanceDetails(String controllerId,
            String instanceId, ProvisioningSettings settings,
            PasswordAuthentication authentication)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        authService.authenticateTMForInstance(controllerId, instanceId,
                authentication);
        try {
            ServiceInstance instance = instanceDAO.getInstanceById(instanceId);
            instance.setInstanceParameters(settings.getParameters());
        } catch (ServiceInstanceNotFoundException e) {
            throw new ObjectNotFoundException(e.getMessage());
        } catch (BadResultException e) {
            throw new APPlatformException(e.getMessage());
        }
    }
}
