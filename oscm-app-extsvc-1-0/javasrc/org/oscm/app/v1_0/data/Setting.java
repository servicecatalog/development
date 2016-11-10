/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Nov 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v1_0.data;

/**
 * Structure class for settings, parameters and attributes.
 * 
 * @author miethaner
 */
public class Setting {

    private String key;
    private String value;
    private boolean encrypted;
    private String controllerId;

    public Setting(String key, String value, boolean encrypted,
            String controllerId) {
        this.key = key;
        this.value = value;
        this.encrypted = encrypted;
        this.controllerId = controllerId;
    }

    public Setting(String key, String value, boolean encrypted) {
        this(key, value, encrypted, null);
    }

    public Setting(String key, String value) {
        this(key, value, false);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getControllerId() {
        return controllerId;
    }

    public void setControllerId(String controllerId) {
        this.controllerId = controllerId;
    }
}
