/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2016 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 20.01.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.vmware.exception;

/**
 * @author kulle
 *
 */
public class ValidationException extends Exception
        implements VmwareControllerException {

    private static final long serialVersionUID = -9004826998456595538L;

    public ValidationException(String msg) {
        super(msg);
    }

}
