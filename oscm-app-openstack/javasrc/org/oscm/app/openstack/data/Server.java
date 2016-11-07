/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 29.09.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack.data;

/**
 * @author tateiwamext
 *
 */
public class Server {
    private String id;
    private String name;
    private String status;

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
}
