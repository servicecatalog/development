/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jun 15, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.filter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.oscm.internal.types.enumtypes.ConfigurationKey.MP_ERROR_REDIRECT_HTTP;
import static org.oscm.types.constants.Configuration.GLOBAL_CONTEXT;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.oscm.internal.cache.MarketplaceConfiguration;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.TenantService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.types.constants.Configuration;
import org.oscm.types.constants.marketplace.Marketplace;
import org.oscm.ui.common.Constants;
import org.oscm.ui.common.ServiceAccess;

public class ClosedMarketplaceFilterTest {

    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private FilterChain chainMock;
    private HttpSession sessionMock;
    private RequestRedirector redirectorMock;
    private ClosedMarketplaceFilter closedMplFilter;
    private final static String EXCLUDE_URL_PATTERN = "(.*/a4j/.*|.*/img/.*|.*/css/.*|.*/fonts/.*|.*/scripts/.*|.*/faq/.*|^/slogout.jsf|^/public/.*|^/marketplace/terms/.*|.*/marketplace/img/.*)";
    private static final String INSUFFICIENT_AUTH_URL = Marketplace.MARKETPLACE_ROOT
            + Constants.INSUFFICIENT_AUTHORITIES_URI;

    @Before
    public void setup() throws Exception {

        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        chainMock = mock(FilterChain.class);
        sessionMock = mock(HttpSession.class);
        redirectorMock = mock(RequestRedirector.class);
        doReturn(sessionMock).when(requestMock).getSession();

        closedMplFilter = spy(new ClosedMarketplaceFilter());
        closedMplFilter.excludeUrlPattern = EXCLUDE_URL_PATTERN;
        closedMplFilter.redirector = redirectorMock;
    }

    @Test
    public void testDoFilter_requestMatchesExcludePattern() throws Exception {
        doReturn(false).when(closedMplFilter).isSAMLAuthentication();

        // given
        doReturn("/css/style.css").when(requestMock).getServletPath();

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testDoFilter_emptyMplId() throws Exception {
        doReturn(false).when(closedMplFilter).isSAMLAuthentication();

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testDoFilter_notRestrictedMarketplace() throws Exception {
        doReturn(false).when(closedMplFilter).isSAMLAuthentication();

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(false, false, null)).when(closedMplFilter)
                .getConfig("mpid");

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testDoFilter_restrictedMarketplaceWithAccess() throws Exception {
        doReturn(false).when(closedMplFilter).isSAMLAuthentication();

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(true, true, "testOrg")).when(closedMplFilter)
                .getConfig("mpid");
        doReturn(getUserDetails("testOrg")).when(sessionMock).getAttribute(
                Constants.SESS_ATTR_USER);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testDoFilter_restrictedMarketplaceWithNoAccess()
            throws Exception {
        doReturn(false).when(closedMplFilter).isSAMLAuthentication();

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(true, true, "testOrg")).when(closedMplFilter)
                .getConfig("mpid");
        doReturn(getUserDetails("anotherOrg")).when(sessionMock).getAttribute(
                Constants.SESS_ATTR_USER);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));

        //when
        doReturn(true).when(sessionMock).getAttribute(
                Constants.PORTAL_HAS_BEEN_REQUESTED);
        ConfigurationService configurationMock = mock(ConfigurationService.class);
        doReturn(configurationMock).when(closedMplFilter).getConfigurationService(requestMock);
        VOConfigurationSetting voConfigSet = new VOConfigurationSetting();
        voConfigSet.setValue("address");
        doReturn(voConfigSet).when(configurationMock).getVOConfigurationSetting(MP_ERROR_REDIRECT_HTTP, GLOBAL_CONTEXT);

