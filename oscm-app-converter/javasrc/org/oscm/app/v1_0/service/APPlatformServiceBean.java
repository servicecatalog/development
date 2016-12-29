/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 21.11.16 14:29
 *
 *******************************************************************************/
package org.oscm.app.v1_0.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.oscm.app.converter.APPInterfaceDataConverter;
import org.oscm.app.converter.APPInterfaceExceptionConverter;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.data.User;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.exceptions.AuthenticationException;
import org.oscm.app.v1_0.exceptions.ConfigurationException;
import org.oscm.app.v1_0.intf.APPlatformService;

@Stateless(mappedName = "java:global/oscm-app/oscm-app/APPlatformServiceBean!org.oscm.app.v1_0.intf.APPlatformService")
@Remote(org.oscm.app.v1_0.intf.APPlatformService.class)
public class APPlatformServiceBean implements APPlatformService {

    private APPInterfaceDataConverter dataConverter;
    private APPInterfaceExceptionConverter exceptionConverter;

    @EJB
    private org.oscm.app.v2_0.intf.APPlatformService delegate;

    @PostConstruct
    public void init() {
        dataConverter = new APPInterfaceDataConverter();
        exceptionConverter = new APPInterfaceExceptionConverter();
    }

    @Override
    public void sendMail(List<String> mailAddresses, String subject, String text)
            throws APPlatformException {
        try {
            delegate.sendMail(mailAddresses, subject, text);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public String getEventServiceUrl() throws ConfigurationException {
        try {
            return delegate.getEventServiceUrl();
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public String getBSSWebServiceUrl() throws ConfigurationException {
        try {
            return delegate.getBSSWebServiceUrl();
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public String getBSSWebServiceWSDLUrl() throws ConfigurationException {
        try {
            return delegate.getBSSWebServiceWSDLUrl();
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public boolean lockServiceInstance(String controllerId, String instanceId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {
        org.oscm.app.v2_0.data.PasswordAuthentication newAuthentication = dataConverter
                .convertToNew(authentication);
        try {
            return delegate.lockServiceInstance(controllerId, instanceId,
                    newAuthentication);
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public void unlockServiceInstance(String controllerId, String instanceId,
            PasswordAuthentication authentication)
            throws AuthenticationException, APPlatformException {
        org.oscm.app.v2_0.data.PasswordAuthentication newAuthentication = dataConverter
                .convertToNew(authentication);
        try {
            delegate.unlockServiceInstance(controllerId, instanceId,
                    newAuthentication);
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public boolean exists(String controllerId, String instanceId) {
        return delegate.exists(controllerId, instanceId);
    }

    @Override
    public HashMap<String, String> getControllerSettings(String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        org.oscm.app.v2_0.data.PasswordAuthentication newAuthentication = dataConverter
                .convertToNew(authentication);
        try {
            return dataConverter.convertToOld(delegate.getControllerSettings(
                    controllerId, newAuthentication));
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public void storeControllerSettings(String controllerId,
            HashMap<String, String> controllerSettings,
            PasswordAuthentication authentication)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        org.oscm.app.v2_0.data.PasswordAuthentication newAuthentication = dataConverter
                .convertToNew(authentication);
        try {
            delegate.storeControllerSettings(controllerId,
                    dataConverter.convertToNew(controllerSettings),
                    newAuthentication);
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public User authenticate(String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        org.oscm.app.v2_0.data.PasswordAuthentication newAuthentication = dataConverter
                .convertToNew(authentication);
        org.oscm.app.v2_0.data.User newUser;
        try {
            newUser = delegate.authenticate(controllerId, newAuthentication);
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
        return dataConverter.convertToOld(newUser);
    }

    @Override
    public void requestControllerSettings(String controllerId)
            throws ConfigurationException, APPlatformException {
        try {
            delegate.requestControllerSettings(controllerId);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public Collection<String> listServiceInstances(String controllerId,
            PasswordAuthentication authentication)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        org.oscm.app.v2_0.data.PasswordAuthentication newAuthentication = dataConverter
                .convertToNew(authentication);
        try {
            return delegate.listServiceInstances(controllerId,
                    newAuthentication);
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }

    @Override
    public ProvisioningSettings getServiceInstanceDetails(String controllerId,
            String instanceId, PasswordAuthentication authentication)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        org.oscm.app.v2_0.data.PasswordAuthentication newAuthentication = dataConverter
                .convertToNew(authentication);
        org.oscm.app.v2_0.data.ProvisioningSettings newServiceInstance = null;
        try {
            newServiceInstance = delegate.getServiceInstanceDetails(
                    controllerId, instanceId, newAuthentication);
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
        return dataConverter.convertToOld(newServiceInstance);
    }

    public void setExceptionConverter(
            APPInterfaceExceptionConverter exceptionConverter) {
        this.exceptionConverter = exceptionConverter;
    }

    public void setDataConverter(APPInterfaceDataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    public org.oscm.app.v2_0.intf.APPlatformService getDelegate() {
        return delegate;
    }

    public void setDelegate(org.oscm.app.v2_0.intf.APPlatformService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void storeServiceInstanceDetails(String controllerId,
            String instanceId, ProvisioningSettings settings,
            PasswordAuthentication authentication)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        try {
            delegate.storeServiceInstanceDetails(controllerId, instanceId,
                    dataConverter.convertToNew(settings),
                    dataConverter.convertToNew(authentication));
        } catch (org.oscm.app.v2_0.exceptions.AuthenticationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.ConfigurationException e) {
            throw exceptionConverter.convertToOld(e);
        } catch (org.oscm.app.v2_0.exceptions.APPlatformException e) {
            throw exceptionConverter.convertToOld(e);
        }
    }
}
