/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: Jun 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import org.junit.Test;

/**
 * @author miethaner
 *
 */
public class SecurityFilterTest {

    //TODO glassfish upgrade
    @SuppressWarnings("boxing")
    @Test
    public void testPositive() {

        /*ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(request.isSecure()).thenReturn(Boolean.TRUE);
        Mockito.when(request.getUserPrincipal()).thenReturn(principal);

        SecurityFilter filter = new SecurityFilter();
        filter.filter(request);*/
    }

    /*
    @SuppressWarnings("boxing")
    @Test(expected = WebApplicationException.class)
    public void testSecureConnectionNegative() throws Exception {

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(request.isSecure()).thenReturn(Boolean.FALSE);
        Mockito.when(request.getUserPrincipal()).thenReturn(principal);

        SecurityFilter filter = new SecurityFilter();
        filter.filter(request);
    }

    @SuppressWarnings("boxing")
    @Test(expected = WebApplicationException.class)
    public void testUserNegative() throws Exception {

        ContainerRequest request = Mockito.mock(ContainerRequest.class);

        Mockito.when(request.isSecure()).thenReturn(Boolean.TRUE);
        Mockito.when(request.getUserPrincipal()).thenReturn(null);

        SecurityFilter filter = new SecurityFilter();
        filter.filter(request);
    }*/

}
