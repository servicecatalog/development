/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Jun 06, 2016
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.types.exception;

import org.oscm.internal.types.exception.beans.SAMLSigningExceptionBean;

/**
 * @author grubskim
 * 
 */
public class SAMLSigningException extends SaaSApplicationException {

    private static final long serialVersionUID = -1093186892330080980L;

    private final SAMLSigningExceptionBean bean = new SAMLSigningExceptionBean();

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized.
     */
    public SAMLSigningException() {

    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public SAMLSigningException(String message) {
        super(message);
    }

}
