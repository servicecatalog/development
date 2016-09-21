/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 23, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.AuthenticationMode;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOUser;
import org.oscm.rest.common.BasicAuthFilter;
import org.oscm.rest.common.CommonParams;
import org.oscm.types.constants.Configuration;

import com.sun.enterprise.security.auth.login.common.LoginException;
import com.sun.web.security.WebProgrammaticLoginImpl;

/**
 * Unit test for BasicAuthFilter
 * 
 * @author miethaner
 */
public class BasicAuthFilterTest {

    private static final Long USER_KEY = new Long(1L);
    private static final String PASSWORD = "admin";
    private static final String HEADER = "Basic YWRtaW46YWRtaW4=";

    @Test
    public void testFilterPositive() throws Exception {
        testFilter(HEADER, AuthenticationMode.INTERNAL.name(), USER_KEY, true,
                1, 1);
    }

    @Test
    public void testFilterNegativeHeader() throws Exception {
        testFilter(null, AuthenticationMode.INTERNAL.name(), USER_KEY, false,
                0, 1);
    }

    @Test
    public void testFilterNegativeAuthMode() throws Exception {
        testFilter(HEADER, AuthenticationMode.SAML_SP.name(), USER_KEY, false,
                0, 0);
    }

    @Test
    public void testFilterNegativeLogin() throws Exception {
        testFilter(HEADER, AuthenticationMode.INTERNAL.name(), USER_KEY, false,
                1, 0);
    }

    @Test
    public void testFilterNegativeUser() throws Exception {
        testFilter(HEADER, AuthenticationMode.INTERNAL.name(), null, false, 0,
                0);
    }

    private void testFilter(String header, String authMode, Long result,
            boolean success, int loginTimes, int chainTimes) throws Exception {

        HttpServletRequest rq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse rs = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);
        Mockito.doThrow(new MockitoException("")).when(rs)
                .sendError(Mockito.anyInt(), Mockito.anyString());

        ConfigurationService config = Mockito.mock(ConfigurationService.class);
        VOConfigurationSetting setting = new VOConfigurationSetting();
        setting.setValue(authMode);
        Mockito.when(
                config.getVOConfigurationSetting(ConfigurationKey.AUTH_MODE,
                        Configuration.GLOBAL_CONTEXT)).thenReturn(setting);

        IdentityService identityService = Mockito.mock(IdentityService.class);
        VOUser user = new VOUser();

        if (result != null) {
            user.setKey(result.longValue());
            Mockito.doReturn(user).when(identityService)
                    .getUser(Mockito.any(VOUser.class));
        } else {
            Mockito.doThrow(new ObjectNotFoundException())
                    .when(identityService).getUser(Mockito.any(VOUser.class));
        }

        WebProgrammaticLoginImpl login = Mockito
                .mock(WebProgrammaticLoginImpl.class);

        if (!success) {
            Mockito.doThrow(new LoginException(true))
                    .when(login)
                    .login(USER_KEY.toString(), PASSWORD.toCharArray(),
                            CommonParams.REALM, rq, rs);
        }

        Mockito.when(rq.getHeader(CommonParams.HEADER_AUTH)).thenReturn(header);

        BasicAuthFilter filter = new BasicAuthFilter();

        filter.setProgrammaticLogin(login);
        filter.setIdentityService(identityService);
        filter.setConfigurationService(config);
        try {
            filter.doFilter(rq, rs, chain);
        } catch (IOException | ServletException e) {
            fail();
        } catch (MockitoException e) {
        }

        Mockito.verify(login, Mockito.times(loginTimes)).login(
                USER_KEY.toString(), PASSWORD.toCharArray(),
                CommonParams.REALM, rq, rs);
        Mockito.verify(chain, Mockito.times(chainTimes)).doFilter(rq, rs);
    }

}
