/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.app.common.ui.filter;

/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) FUJITSU LIMITED - ALL RIGHTS RESERVED.                  
 *                                                                              
 *  Creation Date: 26.05.2014                                                      
 *                                                                              
 *******************************************************************************/
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
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
import org.oscm.app.common.APPlatformServiceMockup;
import org.oscm.app.common.intf.ControllerAccess;
import org.oscm.app.v2_0.data.PasswordAuthentication;
import org.oscm.app.v2_0.data.User;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.AuthenticationException;
import org.oscm.app.v2_0.exceptions.ConfigurationException;
import org.oscm.app.v2_0.intf.APPlatformService;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

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

    private ControllerAccess controllerAccess;

    private boolean exception = false;

    @Override
    protected void setup(TestContainer container) throws Exception {

        platformService = new APPlatformServiceMockup() {
            @Override
            public User authenticate(String controllerId,
                    PasswordAuthentication authentication)
                    throws AuthenticationException, ConfigurationException,
                    APPlatformException {
                User user = new User();
                user.setLocale("en");
                if (exception) {
                    throw new APPlatformException("failed");
                }
                return user;

            }
        };
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
        Mockito.when(config.getInitParameter("exclude-url-pattern")).thenReturn(
                "(.*/a4j/.*|.*/img/.*|.*/css/.*|.*/fonts/.*|.*/scripts/.*|.*/faq/.*|.*/org.richfaces.resources|.*/javax.faces.resource/.*|^/public/.*)");

        controllerAccess = Mockito.mock(ControllerAccess.class);
        Mockito.when(controllerAccess.getControllerId())
                .thenReturn("ess.common");

        filter = new AuthorizationFilter();
        filter.setControllerAccess(controllerAccess);
        filter.init(config);
    }

    @Test
    public void testAuthenticateLoggedIn() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn("user1");

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
        Mockito.when(respWrong.getWriter())
                .thenReturn(new PrintWriter(responseOut));
        filter.doFilter(req, respWrong, chain);
        assertEquals("401", responseOut.toString());

    }

    @Test
    public void testAuthenticateLogin() throws Exception {
        exception = false;
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        String credentials = "user1:password1";
        String credentialsEncoded = Base64
                .encodeBase64String(credentials.getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization")))
                .thenReturn("Basic " + credentialsEncoded);

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

        Mockito.when(req.getHeader(Matchers.eq("Authorization")))
                .thenReturn(null);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether user will be asked for login
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));

    }

    @Test
    public void testAuthenticateLogin_JA() throws Exception {
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        Mockito.when(req.getHeader(Matchers.eq("Authorization")))
                .thenReturn(null);
        Mockito.when(req.getLocale()).thenReturn(new Locale("ja"));

        // when
        filter.doFilter(req, resp, chain);

        // then
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith(
                        "Basic realm=\"Please log in as technology manager\""));

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
        String credentialsEncoded = Base64
                .encodeBase64String(credentials.getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization")))
                .thenReturn("UnknownSSO " + credentialsEncoded);

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
        String credentialsEncoded = Base64
                .encodeBase64String(credentials.getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization")))
                .thenReturn("Basic " + credentialsEncoded);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));
    }

    @Test
    public void testAuthenticateWithException() throws Exception {
        exception = true;
        platformService.exceptionOnGetControllerSettings = new APPlatformException(
                "failed");
        Mockito.when(session.getAttribute(Matchers.eq("loggedInUserId")))
                .thenReturn(null);

        String credentials = "user1:password1";
        String credentialsEncoded = Base64
                .encodeBase64String(credentials.getBytes());

        Mockito.when(req.getHeader(Matchers.eq("Authorization")))
                .thenReturn("Basic " + credentialsEncoded);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));
    }

    @Test
    public void testCustomTabAuth() throws Exception {
        exception = false;
        Mockito.when(req.getServletPath()).thenReturn("/serverInformation.jsf");
        byte[] cipher_byte;
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        String encodedInstId = new String(
                Base64.encodeBase64(instId.getBytes()), "UTF-8");
        final String token = encodedInstId + "_test_9142fd59";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes("UTF-8"));
        cipher_byte = md.digest();
        String encodedCipher = new String(Base64.encodeBase64(cipher_byte),
                "UTF-8");
        doReturn(token + "_" + encodedCipher).when(req)
                .getParameter(Matchers.eq("token"));
        doReturn(instId).when(req).getParameter(Matchers.eq("instId"));

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded and user is logged in
        Mockito.verify(chain).doFilter(Matchers.eq(req), Matchers.eq(resp));
        Mockito.verify(resp, Mockito.never()).setStatus(Matchers.eq(401));
        Mockito.verify(resp, Mockito.never()).setHeader(
                Matchers.eq("WWW-Authenticate"), Matchers.startsWith("Basic "));
        Mockito.verify(session).setAttribute("serverInfoLoggedIn", instId);

        // And destroy
        filter.destroy();
    }

    @Test
    public void testCustomTabAuthPathOther() throws Exception {
        exception = false;
        Mockito.when(req.getServletPath()).thenReturn("/test/path.jsf");

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp).setHeader(Matchers.eq("WWW-Authenticate"),
                Matchers.startsWith("Basic "));
    }

    @Test
    public void testCustomTabAuthTokenNotCorrect() throws Exception {
        exception = false;
        Mockito.when(req.getServletPath()).thenReturn("/serverInformation.jsf");
        byte[] cipher_byte;
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        String encodedInstId = new String(
                Base64.encodeBase64(instId.getBytes()), "UTF-8");
        final String token = encodedInstId + "_test_9142fd59";
        final String wrongInstId = "wrong-instId";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes("UTF-8"));
        cipher_byte = md.digest();
        String encodedCipher = new String(Base64.encodeBase64(cipher_byte),
                "UTF-8");
        doReturn(token + "_" + encodedCipher).when(req)
                .getParameter(Matchers.eq("token"));
        doReturn(wrongInstId).when(req).getParameter(Matchers.eq("instId"));

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded and user is logged in
        Mockito.verify(chain, Mockito.never()).doFilter(Matchers.eq(req),
                Matchers.eq(resp));
        Mockito.verify(resp).setStatus(Matchers.eq(401));
        Mockito.verify(resp, Mockito.never()).setHeader(
                Matchers.eq("WWW-Authenticate"), Matchers.startsWith("Basic "));

        // And destroy
        filter.destroy();
    }

    @Test
    public void testCustomTabAuthPathOtherAndSessionCorrect() throws Exception {
        exception = false;
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        Mockito.when(req.getServletPath()).thenReturn("/test/path.jsf");
        Mockito.when(session.getAttribute("serverInfoLoggedIn"))
                .thenReturn(instId);

        // And go!
        filter.doFilter(req, resp, chain);

        // Check whether request has been forwarded
        Mockito.verify(chain).doFilter(Matchers.eq(req), Matchers.eq(resp));
        Mockito.verify(resp, Mockito.never()).setStatus(Matchers.eq(401));
        Mockito.verify(resp, Mockito.never()).setHeader(
                Matchers.eq("WWW-Authenticate"), Matchers.startsWith("Basic "));
    }

    @Test
    public void testCheckToken() throws Exception {
        // given
        byte[] cipher_byte;
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        String encodedInstId = new String(
                Base64.encodeBase64(instId.getBytes()), "UTF-8");
        final String token = encodedInstId + "_test_9142fd59";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes("UTF-8"));
        cipher_byte = md.digest();
        String encodedCipher = new String(Base64.encodeBase64(cipher_byte),
                "UTF-8");
        doReturn(token + "_" + encodedCipher).when(req)
                .getParameter(Matchers.eq("token"));
        doReturn(instId).when(req).getParameter(Matchers.eq("instId"));

        // when
        boolean actual = filter.checkToken(req);

        // then
        assertTrue(actual);

    }

    @Test
    public void testCheckToken_include_underscore_in_instId() throws Exception {
        // given
        byte[] cipher_byte;
        final String instId = "stack-ad8c51f1-d44b_489c-a2f6-40e8e68e0d86";
        String encodedInstId = new String(
                Base64.encodeBase64(instId.getBytes()), "UTF-8");
        final String token = encodedInstId + "_test_9142fd59";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes("UTF-8"));
        cipher_byte = md.digest();
        String encodedCipher = new String(Base64.encodeBase64(cipher_byte),
                "UTF-8");
        doReturn(token + "_" + encodedCipher).when(req)
                .getParameter(Matchers.eq("token"));
        doReturn(instId).when(req).getParameter(Matchers.eq("instId"));

        // when
        boolean actual = filter.checkToken(req);

        // then
        assertTrue(actual);

    }

    @Test
    public void testCheckTokenWithWrongInstId() throws Exception {
        // given
        byte[] cipher_byte;
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        String encodedInstId = new String(
                Base64.encodeBase64(instId.getBytes()), "UTF-8");
        final String token = encodedInstId + "_test_9142fd59";
        final String wrongInstId = "wrong-id";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes("UTF-8"));
        cipher_byte = md.digest();
        String encodedCipher = new String(Base64.encodeBase64(cipher_byte),
                "UTF-8");
        doReturn(token + "_" + encodedCipher).when(req)
                .getParameter(Matchers.eq("token"));
        doReturn(wrongInstId).when(req).getParameter(Matchers.eq("instId"));

        // when
        boolean actual = filter.checkToken(req);

        // then
        assertFalse(actual);
    }

    @Test
    public void testCheckTokenWithWrongToken() throws Exception {
        // given
        byte[] cipher_byte;
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        String encodedInstId = new String(
                Base64.encodeBase64(instId.getBytes()), "UTF-8");
        final String token = encodedInstId + "_test_9142fd59";
        final String wrongToken = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86_wrong_token123";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes("UTF-8"));
        cipher_byte = md.digest();
        String encodedCipher = new String(Base64.encodeBase64(cipher_byte),
                "UTF-8");
        doReturn(wrongToken + "_" + encodedCipher).when(req)
                .getParameter(Matchers.eq("token"));
        doReturn(instId).when(req).getParameter(Matchers.eq("instId"));

        // when
        boolean actual = filter.checkToken(req);

        // then
        assertFalse(actual);
    }

    @Test
    public void testCheckTokenInatIdNull() throws Exception {
        // given
        byte[] cipher_byte;
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        String encodedInstId = new String(
                Base64.encodeBase64(instId.getBytes()), "UTF-8");
        final String token = encodedInstId + "_test_9142fd59";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(token.getBytes("UTF-8"));
        cipher_byte = md.digest();
        String encodedCipher = new String(Base64.encodeBase64(cipher_byte),
                "UTF-8");
        doReturn(token + "_" + encodedCipher).when(req)
                .getParameter(Matchers.eq("token"));
        doReturn(null).when(req).getParameter(Matchers.eq("instId"));

        // when
        boolean actual = filter.checkToken(req);

        // then
        assertFalse(actual);

    }

    @Test
    public void testCheckTokenTokenNull() throws Exception {
        // given
        final String instId = "stack-ad8c51f1-d44b-489c-a2f6-40e8e68e0d86";
        doReturn(null).when(req).getParameter(Matchers.eq("token"));
        doReturn(instId).when(req).getParameter(Matchers.eq("instId"));

        // when
        boolean actual = filter.checkToken(req);

        // then
        assertFalse(actual);

    }
}