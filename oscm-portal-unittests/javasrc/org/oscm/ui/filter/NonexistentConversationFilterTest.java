/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 25.11.15 10:33
 *
 *******************************************************************************/

package org.oscm.ui.filter;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.weld.context.NonexistentConversationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by ChojnackiD on 2015-11-25.
 */
@RunWith(value = MockitoJUnitRunner.class)
public class NonexistentConversationFilterTest {
    NonexistentConversationFilter classUnderTests;

    @Before
    public void setUp() throws Exception {
        classUnderTests = spy(new NonexistentConversationFilter());
    }

    @Test
    public void testDoFilter() throws Exception {
        // given
        FilterChain chain = mock(FilterChain.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(
                "/marketplace/subscriptions/upgrade/confirmUpgrade.jsf");

        ServletException exc = new ServletException("msg",
                mock(NonexistentConversationException.class));
        doThrow(exc).when(chain).doFilter(req, res);
        // when
        try {
            classUnderTests.doFilter(req, res, chain);
        } catch (Exception e) {
            fail();
        }

        // then
        verify(classUnderTests).sendRedirect(req, res,
                "/marketplace/account/subscriptionDetails.jsf");
    }

    @Test(expected = ServletException.class)
    public void testDoFilterThrowExc() throws Exception {
        // given
        FilterChain chain = mock(FilterChain.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn(
                "/marketplace/subscriptions/upgrade/confirmUpgrade.jsf");

        ServletException exc = new ServletException("msg",
                mock(IOException.class));
        doThrow(exc).when(chain).doFilter(req, res);

        classUnderTests.doFilter(req, res, chain);

        fail();
    }

}
