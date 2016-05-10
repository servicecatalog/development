/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) FUJITSU LIMITED - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 26.05.2014                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.fujitsu.bss.app.v1_0.data.PasswordAuthentication;
import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.v1_0.data.User;
import com.fujitsu.bss.app.v1_0.exceptions.APPlatformException;
import com.fujitsu.bss.app.v1_0.exceptions.AuthenticationException;
import com.fujitsu.bss.app.v1_0.exceptions.ConfigurationException;
import com.fujitsu.bss.app.v1_0.intf.APPlatformService;

/**
 * Mockup for the APP platform service
 */
public class APPlatformServiceMockup implements APPlatformService {
    public APPlatformException exceptionOnGetControllerSettings;
    private HashMap<String, String> ctrl_settings;

    public APPlatformServiceMockup() {
        // Set default AWS controller settings
        ctrl_settings = new HashMap<String, String>();
        ctrl_settings.put("BSS_ORGANIZATION_ID", "orgId");
        ctrl_settings.put("BSS_USER_KEY", "12345");
        ctrl_settings.put("BSS_USER_ID", "userId");
        ctrl_settings.put("BSS_USER_PWD", "secret1");
    }

    public void sendMail(List<String> mailAddresses, String subject, String text)
            throws APPlatformException {
    }

    public String getEventServiceUrl() throws ConfigurationException {
        return null;
    }

    public boolean exists(String controllerId, String instanceId) {
        return false;
    }

    public String getBSSWebServiceUrl() throws ConfigurationException {
        return null;
    }

    public HashMap<String, String> getControllerSettings(String arg0,
            PasswordAuthentication arg1) throws AuthenticationException,
            ConfigurationException, APPlatformException {
        if (exceptionOnGetControllerSettings != null) {
            throw exceptionOnGetControllerSettings;
        }
        return ctrl_settings;
    }

    public boolean lockServiceInstance(String arg0, String arg1,
            PasswordAuthentication arg2) throws AuthenticationException,
            APPlatformException {
        return false;
    }

    public void storeControllerSettings(String arg0,
            HashMap<String, String> controllerSettings,
            PasswordAuthentication arg2) throws AuthenticationException,
            ConfigurationException, APPlatformException {
        ctrl_settings = controllerSettings;
    }

    public void unlockServiceInstance(String arg0, String arg1,
            PasswordAuthentication arg2) throws AuthenticationException,
            APPlatformException {
    }

    @Override
    public User authenticate(String arg0, PasswordAuthentication arg1)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        return null;
    }

    @Override
    public ProvisioningSettings getServiceInstanceDetails(String arg0,
            String arg1, PasswordAuthentication arg2)
            throws AuthenticationException, ConfigurationException,
            APPlatformException {
        return null;
    }

    @Override
    public Collection<String> listServiceInstances(String arg0,
            PasswordAuthentication arg1) throws AuthenticationException,
            ConfigurationException, APPlatformException {
        return null;
    }

    @Override
    public void requestControllerSettings(String arg0)
            throws ConfigurationException, APPlatformException {
    }

}
