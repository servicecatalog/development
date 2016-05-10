/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 12.07.2012                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.v1_0.exceptions.APPlatformException;
import com.fujitsu.bss.app.vmware.data.DataAccessService;
import com.fujitsu.bss.app.vmware.data.VMwareOperation;
import com.fujitsu.bss.app.vmware.i18n.Messages;

/**
 * @author Dirk Bernsau
 * 
 */
public class VMwarePropertyHandlerTest {

    private HashMap<String, String> parameters;
    private HashMap<String, String> configSettings;
    private ProvisioningSettings settings;
    private VMPropertyHandler propertyHandler = new VMPropertyHandler(settings);
    private DataAccessService das;

    @Before
    public void before() {
        parameters = new HashMap<String, String>();
        configSettings = new HashMap<String, String>();
        settings = new ProvisioningSettings(parameters, configSettings,
                Messages.DEFAULT_LOCALE);
        propertyHandler = spy(new VMPropertyHandler(settings));

        das = mock(DataAccessService.class);
        doReturn(das).when(propertyHandler).getDataAccessService();
    }

    @Test
    public void getLinuxTimezone() {
        // given
        settings.getParameters().put(VMPropertyHandler.TS_TIMEZONE_LINUX,
                "363");
        settings.getParameters().put(VMPropertyHandler.TS_TIMEZONE_WINDOWS,
                "110");

        // when
        String timzone = propertyHandler.getLinuxTimezone();

        // then
        assertTrue("Etc/GMT+1".equals(timzone));
    }

    @Test
    public void getWindowsTimezone() {
        // given
        settings.getParameters().put(VMPropertyHandler.TS_TIMEZONE_LINUX,
                "363");
        settings.getParameters().put(VMPropertyHandler.TS_TIMEZONE_WINDOWS,
                "110");

        // when
        String timzone = propertyHandler.getWindowsTimezone();

        // then
        assertTrue(
                "(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"
                        .equals(timzone));
    }

