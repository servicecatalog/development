/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jun 4, 2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.SessionService;
import org.oscm.internal.types.exception.SAML2StatusCodeInvalidException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.saml2.api.SAMLLogoutResponseValidator;
import org.oscm.saml2.api.SAMLResponseExtractor;
import org.oscm.types.constants.marketplace.Marketplace;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.ADMStringUtils;
import org.oscm.ui.common.Constants;
import org.oscm.ui.common.EJBServiceAccess;
import org.oscm.ui.common.ServiceAccess;
import org.oscm.ui.delegates.ServiceLocator;

/**
 * @author farmaki
 * 
 */
public class IdPLogoutFilter implements Filter {

    RequestRedirector redirector;
    String excludeUrlPattern;
    AuthenticationSettings authSettings;
    SAMLResponseExtractor samlResponseExtractor;
    SAMLLogoutResponseValidator samlLogoutResponseValidator;
    SessionService ssl;

    public SessionService getSsl() {
        if (ssl == null) {
            ssl = new ServiceLocator().findService(SessionService.class);
        }
        return ssl;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        redirector = new RequestRedirector(filterConfig);
        excludeUrlPattern = filterConfig
                .getInitParameter("exclude-url-pattern");

        ServiceAccess serviceAccess = new EJBServiceAccess();
        ConfigurationService cfgService = serviceAccess
                .getService(ConfigurationService.class);
        authSettings = new AuthenticationSettings(cfgService);
    }

