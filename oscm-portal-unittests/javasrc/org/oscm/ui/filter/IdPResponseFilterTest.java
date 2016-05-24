/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
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

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import org.oscm.saml2.api.SAMLResponseExtractor;
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

    @Test
    public void testFilter() throws Exception {

        //given
        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        HttpSession mockSession = mock(HttpSession.class);
        FilterChain mockChain = mock(FilterChain.class);
        RequestRedirector mockRedirector = mock(RequestRedirector.class);
        AuthenticationSettings mockSettings = mock(AuthenticationSettings.class);
        SAMLResponseExtractor mockExtractor = mock(SAMLResponseExtractor.class);
        idpFilter.excludeUrlPattern = "servletPathOther";
        idpFilter.authSettings = mockSettings;
        idpFilter.redirector = mockRedirector;
        doReturn(mockSession).when(mockRequest).getSession();
        doReturn(false).when(idpFilter).isInvalidIdpUrl(mockSettings);
        doReturn(true).when(mockSettings).isServiceProvider();
        doReturn("someSamlResponse").when(mockRequest).getParameter("SAMLResponse");
        doReturn("someRelayState").when(mockRequest).getParameter("RelayState");
        doReturn("someServletPath").when(mockRequest).getServletPath();
        doReturn("someForwardURL").when(idpFilter).getForwardUrl(mockRequest, "someRelayState");
        doReturn(mockExtractor).when(idpFilter).getSamlResponseExtractor();
        doReturn("someSAMLSessionId").when(mockExtractor).getSessionIndex("someSamlResponse");

        //when
        idpFilter.doFilter(mockRequest, mockResponse, mockChain);
        //then
        verify(mockRedirector,times(1)).forward(mockRequest, mockResponse, "someForwardURL");
    }

}
