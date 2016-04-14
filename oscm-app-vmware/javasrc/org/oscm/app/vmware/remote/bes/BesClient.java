/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2013 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 22.04.2013                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.remote.bes;

import org.oscm.app.v1_0.BSSWebServiceFactory;
import org.oscm.app.vmware.persistence.APPDataAccessService;

public class BesClient {

    public <T> T getWebServiceAsOrganizationAdmin(Class<T> webService,
            String orgId) throws Exception {
        APPDataAccessService das = new APPDataAccessService();
        Credentials cred = das.getCredentials(orgId);
        return getWebService(webService, cred);
    }

    public <T> T getWebService(Class<T> serviceClass, Credentials credentials)
            throws Exception {
        return BSSWebServiceFactory.getBSSWebService(serviceClass,
                credentials.getPasswordAuthentication());
    }

}
