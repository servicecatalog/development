/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 11.03.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.oscm.app.iaas.PropertyHandler;
import org.oscm.app.iaas.data.FlowState;
import org.oscm.app.iaas.data.Network;
import org.oscm.app.iaas.data.VServerStatus;
import org.oscm.app.iaas.data.VSystemConfiguration;
import org.oscm.app.iaas.i18n.Messages;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.LServerClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.ror.data.LPlatformStatus;
import org.oscm.app.ror.data.LServerConfiguration;
import org.oscm.app.ror.data.LServerStatus;
import org.oscm.app.ror.exceptions.RORException;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.oscm.app.v2_0.data.Setting;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.InstanceExistsException;
import org.oscm.app.v2_0.exceptions.SuspendException;

/**
 * @author iversen
 * 
 */
public class RORVServerCommunicationTest {

    private RORVServerCommunication rorVServerCommunication;
    private LServerClient lServerClient;
    private LServerConfiguration lServerConfiguration;
    private PropertyHandler paramHandler;
    private HashMap<String, Setting> parameters;
    HashMap<String, Setting> configSettings;
    private ProvisioningSettings settings;

    private final String SERVERTYPE1 = "SERVERTYPE1";
    private final String SERVERTYPE2 = "SERVERTYPE2";
    private final String NETWORKID1 = "NETWORKID1";
    private final String NETWORKID2 = "NETWORKID2";
    private final String EXCEPTIONMESSAGE = "Exception message text";
    private final String EXCEPTIONMESSAGETYPE = "error_invalid_networkid";

    @Before
    public void setup() throws Exception {
        rorVServerCommunication = spy(new RORVServerCommunication());
        lServerClient = mock(LServerClient.class);
        lServerConfiguration = mock(LServerConfiguration.class);
        parameters = new HashMap<>();
        configSettings = new HashMap<>();
        settings = new ProvisioningSettings(parameters, configSettings, "en");
        paramHandler = spy(new PropertyHandler(settings));

        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(lServerConfiguration).when(lServerClient).getConfiguration();
        doReturn("123.123.123.123").when(lServerConfiguration).getPrivateIP();

    }

    @Test
    public void getInternalIp() throws Exception {
        // given

        // when
        String internalIP = rorVServerCommunication.getInternalIp(paramHandler);
        // then
        assertEquals("123.123.123.123", internalIP);

    }

    @Test(expected = RuntimeException.class)
    public void getNetworkId() throws Exception {
        // given

        // when
        rorVServerCommunication.getNetworkId(paramHandler);
        // then throw exception

    }

    @Test
    public void getVServerInitialPassword() throws Exception {
        // given
        doReturn("PWD").when(lServerClient).getInitialPassword();

        // when
        String password = rorVServerCommunication
                .getVServerInitialPassword(paramHandler);

        // then
        assertEquals("PWD", password);

    }

    @Test
    public void getVServerStatus() throws Exception {
        // given
        doReturn("SERVERSTATUS").when(lServerClient).getStatus();

        // when
        String serverStatus = rorVServerCommunication
                .getVServerStatus(paramHandler);

        // then
        assertEquals("SERVERSTATUS", serverStatus);
    }

    @Test
    public void resolveValidNetworkId_null_deterministic() throws Exception {
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        VSystemConfiguration vSystemConfiguration = null;
        paramHandler.getIaasContext().add(vSystemConfiguration);
        List<Network> networks = new ArrayList<>();
        networks.add(new Network(NETWORKID1, "net", NETWORKID1, 2));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(networks).when(lPlatformConfiguration).getNetworks();

        // when
        String resolvedId = rorVServerCommunication
                .resolveValidNetworkId(paramHandler);

        // then
        assertEquals(NETWORKID1, resolvedId);
    }

    @Test
    public void resolveValidNetworkId_empty_deterministic() throws Exception {
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        paramHandler.getIaasContext().add(lPlatformConfiguration);
        parameters.put(PropertyHandler.NETWORK_ID, new Setting(
                PropertyHandler.NETWORK_ID, ""));
        List<Network> networks = new ArrayList<>();
        networks.add(new Network(NETWORKID1, "net", NETWORKID1, 2));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(networks).when(lPlatformConfiguration).getNetworks();

        // when
        String resolvedId = rorVServerCommunication
                .resolveValidNetworkId(paramHandler);

        // then
        assertEquals(NETWORKID1, resolvedId);
    }

