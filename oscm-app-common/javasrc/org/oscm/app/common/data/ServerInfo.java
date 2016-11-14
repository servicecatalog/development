/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *                                                                              
 *  Creation Date: 27.05.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.common.data;

import java.util.List;

/**
 * Value object for configuration settings.
 */
public class ServerInfo {

    private String name;
    private String id;
    private String type;
    private String status;
    private List<String> publicIP;
    private List<String> privateIP;

    /**
     * @param name
     * @param id
     * @param type
     * @param status
     * @param publicIP
     * @param privateIP
     */
    public ServerInfo(String name, String id, String type, String status,
            List<String> publicIP, List<String> privateIP) {
        // TODO Auto-generated constructor stub
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
    public List<String> getPublicIP() {
        return publicIP;
    }

    /**
     * @param publicIP
     *            the publicIP to set
     */
    public void setPublicIP(List<String> publicIP) {
        this.publicIP = publicIP;
    }

    /**
     * @return the privateIP
     */
    public List<String> getPrivateIP() {
        return privateIP;
    }

    /**
     * @param privateIP
     *            the privateIP to set
     */
    public void setPrivateIP(List<String> privateIP) {
        this.privateIP = privateIP;
    }

    /**
     * public String getPrivateIPasList(){ StringBuilder sb = new
     * StringBuilder(); for (Iterator iterator = privateIP.iterator();
     * iterator.hasNext();) { String string = (String) iterator.next();
     * 
     * } }
     */

}
