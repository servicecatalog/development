/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) FUJITSU LIMITED - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 26.05.2014                                                      
 *                                                                              
 *******************************************************************************/

package com.fujitsu.bss.app.vmware.ui;

import static org.junit.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import javax.naming.InitialContext;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.fujitsu.bss.app.test.EJBTestBase;
import com.fujitsu.bss.app.test.ejb.TestContainer;
import com.fujitsu.bss.app.v1_0.exceptions.APPlatformException;
import com.fujitsu.bss.app.v1_0.intf.APPlatformService;
import com.fujitsu.bss.app.vmware.APPlatformServiceMockup;
import com.fujitsu.bss.app.vmware.ui.filter.AuthorizationFilter;

/**
 * Unit test of authorization filter
 */
public class AuthorizationFilterTest extends EJBTestBase {
    // Local mockups
    private APPlatformServiceMockup platformService;

    private AuthorizationFilter filter;
    private FilterChain chain;
    private FilterConfig config;
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private HttpSession session;

    private StringWriter responseOut = new StringWriter();

    @Override
    protected void setup(TestContainer container) throws Exception {
        // AWS APP mock-up
        platformService = new APPlatformServiceMockup();
        enableJndiMock();

        InitialContext context = new InitialContext();
        context.bind(APPlatformService.JNDI_NAME, platformService);

        chain = Mockito.mock(FilterChain.class);
        config = Mockito.mock(FilterConfig.class);
        req = Mockito.mock(HttpServletRequest.class);
        resp = Mockito.mock(HttpServletResponse.class);
        session = Mockito.mock(HttpSession.class);

        Mockito.when(resp.getWriter()).thenReturn(new PrintWriter(responseOut));
        Mockito.when(req.getSession()).thenReturn(session);
        Mockito.when(req.getLocale()).thenReturn(new Locale("en"));

        filter = new AuthorizationFilter();
    }

    @Test
    public void testAuthenticateLoggedIn() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn("user1");

        // Init
        filter.init(config);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(chain).doFilter(Matchers.eq(req), Matchers.eq(resp));

    }

    @Test
    public void testAuthenticateWrongInput() throws Exception {
        ServletRequest reqWrong = Mockito.mock(ServletRequest.class);
        filter.doFilter(reqWrong, resp, chain);
        assertEquals("401", responseOut.toString());
        responseOut = new StringWriter();

        ServletResponse respWrong = Mockito.mock(ServletResponse.class);
        Mockito.when(respWrong.getWriter()).thenReturn(
                new PrintWriter(responseOut));
        filter.doFilter(req, respWrong, chain);
        assertEquals("401", responseOut.toString());

    }

    @Test
    public void testAuthenticateLogin() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        String credentials = "user1:password1";
        String credentialsEncoded = Base64.encodeBase64String(credentials
                .getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization"))).thenReturn(
                "Basic " + credentialsEncoded);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded and user is logged in
        Mockito.verify(session).setAttribute(Matchers.eq("loggedInUserId"),
                Matchers.eq("user1"));
        Mockito.verify(session).setAttribute(
                Matchers.eq("loggedInUserPassword"), Matchers.eq("password1"));
        Mockito.verify(chain).doFilter(Matchers.eq(req), Matchers.eq(resp));

        // And destroy
        filter.destroy();
    }

    @Test
    public void testAuthenticateLoginMissingHeader() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        Mockito.when(req.getHeader(Matchers.eq("Authorization"))).thenReturn(
                null);

        // Init
        filter.init(config);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether user will be asked for login
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));

    }

    @Test
    public void testAuthenticateEmptyAuthentication() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        Mockito.when(req.getHeader(Matchers.eq("Authorization")))
                .thenReturn("");

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));
    }

    @Test
    public void testAuthenticateWrongAuthentication() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        String credentials = "user1:password1";
        String credentialsEncoded = Base64.encodeBase64String(credentials
                .getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization"))).thenReturn(
                "UnknownSSO " + credentialsEncoded);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));
    }

    @Test
    public void testAuthenticateWrongCredentials() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        String credentials = "user1_password1";
        String credentialsEncoded = Base64.encodeBase64String(credentials
                .getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization"))).thenReturn(
                "Basic " + credentialsEncoded);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));
    }

    @Test
    public void testAuthenticateWithException() throws Exception {
        platformService.exceptionOnGetControllerSettings = new APPlatformException(
                "failed");
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        String credentials = "user1:password1";
        String credentialsEncoded = Base64.encodeBase64String(credentials
                .getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization"))).thenReturn(
                "Basic " + credentialsEncoded);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));
    }
}
