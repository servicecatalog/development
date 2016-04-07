/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *******************************************************************************/

package org.oscm.billing.external.adapter.bean;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.oscm.billing.external.billing.service.BillingPluginService;
import org.oscm.billing.external.exception.BillingException;
import com.sun.jersey.api.client.WebResource;

/**
 * The implementation of the billing plugin interface
 *
 */
@Stateless
@Remote({ BillingPluginService.class })
public class BillingPlugin implements BillingPluginService {

    public static final String ID = "FILE_BILLING";
    public static final String TEST_CONNECTION_URL = "testConnectionURL";

    ConfigProperties properties = new ConfigProperties(ID);
    RestDAO restDao = new RestDAO();

    @Override
    public void testConnection() throws BillingException {

        String configProperty = properties
                .getConfigProperty(TEST_CONNECTION_URL);

        WebResource webResource = restDao.createWebResource(configProperty);
        restDao.getTextResponse(webResource);
    }

}
