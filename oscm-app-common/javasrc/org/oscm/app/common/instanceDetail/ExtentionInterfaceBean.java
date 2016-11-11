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
package org.oscm.app.common.instanceDetail;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.oscm.app.common.data.ServerInfo;
import org.oscm.app.common.intf.InstanceAccess;
import org.oscm.app.v1_0.data.ControllerConfigurationKey;

/**
 * Bean for showing server information.
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

    private List<ServerInfo> servers;
    private InstanceAccess instanceAccess;

    /**
     * Constructor.
     */
    public ExtentionInterfaceBean() {
    }

    public String getInitialize() {

        return "";
    }

    public List<ServerInfo> getInstanceDetails() {
        if (servers == null) {
            readServerInfo();
        }
        return servers;
    }

    /**
     * 
     */
    private void readServerInfo() {
        FacesContext facesContext = getContext();
        Map<String, String> paramters = facesContext.getExternalContext()
                .getRequestParameterMap();
        List<ServerInfo> serverInfos = null;
        try {
            serverInfos = instanceAccess
                    .getServerDetails(paramters.get("instId"));
        } catch (Exception e) {
            // TODO throw exception
        }
        setServerInfo(serverInfos);
    }

    public void setServerInfo(List<ServerInfo> servers) {
        this.servers = servers;
    }

    public String getSubscriptionName() {
        return "Test instance";
    }

    public String getAccessInfo() {
        return "http://xxxx:1111/test/url";
    }

    public String getAccessInfoTitle() {
        return "Access info";
    }

    // allow stubbing in unit tests
    protected FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

}