    @Test(expected = APPlatformException.class)
    public void resolveValidNetworkId_null_nondeterministic() throws Exception {
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        VSystemConfiguration vSystemConfiguration = null;
        paramHandler.getIaasContext().add(vSystemConfiguration);
        List<Network> networks = new ArrayList<>();
        networks.add(new Network(NETWORKID1, "net", NETWORKID1, 2));
        networks.add(new Network(NETWORKID2, "net", NETWORKID2, 2));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(networks).when(lPlatformConfiguration).getNetworks();

        // when
        rorVServerCommunication.resolveValidNetworkId(paramHandler);
    }

    @Test
    public void resolveValidNetworkId_name_deterministic() throws Exception {
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        VSystemConfiguration vSystemConfiguration = null;
        paramHandler.getIaasContext().add(vSystemConfiguration);
        parameters.put(PropertyHandler.NETWORK_ID, new Setting(
                PropertyHandler.NETWORK_ID, NETWORKID1 + "_name"));
        List<Network> networks = new ArrayList<>();
        networks.add(new Network(NETWORKID1 + "_name", "net", NETWORKID1, 2));
        networks.add(new Network(NETWORKID2 + "_name", "net", NETWORKID2, 2));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(networks).when(lPlatformConfiguration).getNetworks();

        // when
        String resolvedId = rorVServerCommunication
                .resolveValidNetworkId(paramHandler);

        // then
        assertEquals(NETWORKID1, resolvedId);
    }

    @Test
    public void resolveValidNetworkId_id_deterministic() throws Exception {
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        VSystemConfiguration vSystemConfiguration = null;
        paramHandler.getIaasContext().add(vSystemConfiguration);
        parameters.put(PropertyHandler.NETWORK_ID, new Setting(
                PropertyHandler.NETWORK_ID, NETWORKID1));
        List<Network> networks = new ArrayList<>();
        networks.add(new Network(NETWORKID1 + "_name", "net", NETWORKID1, 2));
        networks.add(new Network(NETWORKID2 + "_name", "net", NETWORKID2, 2));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(networks).when(lPlatformConfiguration).getNetworks();

        // when
        String resolvedId = rorVServerCommunication
                .resolveValidNetworkId(paramHandler);

        // then
        assertEquals(NETWORKID1, resolvedId);
    }

    @Test(expected = APPlatformException.class)
    public void resolveValidNetworkId_name_nondeterministic() throws Exception {
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        VSystemConfiguration vSystemConfiguration = null;
        paramHandler.getIaasContext().add(vSystemConfiguration);
        parameters.put(PropertyHandler.NETWORK_ID, new Setting(
                PropertyHandler.NETWORK_ID, NETWORKID1 + "_name"));
        List<Network> networks = new ArrayList<>();
        networks.add(new Network(NETWORKID1 + "_name", "net", NETWORKID1, 2));
        networks.add(new Network(NETWORKID1 + "_name", "net", NETWORKID2, 2));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(networks).when(lPlatformConfiguration).getNetworks();

        // when
        rorVServerCommunication.resolveValidNetworkId(paramHandler);
    }

    @Test
    public void isNetworkIdValid_Valid() throws Exception {
        // given
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        VSystemConfiguration vSystemConfiguration = null;
        paramHandler.getIaasContext().add(vSystemConfiguration);
        parameters.put(PropertyHandler.NETWORK_ID, new Setting(
                PropertyHandler.NETWORK_ID, NETWORKID1));
        List<Network> networks = new ArrayList<>();
        networks.add(new Network("NETWORKID1", "net", NETWORKID1, 2));
        networks.add(new Network("NETWORKID2", "net", NETWORKID2, 2));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(networks).when(lPlatformConfiguration).getNetworks();

        // when
        boolean isNetworkIdValid = rorVServerCommunication
                .isNetworkIdValid(paramHandler);

        // then
        assertTrue(isNetworkIdValid);
    }

