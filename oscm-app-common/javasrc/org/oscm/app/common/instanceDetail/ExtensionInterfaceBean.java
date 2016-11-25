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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.codec.binary.Base64;
import org.oscm.app.common.i18n.Messages;
import org.oscm.app.common.intf.InstanceAccess;
import org.oscm.app.common.intf.ServerInformation;
import org.oscm.app.v2_0.data.ControllerConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean for showing server information.
 */
@Named
public class ExtensionInterfaceBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2153894219559699861L;
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExtensionInterfaceBean.class);

    public static final String[] ACCESS_PARAMETERS = new String[] {
            ControllerConfigurationKey.BSS_ORGANIZATION_ID.name(),
            ControllerConfigurationKey.BSS_USER_ID.name(),
            ControllerConfigurationKey.BSS_USER_KEY.name(),
            ControllerConfigurationKey.BSS_USER_PWD.name() };

    private List<? extends ServerInformation> servers;
    private String subscriptionId;
    private String organizationId;
    private String accessInfo;
    private String instanceId;
    private String locale;

    @Inject
    private InstanceAccess instanceAccess;

    /**
     * Constructor.
     */
    public ExtensionInterfaceBean() {
        FacesContext facesContext = getContext();
        Map<String, String> parameters = facesContext.getExternalContext()
                .getRequestParameterMap();
        this.locale = facesContext.getViewRoot().getLocale().getLanguage();
        try {
            this.subscriptionId = parameters.get("subId") != null
                    ? new String(
                            Base64.decodeBase64(
                                    parameters.get("subId").getBytes("UTF-8")),
                            "UTF-8")
                    : "";
        } catch (UnsupportedEncodingException e) {
            this.subscriptionId = Messages.get(locale,
                    "ui.extentionInterface.noSubscriptionName");
        }
        this.instanceId = parameters.get("instId") != null
                ? parameters.get("instId") : "";

        this.organizationId = parameters.get("orgId") != null
                ? parameters.get("orgId") : "";
    }

    public void setInstanceAccess(InstanceAccess instanceAccess) {
        this.instanceAccess = instanceAccess;
    }

    public List<? extends ServerInformation> getInstanceDetails() {
        readServerInfo();
        return servers;
    }

    /**
     * 
     */
    private void readServerInfo() {
        List<? extends ServerInformation> serverInfos = new ArrayList<ServerInformation>();
        try {
            serverInfos = instanceAccess.getServerDetails(instanceId,
                    subscriptionId, organizationId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        setServerInfo(serverInfos);
    }

    public void setServerInfo(List<? extends ServerInformation> serverInfos) {
        this.servers = serverInfos;
    }

    public String getSubscriptionName() {
        return subscriptionId;
    }

    public String getAccessInfo() {
        setAccessInfo();
        return accessInfo;
    }

    /**
     *
     */
    private void setAccessInfo() {
        String accessInfo = "";
        try {
            accessInfo = instanceAccess.getAccessInfo(instanceId,
                    subscriptionId, organizationId);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        this.accessInfo = accessInfo;
    }

    // allow stubbing in unit tests
    protected FacesContext getContext() {
        return FacesContext.getCurrentInstance();
    }

}
