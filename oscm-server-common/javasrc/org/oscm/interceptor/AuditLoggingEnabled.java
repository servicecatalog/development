/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.interceptor;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.types.constants.Configuration;
import org.oscm.internal.types.enumtypes.ConfigurationKey;

public class AuditLoggingEnabled {
    @EJB
    ConfigurationServiceLocal configurationService;

    @AroundInvoke
    public Object isLoggingEnabled(InvocationContext context) throws Exception {
        Object result = null;
        ConfigurationSetting setting = configurationService
                .getConfigurationSetting(
                        ConfigurationKey.AUDIT_LOG_ENABLED,
                        Configuration.GLOBAL_CONTEXT);
        if (Boolean.parseBoolean(setting.getValue())) {
            result = context.proceed();
        }
        return result;
    }
}
