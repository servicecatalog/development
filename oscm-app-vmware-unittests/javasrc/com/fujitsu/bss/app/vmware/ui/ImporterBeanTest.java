/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 26.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.servlet.http.HttpSession;
import javax.xml.ws.BindingProvider;

import org.apache.myfaces.custom.fileupload.StorageStrategy;
import org.apache.myfaces.custom.fileupload.UploadedFile;
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
import com.fujitsu.bss.app.vmware.VMClientFactoryBean;
import com.fujitsu.bss.app.vmware.VMPropertyHandler;
import com.fujitsu.bss.app.vmware.api.ManagedObjectAccessor;
import com.fujitsu.bss.app.vmware.api.VMwareClient;
import com.fujitsu.bss.app.vmware.bes.BESClient;
import com.fujitsu.bss.app.vmware.bes.Credentials;
import com.fujitsu.bss.intf.AccountService;
import com.fujitsu.bss.intf.IdentityService;
import com.fujitsu.bss.intf.ServiceProvisioningService;
import com.fujitsu.bss.intf.SubscriptionService;
import com.fujitsu.bss.types.enumtypes.ParameterType;
import com.fujitsu.bss.types.enumtypes.ParameterValueType;
import com.fujitsu.bss.vo.VOBillingContact;
import com.fujitsu.bss.vo.VOParameter;
import com.fujitsu.bss.vo.VOParameterDefinition;
import com.fujitsu.bss.vo.VOPaymentInfo;
import com.fujitsu.bss.vo.VOService;
import com.fujitsu.bss.vo.VOUser;
import com.fujitsu.bss.vo.VOUserDetails;
import com.vmware.vim25.Description;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualHardware;
import com.vmware.vim25.VirtualMachineConfigInfo;

/**
 * TODO test class has problems with execution order of tests. sometimes tests
 * are failing...
 * 
 * @author soehnges
 * 
 */
public class ImporterBeanTest extends EJBTestBase {
    private VMwareClient vmwClient;
    private APPlatformService platformService;
    private ImporterBean bean;
    private FacesContext facesContext;
    private ExternalContext externalContext;
    private HttpSession httpSession;
    private String existingInstance;
    private String notExistingVM;
    private boolean mailSent;
    private boolean invalidFacesContext;

    private IdentityService identityServiceMock;
    private SubscriptionService subscriptionServiceMock;
    private ServiceProvisioningService provisioningServiceMock;
    private AccountService accountServiceMock;

