/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: Sep 17, 2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.oscm.app.v1_0.data.ProvisioningSettings;

/**
 * @author Dirk Bernsau
 * 
 */
public class ProvisioningSettingsTest {

    @Test
    public void testSetters() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", "value");
        HashMap<String, String> configSettings = new HashMap<String, String>();
        configSettings.put("key2", "value2");
        configSettings.put("key2a", "value2a");

        ProvisioningSettings pSettings = new ProvisioningSettings(parameters,
                configSettings, "en");

        assertSame(parameters, pSettings.getParameters());
        assertSame(configSettings, pSettings.getConfigSettings());

        parameters = new HashMap<String, String>();
        parameters.put("key3", "value3");
        configSettings = new HashMap<String, String>();
        configSettings.put("key4", "value4");
        pSettings.setParameters(parameters);
        pSettings.setConfigSettings(configSettings);

        assertSame(parameters, pSettings.getParameters());
        assertSame(configSettings, pSettings.getConfigSettings());

        pSettings = new ProvisioningSettings(parameters, configSettings, "de");
        pSettings.setOrganizationId("orgId");
        pSettings.setOrganizationName("orgName");
        pSettings.setSubscriptionId("subId");
        pSettings.setBesLoginUrl("besUrl");

        assertEquals("de", pSettings.getLocale());
        assertEquals("orgId", pSettings.getOrganizationId());
        assertEquals("orgName", pSettings.getOrganizationName());
        assertEquals("subId", pSettings.getSubscriptionId());
        assertEquals("besUrl", pSettings.getBesLoginURL());
    }

    private void assertSame(Map<String, String> one, Map<String, String> two) {
        assertNotNull(one);
        assertNotNull(two);
        assertEquals(one.size(), two.size());
        Set<String> keySet = one.keySet();
        keySet.removeAll(two.keySet());
        assertTrue(keySet.isEmpty());
        for (String key : two.keySet()) {
            assertEquals(one.get(key), two.get(key));
        }
    }
}
