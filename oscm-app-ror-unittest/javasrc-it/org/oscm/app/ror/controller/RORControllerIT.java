/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author:  Malhotra                                                     
 *                                                                              
 *  Creation Date: Oct 05, 2012                                                      
 *                                                                              
 *  Completion Time: Oct 05, 2012                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.naming.InitialContext;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.oscm.app.iaas.ProcessManagerBean;
import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.VServerProcessorBean;
import org.oscm.app.iaas.VSystemProcessorBean;
import org.oscm.app.iaas.data.AccessInformation;
import org.oscm.app.iaas.data.DiskImage;
import org.oscm.app.iaas.data.FWPolicy;
import org.oscm.app.iaas.data.FlowState;
import org.oscm.app.iaas.data.NATRule;
import org.oscm.app.iaas.data.Network;
import org.oscm.app.iaas.data.Operation;
import org.oscm.app.iaas.data.SimpleDiskImage;
import org.oscm.app.iaas.data.SimpleSystemTemplate;
import org.oscm.app.iaas.data.SmallVServerConfiguration;
import org.oscm.app.iaas.data.VServerConfiguration;
import org.oscm.app.iaas.data.VServerStatus;
import org.oscm.app.iaas.data.VSystemConfiguration;
import org.oscm.app.iaas.data.VSystemTemplate;
import org.oscm.app.iaas.data.VSystemTemplateConfiguration;
import org.oscm.app.iaas.i18n.Messages;
import org.oscm.app.iaas.intf.FWCommunication;
import org.oscm.app.ror.MockURLStreamHandler;
import org.oscm.app.ror.RORVDiskCommunication;
import org.oscm.app.ror.RORVServerCommunication;
import org.oscm.app.ror.RORVSystemCommunication;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.LServerClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LPlatformDescriptorConfiguration;
import org.oscm.app.ror.data.LServerConfiguration;
import org.oscm.app.v2_0.data.InstanceDescription;
import org.oscm.app.v2_0.data.InstanceStatus;
import org.oscm.app.v2_0.data.PasswordAuthentication;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.oscm.app.v2_0.data.Setting;
import org.oscm.app.v2_0.data.User;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.SuspendException;
import org.oscm.app.v2_0.intf.APPlatformController;
import org.oscm.app.v2_0.intf.APPlatformService;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

import junit.framework.Assert;

public class RORControllerIT extends EJBTestBase {

    private static String MASTER_TEMPLATE_ID = "image-13c8a812d25";
    private static String SLAVE_TEMPLATE_ID = "image-13c8a821e55";

    private APPlatformController controller;
    private APPlatformService platformService;
    private final HashMap<String, Setting> parameters = new HashMap<>();
    private final HashMap<String, Setting> configSettings = new HashMap<>();
    private ProvisioningSettings settings = new ProvisioningSettings(
            parameters, configSettings, "en");
    private boolean anyServiceLocked;
    private String existingInstanceId;
    private Exception wantedException;
    private LPlatformClient vsysClient;
    private RORClient vdcClient;
    private LServerClient vserverClient;
    private ProvisioningSettings oldSettings;
    private RORClient wantedVDCClient;
    private LPlatformClient wantedVSYSClient;
    private final List<String> serverIds = new LinkedList<>();
    private String platformStatus = "";
    private String combinedServerStatus = "";
    private LServerConfiguration vserverConfig;
    private List<VSystemTemplate> systemTemplates;
    private VSystemConfiguration vsysConfig;
    private List<DiskImage> diskImages;