    @Override
    protected void setup(TestContainer container) throws Exception {
        platformService = new APPlatformService() {
            HashMap<String, String> settings;

            @Override
            public void sendMail(List<String> mailAddresses, String subject,
                    String text) throws APPlatformException {
                mailSent = true;
            }

            @Override
            public String getEventServiceUrl() throws ConfigurationException {
                return null;
            }

            @Override
            public boolean exists(String controllerId, String instanceId) {
                if (existingInstance != null) {
                    return instanceId.equals(existingInstance);
                }
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
                    settings.put(VMPropertyHandler.BSS_USER_KEY, "12345");
                    settings.put(VMPropertyHandler.BSS_USER_PWD, "abcde");

                }
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

        initBESMockup();

        vmwClient = Mockito.mock(VMwareClient.class);
        ManagedObjectAccessor serviceUtil = Mockito
                .mock(ManagedObjectAccessor.class);
        final ManagedObjectReference reference = Mockito
                .mock(ManagedObjectReference.class);
        Mockito.when(vmwClient.getServiceUtil()).thenReturn(serviceUtil);
        Answer<ManagedObjectReference> idVMAnswer = new Answer<ManagedObjectReference>() {
            @Override
            public ManagedObjectReference answer(InvocationOnMock invocation)
                    throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (notExistingVM != null
                        && notExistingVM.equals(arguments[2])) {
                    return null;
                }
                return reference;
            }
        };
        Mockito.doAnswer(idVMAnswer).when(serviceUtil).getDecendentMoRef(
                Matchers.any(ManagedObjectReference.class),
                Matchers.anyString(), Matchers.anyString());
        Mockito.when(vmwClient.getServiceUtil().getDynamicProperty(
                (ManagedObjectReference) Matchers.anyObject(),
                Matchers.eq("summary.config.memorySizeMB"))).thenReturn("1024");
        Mockito.when(vmwClient.getServiceUtil().getDynamicProperty(
                (ManagedObjectReference) Matchers.anyObject(),
                Matchers.eq("summary.config.numCpu"))).thenReturn("2");

        VirtualDisk vdev = new VirtualDisk();
        Description vdevInfo = new Description();
        vdevInfo.setLabel("Hard disk");
        vdev.setDeviceInfo(vdevInfo);
        VirtualHardware vhw = new VirtualHardware();
        vhw.getDevice().add(vdev);
        VirtualMachineConfigInfo configInfo = new VirtualMachineConfigInfo();
        configInfo.setHardware(vhw);
        Mockito.when(vmwClient.getServiceUtil().getDynamicProperty(
                Matchers.any(ManagedObjectReference.class),
                Matchers.eq("config"))).thenReturn(configInfo);

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
        Mockito.when(httpSession.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn("user");
        Mockito.when(
                httpSession.getAttribute(Matchers.eq("loggedInUserPassword")))
                .thenReturn("password");

        bean = createTestingBean();
    }

    private void initBESMockup() throws Exception {
        // IDENTITY SERVICE
        identityServiceMock = Mockito.mock(EnhancedIdentityService.class);
        Answer<VOUser> idGetUserAnswer = new Answer<VOUser>() {
            @Override
            public VOUser answer(InvocationOnMock invocation) throws Throwable {
                VOUser arg1 = (VOUser) invocation.getArguments()[0];
                VOUser isuser = new VOUser();
                if (arg1.getUserId().equals("user")) {
                    isuser.setKey(10000);
                    isuser.setUserId("user");
                    isuser.setOrganizationId("org123");
                    return isuser;
                } else if (arg1.getUserId().equals("custUser")) {
                    isuser.setKey(10010);
                    isuser.setUserId("custUser");
                    isuser.setOrganizationId("org123");
                    return isuser;
                } else if (arg1.getUserId().equals("user_org2")) {
                    isuser.setKey(10020);
                    isuser.setUserId("user_org2");
                    isuser.setOrganizationId("org456");
                    return isuser;
                }
                return null;
            }
        };
        Mockito.doAnswer(idGetUserAnswer).when(identityServiceMock)
                .getUser((VOUser) Matchers.anyObject());

        VOUserDetails isuserDetails = new VOUserDetails();
        isuserDetails.setKey(66655);
        isuserDetails.setEMail("mymail");
        isuserDetails.setOrganizationId("org123");
        Mockito.when(identityServiceMock.getCurrentUserDetails())
                .thenReturn(isuserDetails);

        // SUBSCRIPTION SERVICE
        subscriptionServiceMock = Mockito
                .mock(EnhancedSubscriptionService.class);

        // PROVISIONING SERVICE
        provisioningServiceMock = Mockito
                .mock(EnhancedProvisioningService.class);

        List<VOParameter> parList = new ArrayList<VOParameter>();
        parList.add(new VOParameter(
                new VOParameterDefinition(ParameterType.SERVICE_PARAMETER,
                        "VMW_INSTANCENAME", "", ParameterValueType.STRING, "",
                        new Long(0), new Long(0), false, true, null)));
        parList.add(new VOParameter(
                new VOParameterDefinition(ParameterType.SERVICE_PARAMETER,
                        "VMW_COMPLEXITY", "", ParameterValueType.STRING, "",
                        new Long(0), new Long(0), false, true, null)));
        parList.add(new VOParameter(
                new VOParameterDefinition(ParameterType.SERVICE_PARAMETER,
                        "VMW_RAM", "", ParameterValueType.STRING, "",
                        new Long(0), new Long(0), false, true, null)));
        parList.add(new VOParameter(
                new VOParameterDefinition(ParameterType.SERVICE_PARAMETER,
                        "VMW_CPU", "", ParameterValueType.STRING, "",
                        new Long(0), new Long(0), false, true, null)));
        List<VOService> serviceList = new ArrayList<VOService>();
        VOService svcInfo = new VOService();
        svcInfo.setServiceId("MyService");
        svcInfo.setParameters(parList);
        serviceList.add(svcInfo);
        Mockito.when(provisioningServiceMock.getSuppliedServices())
                .thenReturn(serviceList);

        // ACCOUNT SERVICE
        accountServiceMock = Mockito.mock(EnhancedAccountService.class);

        List<VOBillingContact> listBC = new ArrayList<VOBillingContact>();
        VOBillingContact bc = new VOBillingContact();
        listBC.add(bc);
        Mockito.when(accountServiceMock.getBillingContacts())
                .thenReturn(listBC);

        List<VOPaymentInfo> listPI = new ArrayList<VOPaymentInfo>();
        VOPaymentInfo pi = new VOPaymentInfo();
        listPI.add(pi);
        Mockito.when(accountServiceMock.getPaymentInfos()).thenReturn(listPI);

    }

    private ImporterBean createTestingBean() {
        return new ImporterBean() {
            @Override
            protected String getDefaultLanguage() {
                // Overwrite static method access
                return "en";
            }

            @Override
            protected FacesContext getContext() {
                if (invalidFacesContext) {
                    throw new RuntimeException("Invalid JFaces context");
                }
                return facesContext;
            }

            @Override
            protected BESClient getBESClient() {
                return new BESClient() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T> T getWebService(Class<T> serviceClass,
                            Credentials credentials) throws Exception {
                        if (serviceClass == IdentityService.class) {
                            return (T) identityServiceMock;
                        } else if (serviceClass == SubscriptionService.class) {
                            return (T) subscriptionServiceMock;
                        } else
                            if (serviceClass == ServiceProvisioningService.class) {
                            return (T) provisioningServiceMock;
                        } else if (serviceClass == AccountService.class) {
                            return (T) accountServiceMock;
                        }
                        return null;
                    }

                };
            }

            @Override
            protected VMClientFactoryBean getVMwareFactory() {
                return new VMClientFactoryBean() {
                    @Override
                    public VMwareClient getInstance(
                            VMPropertyHandler paramHandler) throws Exception {
                        return vmwClient;
                    }

                };
            }

        };
    }

