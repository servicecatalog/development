/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-09-19                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v1_0.exceptions;

/**
 * Exception thrown when access to APP fails due to an authentication problem.
 */
public class AuthenticationException extends APPlatformException {

    private static final long serialVersionUID = -531945432402787865L;

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized.
     * 
     * @param message
     *            the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