    @Override
    protected void setup(TestContainer container) throws Exception {

        vsysClient = Mockito.mock(LPlatformClient.class);
        vdcClient = Mockito.mock(RORClient.class);
        vserverClient = Mockito.mock(LServerClient.class);
        vserverConfig = Mockito.mock(LServerConfiguration.class);
        Mockito.doAnswer(new Answer<LServerConfiguration>() {

            @Override
            public LServerConfiguration answer(InvocationOnMock invocation)
                    throws Throwable {
                return vserverConfig;
            }

        }).when(vserverClient).getConfiguration();
        doReturn("UNKNOWN").when(vserverClient).getStatus();
        platformService = Mockito.mock(APPlatformService.class);
        InitialContext context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, platformService);
        Answer<Boolean> answerLock = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return Boolean.valueOf(!anyServiceLocked);
            }
        };
        Mockito.doAnswer(answerLock)
                .when(platformService)
                .lockServiceInstance(Matchers.eq(RORController.ID),
                        Matchers.anyString(),
                        Matchers.any(PasswordAuthentication.class));
        Answer<Boolean> answerExists = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                return Boolean.valueOf(arguments != null
                        && arguments.length > 1 && existingInstanceId != null
                        && existingInstanceId.equals(arguments[1]));
            }
        };
        Mockito.doAnswer(answerExists).when(platformService)
                .exists(Matchers.eq(RORController.ID), Matchers.anyString());

        // handles HTTP connections (add asserts if necessary)
        VSystemProcessorBean.setURLStreamHandler(new MockURLStreamHandler());

        addRORVServerCommunication(container);
        addRORVDiskCommunication(container);
        addRORVSystemCommunication(container);
        addFWCommunication(container);
        container.addBean(new VServerProcessorBean());
        container.addBean(new VSystemProcessorBean());
        addProcessManagerBean(container);
        container.addBean(new RORController());
        controller = container.get(APPlatformController.class);
        parameters.put(PropertyHandler.INSTANCENAME_PREFIX, new Setting(
                PropertyHandler.INSTANCENAME_PREFIX, "estess"));
        parameters
                .put(PropertyHandler.INSTANCENAME_PATTERN, new Setting(
                        PropertyHandler.INSTANCENAME_PATTERN,
                        "estess([a-z0-9]){2,25}"));
        parameters.put(PropertyHandler.INSTANCENAME_CUSTOM, new Setting(
                PropertyHandler.INSTANCENAME_CUSTOM, "demo2"));
        parameters.put(PropertyHandler.SYSTEM_TEMPLATE_ID, new Setting(
                PropertyHandler.SYSTEM_TEMPLATE_ID, "template-13c8ab3348d"));
        parameters.put(PropertyHandler.MASTER_TEMPLATE_ID, new Setting(
                PropertyHandler.MASTER_TEMPLATE_ID, MASTER_TEMPLATE_ID));
        parameters.put(PropertyHandler.SLAVE_TEMPLATE_ID, new Setting(
                PropertyHandler.SLAVE_TEMPLATE_ID, SLAVE_TEMPLATE_ID));
        parameters.put(PropertyHandler.VSYS_ID, new Setting(
                PropertyHandler.VSYS_ID, "SampleTe-3KGU9G1N4"));
        parameters.put(PropertyHandler.VSERVER_ID, new Setting(
                PropertyHandler.VSERVER_ID, "SampleTe-M3Z010QCV-S-0007"));
        parameters.put(PropertyHandler.IAAS_API_URI, new Setting(
                PropertyHandler.IAAS_API_URI,
                "https://ror-demo.fujitsu.com:8014/cfmgapi/endpoint"));
        parameters.put(PropertyHandler.CLUSTER_SIZE, new Setting(
                PropertyHandler.CLUSTER_SIZE, "2"));
        settings.getConfigSettings().put(PropertyHandler.IAAS_API_LOCALE,
                new Setting(PropertyHandler.IAAS_API_LOCALE, "en"));
        settings.getConfigSettings().put(PropertyHandler.IAAS_API_TENANT,
                new Setting(PropertyHandler.IAAS_API_TENANT, "SampleTenant"));
        settings.getConfigSettings().put(PropertyHandler.IAAS_API_USER,
                new Setting(PropertyHandler.IAAS_API_USER, "tenant_admin"));
        settings.getConfigSettings().put(PropertyHandler.IAAS_API_PWD,
                new Setting(PropertyHandler.IAAS_API_PWD, "tenantadmin"));
        settings.getConfigSettings().put(
                PropertyHandler.ENABLE_PARALLEL_PROVISIONING,
                new Setting(PropertyHandler.ENABLE_PARALLEL_PROVISIONING,
                        "false"));
        settings = new ProvisioningSettings(parameters, configSettings, "en");
        parameters.put(PropertyHandler.VDISK_NAME, new Setting(
                PropertyHandler.VDISK_NAME, "disk1"));
        systemTemplates = new ArrayList<>();
        systemTemplates.add(new SimpleSystemTemplate("template-13c8ab3348d"));
        anyServiceLocked = false;
        diskImages = new ArrayList<>();
        SimpleDiskImage masterImg = new SimpleDiskImage(MASTER_TEMPLATE_ID,
                "Master");
        masterImg.setMaxCpuCount("2");
        diskImages.add(masterImg);
        SimpleDiskImage slaveImg = new SimpleDiskImage(SLAVE_TEMPLATE_ID,
                "Slave");
        slaveImg.setMaxCpuCount("2");
        diskImages.add(slaveImg);

        vsysConfig = Mockito.mock(VSystemConfiguration.class);
        when(vsysConfig.getNetworks()).thenReturn(
                Arrays.asList(new Network("kcuf", "123", "456", 6)));
        final HierarchicalConfiguration hc = new HierarchicalConfiguration();
        hc.addProperty("nics.nic.networkId", "456");
        when(vsysConfig.getVServers()).thenAnswer(
                new Answer<List<? extends VServerConfiguration>>() {
                    @Override
                    public List<? extends VServerConfiguration> answer(
                            InvocationOnMock invocation) throws Throwable {
                        return Arrays.asList(new LServerConfiguration(hc));
                    }
                });

        User user = new User();
        user.setLocale("de");
        doReturn(user).when(platformService).authenticate(anyString(),
                any(PasswordAuthentication.class));
    }

    private void addProcessManagerBean(TestContainer container)
            throws Exception {
        container.addBean(new ProcessManagerBean() {

            @Override
            public InstanceStatus getControllerInstanceStatus(
                    String controllerId, String instanceId,
                    PropertyHandler paramHandler) throws Exception {
                if (wantedException != null) {
                    throw wantedException;
                }
                return super.getControllerInstanceStatus(controllerId,
                        instanceId, paramHandler);
            }
        });
    }

    private void addRORVServerCommunication(TestContainer container)
            throws Exception {
        container.addBean(new RORVServerCommunication() {
            @Override
            public LPlatformClient getLPlatformClient(
                    PropertyHandler paramHandler) {
                if (wantedVSYSClient != null) {
                    return null;
                }
                return vsysClient;
            }

            @Override
            public RORClient getVdcClient(PropertyHandler paramHandler) {
                if (wantedVDCClient != null) {
                    return null;
                }
                return vdcClient;
            }

            @Override
            public LServerClient getLServerClient(PropertyHandler paramHandler) {
                return vserverClient;
            }

            @Override
            public boolean isNetworkIdValid(PropertyHandler paramHandler)
                    throws Exception {
                return true;
            }

            @Override
            public boolean isServerTypeValid(PropertyHandler paramHandler)
                    throws Exception {
                return true;
            }
        });
    }

    private void addRORVDiskCommunication(TestContainer container)
            throws Exception {
        container.addBean(new RORVDiskCommunication() {
            @Override
            public LPlatformClient getLPlatformClient(
                    PropertyHandler paramHandler) {
                return vsysClient;
            }

            @Override
            public RORClient getVdcClient(PropertyHandler paramHandler) {
                return vdcClient;
            }

            @Override
            public LServerClient getLServerClient(PropertyHandler paramHandler) {
                return vserverClient;
            }
        });
    }

    private void addFWCommunication(TestContainer container) throws Exception {
        container.addBean(new FWCommunication() {

            @Override
            public Set<String> updateFirewallSetting(
                    PropertyHandler properties, Set<FWPolicy> policies,
                    Set<String> knownPolicyIds) throws Exception {
                return null;
            }

            @Override
            public Set<String> updateNATSetting(PropertyHandler properties,
                    Set<NATRule> rules) throws Exception {
                return null;
            }

            @Override
            public Set<NATRule> getNATSetting(PropertyHandler properties)
                    throws Exception {
                return null;
            }

            @Override
            public String getFirewallStatus(PropertyHandler properties)
                    throws Exception {
                return null;
            }

            @Override
            public void startFirewall(PropertyHandler properties)
                    throws Exception {
                return;
            }

        });
    }

    private void addRORVSystemCommunication(TestContainer container)
            throws Exception {
        container.addBean(new RORVSystemCommunication() {
            @Override
            public LPlatformClient getLPlatformClient(
                    PropertyHandler paramHandler) {
                return vsysClient;
            }

            @Override
            public RORClient getVdcClient(PropertyHandler paramHandler) {
                return vdcClient;
            }

            @Override
            public LServerClient getLServerClient(PropertyHandler paramHandler) {
                return vserverClient;
            }

            @Override
            public List<String> getVServersForTemplate(String serverTemplateId,
                    PropertyHandler properties) throws Exception {
                if (serverIds.size() > 1) {
                    return serverIds.subList(1, serverIds.size());
                }
                return new ArrayList<>();
            }

            @Override
            public String getVSystemState(PropertyHandler properties)
                    throws Exception {

                return platformStatus;
            }

            @Override
            public boolean getCombinedVServerState(PropertyHandler properties,
                    String expectedState) throws Exception {
                return combinedServerStatus != null
                        && combinedServerStatus.equals(expectedState);
            }

            @Override
            public String scaleUp(String masterTemplateId,
                    String slaveTemplateId, PropertyHandler properties)
                    throws Exception {
                int i = 1;
                String name = "";
                do {
                    name = "vserverId" + i++;
                } while (serverIds.contains(name));
                serverIds.add(name);
                return name;
            }

            @Override
            public List<VSystemTemplate> getVSystemTemplates(
                    PropertyHandler properties) throws Exception {
                return systemTemplates;
            }

            @Override
            public VSystemConfiguration getConfiguration(
                    PropertyHandler properties) throws Exception {
                return vsysConfig;
            }

            @Override
            public List<DiskImage> getDiskImages(PropertyHandler properties)
                    throws Exception {
                return diskImages;
            }

            @Override
            public List<AccessInformation> getAccessInfo(
                    PropertyHandler properties) throws Exception {
                return new ArrayList<>();
            }

            @Override
            public VSystemTemplateConfiguration getVSystemTemplateConfiguration(
                    PropertyHandler properties) throws Exception {
                LPlatformDescriptorConfiguration conf = mock(LPlatformDescriptorConfiguration.class);

                final HierarchicalConfiguration hc = new HierarchicalConfiguration();
                hc.addProperty("nics.nic.networkId", "456");
                when(conf.getNetworks()).thenReturn(
                        Arrays.asList(new Network("kcuf", "123", "456", 6)));
                when(conf.getVServers()).thenAnswer(
                        new Answer<List<? extends VServerConfiguration>>() {
                            @Override
                            public List<? extends VServerConfiguration> answer(
                                    InvocationOnMock invocation)
                                    throws Throwable {
                                return Arrays.asList(new LServerConfiguration(
                                        hc));
                            }
                        });
                return conf;
            }
        });
    }

    @Test
    public void createInstance_Creation_Requested() throws Exception {

        parameters.put(PropertyHandler.CLUSTER_SIZE, new Setting(
                PropertyHandler.CLUSTER_SIZE, "2"));
        InstanceDescription instance = createInstance();

        Assert.assertNotNull(instance);
        Assert.assertNotNull(instance.getInstanceId());
        Assert.assertEquals(FlowState.VSYSTEM_CREATION_REQUESTED.toString(),
                parameters.get(PropertyHandler.API_STATUS).getValue());
        Assert.assertEquals(Operation.VSYSTEM_CREATION.toString(), parameters
                .get(PropertyHandler.OPERATION).getValue());

    }

    @Test(expected = APPlatformException.class)
    public void createInstance_EmptyName() throws Exception {
        parameters.put(PropertyHandler.INSTANCENAME_PREFIX, new Setting(
                PropertyHandler.INSTANCENAME_PREFIX, ""));
        parameters.put(PropertyHandler.INSTANCENAME_CUSTOM, new Setting(
                PropertyHandler.INSTANCENAME_CUSTOM, ""));
        createInstance();
    }

    @Test(expected = APPlatformException.class)
    public void createInstance_invalidName() throws Exception {
        parameters.put(PropertyHandler.INSTANCENAME_CUSTOM, new Setting(
                PropertyHandler.INSTANCENAME_CUSTOM, ""));
        createInstance();
    }

    @Test(expected = APPlatformException.class)
    public void createInstance_runtime() throws Exception {
        settings = new ProvisioningSettings(parameters, configSettings, "en") {
            private static final long serialVersionUID = 1L;

            @Override
            public HashMap<String, Setting> getParameters() {
                throw new RuntimeException("Test");
            }
        };
        createInstance();
    }

    @Test(expected = APPlatformException.class)
    public void createInstance_duplicateName() throws Exception {
        existingInstanceId = "estessdemo1";
        parameters.put(PropertyHandler.INSTANCENAME_CUSTOM, new Setting(
                PropertyHandler.INSTANCENAME_CUSTOM, "demo1"));
        createInstance();
    }

    @Test
    public void modifyInstance() throws Exception {
        oldSettings = new ProvisioningSettings(new HashMap<>(parameters),
                configSettings, "en");
        settings = new ProvisioningSettings(parameters, configSettings, "en");
        modifyInstance("abc");
        Assert.assertEquals(
                FlowState.VSYSTEM_MODIFICATION_REQUESTED.toString(), parameters
                        .get(PropertyHandler.API_STATUS).getValue());
        Assert.assertEquals(Operation.VSYSTEM_MODIFICATION.toString(),
                parameters.get(PropertyHandler.OPERATION).getValue());
    }

    @Test(expected = APPlatformException.class)
    public void modifyInstance_runtime() throws Exception {
        wantedException = new RuntimeException("Test");
        modifyInstance("abc");
    }

    @Test(expected = APPlatformException.class)
    public void modifyInstance_invalidName() throws Exception {
        parameters.put(PropertyHandler.INSTANCENAME_PREFIX, new Setting(
                PropertyHandler.INSTANCENAME_PREFIX, ""));
        parameters.put(PropertyHandler.INSTANCENAME_CUSTOM, new Setting(
                PropertyHandler.INSTANCENAME_CUSTOM, ""));
        modifyInstance("abc");
    }

    @Test(expected = APPlatformException.class)
    public void deleteInstance_runtime() throws Exception {
        settings = new ProvisioningSettings(parameters, configSettings, "en") {
            private static final long serialVersionUID = 1L;

            @Override
            public HashMap<String, Setting> getParameters() {
                throw new RuntimeException("Test");
            }
        };
        deleteInstance("abc");
    }

    @Test
    public void deleteInstance() throws Exception {
        deleteInstance("abc");
        Assert.assertEquals(FlowState.VSYSTEM_DELETION_REQUESTED.toString(),
                parameters.get(PropertyHandler.API_STATUS).getValue());
        Assert.assertEquals(Operation.VSYSTEM_DELETION.toString(), parameters
                .get(PropertyHandler.OPERATION).getValue());
    }

    @Test
    public void getInstanceStatus_VSYSTEM_DELETIION_REQUESTED()
            throws Exception {

        platformStatus = "NORMAL";
        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_DELETION.name()));
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_DELETION_REQUESTED.name()));
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VNET_DELETING.toString(), status);
        platformStatus = "NORMAL";
        combinedServerStatus = "STOPPED";
        settings.setSubscriptionId("subscriptionId");
        // stopping servers
        instanceStatus = getInstanceStatus("Guest1");
        assertNotNull(instanceStatus);
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSERVERS_STOPPING.toString(), status);
        // deleting system
        instanceStatus = getInstanceStatus("Guest1");
        assertNotNull(instanceStatus);
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_DELETING.toString(), status);

        // 4. guest_info
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.DESTROYED.toString(), status);
    }

    @Test
    public void getInstanceStatus_VSYSTEM_DELETING_noMail() throws Exception {

        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_DELETION.name()));
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS, FlowState.VSYSTEM_DELETING.name()));
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        // then
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.DESTROYED.toString(), status);
        // check that the right API calls were made
        Mockito.verify(platformService, Mockito.never()).sendMail(
                Matchers.anyListOf(String.class), Matchers.anyString(),
                Matchers.anyString());
    }

    @Test
    public void getInstanceStatus_VSYSTEM_DELETING_mail() throws Exception {

        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_DELETION.name()));
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS, FlowState.VSYSTEM_DELETING.name()));
        settings.setSubscriptionId("subscriptionId");
        // 4. guest_info
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.DESTROYED.toString(), status);
        // check that the right API calls were made
        Mockito.verify(platformService, Mockito.times(1)).sendMail(
                Matchers.anyListOf(String.class), Matchers.anyString(),
                Matchers.anyString());
    }

    @Test
    public void getInstanceStatus_VSYSTEM_CREATION_REQUESTED_without_SCALING()
            throws Exception {
        Mockito.when(platformService.getEventServiceUrl()).thenReturn(
                "http://127.0.0.1/something");
        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_CREATION.name()));
        parameters.put(PropertyHandler.CLUSTER_SIZE, new Setting(
                PropertyHandler.CLUSTER_SIZE, "2"));
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));
        serverIds.add("vserverId1");
        serverIds.add("vserverId2");
        platformStatus = "NORMAL";
        combinedServerStatus = "RUNNING";
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_CREATION_REQUESTED.name()));

        settings.setSubscriptionId("subscriptionId");
        // =======================
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_CREATING.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSERVERS_STARTING.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALING_COMPLETED.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RETRIEVEGUEST.toString(), status);
        parameters.put(PropertyHandler.VSYS_ID, new Setting(
                PropertyHandler.VSYS_ID, "demo_vsysid"));

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.MANUAL.toString(), status);
    }

    @Test
    public void getInstanceStatus_VSYSTEM_CREATION_REQUESTED_with_SCALING()
            throws Exception {

        Mockito.when(platformService.getEventServiceUrl()).thenReturn(
                "http://127.0.0.1/something");
        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_CREATION.name()));
        parameters.put(PropertyHandler.CLUSTER_SIZE, new Setting(
                PropertyHandler.CLUSTER_SIZE, "2"));
        String masterId = parameters.get(PropertyHandler.MASTER_TEMPLATE_ID)
                .getValue();
        parameters.put(PropertyHandler.SLAVE_TEMPLATE_ID, new Setting(
                PropertyHandler.SLAVE_TEMPLATE_ID, masterId));
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));
        serverIds.add("vserverId1");
        serverIds.add("vserverId2");
        platformStatus = "NORMAL";
        combinedServerStatus = "RUNNING";

        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_CREATION_REQUESTED.name()));
        parameters.put(PropertyHandler.ADMIN_REST_URL,
                new Setting(PropertyHandler.ADMIN_REST_URL,
                        "http://{MASTER_IP}/{SLAVE_IP}"));

        settings.setSubscriptionId("subscriptionId");
        // =======================
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_CREATING.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSERVERS_STARTING.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_UP.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(
                FlowState.VSYSTEM_SCALE_UP_WAIT_BEFORE_NOTIFICATION.toString(),
                status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_UP_NOTIFY_ADMIN_AGENT.toString(),
                status);

        // =======================
        // same state since we wait for ~120 seconds
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        assertTrue(instanceStatus.getRunWithTimer());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_UP_NOTIFY_ADMIN_AGENT.toString(),
                status);
        // remove suspension
        new PropertyHandler(settings).suspendProcessInstanceFor(0);
        Thread.sleep(100); // make sure that suspension is over

        // added server needs a private IP to be communicated to cluster manager
        Mockito.when(vserverConfig.getPrivateIP()).thenReturn("192.178.2.4");

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_UP.toString(), status);

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn("STOPPED");
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALING_COMPLETED.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RETRIEVEGUEST.toString(), status);
        parameters.put(PropertyHandler.VSYS_ID, new Setting(
                PropertyHandler.VSYS_ID, "demo_vsysid"));

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.MANUAL.toString(), status);
    }

    @Test
    public void getInstanceStatus_VSYSTEM_CREATION_REQUESTED_concurrent()
            throws Exception {

        anyServiceLocked = true;
        // Simulate parallel creation task
        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_CREATION.name()));
        parameters.put(PropertyHandler.CLUSTER_SIZE, new Setting(
                PropertyHandler.CLUSTER_SIZE, "2"));
        serverIds.add("vserverId1");
        serverIds.add("vserverId2");
        platformStatus = "NORMAL";
        combinedServerStatus = "RUNNING";
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_CREATION_REQUESTED.name()));
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        // then
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_CREATION_REQUESTED.toString(), status);
        // Unlock service
        anyServiceLocked = false;
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_CREATION_REQUESTED.name()));
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        // Now it should work
        assertEquals(FlowState.VSYSTEM_CREATING.toString(), status);
    }

    @Test
    public void getInstanceStatus_VSYSTEM_MODIFICATION_SCALING_UP()
            throws Exception {

        serverIds.add("vserverId1");
        platformStatus = "NORMAL";
        combinedServerStatus = "RUNNING";
        parameters.put(PropertyHandler.OPERATION,
                new Setting(PropertyHandler.OPERATION,
                        Operation.VSYSTEM_MODIFICATION.name()));
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_MODIFICATION_REQUESTED.name()));
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));
        parameters.put(PropertyHandler.ADMIN_REST_URL,
                new Setting(PropertyHandler.ADMIN_REST_URL,
                        "http://{MASTER_IP}/{SLAVE_IP}"));

        settings.setSubscriptionId("subscriptionId");
        // =======================
        InstanceStatus instanceStatus = getInstanceStatus("host1");

        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_UP.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(
                FlowState.VSYSTEM_SCALE_UP_WAIT_BEFORE_NOTIFICATION.toString(),
                status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_UP_NOTIFY_ADMIN_AGENT.toString(),
                status);
        // remove suspension
        new PropertyHandler(settings).suspendProcessInstanceFor(0);
        Thread.sleep(100); // make sure that suspension is over
        // added server needs a private IP to be communicated to cluster manager
        Mockito.when(vserverConfig.getPrivateIP()).thenReturn("192.178.2.4");

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_UP.toString(), status);

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn("STOPPED");
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALING_COMPLETED.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RETRIEVEGUEST.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.FINISHED.toString(), status);
    }

    @Test
    public void getInstanceStatus_VSYSTEM_MODIFICATION_SCALING_DOWN()
            throws Exception {

        serverIds.add("vserverId1");
        serverIds.add("vserverId2");
        serverIds.add("vserverId3");
        platformStatus = "NORMAL";
        combinedServerStatus = "RUNNING";
        parameters.put(PropertyHandler.OPERATION,
                new Setting(PropertyHandler.OPERATION,
                        Operation.VSYSTEM_MODIFICATION.name()));
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_MODIFICATION_REQUESTED.name()));
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));

        settings.setSubscriptionId("subscriptionId");
        // =======================
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN.toString(), status);
        combinedServerStatus = "STOPPED";

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN_STOP_SERVER.toString(),
                status);

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn("STOPPED");
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN_DESTROY_SERVER.toString(),
                status);
        serverIds.remove("vserverId3");

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn("STOPPED");
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALING_COMPLETED.toString(), status);
        combinedServerStatus = "RUNNING";

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RETRIEVEGUEST.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.FINISHED.toString(), status);
    }

    @Test
    public void getInstanceStatus_VSYSTEM_MODIFICATION_SCALING_DOWN_SUSPENDED()
            throws Exception {

        serverIds.add("vserverId1");
        serverIds.add("vserverId2");
        serverIds.add("vserverId3");
        platformStatus = "NORMAL";
        combinedServerStatus = "RUNNING";
        parameters.put(PropertyHandler.OPERATION,
                new Setting(PropertyHandler.OPERATION,
                        Operation.VSYSTEM_MODIFICATION.name()));
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_MODIFICATION_REQUESTED.name()));
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));

        settings.setSubscriptionId("subscriptionId");
        // =======================
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN.toString(), status);
        combinedServerStatus = "STOPPED";

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN_STOP_SERVER.toString(),
                status);

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn(
                VServerStatus.STOPPED);
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN_DESTROY_SERVER.toString(),
                status);
        serverIds.remove("vserverId3");

        // =======================

        Mockito.when(vserverClient.getStatus()).thenReturn(
                VServerStatus.STOP_ERROR);
        try {
            instanceStatus = getInstanceStatus("host1");
        } catch (SuspendException se) {
            assertEquals(Messages.get("en", "error_failed_to_stop_vserver",
                    new Object[] { "vserverId3", "estessdemo2" }),
                    se.getLocalizedMessage("en"));
            assertEquals(
                    Messages.getAll("error_failed_to_stop_vserver",
                            new Object[] { "vserverId3", "estessdemo2" })
                            .size(), se.getLocalizedMessages().size());
        }
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN_DESTROY_SERVER.toString(),
                status);

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn("STOPPED");
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALE_DOWN.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALING_COMPLETED.toString(), status);
        combinedServerStatus = "RUNNING";

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RETRIEVEGUEST.toString(), status);

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.FINISHED.toString(), status);
    }

    @Test
    public void getInstanceStatus_VSYSTEM_MODIFICATION_SCALING_CPU()
            throws Exception {

        serverIds.add("vserverId1");
        serverIds.add("vserverId2");
        platformStatus = "NORMAL";
        combinedServerStatus = "RUNNING";
        parameters.put(PropertyHandler.OPERATION,
                new Setting(PropertyHandler.OPERATION,
                        Operation.VSYSTEM_MODIFICATION.name()));
        parameters.put(PropertyHandler.API_STATUS, new Setting(
                PropertyHandler.API_STATUS,
                FlowState.VSYSTEM_MODIFICATION_REQUESTED.name()));
        parameters.put(PropertyHandler.COUNT_CPU, new Setting(
                PropertyHandler.COUNT_CPU, "2"));
        vsysConfig = Mockito.mock(VSystemConfiguration.class);
        Mockito.when(vserverConfig.getNumOfCPU()).thenReturn("1");
        Mockito.when(vserverClient.getStatus()).thenReturn("RUNNING");
        final List<VServerConfiguration> servers = new ArrayList<>();
        Answer<List<VServerConfiguration>> answerservers = new Answer<List<VServerConfiguration>>() {
            @Override
            public List<VServerConfiguration> answer(InvocationOnMock invocation)
                    throws Throwable {
                return servers;
            }
        };

        final List<Network> networks = new ArrayList<>();
        Answer<List<Network>> answernetworks = new Answer<List<Network>>() {
            @Override
            public List<Network> answer(InvocationOnMock invocation)
                    throws Throwable {
                return networks;
            }
        };
        Mockito.doAnswer(answerservers).when(vsysConfig).getVServers();
        SmallVServerConfiguration server1 = new SmallVServerConfiguration();
        server1.setNumCPU("2"); // this one is already 2CPU
        server1.setDiskImageId(MASTER_TEMPLATE_ID);
        server1.setServerId("vserverId1");
        server1.setNetworkId("network1_id");
        servers.add(server1);
        SmallVServerConfiguration server2 = new SmallVServerConfiguration();
        server2.setNumCPU("1"); // this one will be sclaed up
        server2.setDiskImageId(SLAVE_TEMPLATE_ID);
        server2.setServerId("vserverId2");
        server2.setNetworkId("network1_id");
        servers.add(server2);

        Mockito.doAnswer(answernetworks).when(vsysConfig).getNetworks();
        Network network1 = new Network("network1", "network1_cat",
                "network1_id", 2);
        networks.add(network1);

        // =======================
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RESIZE_VSERVERS.toString(), status);
        assertEquals(FlowState.VSERVER_MODIFICATION_REQUESTED.toString(),
                parameters.get("VSERVER_X_API_STATUS").getValue());
        assertEquals("vserverId2", parameters.get("VSERVER_X_VSERVER_ID")
                .getValue());

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RESIZE_VSERVERS.toString(), status);
        assertEquals(FlowState.VSERVER_STOPPING_FOR_MODIFICATION.toString(),
                parameters.get("VSERVER_X_API_STATUS").getValue());

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn("STOPPED");
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RESIZE_VSERVERS.toString(), status);
        assertEquals(FlowState.VSERVER_UPDATING.toString(),
                parameters.get("VSERVER_X_API_STATUS").getValue());

        // =======================
        Mockito.when(vserverConfig.getNumOfCPU()).thenReturn("2");
        server2.setNumCPU("2");
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RESIZE_VSERVERS.toString(), status);
        assertEquals(FlowState.VSERVER_STARTING.toString(),
                parameters.get("VSERVER_X_API_STATUS").getValue());

        // =======================
        Mockito.when(vserverClient.getStatus()).thenReturn("RUNNING");
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RESIZE_VSERVERS.toString(), status);
        assertEquals(FlowState.VSERVER_STARTED.toString(),
                parameters.get("VSERVER_X_API_STATUS").getValue());

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_RESIZE_VSERVERS.toString(), status);
        assertEquals(FlowState.VSERVER_RETRIEVEGUEST.toString(), parameters
                .get("VSERVER_X_API_STATUS").getValue());

        // =======================
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.VSYSTEM_SCALING_COMPLETED.toString(), status);

    }

    @Test
    public void getInstanceStatus_RETRIEVEGUEST_noMail() throws Exception {

        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_CREATION.name()));
        parameters.put(PropertyHandler.API_STATUS,
                new Setting(PropertyHandler.API_STATUS,
                        FlowState.VSYSTEM_RETRIEVEGUEST.name()));
        InstanceStatus instanceStatus = getInstanceStatus("Guest1");

        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.FINISHED.toString(), status);
        // check that the right API calls were made
        Mockito.verify(platformService, Mockito.never()).sendMail(
                Matchers.anyListOf(String.class), Matchers.anyString(),
                Matchers.anyString());
    }

    @Test
    public void getInstanceStatus_RETRIEVEGUEST_mail() throws Exception {

        settings.setSubscriptionId("subscriptionId");
        parameters.put(PropertyHandler.OPERATION, new Setting(
                PropertyHandler.OPERATION, Operation.VSYSTEM_CREATION.name()));
        parameters.put(PropertyHandler.API_STATUS,
                new Setting(PropertyHandler.API_STATUS,
                        FlowState.VSYSTEM_RETRIEVEGUEST.name()));
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));
        Mockito.when(platformService.getEventServiceUrl()).thenReturn(
                "http://127.0.0.1/something");
        InstanceStatus instanceStatus = getInstanceStatus("Guest1");

        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        assertFalse(instanceStatus.getRunWithTimer());
        // check that the right API calls were made
        Mockito.verify(platformService, Mockito.times(1)).sendMail(
                Matchers.anyListOf(String.class), Matchers.anyString(),
                Matchers.anyString());
    }

    @Test
    public void getInstanceStatus_MODIFICATION_noMail() throws Exception {

        parameters.put(PropertyHandler.OPERATION,
                new Setting(PropertyHandler.OPERATION,
                        Operation.VSYSTEM_MODIFICATION.name()));
        parameters.put(PropertyHandler.API_STATUS,
                new Setting(PropertyHandler.API_STATUS,
                        FlowState.VSYSTEM_RETRIEVEGUEST.name()));
        InstanceStatus instanceStatus = getInstanceStatus("Guest1");

        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        String status = parameters.get(PropertyHandler.API_STATUS).getValue();
        assertEquals(FlowState.FINISHED.toString(), status);
        // check that the right API calls were made
        Mockito.verify(platformService, Mockito.never()).sendMail(
                Matchers.anyListOf(String.class), Matchers.anyString(),
                Matchers.anyString());
    }

    @Test
    public void getInstanceStatus_MODIFICATION_mail() throws Exception {

        parameters.put(PropertyHandler.OPERATION,
                new Setting(PropertyHandler.OPERATION,
                        Operation.VSYSTEM_MODIFICATION.name()));
        parameters.put(PropertyHandler.API_STATUS,
                new Setting(PropertyHandler.API_STATUS,
                        FlowState.VSYSTEM_RETRIEVEGUEST.name()));
        parameters.put(PropertyHandler.MAIL_FOR_COMPLETION, new Setting(
                PropertyHandler.MAIL_FOR_COMPLETION, "test@email.com"));

        settings.setSubscriptionId("subscriptionId");
        Mockito.when(platformService.getEventServiceUrl()).thenReturn(
                "http://127.0.0.1/something");
        InstanceStatus instanceStatus = getInstanceStatus("Guest1");

        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        // check that the right API calls were made
        Mockito.verify(platformService, Mockito.times(1)).sendMail(
                Matchers.anyListOf(String.class), Matchers.anyString(),
                Matchers.anyString());
    }

    @Test(expected = RuntimeException.class)
    public void initializeNoJndi() throws Exception {
        InitialContext context = new InitialContext();
        context.unbind(APPlatformService.JNDI_NAME);
        // when no platform service is available via JNDI, the controller cannot
        // work
        new RORController().initialize();
    }

    @Test(expected = RuntimeException.class)
    public void initializeNoJndi_Communication() throws Exception {
        InitialContext context = new InitialContext();
        context.unbind(APPlatformService.JNDI_NAME);
        // when no platform service is available via JNDI, the controller cannot
        // work
        new ProcessManagerBean().initialize();
    }

    @Test(expected = RuntimeException.class)
    public void initializeNotMatching() throws Exception {
        InitialContext context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, "TestObject");
        // when no suitable platform service is available via JNDI, the
        // controller cannot work
        new RORController().initialize();
    }

    private InstanceDescription createInstance() throws Exception {
        InstanceDescription instance = runTX(new Callable<InstanceDescription>() {

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
                return controller.modifyInstance(instanceId, oldSettings,
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
}
