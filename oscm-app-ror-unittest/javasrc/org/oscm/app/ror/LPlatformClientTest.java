/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Mar 5, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;

import org.oscm.app.iaas.i18n.Messages;
import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LPlatformConfiguration;
import org.oscm.app.v2_0.exceptions.SuspendException;

/**
 * @author zhaohang
 * 
 */
public class LPlatformClientTest {

    private LPlatformClient lPlatformClient;
    private RORClient rorClient;

    private static final String INSTANCENAME = "instanceName";
    private static final String SERVERTYPE = "serverType";
    private static final String DISKIMAGEID = "diskImageId";
    private static final String CONTROLNETWORKID = "controlNetworkId";
    private static final String VMPOOL = "vmPool";
    private static final String STORAGEPOOL = "storagePool";
    private static final String COUNTCPU = "countCPU";
    private static final String LPLATFORMID = "lplatformId";
    private static final String LSERVERID = "lserverId";
    private static final String LPLATFORMSTATUS = "lplatformStatus";
    private static final String LPLATFORM = "lplatform";

    @Before
    public void setup() throws Exception {
        rorClient = mock(RORClient.class);
        lPlatformClient = new LPlatformClient(rorClient, LPLATFORMID);
    }

    @Test
    public void createLServer() throws Exception {
        // given
        HashMap<String, String> request = givenRequest(INSTANCENAME,
                SERVERTYPE, DISKIMAGEID, CONTROLNETWORKID, VMPOOL, STORAGEPOOL,
                COUNTCPU, LPLATFORMID);
        PrepareXMLConfiguration(request, LSERVERID);

        // when
        String result = lPlatformClient.createLServer(INSTANCENAME, SERVERTYPE,
                DISKIMAGEID, CONTROLNETWORKID, VMPOOL, STORAGEPOOL, COUNTCPU);

        // then
        assertEquals(result, LSERVERID);
    }

    @Test
    public void createLServer_vmPoolIsNull() throws Exception {
        // given
        HashMap<String, String> request = givenRequest(INSTANCENAME,
                SERVERTYPE, DISKIMAGEID, CONTROLNETWORKID, null, STORAGEPOOL,
                COUNTCPU, LPLATFORMID);

        PrepareXMLConfiguration(request, LSERVERID);

        // when
        String result = lPlatformClient.createLServer(INSTANCENAME, SERVERTYPE,
                DISKIMAGEID, CONTROLNETWORKID, null, STORAGEPOOL, COUNTCPU);

        // then
        assertEquals(result, LSERVERID);
    }

    @Test
    public void createLServer_storagePoolIsNull() throws Exception {
        // given
        HashMap<String, String> request = givenRequest(INSTANCENAME,
                SERVERTYPE, DISKIMAGEID, CONTROLNETWORKID, VMPOOL, null,
                COUNTCPU, LPLATFORMID);
        PrepareXMLConfiguration(request, LSERVERID);

        // when
        String result = lPlatformClient.createLServer(INSTANCENAME, SERVERTYPE,
                DISKIMAGEID, CONTROLNETWORKID, VMPOOL, null, COUNTCPU);

        // then
        assertEquals(result, LSERVERID);
    }

    @Test
    public void createLServer_storagePoolLengthIsZero() throws Exception {
        // given
        HashMap<String, String> request = givenRequest(INSTANCENAME,
                SERVERTYPE, DISKIMAGEID, CONTROLNETWORKID, VMPOOL, "  ",
                COUNTCPU, LPLATFORMID);
        PrepareXMLConfiguration(request, LSERVERID);

        // when
        String result = lPlatformClient.createLServer(INSTANCENAME, SERVERTYPE,
                DISKIMAGEID, CONTROLNETWORKID, VMPOOL, "  ", COUNTCPU);

        // then
        assertEquals(result, LSERVERID);
    }

    @Test
    public void createLServer_CountCPUIsNull() throws Exception {
        // given
        HashMap<String, String> request = givenRequest(INSTANCENAME,
                SERVERTYPE, DISKIMAGEID, CONTROLNETWORKID, VMPOOL, STORAGEPOOL,
                null, LPLATFORMID);
        PrepareXMLConfiguration(request, LSERVERID);

        // when
        String result = lPlatformClient.createLServer(INSTANCENAME, SERVERTYPE,
                DISKIMAGEID, CONTROLNETWORKID, VMPOOL, STORAGEPOOL, null);

        // then
        assertEquals(result, LSERVERID);
    }

