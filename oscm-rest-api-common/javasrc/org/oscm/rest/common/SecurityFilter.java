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
import javax.ws.rs.ext.Provider;

/**
 * Request filter for checking the security context.
 * 
 * @author miethaner
 */
@Provider
public class SecurityFilter implements ContainerRequestFilter {

    /**
     * Check if the connection is secure and the user authenticated.
     */
    @Override
    public void filter(ContainerRequestContext request)
            throws WebApplicationException {

        if (!request.getSecurityContext().isSecure()) {
            throw WebException.forbidden()
                    .message(CommonParams.ERROR_NOT_SECURE).build();
        }

        if (request.getSecurityContext().getUserPrincipal() == null) {
            throw WebException.forbidden()
                    .message(CommonParams.ERROR_NOT_AUTHENTICATED).build();
        }
    }
}