    @Test
    public void isNetworkIdValid_NoNetworkIds() throws Exception {
        // given
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        LPlatformConfiguration lPlatformConfiguration = mock(LPlatformConfiguration.class);
        VSystemConfiguration vSystemConfiguration = null;
        paramHandler.getIaasContext().add(vSystemConfiguration);
        parameters.put(PropertyHandler.NETWORK_ID, new Setting(
                PropertyHandler.NETWORK_ID, NETWORKID1));

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(lPlatformConfiguration).when(lPlatformClient)
                .getConfiguration();
        doReturn(new ArrayList<Network>()).when(lPlatformConfiguration)
                .getNetworks();

        // when
        boolean isNetworkIdValid = rorVServerCommunication
                .isNetworkIdValid(paramHandler);

        // then
        assertFalse(isNetworkIdValid);
    }

    @Test(expected = SuspendException.class)
    public void isNetworkIdValid_Exception() throws Exception {
        // given
        Exception e = new Exception();

        doThrow(e).when(rorVServerCommunication).getLPlatformClient(
                paramHandler);

        // when
        rorVServerCommunication.isNetworkIdValid(paramHandler);

        // then throw SuspendException
    }

    @Test
    public void isServerTypeValid_invalid_noServerTypes() throws Exception {
        RORClient rorClient = mock(RORClient.class);
        doReturn(rorClient).when(rorVServerCommunication).getVdcClient(
                paramHandler);
        doReturn(null).when(rorClient).listServerTypes();
        // when

        boolean isServerTypeValid = rorVServerCommunication
                .isServerTypeValid(paramHandler);

        // then
        assertFalse(isServerTypeValid);
    }

    @Test
    public void isServerTypeValid_invalid_serverTypeNotMatching()
            throws Exception {
        // given
        RORClient rorClient = mock(RORClient.class);
        parameters.put(PropertyHandler.VSERVER_TYPE, new Setting(
                PropertyHandler.VSERVER_TYPE, SERVERTYPE2));

        List<String> serverList = new ArrayList<>();
        serverList.add(SERVERTYPE1);

        doReturn(rorClient).when(rorVServerCommunication).getVdcClient(
                paramHandler);
        doReturn(serverList).when(rorClient).listServerTypes();
        // when

        boolean isServerTypeValid = rorVServerCommunication
                .isServerTypeValid(paramHandler);

        // then
        assertFalse(isServerTypeValid);
    }

    @Test
    public void isServerTypeValid_valid() throws Exception {
        // given
        RORClient rorClient = mock(RORClient.class);
        parameters.put(PropertyHandler.VSERVER_TYPE, new Setting(
                PropertyHandler.VSERVER_TYPE, SERVERTYPE1));

        List<String> serverList = new ArrayList<>();
        serverList.add(SERVERTYPE1);
        serverList.add(SERVERTYPE2);

        doReturn(rorClient).when(rorVServerCommunication).getVdcClient(
                paramHandler);
        doReturn(serverList).when(rorClient).listServerTypes();
        // when

        boolean isServerTypeValid = rorVServerCommunication
                .isServerTypeValid(paramHandler);

        // then
        assertTrue(isServerTypeValid);
    }

    @Test(expected = SuspendException.class)
    public void isServerTypeValid_exception() throws Exception {
        // given
        Exception e = new RuntimeException();
        doThrow(e).when(rorVServerCommunication).getVdcClient(paramHandler);

        // when

        rorVServerCommunication.isServerTypeValid(paramHandler);

        // then throw SuspendException
    }

    @Test
    public void isVServerDestroyed() throws Exception {
        // when
        boolean isServerDestroyed = rorVServerCommunication
                .isVServerDestroyed(paramHandler);

        // then
        assertTrue(isServerDestroyed);

    }

    @Test(expected = SuspendException.class)
    public void isVSysIdValid_exception() throws Exception {
        // given
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(LPlatformStatus.ERROR).when(lPlatformClient).getStatus();

        // when
        rorVServerCommunication.isVSysIdValid(paramHandler);

        // then throw SuspendException
    }

    @Test
    public void isVSysIdValid_valid() throws Exception {
        // given
        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(LPlatformStatus.NORMAL).when(lPlatformClient).getStatus();

        // when
        boolean isVsysIdValid = rorVServerCommunication
                .isVSysIdValid(paramHandler);

        // then
        assertTrue(isVsysIdValid);
    }

