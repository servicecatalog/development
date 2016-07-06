/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 20, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.io.IOException;
import java.sql.SQLException;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.core.Response.Status;

import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.types.enumtypes.AuthenticationMode;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.types.constants.Configuration;

import com.sun.enterprise.security.auth.login.common.LoginException;
import com.sun.jersey.core.util.Base64;
import com.sun.web.security.WebProgrammaticLoginImpl;

/**
 * Servlet filter for programmatic Glassfish authentication via basic auth
 * 
 * @author miethaner
 */
public class BasicAuthFilter implements Filter {

    private static final String SAML_PWD_PREFIX = "RS";

    private WebProgrammaticLoginImpl programmaticLogin;

    public void setProgrammaticLogin(WebProgrammaticLoginImpl programmaticLogin) {
        this.programmaticLogin = programmaticLogin;
    }

    @EJB
    private ConfigurationService configService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        programmaticLogin = new WebProgrammaticLoginImpl();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest rq = (HttpServletRequest) request;
        HttpServletResponse rs = (HttpServletResponse) response;

        String header = rq.getHeader(CommonParams.HEADER_AUTH);
        if (header != null && !header.isEmpty()
                && header.startsWith(CommonParams.BASIC_AUTH_PREFIX)) {

            String encodedUsrPwd = header.replace(
                    CommonParams.BASIC_AUTH_PREFIX, "");
            String userPwd = Base64.base64Decode(encodedUsrPwd);
            String[] split = userPwd
                    .split(CommonParams.BASIC_AUTH_SEPARATOR, 2);

            String pwd = split[1];

            String authMode = configService.getVOConfigurationSetting(
                    ConfigurationKey.AUTH_MODE, Configuration.GLOBAL_CONTEXT)
                    .getValue();

            if (AuthenticationMode.SAML_SP.name().equals(authMode)) {
                pwd = SAML_PWD_PREFIX + pwd;
            }

            try {
                String userKey = getUserKeyFromId(split[0]);

                programmaticLogin.login(userKey, pwd.toCharArray(),
                        CommonParams.REALM, rq, rs);

            } catch (NamingException | SQLException | LoginException e) {
                rs.sendError(Status.UNAUTHORIZED.getStatusCode(),
                        CommonParams.ERROR_LOGIN_FAILED);
            }

        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    private String getUserKeyFromId(String userId) throws NamingException,
            SQLException {
        long userKey = -1;
        Context context = new InitialContext();
        DataSource ds = (DataSource) context.lookup("BSSDS");
        KeyQuery keyQuery = new KeyQuery(ds, userId);
        keyQuery.execute();
        userKey = keyQuery.getUserKey();
        return String.valueOf(userKey);
    }

}
