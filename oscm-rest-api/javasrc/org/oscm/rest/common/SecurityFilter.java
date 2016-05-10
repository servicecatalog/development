/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 2, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Request filter for checking the security context.
 * 
 * @author miethaner
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    @Context
    SecurityContext securityContext;

    /**
     * Check if connection is secure and user authenticated.
     */
    @Override
    public ContainerRequest filter(ContainerRequest request)
            throws WebApplicationException {

        if (!securityContext.isSecure()) {
            throw WebException.forbidden().build(); // TODO: add more info
        }

        if (securityContext.getUserPrincipal() == null) {
            throw WebException.forbidden().build(); // TODO: add more info
        }

        return request;
    }

}
