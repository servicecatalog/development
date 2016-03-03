/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 07.06.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.filter;

import java.util.StringTokenizer;

import org.oscm.types.constants.Configuration;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.types.enumtypes.AuthenticationMode;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.vo.VOConfigurationSetting;

/**
 * @author stavreva
 * 
 */
public class AuthenticationSettings {

    private final String authenticationMode;
    private final String issuer;
    private final String identityProviderHttpMethod;
    private final String identityProviderURL;
    private final String identityProviderURLContextRoot;
    private final String tuststorePath;
    private final String truststorePassword;
    private String recipient;

    public AuthenticationSettings(ConfigurationService cfgService) {

        recipient = getConfigurationSetting(cfgService,
                ConfigurationKey.BASE_URL);

        if (recipient == null || recipient.length() == 0) {
            recipient = getConfigurationSetting(cfgService,
                    ConfigurationKey.BASE_URL_HTTPS);
        }
        if (recipient != null && !recipient.endsWith("/")) {
            recipient += "/";
        }

        authenticationMode = getConfigurationSetting(cfgService,
                ConfigurationKey.AUTH_MODE);

        issuer = getConfigurationSetting(cfgService,
                ConfigurationKey.SSO_ISSUER_ID);

        identityProviderURL = getConfigurationSetting(cfgService,
                ConfigurationKey.SSO_IDP_URL);

        tuststorePath = getConfigurationSetting(cfgService,
                ConfigurationKey.SSO_IDP_TRUSTSTORE);

        truststorePassword = getConfigurationSetting(cfgService,
                ConfigurationKey.SSO_IDP_TRUSTSTORE_PASSWORD);

        identityProviderURLContextRoot = getContextRoot(identityProviderURL);

        identityProviderHttpMethod = getConfigurationSetting(cfgService,
                ConfigurationKey.SSO_IDP_AUTHENTICATION_REQUEST_HTTP_METHOD);

    }

    String getConfigurationSetting(ConfigurationService cfgService,
            ConfigurationKey key) {
        VOConfigurationSetting voConfSetting = cfgService
                .getVOConfigurationSetting(key, Configuration.GLOBAL_CONTEXT);
        String setting = null;
        if (voConfSetting != null) {
            setting = voConfSetting.getValue();
        }
        return setting;
    }

    /**
     * The URL must be set to lower case, because in the configuration settings,
     * it can be given with upper case and matching will not work.
     */
    String getContextRoot(String idpURL) {
        String contextRoot = null;
        if (idpURL != null && idpURL.length() != 0) {
            StringTokenizer t = new StringTokenizer(idpURL, "/");
            if (t.countTokens() >= 3) {
                contextRoot = t.nextToken().toLowerCase() + "//"
                        + t.nextToken().toLowerCase() + "/"
                        + t.nextToken().toLowerCase();
            }
        }
        return contextRoot;
    }

    public boolean isServiceProvider() {
        return AuthenticationMode.SAML_SP.name().equals(authenticationMode);
    }

    public boolean isIdentityProvider() {
        return AuthenticationMode.SAML_IDP.name().equals(authenticationMode);
    }

    public boolean isInternal() {
        return AuthenticationMode.INTERNAL.name().equals(authenticationMode);
    }

    public boolean isOpenIdRelyingParty() {
        return AuthenticationMode.OPENID_RP.name().equals(authenticationMode);
    }

    public String getIssuer() {
        return issuer;
    }

    public String getIdentityProviderURL() {
        return identityProviderURL;
    }

    public String getIdentityProviderURLContextRoot() {
        return identityProviderURLContextRoot;
    }

    public String getIdentityProviderTruststorePath() {
        return tuststorePath;
    }

    public String getIdentityProviderTruststorePassword() {
        return truststorePassword;
    }

    public String getIdentityProviderHttpMethod() {
        return identityProviderHttpMethod;
    }

    public String getRecipient() {
        return recipient;
    }

}