    /**
     * If the request contains a SAML 2.0 response, forward to the originally
     * requested resource is done. In case of service login, the request is
     * forwarded to an auto-submit page, to do the login in UserBean. <br/>
     * If the response does not contain a SAML 2.0 response, the next filter is
     * called. See web.xml for excluded url pattern.
     * 
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!httpRequest.getServletPath().matches(excludeUrlPattern)) {

            if (containsSamlResponse(httpRequest)) {
                httpRequest.setAttribute(Constants.REQ_ATTR_IS_SAML_FORWARD,
                        Boolean.TRUE);
                samlResponseExtractor = new SAMLResponseExtractor();
                String samlResponse = httpRequest.getParameter("SAMLResponse");
                HttpSession currentSession = httpRequest.getSession();

                if (samlResponseExtractor.isFromLogout(samlResponse)) {
                    getSsl().deletePlatformSession(currentSession.getId());
                    currentSession.invalidate();
                    httpRequest.removeAttribute("SAMLResponse");
                    httpRequest.setAttribute(Constants.REQ_ATTR_IS_SAML_FORWARD,
                            Boolean.FALSE);
                    try {
                        samlLogoutResponseValidator = new SAMLLogoutResponseValidator();
                        if (!samlLogoutResponseValidator.responseStatusCodeSuccessful(samlResponse)) {
                            httpRequest.setAttribute(
                                    Constants.REQ_ATTR_ERROR_KEY,
                                    BaseBean.ERROR_INVALID_SAML_RESPONSE_STATUS_CODE);
                            redirector.forward(httpRequest, httpResponse,
                                    BaseBean.ERROR_PAGE);
                            return;
                        }
                    }catch (SAML2StatusCodeInvalidException e) {
                        httpRequest.setAttribute(
                                Constants.REQ_ATTR_ERROR_KEY,
                                BaseBean.ERROR_INVALID_SAML_RESPONSE);
                        redirector.forward(httpRequest, httpResponse,
                                BaseBean.ERROR_PAGE);
                        return;
                    }
                    String relayState = httpRequest.getParameter("RelayState");
                    if (relayState != null) {
                        String forwardUrl = getForwardUrl(httpRequest, relayState);
                        ((HttpServletResponse) response).sendRedirect(forwardUrl);
                        return;
                    }
                }
            }

            if (httpRequest
                    .getAttribute(Constants.REQ_ATTR_ERROR_KEY) != null) {
                redirector.forward(httpRequest, httpResponse,
                        BaseBean.ERROR_PAGE);
                return;
            }

            httpRequest.setAttribute(Constants.REQ_ATTR_IS_SAML_FORWARD,
                    Boolean.FALSE);
        }

        chain.doFilter(request, response);
    }

    String getForwardUrl(HttpServletRequest httpRequest, String relayState) {

        String forwardUrl;
        setLoginTypeAttribute(httpRequest, relayState);

        if (relayState.startsWith(Marketplace.MARKETPLACE_REGISTRATION)) {
            setRequestAttributesForSelfRegistration(httpRequest, relayState);
            forwardUrl = relayState;
        } else if (relayState.startsWith(Marketplace.MARKETPLACE_ADD)) {
            forwardUrl = relayState;
        } else if (Constants.REQ_ATTR_LOGIN_TYPE_MPL.equals(
                httpRequest.getAttribute(Constants.REQ_ATTR_SERVICE_LOGIN_TYPE))
                || relayState.startsWith(Marketplace.MARKETPLACE_ROOT)) {
            forwardUrl = setRequestAttributesForAutosubmit(httpRequest,
                    relayState);
        } else {
            forwardUrl = relayState;
        }

        return forwardUrl;
    }

    void setLoginTypeAttribute(HttpServletRequest httpRequest,
            String relayState) {
        BesServletRequestReader.copyURLParamToRequestAttribute(httpRequest,
                Constants.REQ_ATTR_SERVICE_LOGIN_TYPE, relayState);
    }

    String setRequestAttributesForAutosubmit(HttpServletRequest httpRequest,
            String relayState) {
        String result = BaseBean.SAML_SP_LOGIN_AUTOSUBMIT_PAGE;
        SAMLCredentials samlCredentials = new SAMLCredentials(httpRequest);
        httpRequest.setAttribute(Constants.REQ_PARAM_USER_ID,
                samlCredentials.getUserId());
        String generatedPassword = samlCredentials.generatePassword();
        if (generatedPassword == null) {
            httpRequest.setAttribute(Constants.REQ_ATTR_ERROR_KEY,
                    BaseBean.ERROR_SAML_TIMEOUT);
            result = BaseBean.MARKETPLACE_START_SITE;
        }
        httpRequest.setAttribute(Constants.REQ_ATTR_PASSWORD,
                generatedPassword);
        httpRequest.setAttribute(Constants.REQ_ATTR_REQUESTED_REDIRECT,
                relayState);
        return result;
    }

    void setRequestAttributesForSelfRegistration(HttpServletRequest httpRequest,
            String relayState) {
        SAMLCredentials samlCredentials = new SAMLCredentials(httpRequest);
        httpRequest.setAttribute(Constants.REQ_PARAM_USER_ID,
                samlCredentials.getUserId());
        httpRequest.setAttribute(Constants.REQ_ATTR_REQUESTED_REDIRECT,
                relayState);
        httpRequest.getSession().setAttribute(Constants.REQ_ATTR_PASSWORD,
                samlCredentials.generatePassword());
    }

    boolean containsSamlResponse(HttpServletRequest httpRequest) {

        if (!authSettings.isServiceProvider()) {
            return false;
        }

        if (isInvalidIdpUrl(authSettings)) {
            httpRequest.setAttribute(Constants.REQ_ATTR_ERROR_KEY,
                    BaseBean.ERROR_INVALID_IDP_URL);
            return false;
        }

        String samlResponse = httpRequest.getParameter("SAMLResponse");
        if (samlResponse != null) {
            return true;
        }

        return false;
    }

    boolean isInvalidIdpUrl(AuthenticationSettings authSettings) {
        return ADMStringUtils.isBlank(authSettings.getIdentityProviderURL())
                || ADMStringUtils.isBlank(
                        authSettings.getIdentityProviderURLContextRoot());
    }

    @Override
    public void destroy() {
    }

    Log4jLogger getLogger() {
        return LoggerFactory.getLogger(IdPLogoutFilter.class);
    }
}
