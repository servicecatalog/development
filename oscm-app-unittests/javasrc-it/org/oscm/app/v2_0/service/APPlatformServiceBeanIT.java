/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: Sep 20, 2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v2_0.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.oscm.app.dao.BesDAO;
import org.oscm.app.dao.ServiceInstanceDAO;
import org.oscm.app.v2_0.data.PasswordAuthentication;
import org.oscm.app.v2_0.data.Setting;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.AuthenticationException;
import org.oscm.app.v2_0.exceptions.SuspendException;
import org.oscm.app.v2_0.intf.APPlatformService;
import org.oscm.app.v2_0.service.APPAuthenticationServiceBean;
import org.oscm.app.v2_0.service.APPCommunicationServiceBean;
import org.oscm.app.v2_0.service.APPConcurrencyServiceBean;
import org.oscm.app.v2_0.service.APPConfigurationServiceBean;
import org.oscm.app.v2_0.service.APPlatformServiceBean;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;
import org.oscm.vo.VOUser;

/**
 * @author Dirk Bernsau
 * 
 */
public class APPlatformServiceBeanIT extends EJBTestBase {

    private APPAuthenticationServiceBean authSvc;
    private APPConfigurationServiceBean configSvc;
    private APPConcurrencyServiceBean concSvc;
    private APPlatformService platformSvc;
    private APPCommunicationServiceBean commSvc;
    private ServiceInstanceDAO instanceDAO;
    private VOUser defaultUser;
    private PasswordAuthentication defaultAuth;

    @Override
    protected void setup(TestContainer container) throws Exception {
        container.enableInterfaceMocking(true);
        container.addBean(Mockito.mock(BesDAO.class));
        container.addBean(instanceDAO = Mockito.mock(ServiceInstanceDAO.class));
        container.addBean(authSvc = Mockito
                .mock(APPAuthenticationServiceBean.class));
        container.addBean(concSvc = Mockito
                .mock(APPConcurrencyServiceBean.class));
        container.addBean(configSvc = Mockito
                .mock(APPConfigurationServiceBean.class));
        container.addBean(commSvc = Mockito
                .mock(APPCommunicationServiceBean.class));
        container.addBean(platformSvc = spy(new APPlatformServiceBean()));
        container.get(APPlatformService.class);
        Answer<HashMap<String, String>> answer = new Answer<HashMap<String, String>>() {

            @Override
            public HashMap<String, String> answer(InvocationOnMock invocation)
                    throws Throwable {
                HashMap<String, String> map = new HashMap<>();
                map.put("key", "value");
                return map;
            }
        };
        Mockito.doAnswer(answer).when(configSvc)
                .getControllerConfigurationSettings(Matchers.anyString());

        defaultUser = new VOUser();
        defaultUser.setUserId("user");
        defaultAuth = new PasswordAuthentication("user", "password");
    }

