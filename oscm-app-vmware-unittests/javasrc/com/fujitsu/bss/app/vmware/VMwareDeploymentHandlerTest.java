/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 12.07.2012                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.fujitsu.bss.app.test.EJBTestBase;
import com.fujitsu.bss.app.test.ejb.TestContainer;
import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.vmware.data.DataAccessService;
import com.fujitsu.bss.app.vmware.data.VMwareDatacenterInventory;
import com.fujitsu.bss.app.vmware.data.VMwareDatacenterInventoryTest;
import com.fujitsu.bss.app.vmware.data.VMwareHost;
import com.fujitsu.bss.app.vmware.data.VMwareStorage;
import com.fujitsu.bss.app.vmware.i18n.Messages;

/**
 * @author Oliver Soehnges
 * 
 */
public class VMwareDeploymentHandlerTest extends EJBTestBase {

    private VMwareDatacenterInventory inventory;

    private HashMap<String, String> parameters = new HashMap<String, String>();
    private HashMap<String, String> configSettings = new HashMap<String, String>();
    private ProvisioningSettings settings = new ProvisioningSettings(
            parameters, configSettings, Messages.DEFAULT_LOCALE);
    private VMPropertyHandler propertyHandler;
    private DataAccessService dataAccessService;

    final String HOSTCONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<essvcenter>"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer\" />"
            + "<host name=\"host1\" enabled=\"true\">"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionStorageBalancer\" storage=\"storage1, storage2,storage3,storage4,storage6\" />"
            + "</host>"
            + "<storage name=\"storage1\" enabled=\"true\" />"
            + "<storage name=\"storage2\" enabled=\"true\" />"
            + "<storage name=\"storage3\" enabled=\"true\" />"
            + "<storage name=\"storage4\" enabled=\"true\" limit=\"80%\"/>"
            + "<storage name=\"storage5\" enabled=\"true\" />"
            + "<storage name=\"storage6\" enabled=\"false\" />"
            + "</essvcenter>";

    @Override
    protected void setup(TestContainer container) throws Exception {

        inventory = new VMwareDatacenterInventory();
        parameters.put(VMPropertyHandler.TS_INSTANCENAME_PREFIX, "estess");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME_PATTERN,
                "estess([a-z0-9]){6,8}");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "123456");

        parameters.put(VMPropertyHandler.TS_NUMBER_OF_CPU, "1");
        parameters.put(VMPropertyHandler.TS_AMOUNT_OF_RAM, "512");
        parameters.put(VMPropertyHandler.TS_DISK_SIZE, "20");

        settings = new ProvisioningSettings(parameters, configSettings,
                Messages.DEFAULT_LOCALE);
        propertyHandler = new VMPropertyHandler(settings);
        dataAccessService = Mockito.mock(DataAccessService.class);

        Mockito.when(
                dataAccessService.getHostLoadBalancerConfig(
                        Matchers.anyString(), Matchers.anyString(),
                        Matchers.anyString())).thenReturn("");

        propertyHandler.useMock(dataAccessService);

    }

    @Test(expected = RuntimeException.class)
    public void testConfigurationEmpty() throws Exception {

        VMTargetLocationHandler deploymentHandler = new VMTargetLocationHandler(
                propertyHandler, inventory);
        deploymentHandler.toString();
    }

    @Test
    public void testSimpleDeployment() throws Exception {

        Mockito.when(
                dataAccessService.getHostLoadBalancerConfig(
                        Matchers.anyString(), Matchers.anyString(),
                        Matchers.anyString())).thenReturn(HOSTCONFIG);

        inventory.addHostSystem(VMwareDatacenterInventoryTest
                .createHostSystemProperties("host1", "16284", "8"));
        inventory.addHostSystem(VMwareDatacenterInventoryTest
                .createHostSystemProperties("host2", "16284", "8"));
        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage1", "12000000000",
                        "5000000000"));
        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage2", "12000000000",
                        "4000000000"));
        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage3", "12000000000",
                        "8000000000"));
        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage4", "12000000000",
                        "100000000"));
        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage5", "12000000000",
                        "9000000000"));
        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage6", "12000000000",
                        "9000000000"));
        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage7", "12000000000",
                        "9000000000"));

        VMTargetLocationHandler deploymentHandler = new VMTargetLocationHandler(
                propertyHandler, inventory);

        VMwareHost host;
        VMwareStorage storage;

        // Now check deployment
        host = deploymentHandler.getTargetHost();
        assertNotNull(host);
        assertEquals("host1", host.getName());
        storage = deploymentHandler.getTargetStorage(host);
        assertNotNull(storage);
        assertEquals("storage3", storage.getName());

        inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("storage3", "12000000000",
                        "3000000000"));

        deploymentHandler = new VMTargetLocationHandler(propertyHandler,
                inventory);
        // Next call (identical because only one host and storage defined)
        host = deploymentHandler.getTargetHost();
        assertNotNull(host);
        assertEquals("host1", host.getName());
        storage = deploymentHandler.getTargetStorage(host);
        assertNotNull(storage);
        assertEquals("storage1", storage.getName());
    }
}
