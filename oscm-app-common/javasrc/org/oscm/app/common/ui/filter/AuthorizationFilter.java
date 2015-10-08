/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 27.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.common.ui.filter;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.oscm.app.common.i18n.Messages;
import org.oscm.app.common.intf.ControllerAccess;
import org.oscm.app.v1_0.APPlatformServiceFactory;
import org.oscm.app.v1_0.data.PasswordAuthentication;
import org.oscm.app.v1_0.data.User;
import org.oscm.app.v1_0.intf.APPlatformService;

/**
 * @author Dirk Bernsau
 * 
 */
public class AuthorizationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AuthorizationFilter.class);

    public static final String LOCALE_DEFAULT = "en";
    public static final String LOCALE_JA = "ja";

    private String controllerId;
    private ControllerAccess controllerAccess;

    @Inject
    public void setControllerAccess(final ControllerAccess controllerAccess) {
        this.controllerAccess = controllerAccess;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)
                || !(response instanceof HttpServletResponse)) {
            response.getWriter().print(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (controllerId == null) {
            controllerId = controllerAccess.getControllerId();
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();
        Object loggedInUserId = session.getAttribute("loggedInUserId");
        if (loggedInUserId != null) {
            // logged in continue normally
            chain.doFilter(httpRequest, response);
            return;
        }

        // Check HTTP Basic authentication
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                String basic = st.nextToken();

                // only handle HTTP Basic authentication
                if (basic.equalsIgnoreCase("basic")) {
                    String credentials = st.nextToken();
                    String userPass = new String(
                            Base64.decodeBase64(credentials));

                    // The decoded string is in the form "userID:password".
                    int p = userPass.indexOf(":");
                    if (p != -1) {
                        String username = userPass.substring(0, p);
                        String password = userPass.substring(p + 1);

                        PasswordAuthentication tpUser = new PasswordAuthentication(
                                username, password);
                        APPlatformService pSvc = APPlatformServiceFactory
                                .getInstance();
                        try {
                            // Check authority
                            User user = pSvc.authenticate(controllerId, tpUser);
                            // It worked! => store credentials in session for
                            // later use
                            session.setAttribute("loggedInUserId", username);
                            session.setAttribute("loggedInUserPassword",
                                    password);
                            session.setAttribute("loggedInUserLocale",
                                    user.getLocale());
                            // And continue
                            chain.doFilter(httpRequest, response);
                            return;

                        } catch (Exception e) {
                            // Problem occurred...
                            LOGGER.debug("doFilter: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // Return 401 error
        String clientLocale = httpRequest.getLocale().getLanguage();
        if (clientLocale.equals(LOCALE_JA)) {
            clientLocale = LOCALE_DEFAULT;
        }
        httpResponse.setHeader(
                "WWW-Authenticate",
                "Basic realm=\""
                        + Messages
                                .get(clientLocale, "ui.config.authentication")
                        + "\"");
        httpResponse.setStatus(401);
        httpResponse.setContentType("text/html");
    }

    @Override
    public void destroy() {
    }

}
