/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Aug 1, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import org.oscm.app.openstack.controller.OpenStackStatus;
import org.oscm.app.openstack.controller.PropertyHandler;
import org.oscm.app.openstack.data.Stack;
import org.oscm.app.openstack.exceptions.HeatException;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.exceptions.InstanceNotAliveException;

/**
 * @author farmaki
 * 
 */
public class HeatProcessorTest {

    private final HashMap<String, String> parameters = new HashMap<String, String>();
    private final HashMap<String, String> configSettings = new HashMap<String, String>();
    private final ProvisioningSettings settings = new ProvisioningSettings(
            parameters, configSettings, "en");
    private final PropertyHandler paramHandler = new PropertyHandler(settings);

    private final MockURLStreamHandler streamHandler = new MockURLStreamHandler();

    @Before
    public void setUp() throws Exception {
        OpenStackConnection.setURLStreamHandler(streamHandler);
        HeatProcessor.setURLStreamHandler(streamHandler);
        paramHandler.setStackId("sID");
    }

    @Test
    public void createStack() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);

        // when
        new HeatProcessor().createStack(paramHandler);

        // then
        assertEquals("idValue", paramHandler.getStackId());
        assertTrue(paramHandler.getStackName().startsWith(instanceName));
    }

    @Test
    public void getStackDetails() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);

        // when
        Stack result = new HeatProcessor().getStackDetails(paramHandler);

        // then
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
        assertEquals("ID", result.getId());
        assertEquals("SSR", result.getStatusReason());
    }

    @Test
    public void getStackDetails_WithoutStatusReason() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);
        streamHandler.put("/stacks/" + instanceName, new MockHttpURLConnection(
                200, MockURLStreamHandler.respStacksInstanceName(null, false)));

        // when
        Stack result = new HeatProcessor().getStackDetails(paramHandler);

        // then
        assertEquals("ID", result.getId());
        assertEquals("n/a", result.getStatusReason());
    }

    @Test
    public void getStackDetails_JSONException() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);
        streamHandler.put("/stacks/" + instanceName, new MockHttpURLConnection(
                200, " "));

        // when
        try {
            new HeatProcessor().getStackDetails(paramHandler);
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            // then
            assertEquals(
                    "A JSONObject text must begin with '{' at character 0 of ",
                    ex.getMessage());
        }
    }

    @Test
    public void resumeStack_notSuspended() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);

        // when
        new HeatProcessor().resumeStack(paramHandler);

        // then
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test
    public void resumeStack() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);
        streamHandler.put(
                "/stacks/" + instanceName,
                new MockHttpURLConnection(200, MockURLStreamHandler
                        .respStacksInstanceName(
                                OpenStackStatus.SUSPEND_COMPLETE, false)));

        // when
        new HeatProcessor().resumeStack(paramHandler);

        // then
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test(expected = InstanceNotAliveException.class)
    public void resumeStack_InstanceNotAliveException_serverNameError()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);
        streamHandler.put(
                "/stacks/" + instanceName,
                new MockHttpURLConnection(200, MockURLStreamHandler
                        .respStacksInstanceName(
                                OpenStackStatus.SUSPEND_COMPLETE, false)));
        streamHandler.put("/servers/serverId", new MockHttpURLConnection(200,
                MockURLStreamHandler.respServer("test")));

        // when
        new HeatProcessor().resumeStack(paramHandler);
    }

    @Test(expected = RuntimeException.class)
    public void suspendStack_InstanceNotAliveException_serverIdError()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);
        streamHandler.put(
                "/stacks/Instance4/resources",
                new MockHttpURLConnection(200, MockURLStreamHandler
                        .respStacksResources(true, "servId")));
        // when
        new HeatProcessor().suspendStack(paramHandler);
    }

    @Test(expected = InstanceNotAliveException.class)
    public void suspendStack_InstanceNotAliveException_serverMissing()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);
        streamHandler.put(
                "/stacks/Instance4/resources",
                new MockHttpURLConnection(200, MockURLStreamHandler
                        .respStacksResources(false, "servId")));
        // when
        new HeatProcessor().suspendStack(paramHandler);
    }

    @Test
    public void suspendStack() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName);

        // when
        new HeatProcessor().suspendStack(paramHandler);

        // then
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test
    public void createStack_JSONException() throws Exception {
        // given
        final String instanceName = "Instance4";
        streamHandler.put("/stacks", new MockHttpURLConnection(200,
                "no valid JSON"));
        streamHandler.put("/stacks/" + instanceName + "-1905561714",
                new MockHttpURLConnection(400, ""));
        createBasicParameters(instanceName);

        // when
        try {
            new HeatProcessor().createStack(paramHandler);
            assertTrue("Test must fail with HeatException!", false);
        } catch (HeatException ex) {
            // then
            assertEquals(
                    "A JSONObject text must begin with '{' at character 1 of no valid JSON",
                    ex.getMessage());
        }
    }

    private void createBasicParameters(String instanceName) {
        parameters.put(PropertyHandler.STACK_NAME, instanceName);
        parameters.put(PropertyHandler.TEMPLATE_NAME, "fosi_v2.json");
        parameters.put(PropertyHandler.TEMPLATE_PARAMETER_PREFIX + "KeyName",
                "key");
        configSettings.put(PropertyHandler.KEYSTONE_API_URL,
                "http://keystone:8080");
        configSettings.put(PropertyHandler.API_USER_NAME, "api_user");
        configSettings.put(PropertyHandler.API_USER_PWD, "secret");
        configSettings.put(PropertyHandler.TENANT_NAME, "demo");
        configSettings.put(PropertyHandler.TEMPLATE_BASE_URL,
                "http://estfarmaki2:8880/templates/");
    }

    @Test
    public void updateStack() throws Exception {
        // given
        createBasicParameters("instanceName");

        // when
        new HeatProcessor().updateStack(paramHandler);

        // then
    }

    @Test
    public void deleteStack() throws Exception {
        // given
        createBasicParameters("instanceName");

        // when
        new HeatProcessor().deleteStack(paramHandler);
    }

}
