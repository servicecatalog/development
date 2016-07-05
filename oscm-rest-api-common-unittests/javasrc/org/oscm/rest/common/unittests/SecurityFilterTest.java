/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 9, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common.unittests;

import java.security.Principal;

import javax.ws.rs.WebApplicationException;

import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.rest.common.SecurityFilter;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * @author miethaner
 *
 */
public class SecurityFilterTest {

    @SuppressWarnings("boxing")
    @Test
    public void testPositive() {

        ContainerRequest request = Mockito.mock(ContainerRequest.class);
        Principal principal = Mockito.mock(Principal.class);

        Mockito.when(request.isSecure()).thenReturn(Boolean.TRUE);
        Mockito.when(request.getUserPrincipal()).thenReturn(principal);

        SecurityFilter filter = new SecurityFilter();
        filter.filter(request);
    }

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
    }

}
