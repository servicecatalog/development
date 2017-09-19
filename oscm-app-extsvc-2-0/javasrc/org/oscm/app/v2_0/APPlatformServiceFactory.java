/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-09-27                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v2_0;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.oscm.app.v2_0.intf.APPlatformService;

import java.util.Properties;

/**
 * Factory for creating <code>APPlatformService</code> instances.
 */
public class APPlatformServiceFactory {

    /**
     * Retrieves an <code>APPlatformService</code> instance.
     * 
     * @return the service instance
     */
    public static APPlatformService getInstance() {
        try {

            Properties p = new Properties();
            p.setProperty (Context.INITIAL_CONTEXT_FACTORY,"org.apache.openejb.client.LocalInitialContextFactory");

            InitialContext context = new InitialContext(p);
            Object lookup = context.lookup(APPlatformService.JNDI_NAME);
            if (!APPlatformService.class.isAssignableFrom(lookup.getClass())) {
                throw new IllegalStateException(
                        "Failed to look up APPlatformService. The returned service is not implementing correct interface");
            }
            return (APPlatformService) lookup;
        } catch (NamingException e) {
            throw new IllegalStateException(
                    "No valid platform service available", e);
        }
    }
}
