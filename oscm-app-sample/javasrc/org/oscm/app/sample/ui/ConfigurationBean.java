/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Sample controller implementation for the 
 *  Asynchronous Provisioning Platform (APP)
 *       
 *  Creation Date: 2012-09-06                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.sample.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.app.sample.i18n.Messages;
import org.oscm.app.v1_0.APPlatformServiceFactory;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.intf.APPlatformService;

/**
 * Bean for reading and writing controller configuration settings.
 */
public class ConfigurationBean {
    private static final Logger logger = LoggerFactory
            .getLogger(ConfigurationBean.class);

    // Reference to an APPlatformService instance
    private APPlatformService platformService;

    // The configuration settings
    private HashMap<String, String> items;

    // Status of the most recent operation
    private String status;

    // Credentials of the controller administrator
    private final String username = "<user>";
    private final String password = "<password>";

    /**
     * Constructor.
     */
    public ConfigurationBean() {
        try {
            platformService = APPlatformServiceFactory.getInstance();
        } catch (IllegalStateException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Returns a map with all the controller configuration settings as key/value
     * pairs.
     * 
     * @return the settings
     */
    public HashMap<String, String> getItems() {
        if (items == null) {
            try {
                // Read settings once
                PasswordAuthentication pwAuth = new PasswordAuthentication(
                        username, password);
                items = platformService.getControllerSettings("ess.sample",
                        pwAuth);

            } catch (Exception e) {
                // Fail until correct credentials are set
                items = new HashMap<String, String>();
                items.put("key A", "value A");
                items.put("key B", "value B");
                items.put("key C", "value C");
                items.put("key D", "value D");
            }
        }
        return items;
    }

    /**
     * Returns the keys of all controller configuration settings.
     * 
     * @return the list of keys
     */
    public List<String> getItemKeys() {
        List<String> keys = new ArrayList<String>();
        keys.addAll(getItems().keySet());
        return keys;
    }

    /**
     * Saves the controller configuration settings.
     */
    public void save() {
        try {
            PasswordAuthentication pwAuth = new PasswordAuthentication(
                    username, password);
            platformService
                    .storeControllerSettings("ess.sample", items, pwAuth);

            // Update status
            Locale currentLocal = FacesContext.getCurrentInstance()
                    .getApplication().getDefaultLocale();
            status = Messages.get(currentLocal.getLanguage(),
                    "ui.config.status.saved");

        } catch (Exception e) {
            status = "*** " + e.getMessage();
        }
    }

    /**
     * Returns the status of the most recent operation.
     * 
     * @return the status
     */
    public String getStatus() {
        return status;
    }
}
