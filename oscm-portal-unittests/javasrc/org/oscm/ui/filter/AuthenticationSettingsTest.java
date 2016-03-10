/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 16.05.2013                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import org.oscm.types.constants.Configuration;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.types.enumtypes.AuthenticationMode;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.vo.VOConfigurationSetting;

/**
 * @author stavreva
 * 
 */
public class AuthenticationSettingsTest {

    private static final String ISSUER = "OSCM";
    private static final String IDP = "http://idp.de:9080/openam/SSORedirect/metaAlias/idp";
    private static final String IDP_UPPERCASE = IDP.toUpperCase();
    private static final String IDP_CONTEXT_ROOT = "http://idp.de:9080/openam";
    private static final String IDP_HTTP_METHOD = "POST";
    private static final String KEYSTORE_PATH = "/openam/keystore.jks";
    private static final String KEYSTORE_PASSWORD = "changeit";
    private static final String BASE_URL = "http://www.example.de";

    private AuthenticationSettings authSettings;
    private ConfigurationService cfgMock;

    @Before
    public void setup() throws Exception {
        cfgMock = mock(ConfigurationService.class);
        doReturn(
                new VOConfigurationSetting(ConfigurationKey.BASE_URL,
                        Configuration.GLOBAL_CONTEXT, BASE_URL)).when(cfgMock)
                .getVOConfigurationSetting(ConfigurationKey.BASE_URL,
                        Configuration.GLOBAL_CONTEXT);
        doReturn(
                new VOConfigurationSetting(ConfigurationKey.SSO_ISSUER_ID,
                        Configuration.GLOBAL_CONTEXT, ISSUER)).when(cfgMock)
                .getVOConfigurationSetting(ConfigurationKey.SSO_ISSUER_ID,
                        Configuration.GLOBAL_CONTEXT);

        doReturn(
                new VOConfigurationSetting(ConfigurationKey.SSO_IDP_TRUSTSTORE,
                        Configuration.GLOBAL_CONTEXT, KEYSTORE_PATH)).when(
                cfgMock).getVOConfigurationSetting(
                ConfigurationKey.SSO_IDP_TRUSTSTORE,
                Configuration.GLOBAL_CONTEXT);

        doReturn(
                new VOConfigurationSetting(
                        ConfigurationKey.SSO_IDP_TRUSTSTORE_PASSWORD,
                        Configuration.GLOBAL_CONTEXT, KEYSTORE_PASSWORD)).when(
                cfgMock).getVOConfigurationSetting(
                ConfigurationKey.SSO_IDP_TRUSTSTORE_PASSWORD,
                Configuration.GLOBAL_CONTEXT);

        doReturn(
                new VOConfigurationSetting(
                        ConfigurationKey.SSO_IDP_AUTHENTICATION_REQUEST_HTTP_METHOD,
                        Configuration.GLOBAL_CONTEXT, IDP_HTTP_METHOD)).when(
                cfgMock).getVOConfigurationSetting(
                ConfigurationKey.SSO_IDP_AUTHENTICATION_REQUEST_HTTP_METHOD,
                Configuration.GLOBAL_CONTEXT);

    }

    private void givenMock(AuthenticationMode authMode, String idpUrl) {
        doReturn(
                new VOConfigurationSetting(ConfigurationKey.AUTH_MODE,
                        Configuration.GLOBAL_CONTEXT, authMode.name())).when(
                cfgMock).getVOConfigurationSetting(ConfigurationKey.AUTH_MODE,
                Configuration.GLOBAL_CONTEXT);
        doReturn(
                new VOConfigurationSetting(ConfigurationKey.SSO_IDP_URL,
                        Configuration.GLOBAL_CONTEXT, idpUrl)).when(cfgMock)
                .getVOConfigurationSetting(ConfigurationKey.SSO_IDP_URL,
                        Configuration.GLOBAL_CONTEXT);
        authSettings = new AuthenticationSettings(cfgMock);
    }

