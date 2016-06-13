/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 18.10.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.common.saml2;

import static org.oscm.ui.beans.BaseBean.ERROR_GENERATE_AUTHNREQUEST;
import static org.oscm.ui.beans.BaseBean.ERROR_INVALID_IDP_URL;
import static org.oscm.ui.beans.BaseBean.OUTCOME_MARKETPLACE_LOGOUT;
import static org.oscm.ui.beans.BaseBean.OUTCOME_PUBLIC_ERROR_PAGE;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.transform.TransformerException;

import org.oscm.converter.XMLConverter;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.SessionService;
import org.oscm.internal.intf.SignerService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.SAML2AuthnRequestException;
import org.oscm.internal.types.exception.SAMLSigningException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.saml2.api.AuthnRequestGenerator;
import org.oscm.saml2.api.LogoutRequestGenerator;
import org.oscm.saml2.api.Marshalling;
import org.oscm.saml2.api.model.protocol.LogoutRequestType;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.common.ADMStringUtils;
import org.oscm.ui.common.Constants;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.common.UiDelegate;

/**
 * @author roderus
 *
 */
@ManagedBean
@RequestScoped
public class Saml2Ctrl{

    protected static final String SAML_SP_REDIRECT_IFRAME = "/saml2/saml2PostInclude.jsf";

    @ManagedProperty(value = "#{saml2Model}")
    private Saml2Model model;

    @EJB(beanInterface = SignerService.class)
    private SignerService samlBean;

    @EJB(beanInterface = SessionService.class)
    private SessionService sessionService;

    @EJB(beanInterface = ConfigurationService.class)
    private ConfigurationService configurationService;


    private UiDelegate uiDelegate ;
    private boolean fromLogout;

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(Saml2Ctrl.class);

