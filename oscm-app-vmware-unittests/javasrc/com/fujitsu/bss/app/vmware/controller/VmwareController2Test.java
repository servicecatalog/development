/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2015 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 25.08.2015                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.fujitsu.bss.app.v1_0.data.InstanceDescription;
import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.v1_0.intf.APPlatformService;
import com.fujitsu.bss.app.vmware.VMPropertyHandler;
import com.fujitsu.bss.app.vmware.data.VMwareOperation;
import com.fujitsu.bss.app.vmware.data.VMwareStatus;
import com.fujitsu.bss.app.vmware.i18n.Messages;

/**
 * @author kulle
 *
 */
public class VmwareController2Test {

    private VMController controller;
    private ProvisioningSettings settings;
    private HashMap<String, String> parameters;

    @Before
    public void before() {
        controller = spy(new VMController());
        controller.platformService = mock(APPlatformService.class);
        parameters = new HashMap<String, String>();
        HashMap<String, String> configSettings = new HashMap<String, String>();
        settings = new ProvisioningSettings(parameters, configSettings,
                Messages.DEFAULT_LOCALE);
    }

    @Test
    public void createInstance_linux() throws Exception {
        // given
        settings.setOrganizationId("orgid");
        parameters.put(VMPropertyHandler.TS_WINDOWS_DOMAIN_JOIN, "false");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "123456");
        parameters.put(VMPropertyHandler.TS_DISK_SIZE, "20");
        parameters.put(VMPropertyHandler.TS_NUMBER_OF_CPU, "1");
        parameters.put(VMPropertyHandler.TS_AMOUNT_OF_RAM, "512");
        parameters.put(VMPropertyHandler.TS_TEMPLATENAME, "centOSTemplate");
        parameters.put(VMPropertyHandler.TS_NUMBER_OF_NICS, "1");
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_SETTINGS, "DHCP");
        parameters.put(VMPropertyHandler.TS_NIC2_NETWORK_SETTINGS, "DHCP");
        parameters.put(VMPropertyHandler.TS_NIC3_NETWORK_SETTINGS, "DHCP");
        parameters.put(VMPropertyHandler.TS_NIC4_NETWORK_SETTINGS, "DHCP");
        doReturn(Boolean.FALSE).when(controller)
                .areTriggerDefined(any(VMPropertyHandler.class), anyString());
        doReturn(Boolean.FALSE).when(controller.platformService)
                .exists(anyString(), anyString());

        // when
        InstanceDescription instance = controller.createInstance(settings);

        // then
        assertNotNull(instance.getInstanceId());
        assertEquals(VMwareStatus.CREATION_REQUESTED.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.CREATION.toString(),
                parameters.get(VMPropertyHandler.OPERATION));
    }

}
