/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2013 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import javax.naming.InitialContext;
import javax.xml.ws.BindingProvider;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.fujitsu.bss.app.test.EJBTestBase;
import com.fujitsu.bss.app.test.ejb.TestContainer;
import com.fujitsu.bss.app.v1_0.data.InstanceDescription;
import com.fujitsu.bss.app.v1_0.data.InstanceStatus;
import com.fujitsu.bss.app.v1_0.data.ProvisioningSettings;
import com.fujitsu.bss.app.v1_0.exceptions.APPlatformException;
import com.fujitsu.bss.app.v1_0.intf.APPlatformController;
import com.fujitsu.bss.app.v1_0.intf.APPlatformService;
import com.fujitsu.bss.app.vmware.VMClientFactoryLocal;
import com.fujitsu.bss.app.vmware.VMPropertyHandler;
import com.fujitsu.bss.app.vmware.api.ManagedObjectAccessor;
import com.fujitsu.bss.app.vmware.api.VMwareClient;
import com.fujitsu.bss.app.vmware.bes.BESClient;
import com.fujitsu.bss.app.vmware.bes.Credentials;
import com.fujitsu.bss.app.vmware.data.VMwareOperation;
import com.fujitsu.bss.app.vmware.data.VMwareStatus;
import com.fujitsu.bss.app.vmware.i18n.Messages;
import com.fujitsu.bss.intf.IdentityService;
import com.fujitsu.bss.vo.VOUser;
import com.vmware.vim25.GuestInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineConfigInfo;

/**
 * @author Dirk Bernsau
 * 
 */
public class VMwareControllerImportTest extends EJBTestBase {
    private final static String DEFAULT_ORG_ID = "org123abc123";
    private final static String DEFAULT_USER_NAME = "user";

    private APPlatformController controller;
    private APPlatformService platformService;

    private VMwareClient vmwClient;
    private ManagedObjectAccessor serviceUtil;
    private VirtualMachineConfigInfo configSpec;

    private final HashMap<String, String> parameters = new HashMap<String, String>();
    private final HashMap<String, String> configSettings = new HashMap<String, String>();
    private ProvisioningSettings settings = new ProvisioningSettings(parameters,
            configSettings, Messages.DEFAULT_LOCALE);
    private ProvisioningSettings oldSettings;
    private Exception wantedException;
    private ManagedObjectReference reference;
    private String existingInstanceId;

    private IdentityService identityServiceMock;

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

