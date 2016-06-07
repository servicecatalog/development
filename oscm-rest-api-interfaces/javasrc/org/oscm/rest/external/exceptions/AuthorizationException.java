/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.external.exceptions;

/**
 * @author miethaner
 *
 */
public class AuthorizationException extends Exception {

    private static final long serialVersionUID = -7476169729539473298L;

    public AuthorizationException(Throwable e) {
        super(e);
    }
}
