/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 27.10.15 14:29
 *
 *******************************************************************************/
package org.oscm.app.v1_0.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.oscm.app.converter.APPInterfaceDataConverter;
import org.oscm.app.converter.APPInterfaceExceptionConverter;
import org.oscm.app.v2_0.data.PasswordAuthentication;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.AuthenticationException;
import org.oscm.app.v2_0.exceptions.ConfigurationException;

@RunWith(MockitoJUnitRunner.class)
public class APPlatformServiceBeanTest {

    @Mock
    private APPInterfaceDataConverter dataConverter;// = new
                                                    // APPInterfaceDataConverter();
    @Mock
    private APPInterfaceExceptionConverter exceptionConverter;// = new
                                                              // APPInterfaceExceptionConverter();

    @Mock
    private org.oscm.app.v2_0.intf.APPlatformService delegate;
    @Spy
    private APPlatformServiceBean bean = new APPlatformServiceBean();
    private org.oscm.app.v1_0.data.PasswordAuthentication authentication;
    private PasswordAuthentication newAuth;

    @Before
    public void setUp() throws Exception {
        bean.setDelegate(delegate);
        bean.setDataConverter(dataConverter);
        bean.setExceptionConverter(exceptionConverter);
        doReturn(new org.oscm.app.v1_0.exceptions.AuthenticationException(""))
                .when(exceptionConverter).convertToOld(
                        any(AuthenticationException.class));
        doReturn(new org.oscm.app.v1_0.exceptions.ConfigurationException(""))
                .when(exceptionConverter).convertToOld(
                        any(ConfigurationException.class));
        doReturn(new org.oscm.app.v1_0.exceptions.APPlatformException(""))
                .when(exceptionConverter).convertToOld(
                        any(APPlatformException.class));
        authentication = mock(org.oscm.app.v1_0.data.PasswordAuthentication.class);
        newAuth = dataConverter.convertToNew(authentication);
    }

