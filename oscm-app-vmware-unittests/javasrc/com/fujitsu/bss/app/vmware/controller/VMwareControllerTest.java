/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: Jul 12, 2012                                                      
 *                                                                              
 *  Completion Time: Jul 12, 2012                                              
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.xml.ws.BindingProvider;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fujitsu.bss.app.test.EJBTestBase;
import com.fujitsu.bss.app.test.ejb.TestContainer;
import com.fujitsu.bss.app.v1_0.data.InstanceDescription;
import com.fujitsu.bss.app.v1_0.data.InstanceStatus;
import com.fujitsu.bss.app.v1_0.data.PasswordAuthentication;
import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.v1_0.exceptions.APPlatformException;
import com.fujitsu.bss.app.v1_0.intf.APPlatformController;
import com.fujitsu.bss.app.v1_0.intf.APPlatformService;
import com.fujitsu.bss.app.vmware.VMClientFactoryLocal;
import com.fujitsu.bss.app.vmware.VMPropertyHandler;
import com.fujitsu.bss.app.vmware.api.ManagedObjectAccessor;
import com.fujitsu.bss.app.vmware.api.ServiceConnection;
import com.fujitsu.bss.app.vmware.api.VMwareClient;
import com.fujitsu.bss.app.vmware.bes.BESClient;
import com.fujitsu.bss.app.vmware.bes.Credentials;
import com.fujitsu.bss.app.vmware.data.DataAccessService;
import com.fujitsu.bss.app.vmware.data.VMwareDatacenterInventoryTest;
import com.fujitsu.bss.app.vmware.data.VMwareOperation;
import com.fujitsu.bss.app.vmware.data.VMwareStatus;
import com.fujitsu.bss.app.vmware.i18n.Messages;
import com.fujitsu.bss.intf.IdentityService;
import com.fujitsu.bss.vo.VOUser;
import com.vmware.vim25.Description;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskFilterSpec;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRuntimeInfo;

/**
 * @author Dirk Bernsau
 * 
 */
public class VMwareControllerTest extends EJBTestBase {

    private static final String DEFAULT_ORG_ID = "org123abc123";
    private static final String DEFAULT_USER_NAME = "user";
    private static final String EIGHT_GIG = "8388608";

    private APPlatformController controller;
    private APPlatformService platformService;

    private VMwareClient vmwClient;
    private ManagedObjectAccessor serviceUtil;
    private ServiceConnection serviceConn;

    private HashMap<String, String> parameters = new HashMap<String, String>();
    private HashMap<String, String> configSettings = new HashMap<String, String>();
    private ProvisioningSettings settings = new ProvisioningSettings(parameters,
            configSettings, Messages.DEFAULT_LOCALE);
    private ProvisioningSettings oldSettings;
    private Exception wantedException;
    private ManagedObjectReference reference;
    private VimPortType service;
    private boolean anyServiceLocked;
    private String existingInstanceId;

    private IdentityService identityServiceMock;
    private DataAccessService dataAccessService;

    final String HOSTCONFIG_NOHOSTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<essvcenter>"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer\" />"
            + "<host name=\"host1\" enabled=\"false\" memory_limit=\"-1GB\">"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer\" storage=\"storage1\" />"
            + "</host>"
            + "<storage name=\"storage1\" enabled=\"true\" limit=\"85%\" />"
            + "</essvcenter>";

    final String HOSTCONFIG_NOSTORAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<essvcenter>"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer\" />"
            + "<host name=\"host1\" enabled=\"true\" memory_limit=\"-1GB\">"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer\" storage=\"storage1\" />"
            + "</host>"
            + "<storage name=\"storage1\" enabled=\"false\" limit=\"85%\" />"
            + "</essvcenter>";

    final String HOSTCONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<essvcenter>"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.EquipartitionHostBalancer\" />"
            + "<host name=\"host1\" enabled=\"true\" memory_limit=\"-1GB\">"
            + "<balancer class=\"com.fujitsu.bss.app.vmware.balancer.SequentialStorageBalancer\" storage=\"storage1\" />"
            + "</host>"
            + "<storage name=\"storage1\" enabled=\"true\" limit=\"85%\" />"
            + "</essvcenter>";

    @Override
    protected void setup(TestContainer container) throws Exception {
        vmwClient = mock(VMwareClient.class);
        VMClientFactoryLocal vmwCf = new VMClientFactoryLocal() {

            @Override
            public VMwareClient getInstance(VMPropertyHandler paramHandler) {
                return vmwClient;
            }

            @Override
            public VMwareClient getInstance(String vcenter, String datacenter,
                    String cluster, String locale) throws Exception {
                return vmwClient;
            }
        };
        container.addBean(vmwCf);

        dataAccessService = mock(DataAccessService.class);

        when(dataAccessService.getHostLoadBalancerConfig(anyString(),
                anyString(), anyString())).thenReturn(HOSTCONFIG);

        platformService = mock(APPlatformService.class);
        enableJndiMock();
        InitialContext context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, platformService);

