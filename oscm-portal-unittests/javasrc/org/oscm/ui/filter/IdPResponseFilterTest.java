/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 09.07.2013                                                    
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.Constants;

/**
 * Unit testing of {@link IdPResponseFilter}.
 * 
 * @author stavreva
 */
public class IdPResponseFilterTest {

    private static final String EMPTY_STRING = "  ";
    private static final String NOT_BLANK = "idp_url";
    private static final String RELAY_STATE = "relay_state";
    private static final String RELAY_STATE_MARKETPLACE = "/marketplace/index.jsf";
    private static final String RELAY_STATE_REGISTRATION = "/marketplace/registration.jsf";

    private IdPResponseFilter idpFilter;

    private HttpServletRequest requestMock;
    private AuthenticationSettings authSettingsMock;
    private HttpSession sessionMock;

    @Before
    public void setup() throws Exception {

        requestMock = mock(HttpServletRequest.class);
        authSettingsMock = mock(AuthenticationSettings.class);

        idpFilter = spy(new IdPResponseFilter());
        idpFilter.authSettings = authSettingsMock;

        sessionMock = mock(HttpSession.class);
        doReturn(sessionMock).when(requestMock).getSession();

    }

    @Test
    public void isInvalidIdpUrl_nullURL() throws Exception {
        // given
        doReturn(null).when(authSettingsMock).getIdentityProviderURL();
        doReturn(null).when(authSettingsMock)
                .getIdentityProviderURLContextRoot();

        // then
        assertTrue(idpFilter.isInvalidIdpUrl(authSettingsMock));
    }

    @Test
    public void isInvalidIdpUrl_emptyURL() throws Exception {
        // given
        doReturn(EMPTY_STRING).when(authSettingsMock).getIdentityProviderURL();
        doReturn(null).when(authSettingsMock)
                .getIdentityProviderURLContextRoot();

        // then
        assertTrue(idpFilter.isInvalidIdpUrl(authSettingsMock));
    }

    @Test
    public void isInvalidIdpUrl_noContextRoot() throws Exception {
        // given
        doReturn(NOT_BLANK).when(authSettingsMock).getIdentityProviderURL();
        doReturn(null).when(authSettingsMock)
                .getIdentityProviderURLContextRoot();

        // then
        assertTrue(idpFilter.isInvalidIdpUrl(authSettingsMock));
    }

    @Test
    public void isInvalidIdpUrl_valid() throws Exception {
        // given
        doReturn(NOT_BLANK).when(authSettingsMock).getIdentityProviderURL();
        doReturn(NOT_BLANK).when(authSettingsMock)
                .getIdentityProviderURLContextRoot();

        // then
        assertFalse(idpFilter.isInvalidIdpUrl(authSettingsMock));
    }

    @Test
    public void getForwardUrl() throws Exception {
        // given
        doReturn(null).when(requestMock).getAttribute(
                Constants.REQ_ATTR_SERVICE_LOGIN_TYPE);
        // when
        String result = idpFilter.getForwardUrl(requestMock, RELAY_STATE);
        // then
        assertEquals(RELAY_STATE, result);
    }

    @Test
    public void getForwardUrl_marketplaceLogin() throws Exception {
        // given
        doReturn(null).when(requestMock).getAttribute(
                Constants.REQ_ATTR_SERVICE_LOGIN_TYPE);
        // when
        String result = idpFilter.getForwardUrl(requestMock,
                RELAY_STATE_MARKETPLACE);
        // then
        verify(idpFilter, times(1)).setRequestAttributesForAutosubmit(
                eq(requestMock), eq(RELAY_STATE_MARKETPLACE));
        assertEquals(BaseBean.MARKETPLACE_START_SITE, result);
    }

    @Test
    public void getForwardUrl_serviceLogin() throws Exception {
        // given
        doReturn(Constants.REQ_ATTR_LOGIN_TYPE_MPL).when(requestMock)
                .getAttribute(Constants.REQ_ATTR_SERVICE_LOGIN_TYPE);
        // when
        String result = idpFilter.getForwardUrl(requestMock, RELAY_STATE);
        // then
        verify(idpFilter, times(1)).setRequestAttributesForAutosubmit(
                eq(requestMock), eq(RELAY_STATE));
        assertEquals(BaseBean.MARKETPLACE_START_SITE, result);
    }

    @Test
    public void getForwardUrl_selfRegistration() throws Exception {
        // given
        doReturn(null).when(requestMock).getAttribute(
                Constants.REQ_ATTR_SERVICE_LOGIN_TYPE);
        // when
        String result = idpFilter.getForwardUrl(requestMock,
                RELAY_STATE_REGISTRATION);
        // then
        verify(idpFilter, times(1)).setRequestAttributesForSelfRegistration(
                eq(requestMock), eq(RELAY_STATE_REGISTRATION));
        assertEquals(RELAY_STATE_REGISTRATION, result);
    }

    @Test
    public void containsSAMLResponse_invalidIdpUrl() throws Exception {
        // given
        doReturn(Boolean.TRUE).when(authSettingsMock).isServiceProvider();
        doReturn(Boolean.TRUE).when(idpFilter)
                .isInvalidIdpUrl(authSettingsMock);

        // then
        assertFalse(idpFilter.containsSamlResponse(requestMock));
    }

    @Test
    public void containsSAMLResponse_noSAMLResponse() throws Exception {
        // given
        doReturn(Boolean.TRUE).when(authSettingsMock).isServiceProvider();
        doReturn(Boolean.FALSE).when(idpFilter).isInvalidIdpUrl(
                authSettingsMock);
        doReturn(null).when(requestMock).getParameter(matches("SAMLResponse"));

        // then
        assertFalse(idpFilter.containsSamlResponse(requestMock));
    }

    @Test
    public void containsSAMLResponse_emptySAMLResponse() throws Exception {
        // given
        doReturn(Boolean.TRUE).when(authSettingsMock).isServiceProvider();
        doReturn(Boolean.FALSE).when(idpFilter).isInvalidIdpUrl(
                authSettingsMock);
        doReturn("").when(requestMock).getParameter(matches("SAMLResponse"));

        // then
        assertTrue(idpFilter.containsSamlResponse(requestMock));
    }

    @Test
    public void containsSAMLResponse_notEmptySAMLResponse() throws Exception {
        // given
        doReturn(Boolean.TRUE).when(authSettingsMock).isServiceProvider();
        doReturn(Boolean.FALSE).when(idpFilter).isInvalidIdpUrl(
                authSettingsMock);
        doReturn("some_saml_response").when(requestMock).getParameter(
                matches("SAMLResponse"));

        // then
        assertTrue(idpFilter.containsSamlResponse(requestMock));
    }

    @Test
    public void containsSAMLResponse_notServiceProvider() throws Exception {
        // given
        doReturn(Boolean.FALSE).when(authSettingsMock).isServiceProvider();

        // then
        assertFalse(idpFilter.containsSamlResponse(requestMock));
    }

}
