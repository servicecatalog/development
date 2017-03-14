/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.interceptor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.interceptor.InvocationContext;

import org.junit.Test;

import org.oscm.configurationservice.local.ConfigurationServiceLocal;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.types.constants.Configuration;
import org.oscm.internal.types.enumtypes.ConfigurationKey;

public class AuditLoggingEnabledTest {
    private AuditLoggingEnabled auditLoggingEnabled;

    @Test
    public void isLoggingEnabled_true() throws Exception {
        // given
        AuditLoggingEnabled auditLoggingEnabled = mockAuditLoggingIsEnabled(true);
        InvocationContext context = mock(InvocationContext.class);

        // when
        auditLoggingEnabled.isLoggingEnabled(context);

        // then
        verify(context, times(1)).proceed();
    }

    private AuditLoggingEnabled mockAuditLoggingIsEnabled(boolean isEnabled) {
        auditLoggingEnabled = spy(new AuditLoggingEnabled());
        auditLoggingEnabled.configurationService = mock(ConfigurationServiceLocal.class);
        doReturn(auditLogIsEnabled(isEnabled)).when(
                auditLoggingEnabled.configurationService)
                .getConfigurationSetting(
                        ConfigurationKey.AUDIT_LOG_ENABLED,
                        Configuration.GLOBAL_CONTEXT);
        return auditLoggingEnabled;
    }

    private ConfigurationSetting auditLogIsEnabled(boolean isEnabled) {
        return new ConfigurationSetting(ConfigurationKey.AUDIT_LOG_ENABLED,
                Configuration.GLOBAL_CONTEXT, Boolean.toString(isEnabled));
    }

    @Test
    public void isLoggingEnabled_false() throws Exception {
        // given
        AuditLoggingEnabled auditLoggingEnabled = mockAuditLoggingIsEnabled(false);
        InvocationContext context = mock(InvocationContext.class);

        // when
        auditLoggingEnabled.isLoggingEnabled(context);

        // then
        verify(context, times(0)).proceed();
    }
}