    @Test
    public void getStatus() throws Exception {
        // given
        HashMap<String, String> request = createRequest("GetLPlatformStatus");
        PrepareXMLConfiguration(request, LPLATFORMSTATUS);

        // when
        String result = lPlatformClient.getStatus();

        // then
        assertEquals(result, LPLATFORMSTATUS);
    }

    @Test
    public void getConfiguration() throws Exception {
        // given
        HashMap<String, String> request = createRequest("GetLPlatformConfiguration");
        PrepareSubnodeConfiguration(request);

        // when
        LPlatformConfiguration result = lPlatformClient.getConfiguration();

        // then
        assertEquals(result.getVSystemId(), LPLATFORMID);
    }

    @Test
    public void destroy() throws Exception {
        // given
        HashMap<String, String> request = createRequest("DestroyLPlatform");

        // when
        lPlatformClient.destroy();

        // then
        verify(rorClient, times(1)).execute(eq(request));
    }

    @Test
    public void stopAllServers() throws Exception {
        // given
        HashMap<String, String> request = createRequest("StopLPlatform");

        // when
        lPlatformClient.stopAllServers();

        // then
        verify(rorClient, times(1)).execute(eq(request));
    }

    @Test
    public void startAllServers() throws Exception {
        // given
        HashMap<String, String> request = createRequest("StartLPlatform");

        // when
        lPlatformClient.startAllServers();

        // then
        verify(rorClient, times(1)).execute(eq(request));
    }

    @Test
    public void getVdcClient() throws Exception {
        // given
        when(rorClient.getBasicParameters()).thenReturn(
                new HashMap<String, String>());

        // when
        lPlatformClient = new LPlatformClient(rorClient, LPLATFORMID);
        RORClient result = lPlatformClient.getVdcClient();

        // then
        assertEquals(result, rorClient);
    }

    @Test(expected = SuspendException.class)
    public void missingPlatformId() throws Exception {
        // given
        lPlatformClient = new LPlatformClient(rorClient, null);
        // when
        try {
            lPlatformClient.stopAllServers();
        }
        // then
        catch (SuspendException se) {
            assertEquals(Messages.get("en", "error_missing_sysid"),
                    se.getLocalizedMessage("en"));
            throw se;
        }

    }

    @Test(expected = SuspendException.class)
    public void emptyPlatformId() throws Exception {
        // given
        lPlatformClient = new LPlatformClient(rorClient, "    ");
        // when
        try {
            lPlatformClient.stopAllServers();
        }
        // then
        catch (SuspendException se) {
            assertEquals(Messages.get("en", "error_missing_sysid"),
                    se.getLocalizedMessage("en"));
            throw se;
        }

    }

    private void PrepareXMLConfiguration(HashMap<String, String> request,
            String configString) throws Exception {
        XMLConfiguration xMLConfiguration = mock(XMLConfiguration.class);
        when(xMLConfiguration.getString(eq(configString))).thenReturn(
                configString);
        when(rorClient.execute(eq(request))).thenReturn(xMLConfiguration);
    }

    private void PrepareSubnodeConfiguration(HashMap<String, String> request)
            throws Exception {
        XMLConfiguration xMLConfiguration = mock(XMLConfiguration.class);
        SubnodeConfiguration config = mock(SubnodeConfiguration.class);
        when(config.getString(eq(LPLATFORMID))).thenReturn(LPLATFORMID);

        when(xMLConfiguration.configurationAt(eq(LPLATFORM)))
                .thenReturn(config);
        when(rorClient.execute(eq(request))).thenReturn(xMLConfiguration);
    }

    private HashMap<String, String> createRequest(String actionValue) {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put("lplatformId", LPLATFORMID);
        request.put("Action", actionValue);

        return request;
    }

    private HashMap<String, String> givenRequest(String instanceName,
            String serverType, String diskImageId, String controlNetworkId,
            String vmPool, String storagePool, String countCPU,
            String lplatformId) {
        HashMap<String, String> request = new HashMap<String, String>();

        request.put("lplatformId", lplatformId);
        request.put("Action", "CreateLServer");
        request.put("lserverName", instanceName);
        request.put("serverType", serverType);
        request.put("diskImageId", diskImageId);
        request.put("controlNetworkId", controlNetworkId);
        if (vmPool != null) {
            request.put("pool", vmPool);
        } else {
            request.put("pool", "VMHostPool");
        }
        if (storagePool != null && storagePool.trim().length() > 0) {
            request.put("storagePool", storagePool);
        } else {
            request.put("storagePool", "StoragePool");
        }
        if (countCPU != null) {
            request.put("numOfCpu", countCPU);
        }
        return request;
    }

}
