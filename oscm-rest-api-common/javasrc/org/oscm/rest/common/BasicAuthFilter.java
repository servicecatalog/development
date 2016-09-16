/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 20, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.AuthenticationMode;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.OrganizationRemovedException;
import org.oscm.internal.vo.VOUser;
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

    private WebProgrammaticLoginImpl programmaticLogin;

    public void setProgrammaticLogin(WebProgrammaticLoginImpl programmaticLogin) {
        this.programmaticLogin = programmaticLogin;
    }

    @EJB
    private ConfigurationService configService;

    public void setConfigurationService(ConfigurationService configService) {
        this.configService = configService;
    }

    @EJB
    private IdentityService identityService;

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

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

            if (!AuthenticationMode.INTERNAL.name().equals(authMode)) {
                rs.sendError(Status.UNAUTHORIZED.getStatusCode(),
                        CommonParams.ERROR_NOT_INTERNAL_MODE);
            }

            try {
                VOUser user = new VOUser();
                user.setUserId(split[0]);

                user = identityService.getUser(user);

                programmaticLogin.login(Long.toString(user.getKey()),
                        pwd.toCharArray(), CommonParams.REALM, rq, rs);

            } catch (ObjectNotFoundException | LoginException
                    | OperationNotPermittedException
                    | OrganizationRemovedException e) {
                rs.sendError(Status.UNAUTHORIZED.getStatusCode(),
                        CommonParams.ERROR_LOGIN_FAILED);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
