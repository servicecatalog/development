/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Mar 5, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.data.DiskImage;
import org.oscm.app.iaas.data.IaasContext;
import org.oscm.app.iaas.data.SimpleDiskImage;
import org.oscm.app.iaas.data.SmallVServerConfiguration;
import org.oscm.app.iaas.data.VServerConfiguration;
import org.oscm.app.iaas.data.VServerStatus;
import org.oscm.app.iaas.data.VSystemConfiguration;
import org.oscm.app.iaas.data.VSystemTemplate;
import org.oscm.app.iaas.data.VSystemTemplateConfiguration;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.LServerClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.ror.data.LPlatformDescriptor;
import org.oscm.app.ror.data.LPlatformDescriptorConfiguration;
import org.oscm.app.ror.data.LServerConfiguration;
import org.oscm.app.ror.exceptions.RORException;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.oscm.app.v2_0.data.Setting;

/**
 * @author zhaohang
 * 
 */
public class RORVSystemCommunicationTest {

    private RORVSystemCommunication rorVSystemCommunication;
    private PropertyHandler properties;
    private final RORClient rorClient = mock(RORClient.class);
    private HashMap<String, Setting> parameters;
    private HashMap<String, Setting> configSettings;
    private ProvisioningSettings settings;
    private PropertyHandler ph;

    private static final String STATUS = "status";
    private static final String VSYSID = "vsysId";
    private static final String VSYSTEMID = "VSystemId";
    private static final String SERVERID = "serverId";
    private static final String INSTANCENAME = "instanceName";
    private static final String TARGETSTATE = "targetState";
    private static final String NULLVSYSIDMESSAGE = "Virtual system ID not defined but required to retrive status of virtual servers";
    private static final String NULLTARGETSTATEMESSAGE = "Target state not defined but required to compare status of virtual servers";
    private static final String RUNTIMEEXCEPTIONMESSAGE = "Master does not exist";
    private static final String MASTER_TEMPLATE_ID = "masterTemplateId";
    private static final String SLAVE_TEMPLATE_ID = "slaveTemplateId";
    private static final String SERVER_TEMPLATE_ID = "serverTemplateId";
    private static final String NIC_PRIVATE_IP = "nics.nic.privateIp";
    private static final String PRIVATE_IP = "123.123.123.123";

    @Before
    public void setup() throws Exception {
        IaasContext context = mock(IaasContext.class);

        properties = mock(PropertyHandler.class);
        when(properties.getIaasContext()).thenReturn(context);
        rorVSystemCommunication = spy(new RORVSystemCommunication());

        parameters = new HashMap<>();
        configSettings = new HashMap<>();
        settings = new ProvisioningSettings(parameters, configSettings, "en");
        ph = new PropertyHandler(settings);

    }

    @Test
    public void getVSystemState() throws Exception {
        // given
        prepareVdcClientWithStatus();

        // when
        String result = rorVSystemCommunication.getVSystemState(properties);

        // then
        assertEquals(STATUS, result);
    }

    @Test
    public void getCombinedVServerState_VsysIdIsNull() throws Exception {
        // given
        when(properties.getVsysId()).thenReturn(null);

        // when
        try {
            rorVSystemCommunication.getCombinedVServerState(properties,
                    TARGETSTATE);
            fail();
        } catch (IllegalArgumentException e) {

            // then
            assertEquals(NULLVSYSIDMESSAGE, e.getMessage());
        }
    }

    @Test
    public void getCombinedVServerState_TargetStateIsNull() throws Exception {
        // given
        when(properties.getVsysId()).thenReturn(VSYSID);

        // when
        try {
            rorVSystemCommunication.getCombinedVServerState(properties, null);
            fail();
        } catch (IllegalArgumentException e) {

            // then
            assertEquals(NULLTARGETSTATEMESSAGE, e.getMessage());
        }
    }

    @Test
    public void getCombinedVServerState_VsysIdNotEqualsWithVSystemId()
            throws Exception {
        // given
        RORClient vdcClient = prepareVdcClient();
        preparePlatforms(vdcClient);

        // when
        boolean result = rorVSystemCommunication.getCombinedVServerState(
                properties, TARGETSTATE);

        // then
        assertEquals(Boolean.TRUE, Boolean.valueOf(result));
    }

