/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 04.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.common.saml2;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;

import org.oscm.internal.types.exception.SAML2AuthnRequestException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.saml2.api.AuthnRequestGenerator;
import org.oscm.saml2.api.RedirectSamlURLBuilder;
import org.oscm.saml2.api.model.protocol.AuthnRequestType;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.common.Constants;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.filter.AuthenticationSettings;

/**
 * @author roderus
 * 
 */
public class AuthenticationHandler {

    private final String samlSpRedirectPage = "/saml2/redirectToIdp.jsf";
    private static final String OUTCOME_SAMLSP_REDIRECT = "redirectToIdp";

    private HttpServletRequest request;
    private HttpServletResponse response;
    AuthenticationSettings authSettings;

    private static final Log4jLogger LOGGER = LoggerFactory
            .getLogger(RedirectSamlURLBuilder.class);
    public AuthenticationHandler(HttpServletRequest request,
                                 HttpServletResponse response, AuthenticationSettings authSettings) {

        this.request = request;
        this.response = response;
        this.authSettings = authSettings;
    }

    private boolean isGetMethod() {
        String method = authSettings.getIdentityProviderHttpMethod();
        return method != null && method.equals("GET");
    }

    private String handleAuthentication(boolean isFromBean)
            throws SAML2AuthnRequestException {
        try {
            if (isGetMethod()) {
                return handleRedirect();
            } else {
                return handlePost(isFromBean);
            }
        } catch (Exception e) {
            getLogger().logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_AUTH_REQUEST_GENERATION_FAILED);
            throw new SAML2AuthnRequestException(
                    "Error while redirecting to the SAML IdP",
                    SAML2AuthnRequestException.ReasonEnum.CANNOT_REDIRECT_OR_FORWARD);
        }
    }

    public String handleAuthentication(boolean isFromBean, HttpSession httpSession)
            throws SAML2AuthnRequestException {
        httpSession.setAttribute(Constants.SESS_ATTR_DEFAULT_TIMEOUT,
                httpSession.getMaxInactiveInterval());
        // set session timeout temporally to 30 minutes for saml
        httpSession.setMaxInactiveInterval(30 * 60);
        return handleAuthentication(isFromBean);
    }

    private String handleRedirect() throws Exception {
        Boolean isHttps = Boolean.valueOf(request.isSecure());
        AuthnRequestGenerator gen = new AuthnRequestGenerator(
                authSettings.getIssuer(), isHttps);
        JAXBElement<AuthnRequestType> authRequest = gen.generateAuthnRequest();
        storeAttributeInSession(Constants.SESS_ATTR_IDP_REQUEST_ID, gen.getRequestId());
        storeAttributeInSession(Constants.REQ_PARAM_TENANT_ID, authSettings.getTenantID());
        String redirectUrl = generateRedirectURL(authRequest);
        JSFUtils.sendRedirect(response, redirectUrl);
        return null;
    }

    void storeAttributeInSession(String attirubte, String parameterName) {
        request.getSession().setAttribute(attirubte,
                parameterName);
    }

    private String handlePost(boolean isFromBean) throws Exception {
        if (isFromBean) {
            return handlePostMarketplace();
        } else {
            return handlePostPortal();
        }
    }

    private String handlePostPortal() throws Exception {
        request.getRequestDispatcher(samlSpRedirectPage).forward(request,
                response);
        return null;
    }

    private String handlePostMarketplace() {
        return OUTCOME_SAMLSP_REDIRECT;
    }

    private String generateRedirectURL(JAXBElement<AuthnRequestType> authRequest){

        if (authSettings.getIssuer() == null) {
            return null;
        }

        URL redirectURL;

        RedirectSamlURLBuilder<AuthnRequestType> redirectSamlURLBuilder = new RedirectSamlURLBuilder<>();
        try {
            redirectURL = redirectSamlURLBuilder
                    .addRelayState(getRelayState())
                    .addSamlRequest(authRequest)
                    .addRedirectEndpoint(
                            new URL(authSettings.getIdentityProviderURL()))
                    .getURL();
        } catch (Exception e) {
            getLogger().logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_AUTH_REQUEST_GENERATION_FAILED);
            return null;
        }

        return redirectURL.toString();
    }

    Log4jLogger getLogger() {
        return LOGGER;
    }

    private String getRelayState() {
        HttpSession session = request.getSession();
        String relayState = (String) session
                .getAttribute(Constants.SESS_ATTR_RELAY_STATE);
        return relayState.trim();
    }
}