    @Test(expected = SuspendException.class)
    public void modifyVServerAttributes_SuspendException() throws Exception {
        // given
        LServerClient lServerClient = mock(LServerClient.class);
        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(null).when(lServerClient).getConfiguration();

        // when
        rorVServerCommunication.modifyVServerAttributes(paramHandler);

        // then throw SuspendException
    }

    @Test
    public void modifyVServerAttributes_Finished() throws Exception {
        // given
        paramHandler.setVserverId("serverId");
        LServerClient lServerClient = mock(LServerClient.class);
        LServerConfiguration lServerConfiguration = mock(LServerConfiguration.class);

        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(lServerConfiguration).when(lServerClient).getConfiguration();

        // when
        FlowState newState = rorVServerCommunication
                .modifyVServerAttributes(paramHandler);

        // then
        assertEquals(FlowState.FINISHED, newState);
    }

    @Test
    public void modifyVServerAttributes_VserverUpdating() throws Exception {
        // given
        paramHandler.setVserverId("serverId");
        paramHandler.setCountCPU("4");

        LServerClient lServerClient = mock(LServerClient.class);
        LServerConfiguration lServerConfiguration = mock(LServerConfiguration.class);

        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(lServerConfiguration).when(lServerClient).getConfiguration();
        doReturn("2").when(lServerConfiguration).getNumOfCPU();
        doReturn(LServerStatus.STARTING).when(lServerClient).getStatus();

        FlowState newState = rorVServerCommunication
                .modifyVServerAttributes(paramHandler);

        // then
        assertEquals(FlowState.VSERVER_UPDATING, newState);
    }

    @Test
    public void modifyVServerAttributes_VServerRunning() throws Exception {
        // given
        paramHandler.setVserverId("serverId");
        paramHandler.setCountCPU("4");

        LServerClient lServerClient = mock(LServerClient.class);
        LServerConfiguration lServerConfiguration = mock(LServerConfiguration.class);

        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(lServerConfiguration).when(lServerClient).getConfiguration();
        doReturn("2").when(lServerConfiguration).getNumOfCPU();
        doReturn(LServerStatus.RUNNING).when(lServerClient).getStatus();

        // when
        FlowState newState = rorVServerCommunication
                .modifyVServerAttributes(paramHandler);

        // then
        assertEquals(FlowState.VSERVER_STOPPING_FOR_MODIFICATION, newState);
    }

    @Test
    public void modifyVServerAttributes_VServerStopping() throws Exception {
        // given
        paramHandler.setVserverId("serverId");
        paramHandler.setCountCPU("4");

        LServerClient lServerClient = mock(LServerClient.class);
        LServerConfiguration lServerConfiguration = mock(LServerConfiguration.class);

        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(lServerConfiguration).when(lServerClient).getConfiguration();
        doReturn("2").when(lServerConfiguration).getNumOfCPU();
        doReturn(LServerStatus.STOPPING).when(lServerClient).getStatus();

        // when
        FlowState newState = rorVServerCommunication
                .modifyVServerAttributes(paramHandler);

        // then
        assertEquals(FlowState.VSERVER_STOPPING_FOR_MODIFICATION, newState);
    }

    @Test
    public void startVServer_NotStarting() throws Exception {
        // given
        LServerClient lServerClient = mock(LServerClient.class);
        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(LServerStatus.RUNNING).when(lServerClient).getStatus();

        // when
        boolean serverStarted = rorVServerCommunication
                .startVServer(paramHandler);

        // then
        assertFalse(serverStarted);
    }

    @Test
    public void startVServer_Starting() throws Exception {
        // given
        LServerClient lServerClient = mock(LServerClient.class);
        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(LServerStatus.STOPPED).when(lServerClient).getStatus();

        // when
        boolean serverStarted = rorVServerCommunication
                .startVServer(paramHandler);

        // then
        assertTrue(serverStarted);
    }

    @Test
    public void stopVServer_Stopping() throws Exception {
        // given
        LServerClient lServerClient = mock(LServerClient.class);
        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(LServerStatus.RUNNING).when(lServerClient).getStatus();

        // when
        rorVServerCommunication.stopVServer(paramHandler);

        // then
        verify(lServerClient, times(1)).stop();
    }

