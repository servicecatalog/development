/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2016 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 22.01.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.business.statemachine.api;

import org.oscm.app.v1_0.exceptions.APPlatformException;

/**
 * @author kulle
 *
 */
public class StateMachineException extends APPlatformException {

    private static final long serialVersionUID = -5619220680463876008L;

    public StateMachineException(String msg) {
        super(msg);
    }

    public StateMachineException(String msg, Exception e) {
        super(msg, e);
    }

}
