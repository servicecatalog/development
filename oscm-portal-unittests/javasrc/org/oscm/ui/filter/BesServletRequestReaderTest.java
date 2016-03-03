/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 17.01.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

/**
 * @author weiser
 * 
 */
public class BesServletRequestReaderTest {

    private HttpServletRequest request;

    @Before
    public void setup() throws Exception {
        request = mock(HttpServletRequest.class);
        when(request.getParameter(anyString())).thenReturn(null);
    }

    @Test
    public void isRequestedToChangePassword_Null() throws Exception {
        assertFalse(BesServletRequestReader
                .isRequestedToChangePassword(request));
    }

    @Test
    public void isRequestedToChangePassword_Empty() throws Exception {
        when(request.getParameter(anyString())).thenReturn("");
        assertFalse(BesServletRequestReader
                .isRequestedToChangePassword(request));
    }

    @Test
    public void isRequestedToChangePassword_NewWhiteSpaces() throws Exception {
        when(request.getParameter(anyString())).thenReturn("oldPassword",
                "      ", "      ");
        assertTrue(BesServletRequestReader.isRequestedToChangePassword(request));
    }

    @Test
    public void isRequestedToChangePassword_OldWhiteSpaces() throws Exception {
        when(request.getParameter(anyString())).thenReturn("      ",
                "newPassword", "newPassword");
        assertTrue(BesServletRequestReader.isRequestedToChangePassword(request));
    }

    @Test
    public void isMarketplaceErrorPageRequest_Empty() {
        when(request.getServletPath()).thenReturn("");
        assertFalse(BesServletRequestReader
                .isMarketplaceErrorPageRequest(request));
    }

    @Test
    public void isMarketplaceErrorPageRequest_NotMarketplaceErrorPage() {
        when(request.getServletPath()).thenReturn("somePage");
        assertFalse(BesServletRequestReader
                .isMarketplaceErrorPageRequest(request));
    }

    @Test
    public void isMarketplaceErrorPageRequest_WithMarketplaceErrorPage() {
        when(request.getServletPath()).thenReturn("/marketplace/errorPage.jsf");
        assertTrue(BesServletRequestReader
                .isMarketplaceErrorPageRequest(request));
    }

    @Test
    public void isMarketplaceErrorPageRequest_MarketplaceErrorPageWithParameter() {
        when(request.getServletPath()).thenReturn(
                "/marketplace/errorPage.jsf?mId=111111");
        assertTrue(BesServletRequestReader
                .isMarketplaceErrorPageRequest(request));
    }

    /**
     * Checks if the given URL is the landing page of the marketplace
     */
    @Test
    public void isLandingPage() {

        // given landing page URL
        when(request.getServletPath()).thenReturn(
                "/marketplace/index.jsf?mId=1111");

        // then URL is recognized as landing page
        assertTrue(BesServletRequestReader.isLandingPage(request));
    }


    /**
     * Asserts that a non-landing page URL is recognized as a non-landing page
     */
    @Test
    public void isLandingPage_negative() {

        // given some URL
        when(request.getServletPath()).thenReturn(
                "/marketplace/SOME_OTHER_NON_LANDING_PAGE.jsf?mId=1111");

        // than URL is recognized as a non-landing page
        assertFalse(BesServletRequestReader.isLandingPage(request));
    }
}
