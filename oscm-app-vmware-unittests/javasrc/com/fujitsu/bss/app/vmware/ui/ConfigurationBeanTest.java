/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2012 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 26.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

/**
 * @author soehnges
 * 
 */
public class ConfigurationBeanTest extends EJBTestBase {

    private APPlatformService platformService;
    private ConfigurationBean bean;
    private FacesContext facesContext;
    private ExternalContext externalContext;
    private HttpSession httpSession;

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

        bean = createTestingBean();
    }

    private ConfigurationBean createTestingBean() {
        return new ConfigurationBean() {
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
    public void testGetItemKeys() throws Exception {
        List<String> itemKeys = bean.getItemKeys();
        assertEquals(14, itemKeys.size());

        // Try it twice (cache test)
        itemKeys = bean.getItemKeys();
        assertEquals(14, itemKeys.size());
    }

    @Test
    public void testGetItems() throws Exception {
        HashMap<String, String> items = bean.getItems();
        assertEquals(14, items.size());
        assertTrue(items.containsKey(VMPropertyHandler.CTL_DATABASE_IP_POOL));

        // Try it twice (cache test)
        items = bean.getItems();
        assertEquals(14, items.size());
    }

    @Test
    public void testGetEmptyStatus() throws Exception {
        assertNull(bean.getStatus());
    }
}
