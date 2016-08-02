/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jun 15, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.filter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;
import java.util.TreeSet;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.types.constants.marketplace.Marketplace;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.MarketplaceConfigurationBean;
import org.oscm.ui.common.Constants;
import org.oscm.ui.model.MarketplaceConfiguration;

public class ClosedMarketplaceFilterTest {

    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private FilterChain chainMock;
    private HttpSession sessionMock;
    private ServletContext contextMock;
    private RequestRedirector redirectorMock;
    private ClosedMarketplaceFilter closedMplFilter;
    private MarketplaceConfigurationBean configBean;
    private final static String EXCLUDE_URL_PATTERN = "(.*/a4j/.*|.*/img/.*|.*/css/.*|.*/fonts/.*|.*/scripts/.*|.*/faq/.*|^/slogout.jsf|^/public/.*|^/marketplace/terms/.*|.*/marketplace/img/.*)";
    private static final String INSUFFICIENT_AUTH_URL = Marketplace.MARKETPLACE_ROOT
            + Constants.INSUFFICIENT_AUTHORITIES_URI;
    private static final String MPL_START_URL = BaseBean.MARKETPLACE_START_SITE;

    @Before
    public void setup() throws Exception {

        requestMock = mock(HttpServletRequest.class);
        responseMock = mock(HttpServletResponse.class);
        chainMock = mock(FilterChain.class);
        sessionMock = mock(HttpSession.class);
        contextMock = mock(ServletContext.class);
        redirectorMock = mock(RequestRedirector.class);
        configBean = mock(MarketplaceConfigurationBean.class);
        doReturn(sessionMock).when(requestMock).getSession();
        doReturn(contextMock).when(requestMock).getServletContext();
        doReturn(configBean).when(contextMock).getAttribute(
                "marketplaceConfigurationBean");

        closedMplFilter = spy(new ClosedMarketplaceFilter());
        closedMplFilter.excludeUrlPattern = EXCLUDE_URL_PATTERN;
        closedMplFilter.redirector = redirectorMock;
        doReturn(false).when(closedMplFilter).isSAMLAuthentication();
    }

    @Test
    public void testDoFilter_requestMatchesExcludePattern() throws Exception {

        // given
        doReturn("/css/style.css").when(requestMock).getServletPath();

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testDoFilter_emptyMplId() throws Exception {

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

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(false, false, null)).when(configBean)
                .getConfiguration("mpid", requestMock);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
    }

    @Test
    public void testDoFilter_restrictedMarketplaceWithAccess() throws Exception {

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(true, true, "testOrg")).when(configBean)
                .getConfiguration("mpid", requestMock);
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

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(true, true, "testOrg")).when(configBean)
                .getConfiguration("mpid", requestMock);
        doReturn(getUserDetails("anotherOrg")).when(sessionMock).getAttribute(
                Constants.SESS_ATTR_USER);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(redirectorMock, times(1)).forward(eq(requestMock),
                eq(responseMock), eq(INSUFFICIENT_AUTH_URL));
    }

    @Test
    public void testDoFilter_restrictedMarketplaceWithNullUser()
            throws Exception {

        // given
        doReturn("/portal/*").when(requestMock).getServletPath();
        doReturn("mpid").when(sessionMock).getAttribute(
                Constants.REQ_PARAM_MARKETPLACE_ID);
        doReturn(getConfiguration(true, true, "testOrg")).when(configBean)
                .getConfiguration("mpid", requestMock);
        doReturn(null).when(sessionMock).getAttribute(Constants.SESS_ATTR_USER);

        // when
        closedMplFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(chainMock, times(1)).doFilter(requestMock, responseMock);
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