    @Test
    public void testGetControllerSettings() throws Exception {
        HashMap<String, Setting> settings = platformSvc.getControllerSettings(
                "test", defaultAuth);
        assertNotNull(settings);
        assertEquals(settings.get("key"), "value");
        Mockito.verify(authSvc, Mockito.times(1)).authenticateTMForController(
                Matchers.anyString(),
                Matchers.any(PasswordAuthentication.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStoreControllerSettings() throws Exception {
        // given
        doNothing().when(platformSvc).requestControllerSettings(anyString());
        final HashMap<String, Setting> map = new HashMap<>();
        map.put("key2", new Setting("key2", "value2"));
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                assertTrue(arguments.length == 2);
                assertEquals(arguments[0], "test");
                assertEquals(arguments[1], map);
                return null;
            }
        };

        Mockito.doAnswer(answer)
                .when(configSvc)
                .storeControllerConfigurationSettings(Matchers.anyString(),
                        Matchers.any(HashMap.class));

        // when
        platformSvc.storeControllerSettings("test", map, defaultAuth);

        // then
        Mockito.verify(authSvc, Mockito.times(1)).authenticateTMForController(
                Matchers.anyString(),
                Matchers.any(PasswordAuthentication.class));
    }

    @Test(expected = AuthenticationException.class)
    public void testGetControllerSettings_unauthorized() throws Exception {
        // given
        Mockito.doThrow(new AuthenticationException("some"))
                .when(authSvc)
                .authenticateTMForController(Mockito.anyString(),
                        Matchers.any(PasswordAuthentication.class));

        // when
        platformSvc.getControllerSettings("test", defaultAuth);
    }

    @Test(expected = AuthenticationException.class)
    public void testStoreControllerSettings_unauthorized() throws Exception {
        // given
        Mockito.doThrow(new AuthenticationException("some"))
                .when(authSvc)
                .authenticateTMForController(Mockito.anyString(),
                        Matchers.any(PasswordAuthentication.class));

        // when
        platformSvc.storeControllerSettings("test",
                new HashMap<String, Setting>(), defaultAuth);
    }

    @Test
    public void testExists() throws Exception {
        // given
        Mockito.when(
                new Boolean(instanceDAO.exists(Matchers.matches("ctrl.id"),
                        Matchers.matches("inst.id")))).thenReturn(Boolean.TRUE);

        // then
        assertTrue(platformSvc.exists("ctrl.id", "inst.id"));
        Mockito.verify(instanceDAO, Mockito.times(1)).exists(
                Matchers.eq("ctrl.id"), Matchers.eq("inst.id"));
    }

    @Test
    public void testLockServiceInstance() throws Exception {
        // given
        Mockito.when(
                new Boolean(concSvc.lockServiceInstance(
                        Matchers.matches("ctrl.id"),
                        Matchers.matches("inst.id")))).thenReturn(Boolean.TRUE);

        // then
        assertTrue(platformSvc.lockServiceInstance("ctrl.id", "inst.id",
                defaultAuth));
        Mockito.verify(concSvc, Mockito.times(1)).lockServiceInstance(
                Matchers.eq("ctrl.id"), Matchers.eq("inst.id"));
        Mockito.verify(authSvc, Mockito.times(1)).authenticateTMForInstance(
                Matchers.anyString(), Matchers.anyString(),
                Matchers.any(PasswordAuthentication.class));
    }

    @Test
    public void testUnlockServiceInstance() throws Exception {
        // when
        platformSvc.unlockServiceInstance("ctrl.id", "inst.id", defaultAuth);

        // then
        Mockito.verify(concSvc, Mockito.times(1)).unlockServiceInstance(
                Matchers.eq("ctrl.id"), Matchers.eq("inst.id"));
        Mockito.verify(authSvc, Mockito.times(1)).authenticateTMForInstance(
                Matchers.anyString(), Matchers.anyString(),
                Matchers.any(PasswordAuthentication.class));
    }

    @Test(expected = AuthenticationException.class)
    public void testLockServiceInstance_unauthorized() throws Exception {
        // given
        Mockito.doThrow(new AuthenticationException("some"))
                .when(authSvc)
                .authenticateTMForInstance(Mockito.anyString(),
                        Mockito.anyString(),
                        Matchers.any(PasswordAuthentication.class));
        // when
        platformSvc.lockServiceInstance("ctrl.id", "inst.id", defaultAuth);
    }

    @Test(expected = AuthenticationException.class)
    public void testUnlockServiceInstance_unauthorized() throws Exception {
        // given
        Mockito.doThrow(new AuthenticationException("some"))
                .when(authSvc)
                .authenticateTMForInstance(Mockito.anyString(),
                        Mockito.anyString(),
                        Matchers.any(PasswordAuthentication.class));

        // when
        platformSvc.unlockServiceInstance("ctrl.id", "inst.id", defaultAuth);
    }

    @Test(expected = SuspendException.class)
    public void testSendMail() throws APPlatformException {
        // given
        Mockito.doThrow(new APPlatformException("some cause"))
                .when(commSvc)
                .sendMail(Matchers.anyListOf(String.class),
                        Matchers.anyString(), Matchers.anyString());

        // when
        platformSvc.sendMail(new ArrayList<String>(), "subject", "text");
    }
}