    @Test
    public void getCombinedVServerState() throws Exception {
        // given
        RORClient vdcClient = prepareVdcClient();
        preparePlatformsWithValue(vdcClient, VServerStatus.STOPPED,
                VServerStatus.STOPPED);

        // when
        boolean result = rorVSystemCommunication.getCombinedVServerState(
                properties, VServerStatus.STOPPED);

        // then
        assertEquals(Boolean.TRUE, Boolean.valueOf(result));
    }

    @Test
    public void getCombinedVServerState_TargetStateWrong() throws Exception {
        // given
        RORClient vdcClient = prepareVdcClient();
        preparePlatformsWithValue(vdcClient, VServerStatus.STOPPED, null,
                VServerStatus.STOPPING);

        // when
        boolean result = rorVSystemCommunication.getCombinedVServerState(
                properties, VServerStatus.STOPPED);

        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void startAllVServers() throws Exception {
        // given
        preparePlatformClientForStartAllServers();

        // when
        rorVSystemCommunication.startAllVServers(properties);
    }

    @Test
    public void startVServers() throws Exception {
        // given
        LServerClient serverClient = prepareServerClientForStartStopServers();
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);

        Set<String> servers = new HashSet<>();
        servers.add("test1");
        doReturn(servers).when(properties).getVserversToBeStarted();

        // when
        rorVSystemCommunication.startVServers(properties);

        // then
        verify(serverClient).start();
        verify(properties, times(2)).setVserverId(idCaptor.capture());
        assertEquals("test1", idCaptor.getAllValues().get(0));
    }

    @Test
    public void startVServers_IsStateAlreadyPresent() throws Exception {
        // given
        LServerClient serverClient = prepareServerClientForStartStopServers();
        doThrow(new RORException("VSYS10120message")).when(serverClient)
                .start();
        Set<String> servers = new HashSet<>();
        servers.add("test1");
        doReturn(servers).when(properties).getVserversToBeStarted();

        // when
        rorVSystemCommunication.startVServers(properties);
    }

    @Test(expected = RORException.class)
    public void startVServers_RORException() throws Exception {
        // given
        LServerClient serverClient = prepareServerClientForStartStopServers();
        doThrow(new RORException("message")).when(serverClient).start();
        Set<String> servers = new HashSet<>();
        servers.add("test1");
        doReturn(servers).when(properties).getVserversToBeStarted();

        // when
        rorVSystemCommunication.startVServers(properties);
    }

    @Test
    public void startVServers_emptyId() throws Exception {
        // given
        LServerClient serverClient = prepareServerClientForStartStopServers();

        Set<String> servers = new HashSet<>();
        servers.add("");
        doReturn(servers).when(properties).getVserversToBeStarted();

        // when
        rorVSystemCommunication.startVServers(properties);

        // then
        verify(serverClient, times(0)).start();
    }

    @Test
    public void startAllVServers_IsStateAlreadyPresent() throws Exception {
        // given
        preparePlatformClientForStartServersWithException("VSYS10120message");

        // when
        rorVSystemCommunication.startAllVServers(properties);
    }

    @Test(expected = RORException.class)
    public void startAllVServers_Exception() throws Exception {
        // given
        preparePlatformClientForStartServersWithException("message");

        // when
        rorVSystemCommunication.startAllVServers(properties);
    }