    @Test
    public void testNetworkParameter() {
        settings.getParameters().put(VMPropertyHandler.TS_NIC1_NETWORK_SETTINGS,
                "DHCP");
        settings.getParameters().put(VMPropertyHandler.TS_NIC2_NETWORK_SETTINGS,
                "DHCP");
        settings.getParameters().put(VMPropertyHandler.TS_NIC3_NETWORK_SETTINGS,
                "DHCP");
        settings.getParameters().put(VMPropertyHandler.TS_NIC4_NETWORK_SETTINGS,
                "DHCP");

        settings.getParameters().put(VMPropertyHandler.TS_NIC1_GATEWAY,
                "127.0.0.1");
        settings.getParameters().put(VMPropertyHandler.TS_NIC2_GATEWAY,
                "127.0.0.2");
        settings.getParameters().put(VMPropertyHandler.TS_NIC3_GATEWAY,
                "127.0.0.3");
        settings.getParameters().put(VMPropertyHandler.TS_NIC4_GATEWAY,
                "127.0.0.4");

        settings.getParameters().put(VMPropertyHandler.TS_NIC1_IP_ADDRESS,
                "127.1.0.1");
        settings.getParameters().put(VMPropertyHandler.TS_NIC2_IP_ADDRESS,
                "127.1.0.2");
        settings.getParameters().put(VMPropertyHandler.TS_NIC3_IP_ADDRESS,
                "127.1.0.3");
        settings.getParameters().put(VMPropertyHandler.TS_NIC4_IP_ADDRESS,
                "127.1.0.4");

        settings.getParameters().put(VMPropertyHandler.TS_NIC1_DNS_SERVER,
                "127.2.0.1");
        settings.getParameters().put(VMPropertyHandler.TS_NIC2_DNS_SERVER,
                "127.2.0.2");
        settings.getParameters().put(VMPropertyHandler.TS_NIC3_DNS_SERVER,
                "127.2.0.3");
        settings.getParameters().put(VMPropertyHandler.TS_NIC4_DNS_SERVER,
                "127.2.0.4");

        settings.getParameters().put(VMPropertyHandler.TS_NIC1_DNS_SUFFIX,
                "suffix1");
        settings.getParameters().put(VMPropertyHandler.TS_NIC2_DNS_SUFFIX,
                "suffix2");
        settings.getParameters().put(VMPropertyHandler.TS_NIC3_DNS_SUFFIX,
                "suffix3");
        settings.getParameters().put(VMPropertyHandler.TS_NIC4_DNS_SUFFIX,
                "suffix4");

        settings.getParameters().put(VMPropertyHandler.TS_NIC1_SUBNET_MASK,
                "255.255.1.0");
        settings.getParameters().put(VMPropertyHandler.TS_NIC2_SUBNET_MASK,
                "255.255.2.0");
        settings.getParameters().put(VMPropertyHandler.TS_NIC3_SUBNET_MASK,
                "255.255.3.0");
        settings.getParameters().put(VMPropertyHandler.TS_NIC4_SUBNET_MASK,
                "255.255.4.0");

        for (int i = 1; i < 5; i++) {
            propertyHandler.useDHCP(i);
            propertyHandler.getGateway(i);
            propertyHandler.getIPAddress(i);
            propertyHandler.getDNSServer(i);
            propertyHandler.getDNSSuffix(i);
            propertyHandler.getSubnetMask(i);
        }

        try {
            propertyHandler.useDHCP(5);
        } catch (IllegalArgumentException e) {
            assertTrue("NIC identifier 5 is out of range. Valid range is [1-4]."
                    .equals(e.getMessage()));
        }

        try {
            propertyHandler.getGateway(5);
        } catch (IllegalArgumentException e) {
            assertTrue("NIC identifier 5 is out of range. Valid range is [1-4]."
                    .equals(e.getMessage()));
        }

        try {
            propertyHandler.getIPAddress(5);
        } catch (IllegalArgumentException e) {
            assertTrue("NIC identifier 5 is out of range. Valid range is [1-4]."
                    .equals(e.getMessage()));
        }

        try {
            propertyHandler.getDNSServer(5);
        } catch (IllegalArgumentException e) {
            assertTrue("NIC identifier 5 is out of range. Valid range is [1-4]."
                    .equals(e.getMessage()));
        }

        try {
            propertyHandler.getDNSSuffix(5);
        } catch (IllegalArgumentException e) {
            assertTrue("NIC identifier 5 is out of range. Valid range is [1-4]."
                    .equals(e.getMessage()));
        }

        try {
            propertyHandler.getSubnetMask(5);
        } catch (IllegalArgumentException e) {
            assertTrue("NIC identifier 5 is out of range. Valid range is [1-4]."
                    .equals(e.getMessage()));
        }
    }

    @Test
    public void getTargetClusterFromLocation() throws APPlatformException {
        // given
        settings.getParameters().put(VMPropertyHandler.TS_TARGET_LOCATION,
                "site@datacenter@cluster");

        // when
        String cluster = propertyHandler.getTargetCluster();

        // then
        assertTrue("cluster".equals(cluster));
    }

    @Test
    public void testDiskSizeParameter() throws APPlatformException {
        // given
        settings.getParameters().put(VMPropertyHandler.TS_DISK_SIZE, "17");

        // when
        double diskSize = propertyHandler.getConfigDiskSpaceMB();

        // then
        assertTrue(diskSize == (17.0 * 1024));
    }

    @Test
    public void testDiskSizeParameterMissing() throws APPlatformException {
        // given

        // when
        double diskSize = propertyHandler.getConfigDiskSpaceMB();

        // then
        assertTrue(diskSize == .0);
    }

    @Test(expected = APPlatformException.class)
    public void testDiskSizeParameterInvalid() throws APPlatformException {
        // given
        settings.getParameters().put(VMPropertyHandler.TS_DISK_SIZE, "12abc");

        // when
        propertyHandler.getConfigDiskSpaceMB();
    }

