/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Aug 1, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.oscm.app.openstack.controller.PropertyHandler;
import org.oscm.app.openstack.controller.StackStatus;
import org.oscm.app.openstack.data.Stack;
import org.oscm.app.openstack.exceptions.HeatException;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.exceptions.AbortException;
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

    enum InstanceType {
        NOVA("OS::Nova::Server"), EC2("AWS::EC2::Instance"), TROVE(
                "OS::Trove::Instance");

        private final String text;

        private InstanceType(final String text) {
            this.text = text;
        }

        public String getString() {
            return this.text;
        }
    }

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
        createBasicParameters(instanceName, "fosi_v2.json", "http");

        // when
        new HeatProcessor().createStack(paramHandler);

        // then
        assertEquals("idValue", paramHandler.getStackId());
        assertTrue(paramHandler.getStackName().startsWith(instanceName));
    }

    @Test
    public void createStack_https() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "https");

        // when
        new HeatProcessor().createStack(paramHandler);

        // then
        assertEquals("idValue", paramHandler.getStackId());
        assertTrue(paramHandler.getStackName().startsWith(instanceName));
    }

    @Test(expected = AbortException.class)
    public void createStack_https_notTemplate() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "https");
        configSettings.put(PropertyHandler.TEMPLATE_BASE_URL,
                "estfarmaki2:8880/templates/");
        // when
        new HeatProcessor().createStack(paramHandler);
    }

    @Test
    public void createStack_https_withHttpsTemplate() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "https");
        configSettings.put(PropertyHandler.TEMPLATE_BASE_URL,
                "https://objectstorage/v1/templates/");

        // when
        new HeatProcessor().createStack(paramHandler);

        // then
        assertEquals("idValue", paramHandler.getStackId());
        assertTrue(paramHandler.getStackName().startsWith(instanceName));
    }

    @Test(expected = HeatException.class)
    public void createStack_connectionFailed() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(404,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks", connection);
        streamHandler.put("/stacks/Instance4-1905561714", connection);

        // when
        new HeatProcessor().createStack(paramHandler);
    }

    @Test
    public void getStackDetails() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");

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
        createBasicParameters(instanceName, "fosi_v2.json", "http");
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
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        streamHandler.put("/stacks/" + instanceName,
                new MockHttpURLConnection(200, " "));

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
        createBasicParameters(instanceName, "fosi_v2.json", "http");

        // when
        boolean result = new HeatProcessor().resumeStack(paramHandler);

        // then
        assertFalse(result);
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test
    public void resumeStack() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        streamHandler.put("/stacks/" + instanceName,
                new MockHttpURLConnection(200,
                        MockURLStreamHandler.respStacksInstanceName(
                                StackStatus.SUSPEND_COMPLETE, false)));

        // when
        boolean result = new HeatProcessor().resumeStack(paramHandler);

        // then
        assertTrue(result);
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test
    public void resumeStackWithNovaServer() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        final List<String> serverNames = Arrays.asList("server1");
        streamHandler.put("/stacks/" + instanceName,
                new MockHttpURLConnection(200,
                        MockURLStreamHandler.respStacksInstanceName(
                                StackStatus.SUSPEND_COMPLETE, false)));
        streamHandler
                .put("/stacks/" + instanceName + "/resources",
                        new MockHttpURLConnection(200,
                                MockURLStreamHandler.respStacksResources(
                                        serverNames, InstanceType.NOVA
                                                .getString())));

        // when
        boolean result = new HeatProcessor().resumeStack(paramHandler);

        // then
        assertTrue(result);
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test
    public void resumeStackWithTroveInstance() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        final List<String> serverNames = Arrays.asList("server1");
        streamHandler.put("/stacks/" + instanceName,
                new MockHttpURLConnection(200,
                        MockURLStreamHandler.respStacksInstanceName(
                                StackStatus.SUSPEND_COMPLETE, false)));
        streamHandler
                .put("/stacks/" + instanceName + "/resources",
                        new MockHttpURLConnection(200,
                                MockURLStreamHandler.respStacksResources(
                                        serverNames, InstanceType.TROVE
                                                .getString())));

        // when
        boolean result = new HeatProcessor().resumeStack(paramHandler);

        // then
        assertTrue(result);
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test(expected = InstanceNotAliveException.class)
    public void resumeStack_InstanceNotAliveException_serverIDError()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        streamHandler.put("/stacks/" + instanceName,
                new MockHttpURLConnection(200,
                        MockURLStreamHandler.respStacksInstanceName(
                                StackStatus.SUSPEND_COMPLETE, false)));
        streamHandler.put("/servers/0-Instance-server1",
                new MockHttpURLConnection(200,
                        MockURLStreamHandler.respServer("test", "servId")));

        // when
        new HeatProcessor().resumeStack(paramHandler);
    }

    @Test(expected = InstanceNotAliveException.class)
    public void resumeStack_InstanceNotAliveException_serverIDNotFoundError()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        List<String> serverNames = new LinkedList<String>();
        streamHandler
                .put("/stacks/" + instanceName + "/resources",
                        new MockHttpURLConnection(200,
                                MockURLStreamHandler.respStacksResources(
                                        serverNames,
                                        InstanceType.EC2.getString())));
        // when
        new HeatProcessor().resumeStack(paramHandler);
    }

    @Test(expected = HeatException.class)
    public void resumeStack_connectionFailedToGetStack() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks/" + instanceName, connection);

        // when
        new HeatProcessor().resumeStack(paramHandler);
    }

    @Test(expected = HeatException.class)
    public void resumeStack_connectionFailedToServerDetail() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/servers/0-Instance-server1", connection);

        // when
        new HeatProcessor().resumeStack(paramHandler);

    }

    @Test(expected = HeatException.class)
    public void resumeStack_connectionFailedToStackResource() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks/" + instanceName + "/resources", connection);

        // when
        new HeatProcessor().resumeStack(paramHandler);
    }

    @Test(expected = HeatException.class)
    public void resumeStack_connectionFailedToAction() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks/" + instanceName,
                new MockHttpURLConnection(200,
                        MockURLStreamHandler.respStacksInstanceName(
                                StackStatus.SUSPEND_COMPLETE, false)));
        streamHandler.put("/stacks/" + instanceName + "/sID/actions",
                connection);

        // when
        new HeatProcessor().resumeStack(paramHandler);
    }

    @Test(expected = InstanceNotAliveException.class)
    public void suspendStack_InstanceNotAliveException_serverMissing()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        final List<String> serverNames = new ArrayList<String>();
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        streamHandler
                .put("/stacks/Instance4/resources",
                        new MockHttpURLConnection(200,
                                MockURLStreamHandler.respStacksResources(
                                        serverNames,
                                        InstanceType.EC2.getString())));
        // when
        new HeatProcessor().suspendStack(paramHandler);
    }

    @Test
    public void suspendStack() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");

        // when
        boolean result = new HeatProcessor().suspendStack(paramHandler);

        // then
        assertTrue(result);
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test
    public void suspendStack_alredySuspended() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");

        streamHandler.put("/stacks/" + instanceName,
                new MockHttpURLConnection(200,
                        MockURLStreamHandler.respStacksInstanceName(
                                StackStatus.SUSPEND_COMPLETE, false)));

        // when
        boolean result = new HeatProcessor().suspendStack(paramHandler);

        // then
        assertFalse(result);
        assertEquals("sID", paramHandler.getStackId());
        assertEquals(instanceName, paramHandler.getStackName());
    }

    @Test(expected = RuntimeException.class)
    public void suspendStack_InstanceNotAliveException_serverIdError()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        final List<String> serverNames = Arrays.asList("serv");
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        streamHandler
                .put("/stacks/Instance4/resources",
                        new MockHttpURLConnection(200,
                                MockURLStreamHandler.respStacksResources(
                                        serverNames,
                                        InstanceType.EC2.getString())));
        // when
        new HeatProcessor().suspendStack(paramHandler);
    }

    @Test(expected = HeatException.class)
    public void suspendStack_connectionFailed() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());

        streamHandler.put("/stacks/" + instanceName, connection);

        // when
        new HeatProcessor().suspendStack(paramHandler);
    }

    @Test(expected = HeatException.class)
    public void suspendStack_connectionFailedToAction() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());

        streamHandler.put("/stacks/" + instanceName + "/sID/actions",
                connection);

        // when
        new HeatProcessor().suspendStack(paramHandler);
    }

    @Test
    public void createStack_JSONException() throws Exception {
        // given
        final String instanceName = "Instance4";
        streamHandler.put("/stacks",
                new MockHttpURLConnection(200, "no valid JSON"));
        streamHandler.put("/stacks/" + instanceName + "-1905561714",
                new MockHttpURLConnection(400, ""));
        createBasicParameters(instanceName, "fosi_v2.json", "http");

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

    private void createBasicParameters(String instanceName, String templateName,
            String httpMethod) {
        parameters.put(PropertyHandler.STACK_NAME, instanceName);
        parameters.put(PropertyHandler.TEMPLATE_NAME, templateName);
        parameters.put(PropertyHandler.TEMPLATE_PARAMETER_PREFIX + "KeyName",
                "key");
        if (httpMethod == "https") {
            configSettings.put(PropertyHandler.KEYSTONE_API_URL,
                    "https://keystone:8080/v3/auth");
        } else {

            configSettings.put(PropertyHandler.KEYSTONE_API_URL,
                    "http://keystone:8080/v3/auth");
        }
        configSettings.put(PropertyHandler.DOMAIN_NAME, "testDomain");
        configSettings.put(PropertyHandler.TENANT_ID, "testTenantID");
        configSettings.put(PropertyHandler.API_USER_NAME, "api_user");
        configSettings.put(PropertyHandler.API_USER_PWD, "secret");
        configSettings.put(PropertyHandler.TEMPLATE_BASE_URL,
                "http://estfarmaki2:8880/templates/");
    }

    @Test
    public void updateStack() throws Exception {
        // given
        createBasicParameters("instanceName", "fosi_v2.json", "http");

        // when
        new HeatProcessor().updateStack(paramHandler);

        // then
    }

    @Test(expected = HeatException.class)
    public void updateStack_connectionFailed500() throws Exception {
        // given
        createBasicParameters("instanceName", "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(500,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks/instanceName", connection);

        // when
        new HeatProcessor().updateStack(paramHandler);
    }

    @Test(expected = HeatException.class)
    public void updateStack_connectionFailed400() throws Exception {
        // given
        createBasicParameters("instanceName", "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks/instanceName", connection);

        // when
        new HeatProcessor().updateStack(paramHandler);
    }

    @Test
    public void deleteStack() throws Exception {
        // given
        createBasicParameters("instanceName", "fosi_v2.json", "http");

        // when
        new HeatProcessor().deleteStack(paramHandler);
    }

    @Test(expected = HeatException.class)
    public void deleteStack_connectionFailed() throws Exception {
        // given
        createBasicParameters("instanceName", "fosi_v2.json", "http");
        MockHttpURLConnection connection = new MockHttpURLConnection(400,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks/instanceName", connection);

        // when
        new HeatProcessor().deleteStack(paramHandler);
    }

}
