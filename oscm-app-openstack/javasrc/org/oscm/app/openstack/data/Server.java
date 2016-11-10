/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 29.09.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack.data;

import java.util.List;

/**
 * @author tateiwamext
 *
 */
public class Server {
    private String id;
    private String name;
    private String status;
    private String flavor;
    private List<String> floatingIP;
    private List<String> fixedIP;

    /**
     * @param serverId
     */
    public Server(String id) {
        this.id = id;
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
     * @return the flavor
     */
    public String getFlavor() {
        return flavor;
    }

    /**
     * @param flavor
     *            the flavor to set
     */
    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    /**
     * @return the floatingIP
     */
    public List<String> getFloatingIP() {
        return floatingIP;
    }

    /**
     * @param floatingIP
     *            the floatingIP to set
     */
    public void setFloatingIP(List<String> floatingIP) {
        this.floatingIP = floatingIP;
    }

    /**
     * @return the fixedIP
     */
    public List<String> getFixedIP() {
        return fixedIP;
    }

    /**
     * @param fixedIP
     *            the fixedIP to set
     */
    public void setFixedIP(List<String> fixedIP) {
        this.fixedIP = fixedIP;
    }
}