    private UploadedFile createUploader(final String filename,
            final String content) {
        return new UploadedFile() {
            private static final long serialVersionUID = 7479924149513583952L;

            @Override
            public StorageStrategy getStorageStrategy() {
                return null;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public String getName() {
                return filename;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(content.getBytes("UTF-8"));
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return content.getBytes("UTF-8");
            }
        };
    }

    @Test
    public void testGetStatus() throws Exception {
        assertNull(bean.getStatus());
    }

    @Test
    public void testImportSubscription() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId,ResponsibleUser\n"
                + "essimport3,20,MyService,user\n"
                + "essimport4,20,MyService,user\n";

        assertFalse(bean.isImportRunning());
        assertFalse(bean.isProgressPanelVisible());
        assertEquals(0, bean.getProgressValue());
        assertNull(bean.getProgressStatus());

        UploadedFile uplFile = createUploader("myfile.csv", CSVFILE);
        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(uplFile);
        bean.importInstances();

        assertTrue(bean.isImportRunning());
        assertTrue(bean.isProgressPanelVisible());
        assertEquals(uplFile.getName(), bean.getUploadedFile().getName());

        waitForImport();

        String result = bean.getProgressStatus();

        assertTrue(result.contains(" Import finished"));

        assertTrue(result.contains("[essimport3]...ok."));
        assertTrue(result.contains("[essimport4]...ok."));

        assertEquals(100, bean.getProgressValue());

        assertTrue(mailSent);
    }

    @Test
    public void testGetCancelRequestedWithoutImport() throws Exception {
        assertFalse(bean.getCancelRequested());
        assertFalse(bean.isImportRunning());

        bean.cancelImport();

        assertFalse(bean.getCancelRequested());
    }

    @Test
    public void testGetCancelRequestedDuringImport() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId\n"
                + "essimport3,20,MyService\n" + "essimport4,20,MyService\n";

        assertFalse(bean.isImportRunning());
        assertFalse(bean.getCancelRequested());

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        assertTrue(bean.isImportRunning());

        bean.cancelImport();

        assertTrue(bean.getCancelRequested());

        waitForImport();

