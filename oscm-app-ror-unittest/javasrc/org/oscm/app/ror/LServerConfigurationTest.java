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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;

import org.oscm.app.ror.data.LServerConfiguration;

/**
 * @author zhaohang
 * 
 */
public class LServerConfigurationTest {

    private LServerConfiguration lServerConfiguration;

    private static final String PRIVATEIP = "privateIp";
    private static final String NETWORKID = "networkId";
    private static final String DISKIMAGEID = "diskimageId";
    private static final String LSERVERTYPE = "lserverType";
    private static final String VMTYPE = "vmType";
    private static final String SERVERTYPE = "serverType";
    private static final String LSERVERID = "lserverId";
    private static final String LSERVERNAME = "lserverName";
    private static final String LSERVERSTATUS = "lserverStatus";
    private static final String NUMOFCPU = "numOfCpu";
    private static final String POOL = "pool";
    private static final String STORAGEPOOL = "storagePool";
    private static final String HOSTNAME = "hostName";
    private static final String NICNETWORKID = "nics.nic.networkId";
    private static final String NICPRIVATEIP = "nics.nic.privateIp";
    private static final String NICNO = "nicNo";
    private static final String MANAGEMENT = "management";

    @Test
    public void getPrivateIP() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                NICPRIVATEIP, PRIVATEIP);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(PRIVATEIP, lServerConfiguration.getPrivateIP());
    }

    @Test
    public void getNetworkId() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                NICNETWORKID, NETWORKID);
        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(NETWORKID, lServerConfiguration.getNetworkId());
    }

    @Test
    public void getDiskImageId() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                DISKIMAGEID, DISKIMAGEID);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(DISKIMAGEID, lServerConfiguration.getDiskImageId());
    }

    @Test
    public void getLServerType() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERTYPE, LSERVERTYPE);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(LSERVERTYPE, lServerConfiguration.getLServerType());
    }

    @Test
    public void getVmType() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(VMTYPE,
                VMTYPE);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(VMTYPE, lServerConfiguration.getVmType());
    }

    @Test
    public void getServerType() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                SERVERTYPE, SERVERTYPE);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(SERVERTYPE, lServerConfiguration.getServerType());
    }

    @Test
    public void getServerId() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERID, LSERVERID);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(LSERVERID, lServerConfiguration.getServerId());
    }

    @Test
    public void getServerName() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERNAME, LSERVERNAME);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(LSERVERNAME, lServerConfiguration.getServerName());
    }

    @Test
    public void getLServerStatus() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                LSERVERSTATUS, LSERVERSTATUS);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(LSERVERSTATUS, lServerConfiguration.getLServerStatus());
    }

    @Test
    public void getNumOfCPU() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                NUMOFCPU, NUMOFCPU);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(NUMOFCPU, lServerConfiguration.getNumOfCPU());
    }

    @Test
    public void getPool() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(POOL,
                POOL);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(POOL, lServerConfiguration.getPool());
    }

    @Test
    public void getStoragePool() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                STORAGEPOOL, STORAGEPOOL);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(STORAGEPOOL, lServerConfiguration.getStoragePool());
    }

    @Test
    public void getHostName() {
        // given
        HierarchicalConfiguration configuration = prepareConfiguration(
                HOSTNAME, HOSTNAME);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(HOSTNAME, lServerConfiguration.getHostName());
    }

    @Test
    public void toString_TestString() {
        // given
        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        when(configuration.getString(eq(NICNETWORKID)))
                .thenReturn(NICNETWORKID);
        when(configuration.getString(eq(DISKIMAGEID))).thenReturn(DISKIMAGEID);
        when(configuration.getString(eq(POOL))).thenReturn(POOL);
        when(configuration.getString(eq(LSERVERID))).thenReturn(LSERVERID);
        when(configuration.getString(eq(LSERVERNAME))).thenReturn(LSERVERNAME);
        when(configuration.getString(eq(SERVERTYPE))).thenReturn(SERVERTYPE);
        when(configuration.getString(eq(STORAGEPOOL))).thenReturn(STORAGEPOOL);
        when(configuration.getString(eq(NETWORKID))).thenReturn(NETWORKID);
        when(configuration.getString(eq(NICNO))).thenReturn(NICNO);
        when(configuration.getString(eq(MANAGEMENT))).thenReturn(MANAGEMENT);

        List<HierarchicalConfiguration> nics = new ArrayList<HierarchicalConfiguration>();
        nics.add(configuration);
        when(configuration.configurationsAt(eq("nics.nic"))).thenReturn(nics);

        // when
        lServerConfiguration = new LServerConfiguration(configuration);

        // then
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        NICNETWORKID)));
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        DISKIMAGEID)));
        assertEquals(Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(POOL)));
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        LSERVERID)));
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        LSERVERNAME)));
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        SERVERTYPE)));
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        STORAGEPOOL)));
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        NETWORKID)));
        assertEquals(Boolean.TRUE, Boolean.valueOf(lServerConfiguration
                .toString().contains(NICNO)));
        assertEquals(
                Boolean.TRUE,
                Boolean.valueOf(lServerConfiguration.toString().contains(
                        MANAGEMENT)));
    }

    private HierarchicalConfiguration prepareConfiguration(String requestValue,
            String returnValue) {
        HierarchicalConfiguration configuration = mock(HierarchicalConfiguration.class);
        when(configuration.getString(eq(requestValue))).thenReturn(returnValue);

        return configuration;
    }

}
