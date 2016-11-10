/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  AWS controller implementation for the 
 *  Asynchronous Provisioning Platform (APP)
 *       
 *  Creation Date: 2013-10-17                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.common.ui;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.oscm.app.v1_0.data.ControllerConfigurationKey;

/**
 * Bean for reading and writing controller configuration settings.
 */
@SessionScoped
@Named
public class ExtentionInterfaceBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2153894219559699861L;

    public static final String[] ACCESS_PARAMETERS = new String[] {
            ControllerConfigurationKey.BSS_ORGANIZATION_ID.name(),
            ControllerConfigurationKey.BSS_USER_ID.name(),
            ControllerConfigurationKey.BSS_USER_KEY.name(),
            ControllerConfigurationKey.BSS_USER_PWD.name() };

    private ServerInfoModel servers = new ServerInfoModel();

    /**
     * Constructor.
     */
    public ExtentionInterfaceBean() {
    }

    public String getInitialize() {

        return "";
    }

    public ServerInfoModel getInstanceDetails() {
        return servers;
    }

    public void setServerInfo(ServerInfoModel server) {
        this.servers = server;
    }

    public String getSubscriptionName() {
        return "Test instance";
    }

    public String getAccessInfo() {
        return "Access info";
    }

}
