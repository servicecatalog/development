/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 2016/11/14                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.openstack.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;

import org.junit.Test;
import org.oscm.app.common.intf.InstanceAccess;
import org.oscm.app.common.intf.ServerInformation;
import org.oscm.app.openstack.MockHttpURLConnection;
import org.oscm.app.openstack.MockURLStreamHandler;
import org.oscm.app.openstack.OpenStackConnection;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.intf.APPlatformService;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

/**
 * @author tateiwamext
 *
 */
public class OpenStackInstanceAccessTest extends EJBTestBase {

    private APPlatformService platformService;
    private InstanceAccess instanceAccess;
    private final HashMap<String, String> parameters = new HashMap<String, String>();
    private final HashMap<String, String> configSettings = new HashMap<String, String>();
    private final ProvisioningSettings settings = new ProvisioningSettings(
            parameters, configSettings, "en");
    private final PropertyHandler paramHandler = new PropertyHandler(settings);
    private final MockURLStreamHandler streamHandler = new MockURLStreamHandler();
    private InitialContext context;

    /*
     * (non-Javadoc)
     * 
     * @see org.oscm.test.EJBTestBase#setup(org.oscm.test.ejb.TestContainer)
     */
    @Override
    protected void setup(TestContainer container) throws Exception {

        platformService = mock(APPlatformService.class);
        enableJndiMock();
        context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, platformService);
        container.addBean(new OpenStackInstanceAccess());
        instanceAccess = container.get(OpenStackInstanceAccess.class);
        OpenStackConnection.setURLStreamHandler(streamHandler);
        paramHandler.setStackId("sID");
    }

    @Test
    public void getServerDetails() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        when(platformService.getServiceInstanceDetails(
                eq(OpenStackController.ID), eq("Instance4"),
                any(PasswordAuthentication.class))).thenReturn(settings);
        // when
        List<? extends ServerInformation> result = instanceAccess
                .getServerDetails(instanceName);

        // then
        assertEquals(1, result.size());
        assertEquals("0-Instance-server1", result.get(0).getId());
        assertEquals("server1", result.get(0).getName());
        assertEquals(ServerStatus.ACTIVE.name(), result.get(0).getStatus());
        assertEquals("S-1", result.get(0).getType());
        assertEquals(Arrays.asList("133.162.161.216"),
                result.get(0).getPublicIP());
        assertEquals(Arrays.asList("192.168.0.4"),
                result.get(0).getPrivateIP());
    }

    @Test(expected = APPlatformException.class)
    public void getServerDetails_getAPPlatformException() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        when(platformService.getServiceInstanceDetails(
                eq(OpenStackController.ID), eq("Instance4"),
                any(PasswordAuthentication.class))).thenReturn(settings);
        MockHttpURLConnection connection = new MockHttpURLConnection(401,
                MockURLStreamHandler.respTokens(true, true, false));
        connection.setIOException(new IOException());
        streamHandler.put("/v3/auth/tokens", connection);
        // when
        instanceAccess.getServerDetails(instanceName);

    }

    @Test
    public void getServerDetails_getInstanceNotAliveException()
            throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        when(platformService.getServiceInstanceDetails(
                eq(OpenStackController.ID), eq("Instance4"),
                any(PasswordAuthentication.class))).thenReturn(settings);
        MockHttpURLConnection connection = new MockHttpURLConnection(401,
                MockURLStreamHandler.respServerActions());
        connection.setIOException(new IOException());
        streamHandler.put("/stacks/" + instanceName + "/resources",
                new MockHttpURLConnection(200, MockURLStreamHandler
                        .respStacksResources(new ArrayList<String>(), null)));
        // when
        List<? extends ServerInformation> result = instanceAccess
                .getServerDetails(instanceName);

        // then
        assertEquals(0, result.size());

    }

    @Test
    public void getMessage() throws Exception {

        String result = instanceAccess.getMessage(null, null, new Object());

        assertEquals(null, result);
    }

    @Test
    public void getAccessInfo() throws Exception {
        // given
        final String instanceName = "Instance4";
        createBasicParameters(instanceName, "fosi_v2.json", "http");
        when(platformService.getServiceInstanceDetails(
                eq(OpenStackController.ID), eq("Instance4"),
                any(PasswordAuthentication.class))).thenReturn(settings);
        settings.setServiceAccessInfo("Access Information");

        // when
        String result = instanceAccess.getAccessInfo(instanceName);
        // then
        assertEquals("Access Information", result);
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
}