    @Test
    public void testOperationParameterUnknown() {
        // given no parameters

        // when
        VMwareOperation operation = propertyHandler.getOperation();

        // then
        assertEquals(VMwareOperation.UNKNOWN, operation);
    }

    @Test
    public void testLDAPParameterTest() {
        configSettings.put(VMPropertyHandler.CTL_LDAP_HOSTURL, "ldap_host");
        configSettings.put(VMPropertyHandler.CTL_LDAP_USER, "ldap_user");
        configSettings.put(VMPropertyHandler.CTL_LDAP_PWD, "ldap_pwd");
        configSettings.put(VMPropertyHandler.CTL_LDAP_QUERY_GETUSERCN,
                "ldap_query");
        VMPropertyHandler propertyHandler = new VMPropertyHandler(settings);
        assertEquals("ldap_host", propertyHandler
                .getControllerSetting(VMPropertyHandler.CTL_LDAP_HOSTURL));
        assertEquals("ldap_user", propertyHandler
                .getControllerSetting(VMPropertyHandler.CTL_LDAP_USER));
        assertEquals("ldap_pwd", propertyHandler
                .getControllerSetting(VMPropertyHandler.CTL_LDAP_PWD));
        assertEquals("ldap_query", propertyHandler.getControllerSetting(
                VMPropertyHandler.CTL_LDAP_QUERY_GETUSERCN));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNetworkAdapter_0() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER, "adapter 1");
        parameters.put(VMPropertyHandler.TS_NIC2_NETWORK_ADAPTER, "adapter 2");
        parameters.put(VMPropertyHandler.TS_NIC3_NETWORK_ADAPTER, "adapter 3");
        parameters.put(VMPropertyHandler.TS_NIC4_NETWORK_ADAPTER, "adapter 4");

        // when
        propertyHandler.getNetworkAdapter(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNetworkAdapter_greater_4() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER, "adapter 1");
        parameters.put(VMPropertyHandler.TS_NIC2_NETWORK_ADAPTER, "adapter 2");
        parameters.put(VMPropertyHandler.TS_NIC3_NETWORK_ADAPTER, "adapter 3");
        parameters.put(VMPropertyHandler.TS_NIC4_NETWORK_ADAPTER, "adapter 4");

