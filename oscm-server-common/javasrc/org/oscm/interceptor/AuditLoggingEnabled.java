/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.interceptor;

import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.TypedQuery;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.types.constants.Configuration;
import org.oscm.internal.types.enumtypes.ConfigurationKey;

public class AuditLoggingEnabled {

    @EJB
    protected DataService dm;

    @AroundInvoke
    public Object isLoggingEnabled(InvocationContext context) throws Exception {
        Object result = null;

        TypedQuery<ConfigurationSetting> query = dm.createNamedQuery(
                "ConfigurationSetting.findByInfoAndContext",
                ConfigurationSetting.class);
        query.setParameter("informationId", ConfigurationKey.AUDIT_LOG_ENABLED);
        query.setParameter("contextId", Configuration.GLOBAL_CONTEXT);

        ConfigurationSetting setting = query.getSingleResult();

        if (Boolean.parseBoolean(setting.getValue())) {
            result = context.proceed();
        }
        return result;
    }
}
