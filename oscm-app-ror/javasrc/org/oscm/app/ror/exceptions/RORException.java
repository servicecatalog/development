/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 2012-11-09                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.ror.exceptions;

import org.oscm.app.iaas.exceptions.IaasException;

public class RORException extends IaasException {

    private static final long serialVersionUID = -5782244667241682879L;

    private boolean isBusy = false;
    private boolean isStateAlreadyPresent = false;
    private boolean isIllegalState = false;

    public RORException(String message) {
        // do not use exception cause - it might not be serializable and will
        // result in problems when communication with APP
        super(message);
        if (message != null) {
            if (message.contains("[code:67210]")
                    || message.startsWith("VSYS10124")) {
                // [code:67210] signals that target resource is busy
                // VSYS10124 signals that target resource is busy
                isBusy = true;
            } else if (message.contains("VSYS10120")
                    || message.contains("VSYS10121")
                    || message.contains("VSYS10122")
                    || message.contains("VSYS10123")) {
                isStateAlreadyPresent = true;
                // this codes signal that the requested operation was not
                // executed because the desired outcome is already there
            } else if (message.contains("ILLEGAL_STATE")) {
                isIllegalState = true;
            }
        }
    }

    @Override
    public boolean isBusyMessage() {
        return isBusy;
    }

    public boolean isStateAlreadyPresent() {
        return isStateAlreadyPresent;
    }

    @Override
    public boolean isIllegalState() {
        return isIllegalState;
    }
}
