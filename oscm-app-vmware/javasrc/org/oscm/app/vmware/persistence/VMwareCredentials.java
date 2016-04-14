/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2014 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 25.04.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.persistence;

public class VMwareCredentials {

    private final String url;
    private final String userId;
    private final String password;

    public VMwareCredentials(String url, String userId, String password) {
        this.url = url;
        this.userId = userId;
        this.password = password;
    }

    public String getURL() {
        return url;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

}
