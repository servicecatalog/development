/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                             
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

import org.oscm.app.ror.client.LPlatformClient;
import org.oscm.app.ror.client.LServerClient;
import org.oscm.app.ror.client.RORClient;
import org.oscm.app.ror.data.LServerConfiguration;
import org.oscm.app.ror.exceptions.RORException;

/**
 * @author zhaohang
 * 
 */
public class LServerClientTest {

    private LServerClient lServerClient;
    private RORClient vdcClient;

    private static final String LPLATFORMID = "lplatformId";
    private static final String LSERVERID = "lserverId";
    private static final String ACTION = "Action";
    private static final String DESTROYLSERVER = "DestroyLServer";
    private static final String INITIALPASSWORD = "initialPassword";
    private static final String LSERVERSTATUS = "lserverStatus";
    private static final String VMTYPE = "vmType";
    private static final String MEMORYSIZE = "memorySize";

    @Before
    public void setup() throws Exception {
        vdcClient = mock(RORClient.class);
    }

    @Test
    public void destroy() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();
        HashMap<String, String> request = prepareRequest(DESTROYLSERVER);

        // when
        prepareLServerClient(lplatformClient);
        lServerClient.destroy();

        // then
        verify(vdcClient, times(1)).execute(eq(request));
    }

    @Test
    public void getInitialPassword() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();
        HashMap<String, String> request = prepareRequest("GetLServerInitialPassword");
        prepareConfiguration_getString(request, INITIALPASSWORD);

        // // when
        prepareLServerClient(lplatformClient);
        String result = lServerClient.getInitialPassword();

        // then
        assertEquals(result, "initialPassword");
    }

    @Test
    public void getStatus() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();
        HashMap<String, String> request = prepareRequest("GetLServerStatus");
        prepareConfiguration_getString(request, LSERVERSTATUS);

        // when
        prepareLServerClient(lplatformClient);
        String result = lServerClient.getStatus();

        // then
        assertEquals(result, "lserverStatus");
    }

    @Test
    public void getConfiguration() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();
        HashMap<String, String> request = prepareRequest("GetLServerConfiguration");
        XMLConfiguration xMLConfiguration = mock(XMLConfiguration.class);
        SubnodeConfiguration config = mock(SubnodeConfiguration.class);
        when(config.getString(eq(VMTYPE))).thenReturn(VMTYPE);
        when(xMLConfiguration.configurationAt(eq("lserver")))
                .thenReturn(config);
        when(vdcClient.execute(eq(request))).thenReturn(xMLConfiguration);

        // when
        prepareLServerClient(lplatformClient);
        LServerConfiguration result = lServerClient.getConfiguration();

        // then
        assertEquals(result.getVmType(), VMTYPE);
    }

    @Test
    public void updateConfiguration_CountCPUIsNull() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();
        HashMap<String, String> request = prepareRequest("UpdateLServerConfiguration");
        request.put(MEMORYSIZE, MEMORYSIZE);
        prepareConfiguration_toString(request);

        // when
        prepareLServerClient(lplatformClient);
        lServerClient.updateConfiguration(null, MEMORYSIZE);

        // then
        verify(vdcClient, times(1)).execute(eq(request));
    }

    @Test
    public void updateConfiguration_MemorySizeIsNull() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();
        HashMap<String, String> request = prepareRequest("UpdateLServerConfiguration");
        request.put("numOfCpu", "countCPU");
        prepareConfiguration_toString(request);

        // when
        prepareLServerClient(lplatformClient);
        lServerClient.updateConfiguration("countCPU", null);

        // then
        verify(vdcClient, times(1)).execute(eq(request));
    }

    @Test(expected = RORException.class)
    public void updateConfiguration() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();

        // when
        prepareLServerClient(lplatformClient);
        lServerClient.updateConfiguration(null, null);
    }

    @Test
    public void start() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();

        HashMap<String, String> request = prepareRequest("StartLServer");

        // when
        prepareLServerClient(lplatformClient);
        lServerClient.start();

        // then
        verify(vdcClient, times(1)).execute(eq(request));
    }

    @Test
    public void stop() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();
        HashMap<String, String> request = prepareRequest("StopLServer");

        // when
        prepareLServerClient(lplatformClient);
        lServerClient.stop();

        // then
        verify(vdcClient, times(1)).execute(eq(request));
    }

    @Test
    public void createImage() throws Exception {
        // given
        LPlatformClient lplatformClient = prepareLPlatformClient();

        HashMap<String, String> request = prepareRequest("CreateImage");
        request.put("name", "name");
        request.put("imagePool", "imagePool");
        request.put("comment", "comment");

        // when
        prepareLServerClient(lplatformClient);
        lServerClient.createImage("name", "imagePool", "comment");

        // then
        verify(vdcClient, times(1)).execute(eq(request));
    }

    private LPlatformClient prepareLPlatformClient() {
        LPlatformClient lplatformClient = new LPlatformClient(vdcClient,
                LPLATFORMID);
        when(vdcClient.getBasicParameters()).thenReturn(
                new HashMap<String, String>());
        return lplatformClient;
    }

    private void prepareLServerClient(LPlatformClient lplatformClient) {
        lServerClient = new LServerClient(lplatformClient, LSERVERID);
    }

    private HashMap<String, String> prepareRequest(String actionValue) {
        HashMap<String, String> request = new HashMap<String, String>();
        request.put(LPLATFORMID, LPLATFORMID);
        request.put(LSERVERID, LSERVERID);
        request.put(ACTION, actionValue);
        return request;
    }

    private void prepareConfiguration_getString(
            HashMap<String, String> request, String mockValue) throws Exception {
        XMLConfiguration xMLConfiguration = mock(XMLConfiguration.class);
        when(xMLConfiguration.getString(mockValue)).thenReturn(mockValue);
        when(vdcClient.execute(eq(request))).thenReturn(xMLConfiguration);
    }

    private void prepareConfiguration_toString(HashMap<String, String> request)
            throws Exception {
        XMLConfiguration xMLConfiguration = mock(XMLConfiguration.class);
        when(vdcClient.execute(eq(request))).thenReturn(xMLConfiguration);
        when(xMLConfiguration.toString()).thenReturn("xMLConfiguration");
    }
}
