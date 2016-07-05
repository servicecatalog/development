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
import org.oscm.rest.common.BasicAuthFilter;
import org.oscm.rest.common.CommonParams;

import com.sun.web.security.WebProgrammaticLoginImpl;

/**
 * Unit test for BasicAuthFilter
 * 
 * @author miethaner
 */
public class BasicAuthFilterTest {

    private static final String USER = "admin";
    private static final String PASSWORD = "admin";

    @Test
    public void testFilterNegative() {
        testFilter(null, 0);
    }

    private void testFilter(String header, int times) {

        HttpServletRequest rq = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse rs = Mockito.mock(HttpServletResponse.class);
        FilterChain chain = Mockito.mock(FilterChain.class);
        WebProgrammaticLoginImpl login = Mockito
                .mock(WebProgrammaticLoginImpl.class);

        Mockito.when(rq.getHeader(CommonParams.HEADER_AUTH)).thenReturn(header);

        BasicAuthFilter filter = new BasicAuthFilter();

        filter.setProgrammaticLogin(login);
        try {
            filter.doFilter(rq, rs, chain);
        } catch (IOException | ServletException e) {
            fail();
        }

        Mockito.verify(login, Mockito.times(times)).login(USER,
                PASSWORD.toCharArray(), CommonParams.REALM, rq, rs);
        try {
            Mockito.verify(chain).doFilter(rq, rs);
        } catch (IOException | ServletException e) {
        }
    }

}
