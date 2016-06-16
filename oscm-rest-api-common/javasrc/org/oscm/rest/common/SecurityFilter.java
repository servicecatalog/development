/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 2, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.WebApplicationException;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * Request filter for checking the security context.
 * 
 * @author miethaner
 */
public class SecurityFilter implements ContainerRequestFilter {

    /**
     * Check if the connection is secure and the user authenticated.
     */
    @Override
    public ContainerRequest filter(ContainerRequest request)
            throws WebApplicationException {

        if (!request.isSecure()) {
            throw WebException.forbidden()
                    .message(CommonParams.ERROR_NOT_SECURE).build();
        }

        if (request.getUserPrincipal() == null) {
            throw WebException.forbidden()
                    .message(CommonParams.ERROR_NOT_AUTHENTICATED).build();
        }

        return request;
    }
}
