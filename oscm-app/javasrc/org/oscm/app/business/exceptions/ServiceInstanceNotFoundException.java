/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.app.business.exceptions;

public class ServiceInstanceNotFoundException extends Exception {

    private static final long serialVersionUID = 142484503803299487L;

    public ServiceInstanceNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
