/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: May 2, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.common;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import java.io.IOException;

/**
 * Request filter for checking the security context.
 * 
 * @author miethaner
 */
public class SecurityFilter implements ContainerRequestFilter {

    /**
     * Check if the connection is secure and the user authenticated.
     */
    //TODO glassfish upgrade
    /*
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
    }*/

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

    }
}
