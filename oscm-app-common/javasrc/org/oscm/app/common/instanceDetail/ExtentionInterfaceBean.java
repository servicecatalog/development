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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.oscm.app.common.intf.InstanceAccess;
import org.oscm.app.common.intf.ServerInformation;
import org.oscm.app.v1_0.data.ControllerConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExtentionInterfaceBean.class);

    public static final String[] ACCESS_PARAMETERS = new String[] {
            ControllerConfigurationKey.BSS_ORGANIZATION_ID.name(),
            ControllerConfigurationKey.BSS_USER_ID.name(),
            ControllerConfigurationKey.BSS_USER_KEY.name(),
            ControllerConfigurationKey.BSS_USER_PWD.name() };

    private List<? extends ServerInformation> servers;
    private InstanceAccess instanceAccess;

    /**
     * Constructor.
     */
    public ExtentionInterfaceBean() {
    }

    @Inject
    public void setInstanceAccess(final InstanceAccess instanceAccess) {
        this.instanceAccess = instanceAccess;
    }

    public String getInitialize() {

        return "";
    }

    public List<? extends ServerInformation> getInstanceDetails() {
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
        List<? extends ServerInformation> serverInfos = new ArrayList<ServerInformation>();
        try {
            serverInfos = instanceAccess
                    .getServerDetails(paramters.get("instId"));
        } catch (Exception e) {
            // TODO throw exception
            LOGGER.error(e.getMessage());
        }
        setServerInfo(serverInfos);
    }

    public void setServerInfo(List<? extends ServerInformation> serverInfos) {
        this.servers = serverInfos;
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
