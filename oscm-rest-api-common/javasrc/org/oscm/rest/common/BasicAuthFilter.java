/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: May 20, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import java.io.IOException;

import javax.ejb.EJB;
import javax.security.auth.login.LoginContext;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.AuthenticationMode;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.OrganizationRemovedException;
import org.oscm.internal.vo.VOUser;
import org.oscm.security.WSCallbackHandler;
import org.oscm.types.constants.Configuration;

/**
 * Servlet filter for programmatic authentication via basic auth
 * 
 * @author miethaner
 */
public class BasicAuthFilter implements Filter {

    @EJB
    private ConfigurationService configService;
    @EJB
    private IdentityService identityService;

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) request;
        HttpServletResponse rs = (HttpServletResponse) response;

        String header = rq.getHeader(CommonParams.HEADER_AUTH);
        if (header != null && !header.isEmpty()
                && header.startsWith(CommonParams.BASIC_AUTH_PREFIX)) {

            String encodedUsrPwd = header
                    .replace(CommonParams.BASIC_AUTH_PREFIX, "");
            String userPwd = new String(Base64.decodeBase64(encodedUsrPwd));
            String[] split = userPwd.split(CommonParams.BASIC_AUTH_SEPARATOR,
                    2);

            String pwd = split[1];

            String authMode = configService
                    .getVOConfigurationSetting(ConfigurationKey.AUTH_MODE,
                            Configuration.GLOBAL_CONTEXT)
                    .getValue();

            if (!AuthenticationMode.INTERNAL.name().equals(authMode)) {
                rs.sendError(Status.UNAUTHORIZED.getStatusCode(),
                        CommonParams.ERROR_NOT_INTERNAL_MODE);
            }

            try {
                VOUser user = new VOUser();
                user.setUserId(split[0]);

                user = identityService.getUser(user);

                LoginContext lc = new LoginContext("bssrealm",
                        new WSCallbackHandler(user.getUserId(), pwd));
                lc.login();

            } catch (ObjectNotFoundException
                    | javax.security.auth.login.LoginException
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
