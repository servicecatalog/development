/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.app.business.exceptions;

public class ServiceInstanceInProcessingException extends Exception {

    private static final long serialVersionUID = -2891365531402403187L;

    public ServiceInstanceInProcessingException(String message, Object... args) {
        super(String.format(message, args));
    }
}
