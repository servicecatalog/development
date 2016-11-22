/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Mar 4, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.oscm.app.v2_0.data.Setting;

/**
 * Unit tests for RORCommonInfo
 * 
 */
public class RORCommonInfoTest {
    private RORCommonInfo rorCommonInfo;
    private PropertyHandler ph;
    private final HashMap<String, Setting> parameters = new HashMap<>();
    private final HashMap<String, Setting> configSettings = new HashMap<>();
    private ProvisioningSettings settings = new ProvisioningSettings(
            parameters, configSettings, "en");

    @Before
    public void setup() throws Exception {
        rorCommonInfo = spy(new RORCommonInfo());
        prepareSettings();
        ph = spy(new PropertyHandler(settings));
    }

    @Test
    public void getVdcClient() throws Exception {
        // when
        RORClient client = rorCommonInfo.getVdcClient(ph);
        // then
        assertEquals(configSettings.get(PropertyHandler.IAAS_API_USER)
                .getValue(), client.getBasicParameters().get("userId"));
        assertEquals(configSettings.get(PropertyHandler.IAAS_API_TENANT)
                .getValue(), client.getBasicParameters().get("orgId"));
    }

    @Test
    public void getLPlatformClient() throws Exception {
        // when
        LPlatformClient client = rorCommonInfo.getLPlatformClient(ph);
        // then
        assertEquals(configSettings.get(PropertyHandler.IAAS_API_USER)
                .getValue(),
                client.getVdcClient().getBasicParameters().get("userId"));
        assertEquals(configSettings.get(PropertyHandler.IAAS_API_TENANT)
                .getValue(),
                client.getVdcClient().getBasicParameters().get("orgId"));
    }

    @Test
    public void getLServerClient() throws Exception {
        // given
        doReturn("serverId").when(ph).getVserverId();
        // when
        rorCommonInfo.getLServerClient(ph);
        // then
        verify(rorCommonInfo, times(1)).getLPlatformClient(eq(ph));
    }

    private void prepareSettings() {
        configSettings.put(PropertyHandler.IAAS_API_URI, new Setting(
                PropertyHandler.IAAS_API_URI,
                "https://ror-demo.fujitsu.com:8014/cfmgapi/endpoint"));
        configSettings.put(PropertyHandler.IAAS_API_LOCALE, new Setting(
                PropertyHandler.IAAS_API_LOCALE, "en"));
        configSettings.put(PropertyHandler.IAAS_API_TENANT, new Setting(
                PropertyHandler.IAAS_API_TENANT, "SampleTenant"));
        configSettings.put(PropertyHandler.IAAS_API_USER, new Setting(
                PropertyHandler.IAAS_API_USER, "tenant_admin"));
        configSettings.put(PropertyHandler.IAAS_API_PWD, new Setting(
                PropertyHandler.IAAS_API_PWD, "tenantadmin"));
    }

}
