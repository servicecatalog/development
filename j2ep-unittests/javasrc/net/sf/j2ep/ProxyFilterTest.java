/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2013 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 2013-5-29                                                      
 *                                                                              
 *******************************************************************************/

package net.sf.j2ep;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.ConnectException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.j2ep.model.ResponseHandler;
import net.sf.j2ep.model.Rule;
import net.sf.j2ep.model.Server;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test of ProxyFilter
 * 
 * @author Qiu
 * 
 */
public class ProxyFilterTest {

    private ProxyFilter proxyFilter;
    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private FilterChain chainMock;
    private ResponseHandler responseHandlerMock;
    private FilterConfig filterConfigMock;
    private Server serverMock;
    private Rule ruleMock;

    @Before
    public void setup() throws Exception {
        proxyFilter = spy(new ProxyFilter());
        requestMock = mock(HttpServletRequest.class);
        doReturn("/oscm-portal").when(requestMock).getContextPath();
        doReturn("/oscm-portal/opt/80e8/").when(requestMock)
                .getRequestURI();
        responseMock = mock(HttpServletResponse.class);
        chainMock = mock(FilterChain.class);
        responseHandlerMock = mock(ResponseHandler.class);
        filterConfigMock = mock(FilterConfig.class);
        serverMock = mock(Server.class);
        doReturn(serverMock).when(requestMock).getAttribute(eq("proxyServer"));
        ruleMock = mock(Rule.class);
        doReturn(ruleMock).when(serverMock).getRule();
        doReturn(requestMock).when(serverMock).preExecute(
                any(HttpServletRequest.class));
        doReturn("/oscm-portal/opt/80e8/").when(ruleMock).process(
                anyString());
        doReturn("http").when(requestMock).getScheme();
        doReturn("localhost:8180").when(serverMock).getDomainName();
        doReturn("").when(serverMock).getPath();
        proxyFilter.init(filterConfigMock);
    }

    @Test
    public void doFilter() throws Exception {
        // given
        doReturn(responseHandlerMock).when(proxyFilter).executeRequest(
                any(HttpServletRequest.class), anyString());
        doReturn(responseMock).when(serverMock).postExecute(
                any(HttpServletResponse.class));

        doReturn(Integer.valueOf(200)).when(responseHandlerMock)
                .getStatusCode();

        // when
        proxyFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(responseHandlerMock, times(1)).process(eq(responseMock));

    }

    @Test
    public void doFilter_WithFailCode() throws Exception {
        // given
        doReturn(responseHandlerMock).when(proxyFilter).executeRequest(
                any(HttpServletRequest.class), anyString());
        doReturn(responseMock).when(serverMock).postExecute(
                any(HttpServletResponse.class));

        doReturn(Integer.valueOf(404)).when(responseHandlerMock)
                .getStatusCode();
        // when
        proxyFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(proxyFilter, times(1)).redirectToErrorPage(eq(requestMock),
                eq(responseMock), eq("404"));

    }

    @Test
    public void doFilter_Bug10022() throws Exception {
        // given
        ConnectException ce = new ConnectException();
        doThrow(ce).when(proxyFilter).executeRequest(
                any(HttpServletRequest.class), anyString());

        String nullstring = null;

        // when
        proxyFilter.doFilter(requestMock, responseMock, chainMock);

        // then
        verify(proxyFilter, times(1)).redirectToErrorPage(eq(requestMock),
                eq(responseMock), eq(nullstring));

    }

    @Test
    public void redirectToErrorPage() throws Exception {

        // when
        proxyFilter.redirectToErrorPage(requestMock, responseMock, "404");
        // then
        verify(responseMock, times(1))
                .sendRedirect(
                        eq("/oscm-portal/public/reverseProxyError.jsf?errorcode=404"));
    }

    @Test
    public void redirectToErrorPage_WithNullCode() throws Exception {

        // when
        proxyFilter.redirectToErrorPage(requestMock, responseMock, null);
        // then
        verify(responseMock, times(1)).sendRedirect(
                eq("/oscm-portal/public/reverseProxyError.jsf"));
    }
}