    @Test
    public void stopVServer_NotStopping() throws Exception {
        // given
        LServerClient lServerClient = mock(LServerClient.class);
        doReturn(lServerClient).when(rorVServerCommunication).getLServerClient(
                paramHandler);
        doReturn(LServerStatus.STOPPED).when(lServerClient).getStatus();

        // when
        rorVServerCommunication.stopVServer(paramHandler);

        // then
        verify(lServerClient, times(0)).stop();
    }

    @Test
    public void getSuspendException_MissingCause() {
        // given
        RORException rorException = new RORException(EXCEPTIONMESSAGE);
        String localizedMessage = Messages.get("en", EXCEPTIONMESSAGETYPE,
                EXCEPTIONMESSAGE);

        // when
        SuspendException suspendException = rorVServerCommunication
                .getSuspendException(rorException, EXCEPTIONMESSAGETYPE);

        // then
        assertEquals(localizedMessage,
                suspendException.getLocalizedMessage("en"));
    }

    @Test
    public void getSuspendException() {
        // given
        RORException rorException = spy(new RORException(EXCEPTIONMESSAGE));
        Throwable cause = new Throwable();
        String localizedMessage = Messages.get("en", EXCEPTIONMESSAGETYPE,
                cause.getClass().getName());
        doReturn(cause).when(rorException).getCause();

        // when
        SuspendException suspendException = rorVServerCommunication
                .getSuspendException(rorException, EXCEPTIONMESSAGETYPE);

        // then
        assertEquals(localizedMessage,
                suspendException.getLocalizedMessage("en"));
        assertEquals(cause, suspendException.getCause());
    }

    @Test(expected = InstanceExistsException.class)
    public void createVServer_ServerAlreadyExisting() throws Exception {
        // given
        String SERVERID = "SERVER1";
        String INSTANCENAME = "INSTANCENAME1";

        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        ArrayList<LServerConfiguration> lServerList = new ArrayList<>();
        LServerConfiguration server1 = mock(LServerConfiguration.class);
        LPlatformConfiguration configuration = mock(LPlatformConfiguration.class);
        lServerList.add(server1);

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(configuration).when(lPlatformClient).getConfiguration();
        doReturn(lServerList).when(configuration).getVServers();
        doReturn(INSTANCENAME).when(server1).getServerName();
        doReturn(INSTANCENAME).when(paramHandler).getInstanceName();
        doReturn(SERVERID).when(server1).getServerId();

        // when
        rorVServerCommunication.createVServer(paramHandler);

    }

    @Test
    public void createVServer() throws Exception {
        // given
        String SERVERID1 = "SERVER1";
        String INSTANCENAME1 = "INSTANCENAME1";
        String INSTANCENAME2 = "INSTANCENAME2";

        LPlatformClient lPlatformClient = mock(LPlatformClient.class);
        ArrayList<LServerConfiguration> lServerList = new ArrayList<>();
        LServerConfiguration server1 = mock(LServerConfiguration.class);
        LPlatformConfiguration configuration = mock(LPlatformConfiguration.class);
        lServerList.add(server1);

        doReturn(lPlatformClient).when(rorVServerCommunication)
                .getLPlatformClient(paramHandler);
        doReturn(configuration).when(lPlatformClient).getConfiguration();
        doReturn(lServerList).when(configuration).getVServers();
        doReturn(INSTANCENAME1).when(server1).getServerName();
        doReturn(INSTANCENAME2).when(paramHandler).getInstanceName();
        doReturn("servertype").when(paramHandler).getVserverType();
        doReturn("diskimageID").when(paramHandler).getDiskImageId();
        doReturn("vmPool").when(paramHandler).getVMPool();
        doReturn("storagePool").when(paramHandler).getStoragePool();
        doReturn("CountCPU").when(paramHandler).getCountCPU();
        doReturn("networkId").when(rorVServerCommunication)
                .resolveValidNetworkId(paramHandler);

        doReturn(SERVERID1).when(server1).getServerId();
        doReturn(SERVERID1).when(lPlatformClient).createLServer(anyString(),
                anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString());

        // when
        String resultingServerId = rorVServerCommunication
                .createVServer(paramHandler);

        // then
        assertEquals(SERVERID1, resultingServerId);

    }