        // when
        propertyHandler.getNetworkAdapter(5);
    }

    @Test
    public void getNetworkAdapter_1() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER, "adapter 1");
        parameters.put(VMPropertyHandler.TS_NIC2_NETWORK_ADAPTER, "adapter 2");
        parameters.put(VMPropertyHandler.TS_NIC3_NETWORK_ADAPTER, "adapter 3");
        parameters.put(VMPropertyHandler.TS_NIC4_NETWORK_ADAPTER, "adapter 4");

        // when
        String adapter = propertyHandler.getNetworkAdapter(1);

        // then
        assertEquals("adapter 1", adapter);
    }

    @Test
    public void getNetworkAdapter_2() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER, "adapter 1");
        parameters.put(VMPropertyHandler.TS_NIC2_NETWORK_ADAPTER, "adapter 2");
        parameters.put(VMPropertyHandler.TS_NIC3_NETWORK_ADAPTER, "adapter 3");
        parameters.put(VMPropertyHandler.TS_NIC4_NETWORK_ADAPTER, "adapter 4");

        // when
        String adapter = propertyHandler.getNetworkAdapter(2);

        // then
        assertEquals("adapter 2", adapter);
    }

    @Test
    public void getNetworkAdapter_3() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER, "adapter 1");
        parameters.put(VMPropertyHandler.TS_NIC2_NETWORK_ADAPTER, "adapter 2");
        parameters.put(VMPropertyHandler.TS_NIC3_NETWORK_ADAPTER, "adapter 3");
        parameters.put(VMPropertyHandler.TS_NIC4_NETWORK_ADAPTER, "adapter 4");

        // when
        String adapter = propertyHandler.getNetworkAdapter(3);

        // then
        assertEquals("adapter 3", adapter);
    }

    @Test
    public void getNetworkAdapter_4() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_ADAPTER, "adapter 1");
        parameters.put(VMPropertyHandler.TS_NIC2_NETWORK_ADAPTER, "adapter 2");
        parameters.put(VMPropertyHandler.TS_NIC3_NETWORK_ADAPTER, "adapter 3");
        parameters.put(VMPropertyHandler.TS_NIC4_NETWORK_ADAPTER, "adapter 4");

        // when
        String adapter = propertyHandler.getNetworkAdapter(4);

        // then
        assertEquals("adapter 4", adapter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIPAddress_NIC0() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_IP_ADDRESS, "ip address 1");
        parameters.put(VMPropertyHandler.TS_NIC2_IP_ADDRESS, "ip address 2");
        parameters.put(VMPropertyHandler.TS_NIC3_IP_ADDRESS, "ip address 3");
        parameters.put(VMPropertyHandler.TS_NIC4_IP_ADDRESS, "ip address 4");

        // when
        propertyHandler.getIPAddress(0);
    }

    @Test
    public void getIPAddress_NIC1() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_IP_ADDRESS, "ip address 1");
        parameters.put(VMPropertyHandler.TS_NIC2_IP_ADDRESS, "ip address 2");
        parameters.put(VMPropertyHandler.TS_NIC3_IP_ADDRESS, "ip address 3");
        parameters.put(VMPropertyHandler.TS_NIC4_IP_ADDRESS, "ip address 4");

        // when
        String ipAddress = propertyHandler.getIPAddress(1);

        // then
        assertEquals("ip address 1", ipAddress);
    }

    @Test
    public void getIPAddress_NIC1_undefined() {
        // given

        // when
        String ipAddress = propertyHandler.getIPAddress(1);

        // then
        assertNull(ipAddress);
    }

    @Test
    public void getIPAddress_NIC2() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_IP_ADDRESS, "ip address 1");
        parameters.put(VMPropertyHandler.TS_NIC2_IP_ADDRESS, "ip address 2");
        parameters.put(VMPropertyHandler.TS_NIC3_IP_ADDRESS, "ip address 3");
        parameters.put(VMPropertyHandler.TS_NIC4_IP_ADDRESS, "ip address 4");

        // when
        String ipAddress = propertyHandler.getIPAddress(2);

        // then
        assertEquals("ip address 2", ipAddress);
    }

    @Test
    public void getIPAddress_NIC3() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_IP_ADDRESS, "ip address 1");
        parameters.put(VMPropertyHandler.TS_NIC2_IP_ADDRESS, "ip address 2");
        parameters.put(VMPropertyHandler.TS_NIC3_IP_ADDRESS, "ip address 3");
        parameters.put(VMPropertyHandler.TS_NIC4_IP_ADDRESS, "ip address 4");

        // when
        String ipAddress = propertyHandler.getIPAddress(3);

        // then
        assertEquals("ip address 3", ipAddress);
    }

    @Test
    public void getIPAddress_NIC4() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_IP_ADDRESS, "ip address 1");
        parameters.put(VMPropertyHandler.TS_NIC2_IP_ADDRESS, "ip address 2");
        parameters.put(VMPropertyHandler.TS_NIC3_IP_ADDRESS, "ip address 3");
        parameters.put(VMPropertyHandler.TS_NIC4_IP_ADDRESS, "ip address 4");

        // when
        String ipAddress = propertyHandler.getIPAddress(4);

        // then
        assertEquals("ip address 4", ipAddress);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIPAddress_greater_4() {
        // given
        parameters.put(VMPropertyHandler.TS_NIC1_IP_ADDRESS, "ip address 1");
        parameters.put(VMPropertyHandler.TS_NIC2_IP_ADDRESS, "ip address 2");
        parameters.put(VMPropertyHandler.TS_NIC3_IP_ADDRESS, "ip address 3");
        parameters.put(VMPropertyHandler.TS_NIC4_IP_ADDRESS, "ip address 4");

        // when
        propertyHandler.getIPAddress(5);
    }
}