        closedMplFilter.doFilter(requestMock, responseMock, chainMock);
        verify(responseMock, times(1)).sendRedirect(anyString());

    }

    @Test
    public void testDoFilter_restrictedMarketplaceWithNullUser()
            throws Exception {
        doReturn(false).when(closedMplFilter).isSAMLAuthentication();

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(true, true, "testOrg")).when(closedMplFilter)
                .getConfig("mpid");
        doReturn(null).when(sessionMock).getAttribute(Constants.SESS_ATTR_USER);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testDoFilter_saml() throws Exception {
        doReturn(true).when(closedMplFilter).isSAMLAuthentication();

        // given
        RequestDispatcher dispatcherMock = mock(RequestDispatcher.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        doReturn(mockServletContext).when(requestMock).getServletContext();
        doReturn(dispatcherMock).when(mockServletContext).getRequestDispatcher(
                any(String.class));
        doReturn(true).when(closedMplFilter).isSAMLAuthentication();
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(true, true, "testOrg")).when(closedMplFilter)
                .getConfig("mpid");
        doReturn(getUserDetails("anotherOrg")).when(sessionMock).getAttribute(
                Constants.SESS_ATTR_USER);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(dispatcherMock, times(1)).forward(requestMock, responseMock);

    }

    @Test
    public void testIsSAMLAuthentication() {
        // given
        ServiceAccess mockServiceAccess = mock(ServiceAccess.class);
        ConfigurationService mockConfServ = mock(ConfigurationService.class);
        closedMplFilter.serviceAccess = mockServiceAccess;
        doReturn(mockConfServ).when(mockServiceAccess).getService(ConfigurationService.class);
        TenantService tenantMock = mock(TenantService.class);
        doReturn(tenantMock).when(mockServiceAccess).getService(TenantService.class);
        // when
        closedMplFilter.isSAMLAuthentication();
        // then
    }

    @Test
    public void testInvalidTenants_differentIds()
            throws IOException, ServletException {
        // given

        final String tenant1 = "a1234567";
        final String tenant2 = "b1234567";
        final String tenantDefault = "default1";

        // when

        testDoFilterWithTenants(tenant1, tenant2, tenantDefault);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_sameIds()
            throws IOException, ServletException {
        // given

        final String tenant1 = "a1234567";
        final String tenantDefault = "default1";

        // when

        testDoFilterWithTenants(tenant1, tenant1, tenantDefault);

        // then
        verify(redirectorMock, times(0)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_MpTenantIdNull()
            throws IOException, ServletException {
        // given

        final String tenant1 = null;
        final String tenant2 = "a1234567";
        final String tenantDefault = "default1";

        // when

        testDoFilterWithTenants(tenant1, tenant2, tenantDefault);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_SessionTenantIdNull()
            throws IOException, ServletException {
        // given

        final String tenant1 = null;
        final String tenant2 = "a1234567";
        final String tenantDefault = "default1";

        // when

        testDoFilterWithTenants(tenant2, tenant1, tenantDefault);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_BothTenantIdsNull()
            throws IOException, ServletException {
        // given

        final String tenant1 = null;
        final String tenantDefault = "default1";

        // when

        testDoFilterWithTenants(tenant1, tenant1, tenantDefault);

        // then
        verify(redirectorMock, times(0)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_AllDefaultTenants()
            throws IOException, ServletException {
        // given

        final String tenantDefault = null;

        // when

        testDoFilterWithTenants(tenantDefault, tenantDefault, tenantDefault);

        // then
        verify(redirectorMock, times(0)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_SessionDefaultTenant()
            throws IOException, ServletException {
        // given

        final String tenantDefault = null;
        final String tenantMP = "a1234567";

        // when

        testDoFilterWithTenants(tenantMP, tenantDefault, tenantDefault);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_MPDefaultTenant()
            throws IOException, ServletException {
        // given

        final String tenantDefault = null;
        final String tenantSession = "a1234567";

        // when

        testDoFilterWithTenants(tenantDefault, tenantSession, tenantDefault);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_NullDefaultTenant_SameTenants()
            throws IOException, ServletException {
        // given

        final String tenant1 = "a1234567";
        final String tenantDefault = null;

        // when

        testDoFilterWithTenants(tenant1, tenant1, tenantDefault);

        // then
        verify(redirectorMock, times(0)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testInvalidTenants_NullDefaultTenant_DifferentTenants()
            throws IOException, ServletException {
        // given

        final String tenant1 = "a1234567";
        final String tenant2 = "b1234567";
        final String tenantDefault = null;

        // when

        testDoFilterWithTenants(tenant1, tenant2, tenantDefault);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }


    private void testDoFilterWithTenants(String tenantFromMp, String tenantFromSession, String defaultTenant) throws IOException, ServletException {
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock)
                .getAttribute(Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(tenantFromSession).when(sessionMock).getAttribute(Constants.REQ_PARAM_TENANT_ID);
        ServiceAccess mockServiceAccess = mock(ServiceAccess.class);
        ConfigurationService mockConfServ = mock(ConfigurationService.class);

        VOConfigurationSetting voSetting = new VOConfigurationSetting();
        voSetting.setInformationId(ConfigurationKey.SSO_DEFAULT_TENANT_ID);
        voSetting.setValue(defaultTenant);

        doReturn(voSetting).when(mockConfServ).getVOConfigurationSetting(ConfigurationKey.SSO_DEFAULT_TENANT_ID,
                Configuration.GLOBAL_CONTEXT);

        closedMplFilter.serviceAccess = mockServiceAccess;
        doReturn(mockConfServ).when(mockServiceAccess).getService(ConfigurationService.class);
        MarketplaceConfiguration config = getConfiguration(false, true,
                "someOrgId");
        config.setTenantId(tenantFromMp);

        doReturn(config).when(closedMplFilter).getConfig("mpid");

        closedMplFilter.doFilter(requestMock, responseMock, chainMock);
    }

    private MarketplaceConfiguration getConfiguration(boolean isRestricted,
            boolean hasPublicLandingPage, String orgId) {
        MarketplaceConfiguration config = new MarketplaceConfiguration();
        config.setRestricted(isRestricted);
        config.setLandingPage(hasPublicLandingPage);
        Set<String> set = new TreeSet<String>();
        if (orgId != null) {
            set.add(orgId);
        }
        config.setAllowedOrganizations(set);
        return config;
    }

    private VOUserDetails getUserDetails(String orgId) {
        VOUserDetails userDetails = new VOUserDetails();
        userDetails.setOrganizationId(orgId);
        return userDetails;
    }

}
