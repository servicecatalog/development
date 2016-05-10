/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 12.07.2012                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.balancer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.fujitsu.bss.app.vmware.VMPropertyHandler;
import com.fujitsu.bss.app.vmware.data.VMwareDatacenterInventory;
import com.fujitsu.bss.app.vmware.data.VMwareDatacenterInventoryTest;
import com.fujitsu.bss.app.vmware.data.VMwareStorage;
import com.fujitsu.bss.app.vmware.data.VMwareValue;

/**
 * @author Oliver Soehnges
 * 
 */
public class SequentialStorageBalancerTest {

    private VMPropertyHandler properties;

    @Before
    public void setup() throws Exception {
        properties = Mockito.mock(VMPropertyHandler.class);
    }

    @Test
    public void testEmpty() throws Exception {
        SequentialStorageBalancer balancer = new SequentialStorageBalancer();
        VMwareStorage elm = balancer.next(properties);
        assertNull(elm);
    }

    @Test
    public void testNoneEnabled() throws Exception {

        SequentialStorageBalancer balancer = new SequentialStorageBalancer();
        VMwareDatacenterInventory inventory = new VMwareDatacenterInventory();
        VMwareStorage elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm1", "100", "100"));
        elm.setEnabled(false);

        elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm2", "100", "100"));
        elm.setEnabled(false);

        balancer.setInventory(inventory);
        elm = balancer.next(properties);
        assertNull(elm);
    }

    @Test
    public void testSingle() throws Exception {

        SequentialStorageBalancer balancer = new SequentialStorageBalancer();

        String balancerConfig = "<host><balancer storage=\"elm1\" /></host>";
        XMLConfiguration xmlConfiguration = new XMLHostConfiguration();
        xmlConfiguration.load(new StringReader(balancerConfig));
        balancer.setConfiguration(xmlConfiguration.configurationAt("balancer"));

        VMwareDatacenterInventory inventory = new VMwareDatacenterInventory();

        VMwareStorage elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm1", "100", "100"));
        elm.setEnabled(true);
        elm.setLimit(VMwareValue.parse("90%"));

        balancer.setInventory(inventory);

        elm = balancer.next(properties);
        assertNotNull(elm);
        assertEquals("elm1", elm.getName());

        elm = balancer.next(properties);
        assertNotNull(elm);
        assertEquals("elm1", elm.getName());
    }

    @Test
    public void testMultiple() throws Exception {

        SequentialStorageBalancer balancer = new SequentialStorageBalancer();

        String balancerConfig = "<host><balancer storage=\"elm1,elm2,elm3,elm4\" /></host>";
        XMLConfiguration xmlConfiguration = new XMLHostConfiguration();
        xmlConfiguration.load(new StringReader(balancerConfig));
        balancer.setConfiguration(xmlConfiguration.configurationAt("balancer"));

        VMwareDatacenterInventory inventory = new VMwareDatacenterInventory();

        // elm1 does not provide enough resources with respect to configured
        // limit
        VMwareStorage elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm1", "100", "40"));
        elm.setEnabled(true);
        elm.setLimit(VMwareValue.parse("50%"));

        elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm2", "100", "100"));
        elm.setEnabled(true);
        elm.setLimit(VMwareValue.parse("90%"));

        elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm3", "100", "100"));
        elm.setEnabled(false);
        elm.setLimit(VMwareValue.parse("90%"));

        elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm4", "100", "100"));
        elm.setEnabled(true);
        elm.setLimit(VMwareValue.parse("90%"));

        balancer.setInventory(inventory);

        // getting elm2 since elm1 is not applicable
        elm = balancer.next(properties);
        assertNotNull(elm);
        assertEquals("elm2", elm.getName());

        elm = balancer.next(properties);
        assertNotNull(elm);
        assertEquals("elm2", elm.getName());

        elm = balancer.next(properties);
        assertNotNull(elm);
        assertEquals("elm2", elm.getName());

    }

    @Test
    public void testMultipleReduce() throws Exception {

        SequentialStorageBalancer balancer = new SequentialStorageBalancer();

        String balancerConfig = "<host><balancer storage=\"elm1,elm2,elm3\" /></host>";
        XMLConfiguration xmlConfiguration = new XMLHostConfiguration();
        xmlConfiguration.load(new StringReader(balancerConfig));
        balancer.setConfiguration(xmlConfiguration.configurationAt("balancer"));

        VMwareDatacenterInventory inventory = new VMwareDatacenterInventory();

        VMwareStorage elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm1", "100", "100"));
        elm.setEnabled(true);
        elm.setLimit(VMwareValue.parse("90%"));

        elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm2", "100", "100"));
        elm.setEnabled(true);
        elm.setLimit(VMwareValue.parse("90%"));

        balancer.setInventory(inventory);

        elm = balancer.next(properties);
        assertNotNull(elm);
        assertEquals("elm1", elm.getName());

        // Now shorten list, so "elm2" is not longer next element
        inventory = new VMwareDatacenterInventory();

        elm = inventory.addStorage(VMwareDatacenterInventoryTest
                .createDataStoreProperties("elm3", "100", "100"));
        elm.setEnabled(true);
        elm.setLimit(VMwareValue.parse("90%"));
        balancer.setInventory(inventory);

        elm = balancer.next(properties);
        assertNotNull(elm);
        assertEquals("elm3", elm.getName());
    }

}