    @Test
    public void constructor() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        verify(cfgMock, times(1)).getVOConfigurationSetting(
                ConfigurationKey.AUTH_MODE, Configuration.GLOBAL_CONTEXT);
        verify(cfgMock, times(1)).getVOConfigurationSetting(
                ConfigurationKey.BASE_URL, Configuration.GLOBAL_CONTEXT);
        verify(cfgMock, times(1)).getVOConfigurationSetting(
                ConfigurationKey.SSO_IDP_URL, Configuration.GLOBAL_CONTEXT);
        verify(cfgMock, times(1)).getVOConfigurationSetting(
                ConfigurationKey.SSO_ISSUER_ID, Configuration.GLOBAL_CONTEXT);
        verify(cfgMock, times(1)).getVOConfigurationSetting(
                ConfigurationKey.SSO_IDP_TRUSTSTORE,
                Configuration.GLOBAL_CONTEXT);
        verify(cfgMock, times(1)).getVOConfigurationSetting(
                ConfigurationKey.SSO_IDP_TRUSTSTORE_PASSWORD,
                Configuration.GLOBAL_CONTEXT);
    }

    @Test
    public void isServiceProvider() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertTrue(authSettings.isServiceProvider());
    }

    @Test
    public void isIdentityProvider() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_IDP, IDP);

        // then
        assertTrue(authSettings.isIdentityProvider());
    }

    @Test
    public void isInternal() throws Exception {

        // given
        givenMock(AuthenticationMode.INTERNAL, IDP);

        // then
        assertTrue(authSettings.isInternal());
    }

    @Test
    public void isOpenIdRelyingParty() throws Exception {

        // given
        givenMock(AuthenticationMode.OPENID_RP, IDP);

        // then
        assertTrue(authSettings.isOpenIdRelyingParty());
    }

    @Test
    public void getIssuer() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(ISSUER, authSettings.getIssuer());
    }

    @Test
    public void getIdentityProviderURL() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(IDP, authSettings.getIdentityProviderURL());
    }

    @Test
    public void getIdentityProviderURLContextRoot() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(IDP_CONTEXT_ROOT,
                authSettings.getIdentityProviderURLContextRoot());
    }

    @Test
    public void getIdentityProviderURLContextRoot_Uppercase() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP_UPPERCASE);

        // then
        assertEquals(IDP_CONTEXT_ROOT,
                authSettings.getIdentityProviderURLContextRoot());
    }

    @Test
    public void getKeystorePath() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(KEYSTORE_PATH,
                authSettings.getIdentityProviderTruststorePath());
    }

    @Test
    public void getKeystorePassword() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(KEYSTORE_PASSWORD,
                authSettings.getIdentityProviderTruststorePassword());
    }

    @Test
    public void getRecipient() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(BASE_URL + "/", authSettings.getRecipient());
    }

    @Test
    public void getConfigurationSetting_null() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);
        doReturn(null).when(cfgMock).getVOConfigurationSetting(
                ConfigurationKey.LOG_LEVEL, Configuration.GLOBAL_CONTEXT);

        // then
        assertNull(authSettings.getConfigurationSetting(cfgMock,
                ConfigurationKey.LOG_LEVEL));
    }

    @Test
    public void getConfigurationSetting() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals("SAML_SP", authSettings.getConfigurationSetting(cfgMock,
                ConfigurationKey.AUTH_MODE));
    }

    @Test
    public void getContextRoot_null() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertNull(authSettings.getContextRoot(null));
    }

    @Test
    public void getContextRoot_empty() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertNull(authSettings.getContextRoot(""));
    }

    @Test
    public void getContextRoot_lessTokens() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertNull(authSettings.getContextRoot("http://www.idp.de/"));
    }

    @Test
    public void getContextRoot() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(IDP_CONTEXT_ROOT, authSettings.getContextRoot(IDP));
    }

    @Test
    public void getIdentityProviderHttpMethod() throws Exception {

        // given
        givenMock(AuthenticationMode.SAML_SP, IDP);

        // then
        assertEquals(IDP_HTTP_METHOD,
                authSettings.getIdentityProviderHttpMethod());
    }

}
