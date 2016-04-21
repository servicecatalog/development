/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2013 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: Jan 18, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.remote.vmware;

import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

/**
 * @author Dirk Bernsau
 * 
 */
public class ServiceConnection {

    private VimPortType service;
    private ServiceContent content;

    public ServiceConnection(VimPortType service, ServiceContent content) {
        this.service = service;
        this.content = content;
    }

    /**
     * Returns the service interface of the connection.
     * 
     * @return the service interface
     */
    public VimPortType getService() {
        return service;
    }

    /**
     * Returns the service content of the connection.
     * 
     * @return the service content
     */
    public ServiceContent getServiceContent() {
        return content;
    }

    /**
     * Internally disconnect the client.
     * 
     * @throws RuntimeFaultFaultMsg
     */
    protected void disconnect() throws RuntimeFaultFaultMsg {
        service.logout(content.getSessionManager());
    }
}
