/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2015 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 13.01.2015                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fujitsu.bss.app.test.EJBTestBase;
import com.fujitsu.bss.app.test.ejb.TestContainer;
import com.fujitsu.bss.app.v1_0.data.PasswordAuthentication;
import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.v1_0.data.User;
import com.fujitsu.bss.app.v1_0.exceptions.APPlatformException;
import com.fujitsu.bss.app.v1_0.exceptions.AuthenticationException;
import com.fujitsu.bss.app.v1_0.exceptions.ConfigurationException;
import com.fujitsu.bss.app.v1_0.intf.APPlatformService;
import com.fujitsu.bss.app.vmware.VMPropertyHandler;
import com.fujitsu.bss.app.vmware.data.Cluster;
import com.fujitsu.bss.app.vmware.data.DataAccessService;
import com.fujitsu.bss.app.vmware.data.Datacenter;
import com.fujitsu.bss.app.vmware.data.VCenter;

public class TargetLocationBeanTest extends EJBTestBase {

    private APPlatformService platformService;
    private TargetLocationBean bean;
    private FacesContext facesContext;
    private ExternalContext externalContext;
    private HttpSession httpSession;
    private DataAccessService dataAccessService;

    @Override
    protected void setup(TestContainer container) throws Exception {
        platformService = new APPlatformService() {
            HashMap<String, String> settings;

            @Override
            public void sendMail(List<String> mailAddresses, String subject,
                    String text) throws APPlatformException {
            }

            @Override
            public String getEventServiceUrl() throws ConfigurationException {
                return null;
            }

            @Override
            public boolean exists(String controllerId, String instanceId) {
                return false;
            }

            @Override
            public String getBSSWebServiceUrl() throws ConfigurationException {
                return null;
            }

            @Override
            public HashMap<String, String> getControllerSettings(String arg0,
                    PasswordAuthentication arg1) throws AuthenticationException,
                            ConfigurationException, APPlatformException {
                if (settings == null) {
                    settings = new HashMap<String, String>();
                }
                settings.put(VMPropertyHandler.BSS_USER_ID, "user");
                settings.put(VMPropertyHandler.BSS_USER_KEY, "1");
                return settings;
            }

            @Override
            public boolean lockServiceInstance(String arg0, String arg1,
                    PasswordAuthentication arg2) throws AuthenticationException,
                            APPlatformException {
                return false;
            }

            @Override
            public void storeControllerSettings(String arg0,
                    HashMap<String, String> controllerSettings,
                    PasswordAuthentication arg2) throws AuthenticationException,
                            ConfigurationException, APPlatformException {
                settings = controllerSettings;
            }

            @Override
            public void unlockServiceInstance(String arg0, String arg1,
                    PasswordAuthentication arg2) throws AuthenticationException,
                            APPlatformException {
            }

            @Override
            public User authenticate(String arg0, PasswordAuthentication arg1)
                    throws AuthenticationException, ConfigurationException,
                    APPlatformException {
                return null;
            }

            @Override
            public ProvisioningSettings getServiceInstanceDetails(String arg0,
                    String arg1, PasswordAuthentication arg2)
                            throws AuthenticationException,
                            ConfigurationException, APPlatformException {
                return null;
            }

            @Override
            public Collection<String> listServiceInstances(String arg0,
                    PasswordAuthentication arg1) throws AuthenticationException,
                            ConfigurationException, APPlatformException {
                return null;
            }

            @Override
            public void requestControllerSettings(String arg0)
                    throws ConfigurationException, APPlatformException {
            }

        };

        enableJndiMock();
        InitialContext context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, platformService);

        facesContext = Mockito.mock(FacesContext.class);
        externalContext = Mockito.mock(ExternalContext.class);
        httpSession = Mockito.mock(HttpSession.class);
        Mockito.when(facesContext.getExternalContext())
                .thenReturn(externalContext);
        Mockito.when(externalContext.getSession(Matchers.anyBoolean()))
                .thenReturn(httpSession);
        Mockito.when(httpSession.getAttribute(Matchers.anyString()))
                .thenReturn("aValue");

        dataAccessService = Mockito.mock(DataAccessService.class);

        Answer<List<VCenter>> answerVCenter = new Answer<List<VCenter>>() {
            @Override
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

        bean = createTestingBean();
    }

    private TargetLocationBean createTestingBean() {
        return new TargetLocationBean(dataAccessService) {
            @Override
            protected String getDefaultLanguage() {
                // Overwrite static method access
                return "en";
            }

            @Override
            protected FacesContext getContext() {

                return facesContext;
            }

        };
    }

