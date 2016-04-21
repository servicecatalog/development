/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 04.07.2012                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.business.model;

/**
 * Reflects a single IP address configuration.
 */
public class IPAddress {

    private String ipaddress = "";

    public String getIPAddress() {
        return ipaddress;
    }

    public void setIPAddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

}