        Answer<Boolean> answerLock = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation)
                    throws Throwable {
                return Boolean.valueOf(!anyServiceLocked);
            }
        };
        doAnswer(answerLock).when(platformService).lockServiceInstance(
                eq(VMController.ID), anyString(),
                any(PasswordAuthentication.class));
        Answer<Boolean> answerExists = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation)
                    throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (wantedException != null) {
                    throw wantedException;
                }
                return Boolean.valueOf(arguments != null && arguments.length > 1
                        && existingInstanceId != null
                        && existingInstanceId.equals(arguments[1]));
            }
        };
        doAnswer(answerExists).when(platformService).exists(eq(VMController.ID),
                anyString());

        container.addBean(new VMDispatcherBean() {

            @Override
            public InstanceStatus getVMWStatus(String instanceId,
                    VMPropertyHandler paramHandler) throws Exception {
                if (wantedException != null) {
                    throw wantedException;
                }
                return super.getVMWStatus(instanceId, paramHandler);
            }
        });

        container.addBean(new VMController() {

            @Override
            protected BESClient getBESClient() {
                return new BESClient() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public <T> T getWebService(Class<T> serviceClass,
                            Credentials credentials) throws Exception {
                        if (serviceClass == IdentityService.class) {
                            return (T) identityServiceMock;
                        }
                        return null;
                    }

                };
            }

        });

        initBESMockup();

        controller = container.get(APPlatformController.class);

        parameters.put(VMPropertyHandler.TS_ACCESS_INFO,
                "${HOST} (IP: ${IP}) <br>${CPU} CPU(s), ${MEM} RAM, ${DISK} HDD");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME_PREFIX, "estess");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME_PATTERN,
                "estess([a-z0-9]){6,8}");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "123456");

        parameters.put(VMPropertyHandler.TS_DISK_SIZE, "20");
        parameters.put(VMPropertyHandler.TS_NUMBER_OF_CPU, "1");
        parameters.put(VMPropertyHandler.TS_AMOUNT_OF_RAM, "512");
        parameters.put(VMPropertyHandler.TS_TEMPLATENAME, "centOSTemplate");
        parameters.put(VMPropertyHandler.TS_RESPONSIBLE_PERSON,
                DEFAULT_USER_NAME);

        configSettings.put(VMPropertyHandler.BSS_USER_KEY, "12345");
        configSettings.put(VMPropertyHandler.BSS_USER_PWD, "abcde");

        settings = new ProvisioningSettings(parameters, configSettings,
                Messages.DEFAULT_LOCALE);
        settings.setOrganizationId(DEFAULT_ORG_ID);
        anyServiceLocked = false;
    }

    private void initBESMockup() throws Exception {
        identityServiceMock = mock(EnhancedIdentityService.class);
        VOUser isuser = new VOUser();
        isuser.setKey(10000);
        isuser.setUserId(DEFAULT_USER_NAME);
        isuser.setOrganizationId(DEFAULT_ORG_ID);
        when(identityServiceMock.getUser((VOUser) anyObject()))
                .thenReturn(isuser);
    }

    @Test(expected = APPlatformException.class)
    public void testCreateInstance_invalidName() throws Exception {
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "");
        createInstance();
    }

    @Test(expected = APPlatformException.class)
    public void testCreateInstance_wrongMemory() throws Exception {
        parameters.put(VMPropertyHandler.TS_AMOUNT_OF_RAM, "513");
        createInstance();
    }

    @Test(expected = APPlatformException.class)
    public void testCreateInstance_runtime() throws Exception {
        wantedException = new RuntimeException("Test");
        createInstance();
    }

    @Test(expected = APPlatformException.class)
    public void testCreateInstance_duplicateName() throws Exception {
        existingInstanceId = "estesscustom1";
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "custom1");
        createInstance();
    }

    @Test
    public void testModifyInstance() throws Exception {
        HashMap<String, String> oldParameters = new HashMap<String, String>(
                parameters);
        // Change CPU
        oldParameters.put(VMPropertyHandler.TS_NUMBER_OF_CPU, "4");
        oldSettings = new ProvisioningSettings(oldParameters, configSettings,
                Messages.DEFAULT_LOCALE);
        oldSettings.setOrganizationId(DEFAULT_ORG_ID);
        modifyInstance("123");
        assertEquals(VMwareStatus.MODIFICATION_REQUESTED.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.MODIFICATION.toString(),
                parameters.get(VMPropertyHandler.OPERATION));
    }

    @Test(expected = APPlatformException.class)
    public void testModifyInstanceInvalidDiskSize() throws Exception {
        HashMap<String, String> oldParameters = new HashMap<String, String>(
                parameters);
        // Reduce disk size! (not allowed)
        oldParameters.put(VMPropertyHandler.TS_DISK_SIZE, "4");
        oldSettings = new ProvisioningSettings(oldParameters, configSettings,
                Messages.DEFAULT_LOCALE);
        modifyInstance("123");
    }

    public void testModifyInstanceDiskSize() throws Exception {
        HashMap<String, String> oldParameters = new HashMap<String, String>(
                parameters);
        // Increas disk size! (ok)
        oldParameters.put(VMPropertyHandler.TS_DISK_SIZE, "30");
        oldSettings = new ProvisioningSettings(oldParameters, configSettings,
                Messages.DEFAULT_LOCALE);
        modifyInstance("123");
    }

    @Test
    public void testModifyResponsibleUser() throws Exception {
        HashMap<String, String> oldParameters = new HashMap<String, String>(
                parameters);
        // Change CPU
        oldParameters.put(VMPropertyHandler.TS_RESPONSIBLE_PERSON, "new_user");
        oldSettings = new ProvisioningSettings(oldParameters, configSettings,
                Messages.DEFAULT_LOCALE);
        oldSettings.setOrganizationId(DEFAULT_ORG_ID);

        // explicitly enable manual finish
        parameters.put(VMPropertyHandler.TS_START_PROCESS_AFTER_CREATION,
                "true");

        modifyInstance("123");
        assertEquals(VMwareStatus.MANUAL_STEP.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.MODIFICATION.toString(),
                parameters.get(VMPropertyHandler.OPERATION));

    }

    @Test(expected = APPlatformException.class)
    public void testModifyInstance_wrongMemory() throws Exception {
        oldSettings = new ProvisioningSettings(
                new HashMap<String, String>(parameters), configSettings,
                Messages.DEFAULT_LOCALE);
        parameters.put(VMPropertyHandler.TS_AMOUNT_OF_RAM, "513");
        modifyInstance("123");
    }

    @Test(expected = APPlatformException.class)
    public void testModifyInstance_instanceRename() throws Exception {
        oldSettings = new ProvisioningSettings(
                new HashMap<String, String>(parameters), configSettings,
                Messages.DEFAULT_LOCALE);
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "renamed");
        modifyInstance("123");
    }

    @Test(expected = APPlatformException.class)
    public void testModifyInstance_templateChange() throws Exception {
        oldSettings = new ProvisioningSettings(
                new HashMap<String, String>(parameters), configSettings,
                Messages.DEFAULT_LOCALE);
        parameters.put(VMPropertyHandler.TS_TEMPLATENAME, "renamed");
        modifyInstance("123");
    }

    @Test(expected = APPlatformException.class)
    public void testModifyInstance_runtime() throws Exception {
        wantedException = new RuntimeException("Test");
        modifyInstance("123");
    }

    @Test
    public void testDeleteInstance() throws Exception {
        deleteInstance("123");
        assertEquals(VMwareStatus.DELETION_REQUESTED.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.DELETION.toString(),
                parameters.get(VMPropertyHandler.OPERATION));
    }

    @Test
    public void testActivateInstance() throws Exception {
        activateInstance("123");
        assertEquals(VMwareStatus.ACTIVATION_REQUESTED.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.ACTIVATION.toString(),
                parameters.get(VMPropertyHandler.OPERATION));
    }

    @Test
    public void testDeactivateInstance() throws Exception {
        deactivateInstance("123");
        assertEquals(VMwareStatus.DEACTIVATION_REQUESTED.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.ACTIVATION.toString(),
                parameters.get(VMPropertyHandler.OPERATION));
    }

    @Test(expected = APPlatformException.class)
    public void testDeleteInstance_runtime() throws Exception {
        settings = null; // provoke NPE
        deleteInstance("123");
    }

    @Test
    public void testGetPublicIp() throws Exception {
        prepareGetPublicIp("vmHost1", "1.2.3.4");

        String instanceId = "vmHost1";
        String publicIp = getPublicIp(instanceId);
        assertTrue(publicIp.startsWith(instanceId));
    }

    @Test
    public void testGetPublicIpFQDN() throws Exception {
        prepareGetPublicIp("VMHOST1.fully.qualified.domain", "1.2.3.4");

        String instanceId = "vmHost1";
        String publicIp = getPublicIp(instanceId);
        assertTrue(publicIp.startsWith(instanceId));
    }

    @Test
    public void testGetPublicIp_GuestNotReady() throws Exception {
        prepareGetPublicIp("host1", "1.2.3.4");

        String instanceId = "vmHost1";
        String publicIp = getPublicIp(instanceId);
        assertNull(publicIp);
    }

    @Test
    public void testGetPublicIp_GuestNotReady2() throws Exception {
        prepareGetPublicIp("template1", "1.2.3.4");

        String instanceId = "vmHost1";
        String publicIp = getPublicIp(instanceId);
        assertNull(publicIp);

        prepareGetPublicIp("host1", null);
        publicIp = getPublicIp(instanceId);
        assertNull(publicIp);
    }

    @Test(expected = APPlatformException.class)
    public void testGetPublicIp_Failure() throws Exception {
        prepareGetPublicIp("template1", "1.2.3.4");

        when(vmwClient.getServiceUtil())
                .thenThrow(new RuntimeException("Test"));

        String instanceId = "host1";
        getPublicIp(instanceId);
    }

    @Test
    public void testIsInstanceReady_CREATION_REQUESTED() throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("deleteTask");
        taskInfo.setState(TaskInfoState.SUCCESS);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(taskInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.CREATING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).cloneVMTask(any(ManagedObjectReference.class),
                any(ManagedObjectReference.class), anyString(),
                any(VirtualMachineCloneSpec.class));
    }

    @Test
    public void testIsInstanceReady_CREATION_REQUESTED_Windows()
            throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        // set windows configuration
        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("deleteTask");
        taskInfo.setState(TaskInfoState.SUCCESS);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(taskInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.CREATING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).cloneVMTask(any(ManagedObjectReference.class),
                any(ManagedObjectReference.class), anyString(),
                any(VirtualMachineCloneSpec.class));

    }

    @Test
    public void testIsInstanceReady_CREATION_REQUESTED_Windows_wrongParameters()
            throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());

        parameters.put(VMPropertyHandler.TS_DISK_SIZE, "8");

        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(null);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.CREATING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).cloneVMTask(any(ManagedObjectReference.class),
                any(ManagedObjectReference.class), anyString(),
                any(VirtualMachineCloneSpec.class));

    }

    @Test
    public void testIsInstanceReady_CREATION_REQUESTED_concurrency()
            throws Exception {
        setupDispatcherMocking();
        setupInventoryMocking();

        // Simulate parallel creation task
        anyServiceLocked = true;

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("creationTask");
        taskInfo.setState(TaskInfoState.SUCCESS);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(taskInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);

        // Must be unchanged, because some service owns lock
        assertEquals(VMwareStatus.CREATION_REQUESTED.toString(), status);

        // check that NO API calls were made
        verify(service, times(0)).cloneVMTask(any(ManagedObjectReference.class),
                any(ManagedObjectReference.class), anyString(),
                any(VirtualMachineCloneSpec.class));

        // Unlock service
        anyServiceLocked = false;

        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        status = parameters.get(VMPropertyHandler.STATUS);

        // Now it should work
        assertEquals(VMwareStatus.CREATING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).cloneVMTask(any(ManagedObjectReference.class),
                any(ManagedObjectReference.class), anyString(),
                any(VirtualMachineCloneSpec.class));

    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_noHost()
            throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        // Disable all hosts....
        Answer<String> answerXML = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return HOSTCONFIG_NOHOSTS;
            }
        };
        doAnswer(answerXML).when(dataAccessService).getHostLoadBalancerConfig(
                anyString(), anyString(), anyString());

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_invalidHostMemoryLimit()
            throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        // Disable all hosts....
        Answer<String> answerXML = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return HOSTCONFIG_NOHOSTS;
            }
        };
        doAnswer(answerXML).when(dataAccessService).getHostLoadBalancerConfig(
                anyString(), anyString(), anyString());

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_noDatacenter()
            throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                eq("Datacenter"), anyString())).thenReturn(null);

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_noVM() throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                eq("VirtualMachine"), anyString())).thenReturn(null);

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_noResourcePool()
            throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                eq("ResourcePool"), anyString())).thenReturn(null);

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_noStorage()
            throws Exception {

        setupDispatcherMocking();
        setupInventoryMocking();

        // Disable all storages....
        Answer<String> answerXML = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return HOSTCONFIG_NOSTORAGE;
            }
        };
        doAnswer(answerXML).when(dataAccessService).getHostLoadBalancerConfig(
                anyString(), anyString(), anyString());

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_invalidStorage()
            throws Exception {
        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("summary.name"))).thenReturn("storageX");

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_invalidHost()
            throws Exception {
        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                eq("HostSystem"), anyString())).thenReturn(null);

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_noTemplateSize()
            throws Exception {
        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("summary.storage.unshared"))).thenReturn(null);

        getInstanceStatus("host1");
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_CREATION_REQUESTED_invalidDiskSize()
            throws Exception {
        setupDispatcherMocking();
        setupInventoryMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.CREATION_REQUESTED.name());

        // Request 2 GB disc but template has already 8 GB!
        parameters.put(VMPropertyHandler.TS_DISK_SIZE, "2");

        getInstanceStatus("host1");
    }

    @Test
    public void testIsInstanceReady_CREATING() throws Exception {

        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.CREATING.name());

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.UPDATING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).reconfigVMTask(
                any(ManagedObjectReference.class),
                any(VirtualMachineConfigSpec.class));
    }

    @Test
    public void testIsInstanceReady_CREATING_concurrency() throws Exception {

        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.CREATING.name());

        // Simulate parallel creation task
        anyServiceLocked = true;

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);

        // Must be unchanged, because some service owns lock
        assertEquals(VMwareStatus.CREATING.toString(), status);

        // check that NO API calls were made
        verify(service, times(0)).reconfigVMTask(
                any(ManagedObjectReference.class),
                any(VirtualMachineConfigSpec.class));

        // Unlock service
        anyServiceLocked = false;

        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        status = parameters.get(VMPropertyHandler.STATUS);

        assertEquals(VMwareStatus.UPDATING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).reconfigVMTask(
                any(ManagedObjectReference.class),
                any(VirtualMachineConfigSpec.class));
    }

    @Test
    public void testIsInstanceReady_UPDATING() throws Exception {

        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.UPDATING.name());

        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(null);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STARTING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).powerOnVMTask(
                any(ManagedObjectReference.class),
                any(ManagedObjectReference.class));
    }

    @Test
    public void testIsInstanceReady_UPDATING_concurrency() throws Exception {

        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.UPDATING.name());

        // Simulate parallel creation task (should not affect the STARTING
        // action)
        anyServiceLocked = true;

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("updateTask");
        taskInfo.setState(TaskInfoState.SUCCESS);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(taskInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STARTING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).powerOnVMTask(
                any(ManagedObjectReference.class),
                any(ManagedObjectReference.class));
    }

    @Test
    public void testIsInstanceReady_STARTING() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);
        setupInventoryMocking();

        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        runtimeInfo.setPowerState(VirtualMachinePowerState.POWERED_OFF);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName("host1");
        guestInfo.setIpAddress("1.2.3.4");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        // explicitly enable manual finish
        parameters.put(VMPropertyHandler.OPERATION,
                VMwareOperation.MODIFICATION.name());
        parameters.put(VMPropertyHandler.TS_START_PROCESS_AFTER_CREATION,
                "true");
        when(platformService.getEventServiceUrl())
                .thenReturn("http://127.0.0.1/something");

        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.STARTING.name());

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.WAIT_UNTIL_RUNNING.toString(), status);

        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.MANUAL_STEP.toString(), status);

        // no API call to verify here
    }

    @Test
    public void testIsInstanceReady_MANUAL_STEP_noMail() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MANUAL_STEP.name());

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName("vmGuest1");
        guestInfo.setIpAddress("34.45.56.67");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        InstanceStatus instanceStatus = getInstanceStatus("vmGuest1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.FINISHED.toString(), status);

        // check that the right API calls were made
        verify(serviceUtil, times(1)).getDynamicProperty(
                any(ManagedObjectReference.class), eq("guest"));

        verify(platformService, never()).sendMail(anyListOf(String.class),
                anyString(), anyString());
    }

    @Test
    public void testIsInstanceReady_MANUAL_STEP_mail() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        parameters.put(VMPropertyHandler.OPERATION,
                VMwareOperation.CREATION.name());
        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MANUAL_STEP.name());
        // explicitly enable manual finish
        parameters.put(VMPropertyHandler.TS_START_PROCESS_AFTER_CREATION,
                "true");

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName("vmGuest1");
        guestInfo.setIpAddress("34.45.56.67");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        when(platformService.getEventServiceUrl())
                .thenReturn("http://127.0.0.1/something");

        InstanceStatus instanceStatus = getInstanceStatus("vmGuest1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        assertFalse(instanceStatus.getRunWithTimer());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.MANUAL_STEP.toString(), status);

        // check that the right API calls were made
        verify(serviceUtil, times(1)).getDynamicProperty(
                any(ManagedObjectReference.class), eq("guest"));

        verify(platformService, times(1)).sendMail(anyListOf(String.class),
                anyString(), anyString());
    }

    @Test
    public void testIsInstanceReady_MANUAL_STEP_noHostName() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MANUAL_STEP.name());

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName(null); // not yet defined
        guestInfo.setIpAddress("34.45.56.67");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        InstanceStatus instanceStatus = getInstanceStatus("vmGuest1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.MANUAL_STEP.toString(), status);

        // check that the right API calls were made
        verify(serviceUtil, times(1)).getDynamicProperty(
                any(ManagedObjectReference.class), eq("guest"));

        verify(platformService, never()).sendMail(anyListOf(String.class),
                anyString(), anyString());
    }

    @Test
    public void testIsInstanceReady_DELETION_REQUESTED_poweredOn()
            throws Exception {
        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.DELETION_REQUESTED.name());

        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(null);

        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        runtimeInfo.setPowerState(VirtualMachinePowerState.POWERED_ON);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_DELETION.toString(), status);

        // check that the right API calls were made
        verify(service, times(1))
                .powerOffVMTask(any(ManagedObjectReference.class));
    }

    @Test
    public void testIsInstanceReady_DELETION_REQUESTED_powereOff()
            throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        // check with null task string to enhance coverage
        parameters.put(VMPropertyHandler.TASK_KEY, null);
        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.DELETION_REQUESTED.name());

        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        runtimeInfo.setPowerState(VirtualMachinePowerState.POWERED_OFF);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_DELETION.toString(), status);

        // check that the right API calls were made
        verify(service, never())
                .powerOffVMTask(any(ManagedObjectReference.class));
    }

    @Test
    public void testIsInstanceReady_DELETION_REQUESTED_notexisting()
            throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.DELETION_REQUESTED.name());

        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                eq("VirtualMachine"), anyString())).thenReturn(null);

        // Ok. For stopping the instance must not exist
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_DELETION.toString(), status);

        // Ok. The deletion must also work.
        instanceStatus = getInstanceStatus("host1");

        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.DELETING.toString(), status);

    }

    @Test
    public void testIsInstanceReady_STOPFORDELETION() throws Exception {
        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.STOP_FOR_DELETION.name());
        // check with empty task string to enhance coverage
        parameters.put(VMPropertyHandler.TASK_KEY, "");
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("deleteTask");
        taskInfo.setState(TaskInfoState.SUCCESS);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(taskInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.DELETING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1))
                .destroyTask(any(ManagedObjectReference.class));
    }

    @Test
    public void testIsInstanceReady_DELETING_noMail() throws Exception {
        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.DELETING.name());

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.DESTROYED.toString(), status);

        verify(platformService, never()).sendMail(anyListOf(String.class),
                anyString(), anyString());
    }

    @Test
    public void testIsInstanceReady_DELETING_mail() throws Exception {
        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.DELETING.name());
        parameters.put(VMPropertyHandler.TS_START_PROCESS_AFTER_CREATION,
                "true");

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.DESTROYED.toString(), status);

        verify(platformService, times(1)).sendMail(anyListOf(String.class),
                anyString(), anyString());
    }

    @Test
    public void testIsInstanceReady_MODIFICATIONREQUESTED() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);
        setupInventoryMocking();

        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        runtimeInfo.setPowerState(VirtualMachinePowerState.POWERED_OFF);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName("host1");
        guestInfo.setIpAddress("1.2.3.4");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        // explicitly enable manual finish
        parameters.put(VMPropertyHandler.OPERATION,
                VMwareOperation.MODIFICATION.name());
        parameters.put(VMPropertyHandler.TS_START_PROCESS_AFTER_CREATION,
                "true");
        when(platformService.getEventServiceUrl())
                .thenReturn("http://127.0.0.1/something");

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MODIFICATION_REQUESTED.name());

        // 1. Stopping
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_MODIFICATION.toString(), status);

        // 2. Updating
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.UPDATING.toString(), status);

        // 3. Starting
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STARTING.toString(), status);

        // 4. Wait until the VM is started
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.WAIT_UNTIL_RUNNING.toString(), status);

        // 5. Retrieving host info
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.MANUAL_STEP.toString(), status);

        // 6. Finished
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.FINISHED.toString(), status);
        assertTrue(instanceStatus.isReady());
    }

    @Test
    public void testIsInstanceReady_MODIFICATIONREQUESTED_poweredOn()
            throws Exception {
        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MODIFICATION_REQUESTED.name());
        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        runtimeInfo.setPowerState(VirtualMachinePowerState.POWERED_ON);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("modifyTask");
        taskInfo.setState(TaskInfoState.SUCCESS);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(taskInfo);
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_MODIFICATION.toString(), status);

        // check that the right API calls were made
        verify(service, times(0))
                .powerOffVMTask(any(ManagedObjectReference.class));
    }

    @Test
    public void testIsInstanceReady_MODIFICATIONREQUESTED_poweredOff()
            throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MODIFICATION_REQUESTED.name());
        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        runtimeInfo.setPowerState(VirtualMachinePowerState.POWERED_OFF);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_MODIFICATION.toString(), status);

        // check that the right API calls were made
        verify(service, never())
                .powerOffVMTask(any(ManagedObjectReference.class));
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_MODIFICATIONREQUESTED_notexisting()
            throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MODIFICATION_REQUESTED.name());

        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                eq("VirtualMachine"), anyString())).thenReturn(null);

        // Ok. For stopping the instance must not exist
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());

        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_MODIFICATION.toString(), status);

        // For the update we expect an error
        instanceStatus = getInstanceStatus("host1");
    }

    @Test
    public void testIsInstanceReady_STOPFORMODIFICATION() throws Exception {

        setupDispatcherMocking();

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.STOP_FOR_MODIFICATION.name());
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("updateTask");
        taskInfo.setState(TaskInfoState.SUCCESS);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("info"))).thenReturn(taskInfo);

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.UPDATING.toString(), status);

        // check that the right API calls were made
        verify(service, times(1)).reconfigVMTask(
                any(ManagedObjectReference.class),
                any(VirtualMachineConfigSpec.class));
    }

    @Test
    public void testIsInstanceReady_ACTIVATIONREQUESTED() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);
        setupInventoryMocking();

        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName("host1");
        guestInfo.setIpAddress("1.2.3.4");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.ACTIVATION_REQUESTED.name());

        // 1. Starting
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STARTING.toString(), status);

        // 2. Wait until OS has booted
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.WAIT_UNTIL_RUNNING.toString(), status);

        // 3. Retrieving host info
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.MANUAL_STEP.toString(), status);

        // 4. Finished
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.FINISHED.toString(), status);
    }

    @Test
    public void testIsInstanceReady_DEACTIVATIONREQUESTED() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);
        setupInventoryMocking();

        VirtualMachineRuntimeInfo runtimeInfo = new VirtualMachineRuntimeInfo();
        runtimeInfo.setPowerState(VirtualMachinePowerState.POWERED_OFF);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("runtime"))).thenReturn(runtimeInfo);

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName("host1");
        guestInfo.setIpAddress("1.2.3.4");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.DEACTIVATION_REQUESTED.name());

        // 1. Stopping
        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertFalse(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STOP_FOR_DEACTIVATION.toString(), status);

        // 2. Finished
        instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.FINISHED.toString(), status);
    }

    @Test
    public void testIsInstanceReady_TaskQueued() throws Exception {
        // set queued state to check that status is not changed
        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.UPDATING.name());

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task5");
        taskInfo.setState(TaskInfoState.QUEUED);
        LinkedList<TaskInfo> taskInfos = new LinkedList<TaskInfo>();
        taskInfos.addLast(taskInfo);
        // taskInfos[1] is intentionally null
        taskInfos.addLast(null);
        taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.RUNNING);
        taskInfos.addLast(taskInfo);
        setupDispatcherMocking(taskInfos);

        getInstanceStatus("host1");
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.UPDATING.toString(), status);
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_TaskFailed() throws Exception {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.ERROR);
        setupDispatcherMocking(taskInfo);
        getInstanceStatus("host1");
    }

    @Test
    public void testIsInstanceReady_TaskInfoNull() throws Exception {
        parameters.put(VMPropertyHandler.STATUS, VMwareStatus.UPDATING.name());
        setupDispatcherMocking();
        when(service.readPreviousTasks(any(ManagedObjectReference.class),
                anyInt())).thenReturn(null);
        // no task info is considered as success => state switch to starting
        getInstanceStatus("host1");
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.STARTING.toString(), status);
    }

    @Test
    public void testNotifyInstance_Null() throws Exception {
        InstanceStatus status = controller.notifyInstance(null, null, null);
        assertNull(status);
        status = controller.notifyInstance("someId", null, null);
        assertNull(status);
        status = controller.notifyInstance("someId", settings, null);
        assertNull(status);
        status = controller.notifyInstance("someId", null, new Properties());
        assertNull(status);
    }

    @Test
    public void testNotifyInstance_WrongCommand() throws Exception {
        Properties properties = new Properties();
        properties.put("list", "dummy");
        InstanceStatus status = controller.notifyInstance("someId", settings,
                properties);
        assertNull(status);
        properties = new Properties();
        properties.put("command", "dummy");
        status = controller.notifyInstance("someId", settings, properties);
        assertNull(status);
    }

    @Test
    public void testNotifyInstance_OK() throws Exception {
        Properties properties = new Properties();
        properties.put("command", "finish");
        new VMPropertyHandler(settings).setState(VMwareStatus.MANUAL_STEP);
        InstanceStatus status = controller.notifyInstance("someId", settings,
                properties);
        assertNotNull(status);
        assertTrue(status.isReady());
        assertTrue(status.getRunWithTimer());
    }

    @Test(expected = APPlatformException.class)
    public void testNotifyInstance_WrongStatus() throws Exception {
        Properties properties = new Properties();
        properties.put("command", "finish");
        new VMPropertyHandler(settings).setState(VMwareStatus.FAILED);
        controller.notifyInstance("someId", settings, properties);
    }

    @Test(expected = APPlatformException.class)
    public void testException_APPlatformException() throws Exception {
        wantedException = new APPlatformException("abc");
        try {
            controller.getInstanceStatus("someId", settings);
        } catch (APPlatformException e) {
            assertEquals(wantedException.getMessage(), e.getMessage());
            throw e;
        }
    }

    @Test(expected = APPlatformException.class)
    public void testException_EJBException() throws Exception {
        NullPointerException npe = new NullPointerException("Message of NPE");
        wantedException = new EJBException("test123", npe);
        try {
            controller.getInstanceStatus("someId", settings);
        } catch (APPlatformException e) {
            // do not pass cause to APP because cause might not be serializable
            assertNull(e.getCause());
            throw e;
        }
    }

    @Test(expected = APPlatformException.class)
    public void testException_EJBException_noCause() throws Exception {
        wantedException = new EJBException("test123");
        try {
            controller.getInstanceStatus("someId", settings);
        } catch (APPlatformException e) {
            // do not pass cause to APP because cause might not be serializable
            assertNull(e.getCause());
            throw e;
        }
    }

    @Test(expected = APPlatformException.class)
    public void testException_EJBException_directCause() throws Exception {
        NullPointerException npe = new NullPointerException();
        wantedException = mock(EJBException.class);
        when(wantedException.getCause()).thenReturn(npe);
        try {
            controller.getInstanceStatus("someId", settings);
        } catch (APPlatformException e) {
            // do not pass cause to APP because cause might not be serializable
            assertNull(e.getCause());
            throw e;
        }
    }

    @Test(expected = APPlatformException.class)
    public void testException_EJBException_noText() throws Exception {
        NullPointerException npe = new NullPointerException();
        wantedException = new EJBException("test123", npe);
        try {
            controller.getInstanceStatus("someId", settings);
        } catch (APPlatformException e) {
            // do not pass cause to APP because cause might not be serializable
            assertNull(e.getCause());
            assertTrue(e.getMessage().indexOf(npe.getClass().getName()) >= 0);
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testInitializeNoJndi() throws Exception {
        enableJndiMock();
        InitialContext context = new InitialContext();
        context.unbind(APPlatformService.JNDI_NAME);
        // when no platform service is available via JNDI, the controller cannot
        // work
        new VMController().initialize();
    }

    @Test(expected = RuntimeException.class)
    public void testInitializeNotMatching() throws Exception {
        enableJndiMock();
        InitialContext context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, "TestObject");
        // when no suitable platform service is available via JNDI, the
        // controller cannot work
        new VMController().initialize();
    }

    private void setupInventoryMocking() throws Exception {

        LinkedList<ObjectContent> content = new LinkedList<ObjectContent>();
        ObjectContent oc = new ObjectContent();
        oc.setDynamicType("HostSystem");
        ManagedObjectReference mor = new ManagedObjectReference();
        mor.setType("HostSystem");
        oc.setObj(mor);
        oc.getPropSet().addAll(VMwareDatacenterInventoryTest
                .createHostSystemProperties("host1", "8192", "8"));
        content.add(oc);

        oc = new ObjectContent();
        oc.setDynamicType("Datastore");
        mor = new ManagedObjectReference();
        mor.setType("Datastore");
        oc.setObj(mor);
        oc.getPropSet()
                .addAll(VMwareDatacenterInventoryTest.createDataStoreProperties(
                        "storage1", "171798691840", "161798691840"));
        content.add(oc);

        oc = new ObjectContent();
        oc.setDynamicType("VirtualMachine");
        mor = new ManagedObjectReference();
        mor.setType("VirtualMachine");
        oc.setObj(mor);
        oc.getPropSet().addAll(VMwareDatacenterInventoryTest
                .createVMProperties("centos1", "1024", "4", "host1"));
        content.add(oc);

        // add some elements that test parser flexibility
        oc = new ObjectContent();
        oc.setDynamicType("Unknown");
        mor = new ManagedObjectReference();
        mor.setType("IgnoredType");
        oc.setObj(mor);
        content.add(oc);

        oc = new ObjectContent();
        oc.setDynamicType("VirtualMachine");
        mor = new ManagedObjectReference();
        mor.setType("VirtualMachine");
        oc.setObj(mor);
        content.add(oc);

        when(service.retrieveProperties(any(ManagedObjectReference.class),
                anyListOf(PropertyFilterSpec.class))).thenReturn(content);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("name"))).thenReturn("host1");

    }

    private void setupDispatcherMocking() throws Exception {
        setupDispatcherMocking((TaskInfo) null);
    }

    private void setupDispatcherMocking(TaskInfo task) throws Exception {
        LinkedList<TaskInfo> taskInfos = new LinkedList<TaskInfo>();
        if (task != null) {
            taskInfos.add(task);
        }
        setupDispatcherMocking(taskInfos);
    }

    private void setupDispatcherMocking(List<TaskInfo> tasks) throws Exception {

        parameters.put(VMPropertyHandler.TASK_KEY, "task1");
        parameters.put(VMPropertyHandler.TS_TEMPLATENAME, "os");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "name");
        parameters.put(VMPropertyHandler.TS_WINDOWS_DOMAIN_JOIN, "false");
        parameters.put(VMPropertyHandler.TS_WINDOWS_WORKGROUP, "Workgroup");
        parameters.put(VMPropertyHandler.TS_WINDOWS_LOCAL_ADMIN_PWD,
                "admin123");
        parameters.put(VMPropertyHandler.TS_TARGET_FOLDER, "target folder");
        parameters.put(VMPropertyHandler.TS_NIC1_NETWORK_SETTINGS, "DHCP");

        serviceUtil = mock(ManagedObjectAccessor.class);
        serviceConn = mock(ServiceConnection.class);
        ServiceContent content = mock(ServiceContent.class);
        reference = mock(ManagedObjectReference.class);
        service = mock(VimPortType.class);
        VirtualDisk vdev = new VirtualDisk();

        Description vdevInfo = new Description();
        vdevInfo.setLabel("Hard disk");
        vdev.setDeviceInfo(vdevInfo);
        vdev.setCapacityInKB(Long.parseLong(EIGHT_GIG));

        VirtualHardware vhw = new VirtualHardware();
        vhw.getDevice().add(vdev);
        VirtualMachineConfigInfo configInfo = new VirtualMachineConfigInfo();
        configInfo.setHardware(vhw);
        configInfo.setGuestId("windows");

        when(vmwClient.getServiceUtil()).thenReturn(serviceUtil);
        when(vmwClient.getConnection()).thenReturn(serviceConn);
        when(serviceConn.getServiceContent()).thenReturn(content);
        when(serviceConn.getService()).thenReturn(service);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("config"))).thenReturn(configInfo);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("parent"))).thenReturn(reference);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("summary.name"))).thenReturn("storage1");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("datastore")))
                        .thenReturn(Collections.singletonList(reference));
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("summary.storage.unshared")))
                        .thenReturn(Long.valueOf(EIGHT_GIG));
        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                anyString(), anyString())).thenReturn(reference);

        when(content.getTaskManager()).thenReturn(reference);
        when(service.createCollectorForTasks(any(ManagedObjectReference.class),
                any(TaskFilterSpec.class))).thenReturn(reference);
        when(service.readPreviousTasks(any(ManagedObjectReference.class),
                anyInt())).thenReturn(tasks);
        when(service.findByInventoryPath(any(ManagedObjectReference.class),
                anyString())).thenReturn(reference);
    }

    private InstanceDescription createInstance() throws Exception {
        InstanceDescription instance = runTX(
                new Callable<InstanceDescription>() {

                    @Override
                    public InstanceDescription call() throws Exception {
                        return controller.createInstance(settings);
                    }
                });
        return instance;
    }

    private InstanceStatus modifyInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.modifyInstance(instanceId, oldSettings,
                        settings);
            }
        });
    }

    private InstanceStatus deleteInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.deleteInstance(instanceId, settings);
            }
        });
    }

    private InstanceStatus activateInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.activateInstance(instanceId, settings);
            }
        });
    }

    private InstanceStatus deactivateInstance(final String instanceId)
            throws Exception {
        return runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.deactivateInstance(instanceId, settings);
            }
        });
    }

    private void prepareGetPublicIp(final String returnedHostName,
            final String returnedIP) throws Exception {

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setKey("task1");
        taskInfo.setState(TaskInfoState.SUCCESS);
        setupDispatcherMocking(taskInfo);

        parameters.put(VMPropertyHandler.OPERATION,
                VMwareOperation.CREATION.name());
        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.MANUAL_STEP.name());

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName(returnedHostName);
        guestInfo.setIpAddress(returnedIP);
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        when(platformService.getEventServiceUrl())
                .thenReturn("http://127.0.0.1/something");

    }

    private String getPublicIp(final String instanceId) throws Exception {
        String ip = runTX(new Callable<String>() {

            @Override
            public String call() throws Exception {
                InstanceStatus status = controller.getInstanceStatus(instanceId,
                        settings);
                return status.getAccessInfo();
            }
        });

        return ip;
    }

    private InstanceStatus getInstanceStatus(final String instanceId)
            throws Exception {
        InstanceStatus result = runTX(new Callable<InstanceStatus>() {

            @Override
            public InstanceStatus call() throws Exception {
                return controller.getInstanceStatus(instanceId, settings);
            }
        });
        return result;
    }

    // internal interface combiner
    private static interface EnhancedIdentityService
            extends IdentityService, BindingProvider {
    }

}
