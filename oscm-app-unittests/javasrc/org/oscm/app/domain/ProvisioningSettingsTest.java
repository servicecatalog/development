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

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("key", "value");
        HashMap<String, String> configSettings = new HashMap<>();
        configSettings.put("key2", "value2");
        configSettings.put("key2a", "value2a");

        HashMap<String, String> attributes = new HashMap<>();
        parameters.put("key3", "value3");
        HashMap<String, String> customAttributes = new HashMap<>();
        parameters.put("key4", "value4");

        ProvisioningSettings pSettings = new ProvisioningSettings(parameters,
                configSettings, "en");

        pSettings = new ProvisioningSettings(parameters, attributes,
                customAttributes, configSettings, "en");

        assertSame(parameters, pSettings.getParameters());
        assertSame(attributes, pSettings.getAttributes());
        assertSame(customAttributes, pSettings.getCustomAttributes());
        assertSame(configSettings, pSettings.getConfigSettings());

        parameters = new HashMap<>();
        parameters.put("key5", "value5");
        attributes = new HashMap<>();
        attributes.put("key6", "value6");
        customAttributes = new HashMap<>();
        customAttributes.put("key7", "value7");
        configSettings = new HashMap<>();
        configSettings.put("key8", "value8");
        pSettings.setParameters(parameters);
        pSettings.setAttributes(attributes);
        pSettings.setCustomAttributes(customAttributes);
        pSettings.setConfigSettings(configSettings);

        assertSame(parameters, pSettings.getParameters());
        assertSame(configSettings, pSettings.getConfigSettings());

        pSettings = new ProvisioningSettings(parameters, attributes,
                customAttributes, configSettings, "de");
        pSettings.setOrganizationId("orgId");
        pSettings.setOrganizationName("orgName");
        pSettings.setSubscriptionId("subId");
        pSettings.setReferenceId("refId");
        pSettings.setBesLoginUrl("besUrl");

        assertEquals("de", pSettings.getLocale());
        assertEquals("orgId", pSettings.getOrganizationId());
        assertEquals("orgName", pSettings.getOrganizationName());
        assertEquals("subId", pSettings.getSubscriptionId());
        assertEquals("refId", pSettings.getReferenceId());
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

    @Test
    public void testOverwrite() {

        String controller = "ess.example";

        HashMap<String, String> configSettings = new HashMap<>();
        configSettings.put("key1", "value1s");
        configSettings.put("key2", "value2s");
        configSettings.put("key3", "value3s");
        configSettings.put("key4", "value4s");

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("key2", "value2p");
        parameters.put("key3", "value3p");
        parameters.put("key4", "value4p");

        HashMap<String, String> customAttributes = new HashMap<>();
        customAttributes.put(controller + "_key3", "value3c");
        customAttributes.put(controller + "_key4", "value4c");

        HashMap<String, String> attributes = new HashMap<>();
        attributes.put(controller + "_key4", "value4a");

        ProvisioningSettings pSettings = new ProvisioningSettings(parameters,
                attributes, customAttributes, configSettings, "en");

        pSettings.overwriteProperties(controller);

        assertEquals("value1s", configSettings.get("key1"));
        assertEquals("value2p", configSettings.get("key2"));
        assertEquals("value3c", configSettings.get("key3"));
        assertEquals("value4a", configSettings.get("key4"));
        assertEquals("value3c", parameters.get("key3"));
        assertEquals("value4a", parameters.get("key4"));
    }
}
