package org.oscm.app.v2_0;

import org.oscm.app.v2_0.intf.APPTemplateService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by BadziakP on 2017-09-19.
 */
public class APPTemplateServiceFactory {

    public static APPTemplateService getInstance() {
        try {
            Properties p = new Properties();
            p.setProperty (Context.INITIAL_CONTEXT_FACTORY,"org.apache.openejb.client.LocalInitialContextFactory");

            InitialContext context = new InitialContext(p);
            Object lookup = context.lookup(APPTemplateService.JNDI_NAME);
            if (!APPTemplateService.class.isAssignableFrom(lookup.getClass())) {
                throw new IllegalStateException(
                    "Failed to look up APPlatformService. The returned service is not implementing correct interface");
            }
            return (APPTemplateService) lookup;
        } catch (NamingException e) {
            throw new IllegalStateException(
                "No valid platform service available", e);
        }
    }
}