    @Test
    public void stopAllVServers() throws Exception {
        // given
        LServerClient serverClient = prepareServerClientForStartStopServers();
        mockConfigurationWithServers();
        final Stack<String> stati = new Stack<>();
        stati.push(VServerStatus.RUNNING);
        stati.push(VServerStatus.STARTING);
        stati.push(VServerStatus.STOPPED);
        doAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (!stati.isEmpty()) {
                    return stati.pop();
                }
                return VServerStatus.ERROR;
            }
        }).when(serverClient).getStatus();

        // when
        rorVSystemCommunication.stopAllVServers(ph);
    }

    @Test
    public void stopAllVServers_IsStateAlreadyPresent() throws Exception {
        // given
        preparePlatformClientForStopServersWithException("VSYS10120message");
        mockConfigurationWithoutServers();

        // when
        rorVSystemCommunication.stopAllVServers(properties);
    }

    @Test(expected = RORException.class)
    public void stopAllVServers_Exception() throws Exception {
        // given
        preparePlatformClientForStopServersWithException("message");
        mockConfigurationWithoutServers();

        // when
        rorVSystemCommunication.stopAllVServers(properties);
    }

    @Test
    public void getVServersForTemplate_ServerTemplateIdIsNull()
            throws Exception {
        // given
        String serverTemplateId = null;

        // when
        List<String> result = rorVSystemCommunication.getVServersForTemplate(
                serverTemplateId, properties);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getVServersForTemplate_ServerTemplateIdLengthIsZero()
            throws Exception {
        // given
        String serverTemplateId = "   ";

        // when
        List<String> result = rorVSystemCommunication.getVServersForTemplate(
                serverTemplateId, properties);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getVServersForTemplate_ServerTemplateIdNotMatching()
            throws Exception {
        // given
        IaasContext iaasContext = mock(IaasContext.class);
        doReturn(iaasContext).when(properties).getIaasContext();

        LPlatformClient platformClient = preparePlatformClient();
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));

        // when
        List<String> result = rorVSystemCommunication.getVServersForTemplate(
                SERVER_TEMPLATE_ID, properties);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getVServersForTemplate_ServerTemplateIdMatching()
            throws Exception {
        // given
        IaasContext iaasContext = mock(IaasContext.class);
        doReturn(iaasContext).when(properties).getIaasContext();

        LPlatformClient platformClient = preparePlatformClient();
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));

        // when
        List<String> result = rorVSystemCommunication.getVServersForTemplate(
                MASTER_TEMPLATE_ID, properties);

        // then
        assertEquals(1, result.size());
        assertEquals(SERVERID, result.get(0));
    }

    @Test
    public void getConfiguration_VSystemConfiguration() throws Exception {
        // given a VSystemConfiguration
        mockConfigurationWithoutServers();
        // when
        VSystemConfiguration result = rorVSystemCommunication
                .getConfiguration(properties);

        // then
        assertNotNull(result);

    }

    @Test
    public void getConfiguration_LPlatformClientConfiguration()
            throws Exception {
        // given a null VSystemConfiguration
        IaasContext iaasContext = mock(IaasContext.class);
        doReturn(iaasContext).when(properties).getIaasContext();

        LPlatformClient platformClient = preparePlatformClient();
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));

        // when
        VSystemConfiguration result = rorVSystemCommunication
                .getConfiguration(properties);

        // then retrieve the VSystemConfiguration from the LPlatformClient
        assertNotNull(result);
        assertEquals(2, result.getVServers().size());
    }

    @Test
    public void scaleUp() throws Exception {
        // given
        IaasContext iaasContext = new IaasContext();
        when(properties.getIaasContext()).thenReturn(iaasContext);
        LPlatformClient platformClient = preparePlatformClient();
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(properties);

        String result = rorVSystemCommunication.scaleUp(MASTER_TEMPLATE_ID,
                SLAVE_TEMPLATE_ID, properties);
        // then
        assertNull(result);
    }

    @Test
    public void scaleUp_MasterIsNull() throws Exception {
        // given
        LPlatformClient platformClient = mock(LPlatformClient.class);
        LPlatformConfiguration config = mock(LPlatformConfiguration.class);
        List<LServerConfiguration> servers = new ArrayList<>();

        when(config.getVServers()).thenReturn(servers);
        when(platformClient.getConfiguration()).thenReturn(config);

        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));

        try {
            rorVSystemCommunication.scaleUp(MASTER_TEMPLATE_ID,
                    SLAVE_TEMPLATE_ID, properties);
            fail();
        } catch (RuntimeException e) {
            // then
            assertEquals(RUNTIMEEXCEPTIONMESSAGE, e.getMessage());
        }
    }

    @Test
    public void getPublicIps_SlaveTemplateIdIsNull() throws Exception {
        // given
        when(properties.getSlaveTemplateId()).thenReturn(null);

        // when
        List<String> result = rorVSystemCommunication.getPublicIps(properties);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getPublicIps_SlaveTemplateIdEqualsMasterTemplateId()
            throws Exception {
        // given
        when(properties.getSlaveTemplateId()).thenReturn(SLAVE_TEMPLATE_ID);
        when(properties.getMasterTemplateId()).thenReturn(SLAVE_TEMPLATE_ID);

        List<String> list = new ArrayList<>();

        doReturn(list).when(rorVSystemCommunication).getVServersForTemplate(
                eq(SLAVE_TEMPLATE_ID), eq(properties));

        // when
        List<String> result = rorVSystemCommunication.getPublicIps(properties);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getPublicIps_SlaveTemplateId_privateIp() throws Exception {
        // given
        when(properties.getSlaveTemplateId()).thenReturn(SLAVE_TEMPLATE_ID);
        when(properties.getMasterTemplateId()).thenReturn(MASTER_TEMPLATE_ID);

        IaasContext iaasContext = mock(IaasContext.class);
        doReturn(iaasContext).when(properties).getIaasContext();

        LPlatformClient platformClient = preparePlatformClient();
        LServerClient serverClient = spy(new LServerClient(platformClient,
                "lserver_Id"));
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(properties);
        doReturn(serverClient).when(rorVSystemCommunication).getLServerClient(
                properties);

        List<String> list = new ArrayList<>();
        doReturn(list).when(rorVSystemCommunication).getVServersForTemplate(
                SLAVE_TEMPLATE_ID, properties);

        // when
        List<String> result = rorVSystemCommunication.getPublicIps(properties);

        // then
        assertEquals(1, result.size());
        assertEquals(PRIVATE_IP, result.get(0));
    }

    @Test
    public void getVSystemTemplates() throws Exception {
        // given
        prepareVdcClientWithDescriptors();

        // when
        List<VSystemTemplate> result = rorVSystemCommunication
                .getVSystemTemplates(properties);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void getDiskImages() throws Exception {
        // given
        prepareVdcClientWithDiskImages();

        // when
        List<DiskImage> result = rorVSystemCommunication
                .getDiskImages(properties);

        // then
        assertEquals(2, result.size());
    }

    @Test
    public void getTemplateConfiguration() throws Exception {

        // given
        prepareVdcClientWithTemplateDescriptor();

        // when
        VSystemTemplateConfiguration templateConfiguration = rorVSystemCommunication
                .getVSystemTemplateConfiguration(properties);

        // then
        assertNotNull(templateConfiguration);

    }

    private void preparePlatformClientForStopServersWithException(String message)
            throws Exception {
        LPlatformClient platformClient = spy(new LPlatformClient(rorClient,
                NULLTARGETSTATEMESSAGE));
        doThrow(new RORException(message)).when(platformClient)
                .stopAllServers();
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));
    }

    private void preparePlatformClientForStartAllServers() throws Exception {
        LPlatformClient platformClient = spy(new LPlatformClient(rorClient,
                NULLTARGETSTATEMESSAGE));
        doNothing().when(platformClient).startAllServers();
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));
    }

    private LServerClient prepareServerClientForStartStopServers()
            throws Exception {
        LPlatformClient platformClient = spy(new LPlatformClient(rorClient,
                NULLTARGETSTATEMESSAGE));
        LServerClient serverClient = spy(new LServerClient(platformClient,
                "serverId"));
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(any(PropertyHandler.class));
        doReturn(serverClient).when(rorVSystemCommunication).getLServerClient(
                any(PropertyHandler.class));
        doNothing().when(serverClient).start();
        doNothing().when(serverClient).stop();
        return serverClient;
    }

    private void preparePlatformClientForStartServersWithException(
            String message) throws Exception {
        LPlatformClient platformClient = spy(new LPlatformClient(rorClient,
                NULLTARGETSTATEMESSAGE));
        doThrow(new RORException(message)).when(platformClient)
                .startAllServers();
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));
    }

    private RORClient prepareVdcClient() throws Exception {
        when(properties.getVsysId()).thenReturn(VSYSID);
        RORClient vdcClient = mock(RORClient.class);
        doReturn(vdcClient).when(rorVSystemCommunication).getVdcClient(
                eq(properties));

        return vdcClient;
    }

    private void prepareVdcClientWithDiskImages() throws Exception {
        RORClient vdcClient = mock(RORClient.class);
        doReturn(vdcClient).when(rorVSystemCommunication).getVdcClient(
                eq(properties));

        List<DiskImage> diskImages = new ArrayList<>();
        diskImages.add(new SimpleDiskImage(MASTER_TEMPLATE_ID, "Master"));
        diskImages.add(new SimpleDiskImage(SLAVE_TEMPLATE_ID, "Slave"));

        doReturn(diskImages).when(vdcClient).listDiskImages();
    }

    private void prepareVdcClientWithDescriptors() throws Exception {
        RORClient vdcClient = mock(RORClient.class);
        doReturn(vdcClient).when(rorVSystemCommunication).getVdcClient(
                eq(properties));

        List<LPlatformDescriptor> descriptors = new LinkedList<>();

        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        descriptors.add(new LPlatformDescriptor(configuration));

        doReturn(descriptors).when(vdcClient).listLPlatformDescriptors();
    }

    private void prepareVdcClientWithTemplateDescriptor() throws Exception {
        RORClient vdcClient = mock(RORClient.class);
        doReturn(vdcClient).when(rorVSystemCommunication).getVdcClient(
                eq(properties));

        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        LPlatformDescriptorConfiguration config = new LPlatformDescriptorConfiguration(
                configuration);

        doReturn(config).when(vdcClient).getLPlatformDescriptorConfiguration(
                anyString());
    }

    private void prepareVdcClientWithStatus() throws Exception {
        LPlatformClient platformClient = mock(LPlatformClient.class);
        when(platformClient.getStatus()).thenReturn(STATUS);
        doReturn(platformClient).when(rorVSystemCommunication)
                .getLPlatformClient(eq(properties));
    }

    private void preparePlatforms(RORClient vdcClient) throws Exception {
        List<LPlatformConfiguration> platforms = new ArrayList<>();
        LPlatformConfiguration config = mock(LPlatformConfiguration.class);
        when(config.getVSystemId()).thenReturn(VSYSTEMID);
        platforms.add(config);
        when(vdcClient.listLPlatforms(eq(true))).thenReturn(platforms);
    }

    private void preparePlatformsWithValue(RORClient vdcClient, String... stati)
            throws Exception {
        List<LPlatformConfiguration> platforms = new ArrayList<>();
        LPlatformConfiguration config1 = mock(LPlatformConfiguration.class);
        when(config1.getVSystemId()).thenReturn("platform1");
        LPlatformConfiguration config2 = mock(LPlatformConfiguration.class);
        when(config2.getVSystemId()).thenReturn(VSYSID);
        LPlatformConfiguration config3 = mock(LPlatformConfiguration.class);
        when(config3.getVSystemId()).thenReturn("platform3");
        ArrayList<String> values = new ArrayList<>();
        values.addAll(Arrays.asList(stati));
        when(config2.getServerStatus()).thenReturn(values);
        platforms.add(config1);
        platforms.add(config2);
        platforms.add(config3);
        when(vdcClient.listLPlatforms(eq(true))).thenReturn(platforms);
    }

    @SuppressWarnings("unchecked")
    private LPlatformClient preparePlatformClient() throws Exception {
        RORClient rorClient = spy(new RORClient("url", "tenantId", "user",
                "password", "locale"));
        LPlatformClient platformClient = spy(new LPlatformClient(rorClient,
                "lplatform_Id"));
        LPlatformConfiguration config = mock(LPlatformConfiguration.class);
        List<LServerConfiguration> servers = new ArrayList<>();
        LServerConfiguration lsc1 = mock(LServerConfiguration.class);
        when(lsc1.getDiskImageId()).thenReturn(SLAVE_TEMPLATE_ID);
        LServerConfiguration lsc2 = mock(LServerConfiguration.class);
        when(lsc2.getDiskImageId()).thenReturn(MASTER_TEMPLATE_ID);
        when(lsc2.getServerId()).thenReturn(SERVERID);
        servers.add(lsc1);
        servers.add(lsc2);
        doReturn(servers).when(config).getVServers();
        doReturn(config).when(platformClient).getConfiguration();
        XMLConfiguration xmlConfiguration = mock(XMLConfiguration.class);
        doReturn(xmlConfiguration).when(rorClient).execute(any(HashMap.class));

        prepareLServerConfiguration(platformClient);

        return platformClient;
    }

    private void mockConfigurationWithoutServers() {
        VSystemConfiguration configuration = mock(VSystemConfiguration.class);
        IaasContext iaasContext = mock(IaasContext.class);
        doReturn(iaasContext).when(properties).getIaasContext();
        doReturn(configuration).when(iaasContext).getVSystemConfiguration();
        List<VServerConfiguration> servers = new ArrayList<>();
        doReturn(servers).when(configuration).getVServers();
    }

    private void mockConfigurationWithServers() {
        VSystemConfiguration configuration = mock(VSystemConfiguration.class);
        ph.getIaasContext().add(configuration);
        List<VServerConfiguration> servers = new ArrayList<>();
        SmallVServerConfiguration s1 = new SmallVServerConfiguration();
        s1.setServerId("s1");
        servers.add(s1);
        SmallVServerConfiguration s2 = new SmallVServerConfiguration();
        s1.setServerId("s2");
        servers.add(s2);
        SmallVServerConfiguration s3 = new SmallVServerConfiguration();
        s1.setServerId("s3");
        servers.add(s3);
        doReturn(servers).when(configuration).getVServers();
    }

    @SuppressWarnings("unchecked")
    private void prepareLServerConfiguration(LPlatformClient platformClient)
            throws Exception {
        RORClient vdcClient = platformClient.getVdcClient();

        HashMap<String, String> request = new HashMap<>();
        doReturn(request).when(vdcClient).getBasicParameters();

        XMLConfiguration xmlConfiguration = mock(XMLConfiguration.class);
        doReturn(xmlConfiguration).when(vdcClient).execute(any(HashMap.class));
        SubnodeConfiguration config = mock(SubnodeConfiguration.class);
        doReturn(PRIVATE_IP).when(config).getString(NIC_PRIVATE_IP);
        doReturn(config).when(xmlConfiguration).configurationAt("lserver");
    }

    @Test
    public void createVSystem() throws Exception {
        // given
        RORClient vdcClient = prepareVdcClient();
        when(vdcClient.createLPlatform(anyString(), anyString())).thenReturn(
                VSYSID);

        // when
        String result = rorVSystemCommunication.createVSystem(properties);

        // then
        assertEquals(VSYSID, result);
    }

    @Test
    public void createVSystem_VSystemAlreadyExists() throws Exception {
        // given
        RORClient vdcClient = prepareVdcClient();
        String vSystemName = "PLATFORM1";
        LPlatformConfiguration config = mock(LPlatformConfiguration.class);
        doReturn(vSystemName).when(config).getVSystemName();
        doReturn(VSYSID).when(config).getVSystemId();
        doReturn(config).when(rorVSystemCommunication)
                .getVSystemConfigurationByInstanceName(properties);

        // when
        String result = rorVSystemCommunication.createVSystem(properties);

        // then
        assertEquals(VSYSID, result);
        verify(vdcClient, Mockito.never()).createLPlatform(anyString(),
                anyString());
    }

    @Test
    public void getVSystemConfigurationByInstanceName() throws Exception {
        // given
        RORClient vdcClient = prepareVdcClient();
        LPlatformConfiguration config = mock(LPlatformConfiguration.class);
        ArrayList<LPlatformConfiguration> lPlatforms = new ArrayList<>();
        lPlatforms.add(config);
        doReturn(lPlatforms).when(vdcClient).listLPlatforms(false);
        when(config.getVSystemName()).thenReturn(INSTANCENAME);
        when(properties.getInstanceName()).thenReturn(INSTANCENAME);

        // when
        VSystemConfiguration result = rorVSystemCommunication
                .getVSystemConfigurationByInstanceName(properties);

        // then
        assertEquals(config, result);
    }

    @Test
    public void getVSystemConfigurationByInstanceName_NotExisting()
            throws Exception {
        // given
        RORClient vdcClient = prepareVdcClient();
        LPlatformConfiguration config = mock(LPlatformConfiguration.class);
        ArrayList<LPlatformConfiguration> lPlatforms = new ArrayList<>();
        lPlatforms.add(config);
        doReturn(lPlatforms).when(vdcClient).listLPlatforms(false);
        when(config.getVSystemName()).thenReturn(INSTANCENAME);
        when(properties.getInstanceName()).thenReturn("");

        // when
        VSystemConfiguration result = rorVSystemCommunication
                .getVSystemConfigurationByInstanceName(properties);

        // then
        assertNull(result);
    }
}