        String result = bean.getProgressStatus();
        assertTrue(result.contains("User requested cancel"));
    }

    @Test
    public void testImportExistingInstance() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId,ResponsibleUser\n"
                + "essimport3,20,MyService,user\n"
                + "essimport4,20,MyService,user\n";

        existingInstance = "essimport4";

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        waitForImport();

        String result = bean.getProgressStatus();
        assertTrue(result.contains("SKIPPED subscription"));
    }

    @Test
    public void testImportNotExistingVMInstance() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId,ResponsibleUser\n"
                + "essimport3,20,MyService,user\n"
                + "essimport4,20,MyService,user\n";

        notExistingVM = "essimport4";

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        waitForImport();

        String result = bean.getProgressStatus();
        assertTrue(result.contains("SKIPPED subscription"));
    }

    // FIXME this test sometimes fails depending on test case execution order
    @Test
    public void testImportWrongResponsibleUser1() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId,ResponsibleUser\n"
                + "essimport3,20,MyService,user_notexist\n"
                + "essimport4,20,MyService,user\n";

        assertFalse(bean.isImportRunning());
        assertFalse(bean.isProgressPanelVisible());
        assertEquals(0, bean.getProgressValue());
        assertNull(bean.getProgressStatus());

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        assertTrue(bean.isImportRunning());
        assertTrue(bean.isProgressPanelVisible());

        waitForImport();

        String result = bean.getProgressStatus();

        assertTrue(result.contains("SKIPPED subscription"));
        assertTrue(result.contains("not part of customer organization"));
    }

    // FIXME this test sometimes fails depending on test case execution order
    @Test
    public void testImportWrongResponsibleUser2() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId,ResponsibleUser\n"
                + "essimport3,20,MyService,user_org2\n"
                + "essimport4,20,MyService,user\n";

        assertFalse(bean.isImportRunning());
        assertFalse(bean.isProgressPanelVisible());
        assertEquals(0, bean.getProgressValue());
        assertNull(bean.getProgressStatus());

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        assertTrue(bean.isImportRunning());
        assertTrue(bean.isProgressPanelVisible());

        waitForImport();

        String result = bean.getProgressStatus();

        assertTrue(result.contains("SKIPPED subscription"));
        assertTrue(result.contains("not part of customer organization"));
    }

    @Test
    public void testImportEmptyCSVFile() throws Exception {
        final String CSVFILE = "";

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        waitForImport();

        String result = bean.getProgressStatus();
        assertTrue(result.contains("Empty CSV file given"));
    }

    @Test
    public void testImportWrongCSVFile1() throws Exception {
        final String CSVFILE = "InstanceName,Complexity\n" + "essimport3,20\n";

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        waitForImport();

        String result = bean.getProgressStatus();
        assertTrue(result.contains("Missing mandatory column"));
    }

    @Test
    public void testImportWrongCSVFile2() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId,ResponsibleUser\n"
                + "essimport3,MyService,user\n"
                + "essimport4,20,MyService,user\n";

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        waitForImport();

        String result = bean.getProgressStatus();
        assertTrue(result.contains("Missing value"));
    }

    @Test
    public void testImportWrongCSVFile3() throws Exception {
        final String CSVFILE = "InstanceName,Complexity,ServiceId,ResponsibleUser\n"
                + "essimport3,20,,user\n" + "essimport4,20,MyService,user\n";

        bean.getCustomer().setUserId("custUser");
        bean.setUploadedFile(createUploader("myfile.csv", CSVFILE));
        bean.importInstances();

        waitForImport();

        String result = bean.getProgressStatus();
        assertTrue(result.contains("Missing value"));
    }

    // Wait until import has been finished
    private void waitForImport() throws Exception {
        int count = 500;
        while (bean.isImportRunning()) {
            Thread.sleep(100);
            if ((count--) == 0) {
                throw new Exception("Import doesn't return.");
            }
        }
    }

    // internal interface combiner
    private static interface EnhancedIdentityService
            extends IdentityService, BindingProvider {
    }

    private static interface EnhancedSubscriptionService
            extends SubscriptionService, BindingProvider {
    }

    private static interface EnhancedProvisioningService
            extends ServiceProvisioningService, BindingProvider {
    }

    private static interface EnhancedAccountService
            extends AccountService, BindingProvider {
    }

}
