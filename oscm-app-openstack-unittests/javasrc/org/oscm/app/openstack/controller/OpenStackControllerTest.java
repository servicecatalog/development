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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.naming.InitialContext;

import org.junit.Test;
import org.oscm.app.openstack.data.FlowState;
import org.oscm.app.v1_0.data.InstanceDescription;
import org.oscm.app.v1_0.data.InstanceStatus;
import org.oscm.app.v1_0.data.InstanceStatusUsers;
import org.oscm.app.v1_0.data.ProvisioningSettings;
import org.oscm.app.v1_0.data.ServiceUser;
import org.oscm.app.v1_0.exceptions.APPlatformException;
import org.oscm.app.v1_0.intf.APPlatformController;
import org.oscm.app.v1_0.intf.APPlatformService;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

/**
 * 
 */
public class OpenStackControllerTest extends EJBTestBase {

    private APPlatformController controller;
    private APPlatformService platformService;

    private final HashMap<String, String> parameters = new HashMap<String, String>();
    private final HashMap<String, String> configSettings = new HashMap<String, String>();
    private final ProvisioningSettings settings = new ProvisioningSettings(
            parameters, configSettings, "en");
    private InitialContext context;

    @Override
    protected void setup(TestContainer container) throws Exception {

        platformService = mock(APPlatformService.class);
        enableJndiMock();
        context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, platformService);
        container.addBean(new OpenStackController());
        controller = container.get(APPlatformController.class);
    }

    @Test
    public void unbind_noPlatformService() throws Exception {
        context.unbind(APPlatformService.JNDI_NAME);
    }

    @Test
    public void createInstance() throws Exception {

        settings.getParameters().put(PropertyHandler.STACK_NAME, "xyz");

        InstanceDescription instance = createInstanceInternal();
        assertNotNull(instance);
        assertNotNull(instance.getInstanceId());
        assertTrue(instance.getInstanceId().startsWith("stack-"));

        assertEquals(FlowState.CREATION_REQUESTED.toString(),
                parameters.get(PropertyHandler.STATUS));

    }

    @Test
    public void executeServiceOperation_settingsNull() throws Exception {
        controller.executeServiceOperation("userId", "instanceId",
                "transactionId", "operationId", null, null);
    }

    @Test
    public void executeServiceOperation_startSystem() throws Exception {
        controller.executeServiceOperation("userId", "instanceId",
                "transactionId", "START_VIRTUAL_SYSTEM", null,
                new ProvisioningSettings(new HashMap<String, String>(), null,
                        "en"));
    }

    @Test
    public void executeServiceOperation_stopSystem() throws Exception {
        controller.executeServiceOperation("userId", "instanceId",
                "transactionId", "STOP_VIRTUAL_SYSTEM", null,
                new ProvisioningSettings(new HashMap<String, String>(), null,
                        "en"));
    }

    @Test(expected = APPlatformException.class)
    public void createInstance_stackNameEmpty() throws Exception {
        // given
        settings.getParameters().put(PropertyHandler.STACK_NAME, " ");

        // when
        createInstanceInternal();
    }

    @Test(expected = APPlatformException.class)
    public void createInstance_stackNameIllegal() throws Exception {
        // given
        settings.getParameters().put(PropertyHandler.STACK_NAME, "!§$%");

        // when
        createInstanceInternal();
    }

    @Test(expected = APPlatformException.class)
    public void createInstance_stackNameBeginsWithNumber() throws Exception {
        // given
        settings.getParameters().put(PropertyHandler.STACK_NAME, "0a");

        // when
        createInstanceInternal();
    }

    @Test(expected = APPlatformException.class)
    public void createInstance_runtime() throws Exception {
        createInstanceInternal();
    }

    @Test
    public void deleteInstance() throws Exception {

        final String workloadId = "98345";
        InstanceStatus instanceStatus = deleteInstance(workloadId);
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.getRunWithTimer());

        assertEquals(FlowState.DELETION_REQUESTED.toString(),
                parameters.get(PropertyHandler.STATUS));
    }

    @Test
    public void activateInstance() throws Exception {

        final String workloadId = "98345";
        InstanceStatus instanceStatus = activateInstance(workloadId);
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.getRunWithTimer());

        assertEquals(FlowState.ACTIVATION_REQUESTED.toString(),
                parameters.get(PropertyHandler.STATUS));
    }

    @Test
    public void deactivateInstance() throws Exception {

        final String workloadId = "98345";
        InstanceStatus instanceStatus = deactivateInstance(workloadId);
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.getRunWithTimer());

        assertEquals(FlowState.DEACTIVATION_REQUESTED.toString(),
                parameters.get(PropertyHandler.STATUS));
    }

    @Test
    public void notifyInstance() throws Exception {

        String oldStatus = parameters.get(PropertyHandler.STATUS);
        InstanceStatus status = notifyInstance("123", new Properties());
        assertNull(status);
        assertEquals(oldStatus, parameters.get(PropertyHandler.STATUS));
    }

    @Test
    public void modifyInstance() throws Exception {

        modifyInstance("123");
        assertEquals(FlowState.MODIFICATION_REQUESTED.toString(),
                parameters.get(PropertyHandler.STATUS));
    }

    @Test
    public void getInstanceStatus() throws Exception {

        parameters.put(PropertyHandler.STATUS, FlowState.MANUAL.toString());
        InstanceStatus status = getInstanceStatus("123");
        assertEquals(FlowState.MANUAL.toString(),
                parameters.get(PropertyHandler.STATUS));
        assertFalse(status.isReady());
    }

    @Test
    public void createUsers() throws Exception {
        InstanceStatusUsers statusUsers = createUsers("123", null);
        assertNull(statusUsers);
    }

    @Test
    public void updateUsers() throws Exception {
        InstanceStatus status = updateUsers("123", null);
        assertNull(status);
    }

    @Test
    public void deleteUsers() throws Exception {
        InstanceStatus status = deleteUsers("123", null);
        assertNull(status);
    }

    private InstanceDescription createInstanceInternal() throws Exception {
        InstanceDescription instance = runTX(
                new Callable<InstanceDescription>() {

                    @Override
                    public InstanceDescription call() throws Exception {
                        return controller.createInstance(settings);
                    }
                });
        return instance;
    }

    private InstanceStatus modifyInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.modifyInstance(instanceId, settings,
                        settings);
            }
        });
    }

    private InstanceStatus deleteInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.deleteInstance(instanceId, settings);
            }
        });
    }

    private InstanceStatus activateInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.activateInstance(instanceId, settings);
            }
        });
    }

    private InstanceStatus deactivateInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.deactivateInstance(instanceId, settings);
            }
        });
    }

    private InstanceStatus notifyInstance(final String instanceId,
            final Properties properties) throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.notifyInstance(instanceId, settings,
                        properties);
            }
        });
    }

    private InstanceStatus getInstanceStatus(final String instanceId)
            throws Exception {
        InstanceStatus result = runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.getInstanceStatus(instanceId, settings);
            }
        });
        return result;
    }

    private InstanceStatusUsers createUsers(final String instanceId,
            final List<ServiceUser> users) throws Exception {
        InstanceStatusUsers instance = runTX(
                new Callable<InstanceStatusUsers>() {

                    @Override
                    public InstanceStatusUsers call() throws Exception {
                        return controller.createUsers(instanceId, settings,
                                users);
                    }
                });
        return instance;
    }

    private InstanceStatus updateUsers(final String instanceId,
            final List<ServiceUser> users) throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.updateUsers(instanceId, settings, users);
            }
        });
    }

    private InstanceStatus deleteUsers(final String instanceId,
            final List<ServiceUser> users) throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.deleteUsers(instanceId, settings, users);
            }
        });
    }

}