    @Test
    public void testSendMail() throws Exception {
        // given
        String text = "text";
        List<String> list = new ArrayList<>();
        String subject = "sub";
        // when
        bean.sendMail(list, subject, text);
        // then
        verify(delegate, times(1)).sendMail(list, subject, text);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testSendMailException() throws Exception {
        // given
        String text = "text";
        List<String> list = new ArrayList<>();
        String subject = "sub";
        APPlatformException toBeThrown = new APPlatformException(subject);
        doThrow(toBeThrown).when(delegate).sendMail(list, subject, text);
        // when
        bean.sendMail(list, subject, text);
        // then
        verify(delegate, times(1)).sendMail(list, subject, text);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testGetEventServiceUrl() throws Exception {
        // given
        // when
        bean.getEventServiceUrl();
        // then
        verify(delegate, times(1)).getEventServiceUrl();
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testGetEventServiceUrlException() throws Exception {
        // given
        ConfigurationException toBeThrown = new ConfigurationException("text");
        doThrow(toBeThrown).when(delegate).getEventServiceUrl();
        // when
        bean.getEventServiceUrl();
        // then
        verify(delegate, times(1)).getEventServiceUrl();
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testGetBSSWebServiceUrl() throws Exception {
        // given
        // when
        bean.getBSSWebServiceUrl();
        // then
        verify(delegate, times(1)).getBSSWebServiceUrl();
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testGetBSSWebServiceUrlException() throws Exception {
        // given
        ConfigurationException toBeThrown = new ConfigurationException("text");
        doThrow(toBeThrown).when(delegate).getBSSWebServiceUrl();
        // when
        bean.getBSSWebServiceUrl();
        // then
        verify(delegate, times(1)).getBSSWebServiceUrl();
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testGetBSSWebServiceWSDLUrl() throws Exception {
        // given
        // when
        bean.getBSSWebServiceWSDLUrl();
        // then
        verify(delegate, times(1)).getBSSWebServiceWSDLUrl();
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testGetBSSWebServiceWSDLUrlException() throws Exception {
        // given
        ConfigurationException toBeThrown = new ConfigurationException("text");
        doThrow(toBeThrown).when(delegate).getBSSWebServiceWSDLUrl();
        // when
        bean.getBSSWebServiceWSDLUrl();
        // then
        verify(delegate, times(1)).getBSSWebServiceWSDLUrl();
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testLockServiceInstance() throws Exception {
        // given
        String text = "text";
        // when
        bean.lockServiceInstance(text, text, authentication);
        // then
        verify(delegate, times(1)).lockServiceInstance(text, text, newAuth);
        verify(dataConverter, times(2)).convertToNew(authentication);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testLockServiceInstanceException() throws Exception {
        // given
        String text = "text";
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).lockServiceInstance(text, text,
                newAuth);
        // when
        bean.lockServiceInstance(text, text, authentication);
        // then
        verify(delegate, times(1)).lockServiceInstance(text, text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.AuthenticationException.class)
    public void testLockServiceInstanceException2() throws Exception {
        // given
        String text = "text";
        AuthenticationException toBeThrown = new AuthenticationException(text);
        doThrow(toBeThrown).when(delegate).lockServiceInstance(text, text,
                newAuth);
        // when
        bean.lockServiceInstance(text, text, authentication);
        // then
        verify(delegate, times(1)).lockServiceInstance(text, text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testUnlockServiceInstance() throws Exception {
        // given
        String text = "text";
        // when
        bean.unlockServiceInstance(text, text, authentication);
        // then
        verify(delegate, times(1)).unlockServiceInstance(text, text, newAuth);
        verify(dataConverter, times(2)).convertToNew(authentication);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.AuthenticationException.class)
    public void testUnlockServiceInstanceException() throws Exception {
        // given
        String text = "text";
        AuthenticationException toBeThrown = new AuthenticationException(text);
        doThrow(toBeThrown).when(delegate).unlockServiceInstance(text, text,
                newAuth);
        // when
        bean.unlockServiceInstance(text, text, authentication);
        // then
        verify(delegate, times(1)).unlockServiceInstance(text, text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testUnlockServiceInstanceException2() throws Exception {
        // given
        String text = "text";
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).unlockServiceInstance(text, text,
                newAuth);
        // when
        bean.unlockServiceInstance(text, text, authentication);
        // then
        verify(delegate, times(1)).unlockServiceInstance(text, text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testExists() throws Exception {
        // given
        String text = "text";
        // when
        bean.exists(text, text);
        // then
        verify(delegate, times(1)).exists(text, text);
    }

    @Test
    public void testGetControllerSettings() throws Exception {
        // given
        String text = "text";
        // when
        bean.getControllerSettings(text, authentication);
        // then
        verify(delegate, times(1)).getControllerSettings(text, newAuth);
        verify(dataConverter, times(2)).convertToNew(authentication);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testGetControllerSettingsException() throws Exception {
        // given
        String text = "text";
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).getControllerSettings(text, newAuth);
        // when
        bean.getControllerSettings(text, authentication);
        // then
        verify(delegate, times(1)).getControllerSettings(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.AuthenticationException.class)
    public void testGetControllerSettingsException2() throws Exception {
        // given
        String text = "text";
        AuthenticationException toBeThrown = new AuthenticationException(text);
        doThrow(toBeThrown).when(delegate).getControllerSettings(text, newAuth);
        // when
        bean.getControllerSettings(text, authentication);
        // then
        verify(delegate, times(1)).getControllerSettings(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testGetControllerSettingsException3() throws Exception {
        // given
        String text = "text";
        ConfigurationException toBeThrown = new ConfigurationException(text);
        doThrow(toBeThrown).when(delegate).getControllerSettings(text, newAuth);
        // when
        bean.getControllerSettings(text, authentication);
        // then
        verify(delegate, times(1)).getControllerSettings(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testStoreControllerSettingsException() throws Exception {
        // given
        String text = "text";
        HashMap<String, String> controllerSettings = new HashMap<>();
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).storeControllerSettings(eq(text),
                any(HashMap.class), Mockito.eq(newAuth));
        // when
        bean.storeControllerSettings(text, controllerSettings, authentication);
        // then
        verify(delegate, times(1)).storeControllerSettings(eq(text),
                any(HashMap.class), eq(newAuth));
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testStoreControllerSettings() throws Exception {
        // given
        String text = "text";
        // when
        HashMap<String, String> controllerSettings = new HashMap<>();
        bean.storeControllerSettings(text, controllerSettings, authentication);
        // then
        verify(delegate, times(1)).storeControllerSettings(eq(text),
                any(HashMap.class), eq(newAuth));
        verify(dataConverter, times(2)).convertToNew(authentication);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.AuthenticationException.class)
    public void testStoreControllerSettingsException2() throws Exception {
        // given
        String text = "text";
        HashMap<String, String> controllerSettings = new HashMap<>();
        AuthenticationException toBeThrown = new AuthenticationException(text);
        doThrow(toBeThrown).when(delegate).storeControllerSettings(eq(text),
                any(HashMap.class), Mockito.eq(newAuth));
        // when
        bean.storeControllerSettings(text, controllerSettings, authentication);
        // then
        verify(delegate, times(1)).storeControllerSettings(eq(text),
                any(HashMap.class), eq(newAuth));
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testStoreControllerSettingsException3() throws Exception {
        // given
        String text = "text";
        HashMap<String, String> controllerSettings = new HashMap<>();
        ConfigurationException toBeThrown = new ConfigurationException(text);
        doThrow(toBeThrown).when(delegate).storeControllerSettings(eq(text),
                any(HashMap.class), eq(newAuth));
        // when
        bean.storeControllerSettings(text, controllerSettings, authentication);
        // then
        verify(delegate, times(1)).storeControllerSettings(eq(text),
                any(HashMap.class), eq(newAuth));
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testAuthenticate() throws Exception {
        // given
        String text = "text";
        // when
        bean.authenticate(text, authentication);
        // then
        verify(delegate, times(1)).authenticate(text, newAuth);
        verify(dataConverter, times(2)).convertToNew(authentication);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testAuthenticateException() throws Exception {
        // given
        String text = "text";
        ConfigurationException toBeThrown = new ConfigurationException(text);
        doThrow(toBeThrown).when(delegate).authenticate(text, newAuth);
        // when
        bean.authenticate(text, authentication);
        // then
        verify(delegate, times(1)).authenticate(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testAuthenticateException2() throws Exception {
        // given
        String text = "text";
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).authenticate(text, newAuth);
        // when
        bean.authenticate(text, authentication);
        // then
        verify(delegate, times(1)).authenticate(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.AuthenticationException.class)
    public void testAuthenticateException3() throws Exception {
        // given
        String text = "text";
        AuthenticationException toBeThrown = new AuthenticationException(text);
        doThrow(toBeThrown).when(delegate).authenticate(text, newAuth);
        // when
        bean.authenticate(text, authentication);
        // then
        verify(delegate, times(1)).authenticate(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testRequestControllerSettings() throws Exception {
        // given
        String text = "text";
        // when
        bean.requestControllerSettings(text);
        // then
        verify(delegate, times(1)).requestControllerSettings(text);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testRequestControllerSettingsException() throws Exception {
        // given
        String text = "text";
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).requestControllerSettings(text);
        // when
        bean.requestControllerSettings(text);
        // then
        verify(delegate, times(1)).requestControllerSettings(text);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testRequestControllerSettingsException2() throws Exception {
        // given
        String text = "text";
        ConfigurationException toBeThrown = new ConfigurationException(text);
        doThrow(toBeThrown).when(delegate).requestControllerSettings(text);
        // when
        bean.requestControllerSettings(text);
        // then
        verify(delegate, times(1)).requestControllerSettings(text);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testListServiceInstances() throws Exception {
        // given
        String text = "text";
        // when
        bean.listServiceInstances(text, authentication);
        // then
        verify(delegate, times(1)).listServiceInstances(text, newAuth);
        verify(dataConverter, times(2)).convertToNew(authentication);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testListServiceInstancesException() throws Exception {
        // given
        String text = "text";
        ConfigurationException toBeThrown = new ConfigurationException(text);
        doThrow(toBeThrown).when(delegate).listServiceInstances(text, newAuth);
        // when
        bean.listServiceInstances(text, authentication);
        // then
        verify(delegate, times(1)).listServiceInstances(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testListServiceInstancesException2() throws Exception {
        // given
        String text = "text";
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).listServiceInstances(text, newAuth);
        // when
        bean.listServiceInstances(text, authentication);
        // then
        verify(delegate, times(1)).listServiceInstances(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.AuthenticationException.class)
    public void testListServiceInstancesException3() throws Exception {
        // given
        String text = "text";
        AuthenticationException toBeThrown = new AuthenticationException(text);
        doThrow(toBeThrown).when(delegate).listServiceInstances(text, newAuth);
        // when
        bean.listServiceInstances(text, authentication);
        // then
        verify(delegate, times(1)).listServiceInstances(text, newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test
    public void testGetServiceInstanceDetails() throws Exception {
        // given
        String text = "text";
        // when
        bean.getServiceInstanceDetails(text, text, authentication);
        // then
        verify(delegate, times(1)).getServiceInstanceDetails(text, text,
                newAuth);
        verify(dataConverter, times(2)).convertToNew(authentication);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.ConfigurationException.class)
    public void testGetServiceInstanceDetailsException() throws Exception {
        // given
        String text = "text";
        ConfigurationException toBeThrown = new ConfigurationException(text);
        doThrow(toBeThrown).when(delegate).getServiceInstanceDetails(text,
                text, newAuth);
        // when
        bean.getServiceInstanceDetails(text, text, authentication);
        // then
        verify(delegate, times(1)).getServiceInstanceDetails(text, text,
                newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.APPlatformException.class)
    public void testGetServiceInstanceDetailsException2() throws Exception {
        // given
        String text = "text";
        APPlatformException toBeThrown = new APPlatformException(text);
        doThrow(toBeThrown).when(delegate).getServiceInstanceDetails(text,
                text, newAuth);
        // when
        bean.getServiceInstanceDetails(text, text, authentication);
        // then
        verify(delegate, times(1)).getServiceInstanceDetails(text, text,
                newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }

    @Test(expected = org.oscm.app.v1_0.exceptions.AuthenticationException.class)
    public void testGetServiceInstanceDetailsException3() throws Exception {
        // given
        String text = "text";
        AuthenticationException toBeThrown = new AuthenticationException(text);
        doThrow(toBeThrown).when(delegate).getServiceInstanceDetails(text,
                text, newAuth);
        // when
        bean.getServiceInstanceDetails(text, text, authentication);
        // then
        verify(delegate, times(1)).getServiceInstanceDetails(text, text,
                newAuth);
        verify(dataConverter, times(1)).convertToNew(authentication);
        verify(exceptionConverter, times(1)).convertToOld(toBeThrown);
    }
}
