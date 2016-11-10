/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 27.05.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.common.ui;

import java.io.Serializable;

/**
 * Value object for configuration settings.
 */
public class ServerInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 2558051545969373003L;
    private String name;
    private String id;
    private String type;
    private String status;
    private String publicIP;
    private String privateIP;

    public ServerInfo(String name, String id, String type, String status,
            String publicIP, String privateIP) {
        this.setName(name);
        this.setId(id);
        this.setType(type);
        this.setStatus(status);
        this.setPublicIP(publicIP);
        this.setPrivateIP(privateIP);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the publicIP
     */
    public String getPublicIP() {
        return publicIP;
    }

    /**
     * @param publicIP
     *            the publicIP to set
     */
    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

    /**
     * @return the privateIP
     */
    public String getPrivateIP() {
        return privateIP;
    }

    /**
     * @param privateIP
     *            the privateIP to set
     */
    public void setPrivateIP(String privateIP) {
        this.privateIP = privateIP;
    }

}