    @Test
    public void testGetHosts() throws Exception {
        List<HostConfig> items = bean.getHosts();
        assertEquals(2, items.size());

        assertEquals("estvmwdev1.lan.est.fujitsu.de", items.get(0).getName());
        assertTrue(items.get(0).isEnabled());
    }

    @Test
    public void testGetStorages() throws Exception {
        List<StorageConfig> items = bean.getStorages();
        assertEquals(2, items.size());

        assertEquals("vmwdev1", items.get(0).getName());
        assertEquals("85%", items.get(0).getLimit());
        assertTrue(items.get(0).isEnabled());
    }

    @Test
    public void testGetHostBalancer() throws Exception {
        HostBalancerConfig cfg = bean.getHostBalancer();
        assertEquals("1", cfg.getCpuWeight());
        assertEquals("1", cfg.getMemoryWeight());
        assertEquals("0.1", cfg.getVmWeight());
    }

    @Test
    public void testUpdateHostBalancer() throws Exception {
        HostBalancerConfig cfg = bean.getHostBalancer();
        assertEquals("1", cfg.getCpuWeight());
        assertEquals("1", cfg.getMemoryWeight());
        assertEquals("0.1", cfg.getVmWeight());

        cfg.setCpuWeight("91");
        cfg.setMemoryWeight("92");
        cfg.setVmWeight("93");

        cfg = bean.getHostBalancer();
        assertEquals("91", cfg.getCpuWeight());
        assertEquals("92", cfg.getMemoryWeight());
        assertEquals("93", cfg.getVmWeight());
    }

    @Test
    public void testGetStorageBalancer() throws Exception {
        List<HostConfig> items = bean.getHosts();
        assertEquals(2, items.size());

        StorageBalancerConfig cfg = items.get(0).getBalancer();
        assertEquals("vmwdev1,vmwdev2", cfg.getStorages());
    }

    @Test
    public void testUpdateStorageBalancer() throws Exception {
        List<HostConfig> items = bean.getHosts();
        assertEquals(2, items.size());

        StorageBalancerConfig cfg = items.get(0).getBalancer();
        assertEquals("vmwdev1,vmwdev2", cfg.getStorages());

        // Set some CSV values
        String newValues = "vmwdev3, vmwdev4";
        cfg.setStorages(newValues);
        bean.save();

        // And validate
        StorageBalancerConfig newcfg = items.get(0).getBalancer();
        assertEquals(newValues, newcfg.getStorages());
    }

    @Test
    public void testAddHost() throws Exception {
        List<HostConfig> items = bean.getHosts();
        assertEquals(2, items.size());
        bean.addHost();
        items = bean.getHosts();
        assertEquals(3, items.size());
    }

    @Test
    public void testAddStorage() throws Exception {
        List<StorageConfig> items = bean.getStorages();
        assertEquals(2, items.size());
        bean.addStorage();
        items = bean.getStorages();
        assertEquals(3, items.size());
    }

    @Test
    public void testDeleteHost() throws Exception {
        List<HostConfig> items = bean.getHosts();
        assertEquals(2, items.size());

        bean.setSelectedRowNum(0);
        assertEquals(0, bean.getSelectedRowNum());

        bean.deleteHost();

        items = bean.getHosts();
        assertEquals(1, items.size());
    }

    @Test
    public void testDeleteStorage() throws Exception {
        List<StorageConfig> items = bean.getStorages();
        assertEquals(2, items.size());

        bean.setSelectedRowNum(0);
        bean.deleteStorage();

        items = bean.getStorages();
        assertEquals(1, items.size());
        assertEquals("vmwdev1", items.get(0).getName());
        assertEquals("15%", items.get(0).getLimit());

    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testDeleteHostOutOfBound1() throws Exception {
        bean.setSelectedRowNum(-1);
        bean.deleteHost();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testDeleteHostOutOfBound2() throws Exception {
        bean.setSelectedRowNum(1000);
        bean.deleteHost();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testDeleteStorageOutOfBound1() throws Exception {
        bean.setSelectedRowNum(-1);
        bean.deleteStorage();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testDeleteStorageOutOfBound2() throws Exception {
        bean.setSelectedRowNum(1000);
        bean.deleteStorage();
    }

    @Test
    public void testSaveConfiguration() throws Exception {
        HostBalancerConfig cfg = bean.getHostBalancer();
        cfg.setCpuWeight("91");
        bean.save();
        Mockito.verify(dataAccessService)
                .setVCenter(Matchers.any(VCenter.class));
        cfg = bean.getHostBalancer();
        assertEquals("91", cfg.getCpuWeight());
    }

}
