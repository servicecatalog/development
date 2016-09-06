/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 01.09.16 08:10
 *
 ******************************************************************************/

package org.oscm.internal.types.exception;

/**
 * Authored by dawidch
 */
public class NotExistentTenantException extends SaaSApplicationException {

    public NotExistentTenantException(Reason reason) {
        super(String.valueOf(reason));
    }

    public NotExistentTenantException(String message) {
        super(message);
    }

    public enum Reason {
        MISSING_TENANT_PARAM,
        TENANT_NOT_FOUND
    }
}
