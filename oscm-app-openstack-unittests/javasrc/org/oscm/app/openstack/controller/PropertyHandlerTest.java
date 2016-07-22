/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                        
 *       
 *  Unit test.
 *       
 *  Creation Date: 2013-11-29                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.openstack.controller;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.oscm.app.openstack.data.FlowState;
import org.oscm.app.openstack.exceptions.HeatException;
import org.oscm.app.openstack.i18n.Messages;
import org.oscm.app.v1_0.data.LocalizedText;
import org.oscm.app.v1_0.data.ProvisioningSettings;

public class PropertyHandlerTest {

    private HashMap<String, String> parameters;
    private HashMap<String, String> configSettings;
    private ProvisioningSettings settings;
    private PropertyHandler propertyHandler;

    @Before
    public void setUp() {
        parameters = new HashMap<String, String>();
        configSettings = new HashMap<String, String>();
        settings = new ProvisioningSettings(parameters, configSettings, "en");
        propertyHandler = new PropertyHandler(settings);
    }

    @Test()
    public void testGetInstanceName() {
        parameters.put(PropertyHandler.STACK_NAME, "db2");
        propertyHandler = new PropertyHandler(settings);
        String instanceName = propertyHandler.getStackName();
        assertEquals("db2", instanceName);
    }

    @Test()
    public void testGetTemplateURL() throws HeatException {

        // case 1
        parameters.put(PropertyHandler.TEMPLATE_NAME, "template1.json");
        configSettings
                .put(PropertyHandler.TEMPLATE_BASE_URL, "http://test.com");
        propertyHandler = new PropertyHandler(settings);
        String url = propertyHandler.getTemplateUrl();
        assertEquals("http://test.com/template1.json", url);

        // case 2
        parameters.put(PropertyHandler.TEMPLATE_NAME,
                "https://other.com/template1.json");
        url = propertyHandler.getTemplateUrl();
        assertEquals("https://other.com/template1.json", url);

        // case 3
        configSettings.put(PropertyHandler.TEMPLATE_BASE_URL,
                "http://test.com/templates");
        parameters.put(PropertyHandler.TEMPLATE_NAME, "templates/apache.json");
        url = propertyHandler.getTemplateUrl();
        assertEquals("http://test.com/templates/apache.json", url);

    }

    @Test()
    public void testGetInstanceTenant() {
        parameters.put(PropertyHandler.TENANT_NAME, "12345");
        configSettings.put(PropertyHandler.TENANT_NAME, "23455");
        propertyHandler = new PropertyHandler(settings);
        String tenantName = propertyHandler.getTenantName();
        assertEquals("12345", tenantName);
    }

    @Test()
    public void testGetControllerTenant() {
        configSettings.put(PropertyHandler.TENANT_NAME, "23455");
        propertyHandler = new PropertyHandler(settings);
        String tenantName = propertyHandler.getTenantName();
        assertEquals("23455", tenantName);
        parameters.put(PropertyHandler.TENANT_NAME, "");
        tenantName = propertyHandler.getTenantName();
        assertEquals("23455", tenantName);
    }

    @Test()
    public void testGetSettings() {
        propertyHandler = new PropertyHandler(settings);
        ProvisioningSettings testSettings = propertyHandler.getSettings();
        assertNotNull(testSettings);
        assertEquals(settings, testSettings);
    }

    @Test()
    public void testGetState_NullValue() {
        FlowState status = propertyHandler.getState();
        assertEquals(FlowState.FAILED, status);
    }

    @Test()
    public void testGetState() {
        parameters.put(PropertyHandler.STATUS, "CREATING_STACK");
        propertyHandler = new PropertyHandler(settings);
        FlowState status = propertyHandler.getState();
        assertEquals(FlowState.CREATING_STACK, status);
    }

    @Test()
    public void testSetState() {
        parameters.put(PropertyHandler.STATUS, "CREATION_EXECUTING_WORKLOAD");
        propertyHandler = new PropertyHandler(settings);
        propertyHandler.setState(FlowState.FINISHED);
        FlowState status = propertyHandler.getState();
        assertEquals(FlowState.FINISHED, status);
    }

    @Test
    public void testMissingResource() {
        String message = Messages.get("de", "key");
        assertEquals("!key!", message);
    }

    @Test
    public void testLocalizedResources() {
        String messageEn = Messages.get("en",
                "status_" + FlowState.UPDATING.name());
        String messageFr = Messages.get("fr",
                "status_" + FlowState.UPDATING.name());
        assertTrue(messageEn.equals(messageFr));
    }

    @Test
    public void testArgsResources() {
        String messageEn = Messages.get("en",
                "status_" + FlowState.UPDATING.name());
        String messageEnParam = Messages.get("en", "status_"
                + FlowState.UPDATING.name(), "param 1");
        assertTrue(messageEn.equals(messageEnParam));
    }

    @Test
    public void testGetAllResources() {
        List<LocalizedText> all = Messages.getAll("status_"
                + FlowState.UPDATING.name(), "testArg");
        String messageEn = Messages.get("en",
                "status_" + FlowState.UPDATING.name());
        String found = null;
        for (LocalizedText test : all) {
            if ("en".equals(test.getLocale())) {
                found = test.getText();
            }
        }
        assertEquals(messageEn, found);
    }

    @Test
    public void getMailForCompletion_null() {
        String mail = propertyHandler.getMailForCompletion();
        assertNull(mail);
    }

    @Test
    public void getMailForCompletion_empty() {
        String mailAddress = "";
        propertyHandler.getSettings().getParameters()
                .put(PropertyHandler.MAIL_FOR_COMPLETION, mailAddress);
        String mail = propertyHandler.getMailForCompletion();
        assertNull(mail);
    }

    @Test
    public void getMailForCompletion_notNull() {
        String mailAddress = "test@test.com";
        propertyHandler.getSettings().getParameters()
                .put(PropertyHandler.MAIL_FOR_COMPLETION, mailAddress);
        String mail = propertyHandler.getMailForCompletion();
        assertEquals(mailAddress, mail);
    }

    @Test
    public void getKeystoneAPI_v3(){
    	// given
    	String keystoneEndpoint = "https://keystone/v3/auth";
        configSettings.put(PropertyHandler.KEYSTONE_API_URL,
                keystoneEndpoint);

    	// when
    	String  version = propertyHandler.getKeystoneAPIVersion();

    	// then
    	assertTrue((version).equals("v3"));
    }

    @Test
    public void getKeystoneAPI_v2(){
    	// given
    	String keystoneEndpoint = "http://keystone";
        configSettings.put(PropertyHandler.KEYSTONE_API_URL,
                keystoneEndpoint);

    	// when
    	String  version = propertyHandler.getKeystoneAPIVersion();

    	// then
    	assertTrue((version).equals("v2"));
    }

    @Test(expected = RuntimeException.class)
    public void getKeystoneAPI_empty(){
    	// when
    	propertyHandler.getKeystoneAPIVersion();
    }
}
