/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 04.07.2012                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.db.importer;

/**
 * Reflects a single IP address configuration.
 */
public class IPAddress {

    private boolean isInUse;
    private String ipaddress;

    public IPAddress() {
        this.isInUse = false;
        this.ipaddress = "";
    }

    public String getIPAddress() {
        return ipaddress;
    }

    public void setIPAddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public void setInUse(boolean inUse) {
        isInUse = inUse;
    }
}
