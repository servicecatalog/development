/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2011 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: Jul 12, 2012                                                      
 *                                                                              
 *  Completion Time: Jul 12, 2012                                              
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fujitsu.bss.app.test.EJBTestBase;
import com.fujitsu.bss.app.test.ejb.TestContainer;
import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.vmware.api.VMwareClient;
import com.fujitsu.bss.app.vmware.data.Cluster;
import com.fujitsu.bss.app.vmware.data.DataAccessService;
import com.fujitsu.bss.app.vmware.data.Datacenter;
import com.fujitsu.bss.app.vmware.data.VCenter;
import com.fujitsu.bss.app.vmware.data.VMwareCredentials;
import com.fujitsu.bss.app.vmware.i18n.Messages;

/**
 * @author Dirk Bernsau
 * 
 */
public class VMwareClientFactoryBeanTest extends EJBTestBase {
    private HashMap<String, String> parameters = new HashMap<String, String>();
    private HashMap<String, String> configSettings = new HashMap<String, String>();
    private ProvisioningSettings settings = new ProvisioningSettings(
            parameters, configSettings, Messages.DEFAULT_LOCALE);
    private DataAccessService dataAccessService;

    @Override
    protected void setup(TestContainer container) throws Exception {
        dataAccessService = Mockito.mock(DataAccessService.class);

        Answer<List<VCenter>> answerVCenter = new Answer<List<VCenter>>() {
            public List<VCenter> answer(InvocationOnMock invocation)
                    throws Throwable {
                List<VCenter> vcenter = new ArrayList<VCenter>();
                VCenter vc1 = new VCenter();
                vc1.identifier = "id1";
                vc1.setUrl("http://vcenter1.intern.est.fujitsu.com");
                vc1.name = "vcenterserver1";
                vc1.datacenter = new ArrayList<Datacenter>();
                vc1.tkey = 1;
                VCenter vc2 = new VCenter();
                vc2.identifier = "id2";
                vc2.setUrl("http://vcenter2.intern.est.fujitsu.com");
                vc2.name = "vcenterserver2";
                vc2.datacenter = new ArrayList<Datacenter>();
                vc2.tkey = 2;

                Datacenter dc1 = new Datacenter();
                dc1.cluster = new ArrayList<Cluster>();
                dc1.name = "datacenter1";
                Datacenter dc2 = new Datacenter();
                dc2.cluster = new ArrayList<Cluster>();
                dc2.name = "datacenter2";
                Datacenter dc3 = new Datacenter();
                dc3.cluster = new ArrayList<Cluster>();
                dc3.name = "datacenter3";

                vc1.datacenter.add(dc1);
                vc2.datacenter.add(dc2);
                vc2.datacenter.add(dc3);

                Cluster cl1 = new Cluster();
                cl1.name = "cluster 1";
                cl1.setPassword("pw1");
                cl1.setUserid("user1");
                cl1.loadbalancer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><essvcenter><balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer1\" memoryWeight=\"1\" cpuWeight=\"1\" vmWeight=\"0.1\" /><host name=\"estvmwdev1.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer1\" storage=\"vmwdev1,vmwdev2\" /></host><host name=\"estvmwdev21.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer1\" storage=\"vmwdev1,vmwdev1\" /></host><storage name=\"vmwdev1\" enabled=\"true\" limit=\"85%\" /><storage name=\"vmwdev1\" enabled=\"true\" limit=\"15%\" /></essvcenter>";
                Cluster cl2 = new Cluster();
                cl2.name = "cluster 2";
                cl2.setPassword("pw2");
                cl2.setUserid("user2");
                cl2.loadbalancer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><essvcenter><balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer2\" memoryWeight=\"2\" cpuWeight=\"12\" vmWeight=\"0.2\" /><host name=\"estvmwdev2.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer2\" storage=\"vmwdev12,vmwdev2\" /></host><host name=\"estvmwdev22.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer2\" storage=\"vmwdev22,vmwdev2\" /></host><storage name=\"vmwdev21\" enabled=\"true\" limit=\"82%\" /><storage name=\"vmwdev22\" enabled=\"true\" limit=\"25%\" /></essvcenter>";
                Cluster cl3 = new Cluster();
                cl3.name = "cluster 3";
                cl3.setPassword("pw3");
                cl3.setUserid("user3");
                cl3.loadbalancer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><essvcenter><balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer3\" memoryWeight=\"3\" cpuWeight=\"13\" vmWeight=\"0.3\" /><host name=\"estvmwdev3.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer3\" storage=\"vmwdev13,vmwdev2\" /></host><host name=\"estvmwdev23.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer3\" storage=\"vmwdev32,vmwdev3\" /></host><storage name=\"vmwdev31\" enabled=\"true\" limit=\"83%\" /><storage name=\"vmwdev32\" enabled=\"true\" limit=\"35%\" /></essvcenter>";
                Cluster cl4 = new Cluster();
                cl4.name = "cluster 4";
                cl4.setPassword("pw4");
                cl4.setUserid("user4");
                cl4.loadbalancer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><essvcenter><balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer4\" memoryWeight=\"4\" cpuWeight=\"14\" vmWeight=\"0.4\" /><host name=\"estvmwdev4.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer4\" storage=\"vmwdev14,vmwdev2\" /></host><host name=\"estvmwdev24.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer4\" storage=\"vmwdev42,vmwdev4\" /></host><storage name=\"vmwdev41\" enabled=\"true\" limit=\"84%\" /><storage name=\"vmwdev42\" enabled=\"true\" limit=\"45%\" /></essvcenter>";
                Cluster cl5 = new Cluster();
                cl5.name = "cluster 5";
                cl5.setPassword("pw5");
                cl5.setUserid("user5");
                cl5.loadbalancer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><essvcenter><balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer5\" memoryWeight=\"5\" cpuWeight=\"15\" vmWeight=\"0.5\" /><host name=\"estvmwdev5.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer5\" storage=\"vmwdev14,vmwdev2\" /></host><host name=\"estvmwdev25.lan.est.fujitsu.de\" enabled=\"true\"><balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer5\" storage=\"vmwdev52,vmwdev5\" /></host><storage name=\"vmwdev51\" enabled=\"true\" limit=\"85%\" /><storage name=\"vmwdev52\" enabled=\"true\" limit=\"45%\" /></essvcenter>";

                dc1.cluster.add(cl1);
                dc1.cluster.add(cl2);
                dc2.cluster.add(cl3);
                dc2.cluster.add(cl4);
                dc3.cluster.add(cl5);

                vcenter.add(vc1);
                vcenter.add(vc2);
                return vcenter;
            }
        };

        Mockito.doAnswer(answerVCenter).when(dataAccessService).getVCenter();
    }

