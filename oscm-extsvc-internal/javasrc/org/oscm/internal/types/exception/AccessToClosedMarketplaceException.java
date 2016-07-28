/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-05-27
 *
 *******************************************************************************/
package org.oscm.internal.types.exception;

/**
 * Created by BadziakP on 2016-05-27.
 */
public class AccessToClosedMarketplaceException extends SaaSApplicationException {

    /**
     * Constructs a new exception with <code>null</code> as its detail message.
     * The cause is not initialized.
     */
    public AccessToClosedMarketplaceException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message. The cause
     * is not initialized.
     *
     * @param message
     *            the detail message
     */
    public AccessToClosedMarketplaceException(String message) {
        super(message);
    }
}
