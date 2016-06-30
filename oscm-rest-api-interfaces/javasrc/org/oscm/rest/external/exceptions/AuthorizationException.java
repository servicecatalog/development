/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.external.exceptions;

/**
 * Custom authorization exception for REST API
 * 
 * @author miethaner
 */
public class AuthorizationException extends Exception {

    private static final long serialVersionUID = -7476169729539473298L;

    public AuthorizationException(Throwable e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return getCause().getMessage();
    }
}