    @Test(expected = Exception.class)
    public void testGetInstance_missing_URL() throws Exception {
        VMClientFactoryBean bean = new VMClientFactoryBean(dataAccessService);
        VMwareClient instance = bean
                .getInstance(new VMPropertyHandler(settings));
        assertNotNull(instance);
    }

    @Test(expected = Exception.class)
    public void testGetInstance_missing_UserId() throws Exception {
        Mockito.when(dataAccessService.getURL(Matchers.anyString()))
                .thenReturn("");
        VMwareCredentials credentials = new VMwareCredentials();
        Mockito.when(
                dataAccessService.getCredentials(Matchers.anyString(),
                        Matchers.anyString(), Matchers.anyString()))
                .thenReturn(credentials);

        // configSettings.put(VMwarePropertyHandler.CTL_CONTROLLER_DATABASE,
        // "PoolName");
        VMClientFactoryBean bean = new VMClientFactoryBean(dataAccessService);
        VMwareClient instance = bean
                .getInstance(new VMPropertyHandler(settings));
        assertNotNull(instance);
    }

    @Test(expected = Exception.class)
    public void testGetInstance_missing_Password() throws Exception {
        Mockito.when(dataAccessService.getURL(Matchers.anyString()))
                .thenReturn("");
        VMwareCredentials credentials = new VMwareCredentials();
        credentials.setUserid("userid");
        Mockito.when(
                dataAccessService.getCredentials(Matchers.anyString(),
                        Matchers.anyString(), Matchers.anyString()))
                .thenReturn(credentials);

        VMClientFactoryBean bean = new VMClientFactoryBean(dataAccessService);
        VMwareClient instance = bean
                .getInstance(new VMPropertyHandler(settings));
        assertNotNull(instance);
    }

    @Test
    public void testGetInstance() throws Exception {
        Mockito.when(dataAccessService.getURL(Matchers.anyString()))
                .thenReturn("");
        VMwareCredentials credentials = new VMwareCredentials();
        credentials.setUserid("userid");
        credentials.setPassword("secret");
        Mockito.when(
                dataAccessService.getCredentials(Matchers.anyString(),
                        Matchers.anyString(), Matchers.anyString()))
                .thenReturn(credentials);

        VMClientFactoryBean bean = new VMClientFactoryBean(dataAccessService);
        VMwareClient instance = bean
                .getInstance(new VMPropertyHandler(settings));
        assertNotNull(instance);
    }

}