        platformService = mock(APPlatformService.class);
        enableJndiMock();
        InitialContext context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, platformService);

        Answer<Boolean> answerExists = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation)
                    throws Throwable {
                Object[] arguments = invocation.getArguments();
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

        parameters.put(VMPropertyHandler.TS_IMPORT_EXISTING_VM, "true");
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "import3");
        parameters.put(VMPropertyHandler.TS_DISK_SIZE, "20");
        parameters.put(VMPropertyHandler.TS_NUMBER_OF_CPU, "2");
        parameters.put(VMPropertyHandler.TS_AMOUNT_OF_RAM, "512");
        parameters.put(VMPropertyHandler.TS_TEMPLATENAME, "centOSTemplate");
        parameters.put(VMPropertyHandler.TS_RESPONSIBLE_PERSON,
                DEFAULT_USER_NAME);

        configSettings.put(VMPropertyHandler.BSS_USER_KEY, "12345");
        configSettings.put(VMPropertyHandler.BSS_USER_PWD, "abcde");

        settings = new ProvisioningSettings(parameters, configSettings,
                Messages.DEFAULT_LOCALE);
        settings.setOrganizationId(DEFAULT_ORG_ID);
    }

    private void initBESMockup() throws Exception {
        // IDENTITY SERVICE
        identityServiceMock = mock(EnhancedIdentityService.class);
        VOUser isuser = new VOUser();
        isuser.setKey(10000);
        isuser.setUserId(DEFAULT_USER_NAME);
        isuser.setOrganizationId(DEFAULT_ORG_ID);
        when(identityServiceMock.getUser((VOUser) anyObject()))
                .thenReturn(isuser);
    }

    @Test
    public void testCreateInstance() throws Exception {
        InstanceDescription instance = createInstance();
        assertNotNull(instance);
        assertNotNull(instance.getInstanceId());
        assertEquals(VMwareStatus.IMPORT_REQUESTED.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.IMPORT.toString(),
                parameters.get(VMPropertyHandler.OPERATION));

    }

    @Test(expected = APPlatformException.class)
    public void testCreateInstance_invalidName() throws Exception {
        parameters.remove(VMPropertyHandler.TS_INSTANCENAME);
        createInstance();
    }

    @Test(expected = APPlatformException.class)
    public void testCreateInstance_duplicateName() throws Exception {
        existingInstanceId = "estcustom1";
        parameters.put(VMPropertyHandler.TS_INSTANCENAME, "estcustom1");
        createInstance();
    }

    @Test
    public void testIsInstanceReady_IMPORT_REQUESTED() throws Exception {
        serviceUtil = mock(ManagedObjectAccessor.class);
        reference = mock(ManagedObjectReference.class);
        configSpec = mock(VirtualMachineConfigInfo.class);
        VirtualHardware hardware = mock(VirtualHardware.class);
        List<VirtualDevice> devices = new ArrayList<VirtualDevice>();
        VirtualDisk vdisk = mock(VirtualDisk.class);
        devices.add(vdisk);
        devices.add(vdisk);

        when(vmwClient.getServiceUtil()).thenReturn(serviceUtil);
        when(vdisk.getKey()).thenReturn(222);

        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                anyString(), anyString())).thenReturn(reference);

        when(configSpec.getHardware()).thenReturn(hardware);
        when(hardware.getDevice()).thenReturn(devices);

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.IMPORT_REQUESTED.name());

        GuestInfo guestInfo = new GuestInfo();
        guestInfo.setHostName("host1");
        guestInfo.setIpAddress("1.2.3.4");
        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("guest"))).thenReturn(guestInfo);

        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("config"))).thenReturn(configSpec);

        when(serviceUtil.getDynamicProperty(any(ManagedObjectReference.class),
                eq("summary.storage.unshared")))
                        .thenReturn(Long.valueOf("8590371110"));

        InstanceStatus instanceStatus = getInstanceStatus("host1");
        assertNotNull(instanceStatus);
        assertTrue(instanceStatus.isReady());
        String status = parameters.get(VMPropertyHandler.STATUS);
        assertEquals(VMwareStatus.FINISHED.toString(), status);
    }

    @Test(expected = APPlatformException.class)
    public void testIsInstanceReady_IMPORT_REQUESTED_notexisting()
            throws Exception {
        serviceUtil = mock(ManagedObjectAccessor.class);
        when(vmwClient.getServiceUtil()).thenReturn(serviceUtil);
        when(serviceUtil.getDecendentMoRef(any(ManagedObjectReference.class),
                anyString(), anyString())).thenReturn(null); // not
                                                             // found

        parameters.put(VMPropertyHandler.STATUS,
                VMwareStatus.IMPORT_REQUESTED.name());

        getInstanceStatus("host1");
    }

    @Test
    public void testModifyInstance() throws Exception {
        HashMap<String, String> oldParameters = new HashMap<String, String>(
                parameters);
        // Change memory
        oldParameters.put(VMPropertyHandler.TS_AMOUNT_OF_RAM, "1024");
        oldSettings = new ProvisioningSettings(oldParameters, configSettings,
                Messages.DEFAULT_LOCALE);
        oldSettings.setOrganizationId(DEFAULT_ORG_ID);
        modifyInstance("123");
        assertEquals(VMwareStatus.MODIFICATION_REQUESTED.toString(),
                parameters.get(VMPropertyHandler.STATUS));
        assertEquals(VMwareOperation.MODIFICATION.toString(),
                parameters.get(VMPropertyHandler.OPERATION));
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