    @Test
    public void getNonErrorVServerStatus() throws Exception {
        // given
        doReturn(VServerStatus.STARTING).when(lServerClient).getStatus();

        // when
        String serverStatus = rorVServerCommunication
                .getNonErrorVServerStatus(paramHandler);

        // then
        assertEquals(VServerStatus.STARTING, serverStatus);
    }

    @Test(expected = SuspendException.class)
    public void getNonErrorVServerStatus_StopError() throws Exception {
        // given
        String SERVER_ID = "SERVER1";
        String INSTANCENAME = "INSTANCENAME1";
        paramHandler.setVserverId(SERVER_ID);
        doReturn(INSTANCENAME).when(paramHandler).getInstanceName();
        doReturn(VServerStatus.STOP_ERROR).when(lServerClient).getStatus();

        // when
        try {
            rorVServerCommunication.getNonErrorVServerStatus(paramHandler);
        }
        // then
        catch (SuspendException se) {
            assertEquals(Messages.get("en", "error_failed_to_stop_vserver",
                    new Object[] { SERVER_ID, INSTANCENAME }),
                    se.getLocalizedMessage("en"));
            assertEquals(
                    Messages.getAll("error_failed_to_stop_vserver",
                            new Object[] { SERVER_ID, INSTANCENAME }).size(),
                    se.getLocalizedMessages().size());
            throw se;

        }
    }

    @Test(expected = SuspendException.class)
    public void getNonErrorVServerStatus_StartError() throws Exception {
        // given
        String SERVER_ID = "SERVER1";
        String INSTANCENAME = "INSTANCENAME1";
        paramHandler.setVserverId(SERVER_ID);
        doReturn(INSTANCENAME).when(paramHandler).getInstanceName();
        doReturn(VServerStatus.START_ERROR).when(lServerClient).getStatus();

        // when
        try {
            rorVServerCommunication.getNonErrorVServerStatus(paramHandler);
        }
        // then
        catch (SuspendException se) {
            assertEquals(Messages.get("en", "error_failed_to_start_vserver",
                    new Object[] { SERVER_ID, INSTANCENAME }),
                    se.getLocalizedMessage("en"));
            assertEquals(
                    Messages.getAll("error_failed_to_start_vserver",
                            new Object[] { SERVER_ID, INSTANCENAME }).size(),
                    se.getLocalizedMessages().size());
            throw se;

        }
    }

    @Test(expected = SuspendException.class)
    public void getNonErrorVServerStatus_Error() throws Exception {
        // given
        String SERVER_ID = "SERVER1";
        String INSTANCENAME = "INSTANCENAME1";
        paramHandler.setVserverId(SERVER_ID);
        doReturn(INSTANCENAME).when(paramHandler).getInstanceName();
        doReturn(VServerStatus.ERROR).when(lServerClient).getStatus();

        // when
        try {
            rorVServerCommunication.getNonErrorVServerStatus(paramHandler);
        }
        // then
        catch (SuspendException se) {
            assertEquals(
                    Messages.get("en", "error_state_vserver", new Object[] {
                            SERVER_ID, INSTANCENAME }),
                    se.getLocalizedMessage("en"));
            assertEquals(
                    Messages.getAll("error_state_vserver",
                            new Object[] { SERVER_ID, INSTANCENAME }).size(),
                    se.getLocalizedMessages().size());
            throw se;

        }
    }

    @Test(expected = SuspendException.class)
    public void getNonErrorVServerStatus_UnexpectedStop() throws Exception {
        // given
        String SERVER_ID = "SERVER1";
        String INSTANCENAME = "INSTANCENAME1";
        paramHandler.setVserverId(SERVER_ID);
        doReturn(INSTANCENAME).when(paramHandler).getInstanceName();
        doReturn(VServerStatus.UNEXPECTED_STOP).when(lServerClient).getStatus();

        // when
        try {
            rorVServerCommunication.getNonErrorVServerStatus(paramHandler);
        }
        // then
        catch (SuspendException se) {
            assertEquals(Messages.get("en", "error_unexpected_stop_vserver",
                    new Object[] { SERVER_ID, INSTANCENAME }),
                    se.getLocalizedMessage("en"));
            assertEquals(
                    Messages.getAll("error_unexpected_stop_vserver",
                            new Object[] { SERVER_ID, INSTANCENAME }).size(),
                    se.getLocalizedMessages().size());
            throw se;

        }
    }
}