    public String initModelAndCheckForErrors() {

        AuthnRequestGenerator reqGenerator;
        LogoutRequestGenerator logoutGenerator;

        try {
            reqGenerator = getAuthnRequestGenerator();
            logoutGenerator = getLogoutRequestGenerator();
            model.setEncodedAuthnRequest(reqGenerator.getEncodedAuthnRequest());
            model.setRelayState(this.getRelayState());
            model.setAcsUrl(this.getAcsUrl().toExternalForm());
            model.setLogoffUrl(this.getLogoffUrl());
            storeRequestIdInSession(reqGenerator.getRequestId());
            if (fromLogout) {
                model.setEncodedAuthnLogoutRequest(generateLogoutRequest(logoutGenerator));
                handleDeleteSession();
                handleDeleteCookies();
            }
        } catch (SAML2AuthnRequestException e) {
            getLogger().logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_AUTH_REQUEST_GENERATION_FAILED);
            getUiDelegate().handleError(null, ERROR_GENERATE_AUTHNREQUEST);
            return getErrorOutcome();
        } catch (MalformedURLException e) {
            getLogger().logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_MISSING_IDP_URL);
            getUiDelegate().handleError(null, ERROR_INVALID_IDP_URL);
            return getErrorOutcome();
        }

        return null;
    }

    String generateLogoutRequest(LogoutRequestGenerator logoutGenerator) throws SAML2AuthnRequestException {
        try {
            String samlSessionId = sessionService.getSAMLSessionStringForSessionId(getSessionId());
            sessionService.deletePlatformSession(getRequest().getSession().getId());
            final JAXBElement<LogoutRequestType> rootElement =
                    logoutGenerator.generateLogoutRequest(samlSessionId);
            Marshalling<LogoutRequestType> marshaller = new Marshalling<>();
            final String convertedSAMLEnvelope = XMLConverter.convertLogoutRequestToString(
                    marshaller.marshallElement(rootElement), false);
            return logoutGenerator.encode(XMLConverter.removeEOLCharsFromXML(convertedSAMLEnvelope));
        } catch (DatatypeConfigurationException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_SAML_LOGOUT_GENERATION_FAILED);
            return null;
        } catch (TransformerException e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_SAML_LOGOUT_GENERATION_FAILED);
        } catch (SAMLSigningException e){
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_SIGNING_SAML_FAULT);}
        catch (Exception e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_SAML_LOGOUT_GENERATION_FAILED);
        }
        return null;
    }

    private void handleDeleteSession() {
        final HttpSession session = getRequest().getSession();
        session.invalidate();
    }

    private void handleDeleteCookies() {
        final Cookie[] cookies = getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                getResponse().addCookie(cookie);
            }
        }
    }

    String getSessionId() {
        return JSFUtils.getSession().getId();
    }


    public void setModel(Saml2Model model) {
        this.model = model;
    }

    String getErrorOutcome() {
        if (isOnMarketplace()) {
            return OUTCOME_MARKETPLACE_LOGOUT;
        } else {
            return OUTCOME_PUBLIC_ERROR_PAGE;
        }
    }

    String getRelayState() {
        return (String) getSessionAttribute(Constants.SESS_ATTR_RELAY_STATE);
    }

    private Object getSessionAttribute(String key) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(
                false);
        if (session != null) {
            return session.getAttribute(key);
        }
        return null;
    }
    void storeRequestIdInSession(String requestId) {
        setSessionAttribute(Constants.SESS_ATTR_IDP_REQUEST_ID, requestId);
    }

    private void setSessionAttribute(String key, String value) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(
                false);
        session.setAttribute(key, value);
    }

    URL getAcsUrl() throws MalformedURLException {
        String acsURL = configurationService.getVOConfigurationSetting(
                ConfigurationKey.SSO_IDP_URL, "global").getValue();
        return new URL(acsURL);
    }

    String getLogoffUrl() throws MalformedURLException {
        String logoffURL = configurationService.getVOConfigurationSetting(
                ConfigurationKey.SSO_LOGOUT_URL, "global").getValue();
        return logoffURL;
    }

    AuthnRequestGenerator getAuthnRequestGenerator()
            throws SAML2AuthnRequestException {
        Boolean isHttps = Boolean.valueOf(getRequest().isSecure());
        return new AuthnRequestGenerator(getIssuer(), isHttps, samlBean);
    }

    LogoutRequestGenerator getLogoutRequestGenerator()
            throws SAML2AuthnRequestException {
        Boolean isHttps = Boolean.valueOf(getRequest().isSecure());
        return new LogoutRequestGenerator(getIssuer(), isHttps, samlBean);
    }

    String getIssuer() throws SAML2AuthnRequestException {
        String issuer = configurationService.getVOConfigurationSetting(
                ConfigurationKey.SSO_ISSUER_ID, "global").getValue();
        if (ADMStringUtils.isBlank(issuer)) {
            throw new SAML2AuthnRequestException(
                    "No issuer set in the configuration settings",
                    SAML2AuthnRequestException.ReasonEnum.MISSING_ISSUER);
        }
        return issuer;
    }

    Log4jLogger getLogger() {
        return LoggerFactory.getLogger(Saml2Ctrl.class);
    }


    boolean isOnMarketplace() {
        Object marketplaceId = getRequest().getAttribute(Constants.REQ_PARAM_MARKETPLACE_ID);
        return marketplaceId != null && ((String) marketplaceId).trim().length() > 0;
    }

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
    }

    protected HttpServletResponse getResponse() {
        return (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();
    }

    public String getSaml2PostUrl() {
        String url = this.getRequest().getRequestURL().toString();
        url = url.replaceAll(getRequest().getServletPath(),
                SAML_SP_REDIRECT_IFRAME);
        return url;
    }

    public UiDelegate getUiDelegate() {
        if (uiDelegate == null) {
            uiDelegate = new UiDelegate();
        }
        return uiDelegate;
    }

    public boolean isFromLogout() {
        return fromLogout;
    }

    public void setFromLogout(boolean fromLogout) {
        this.fromLogout = fromLogout;
    }

}
