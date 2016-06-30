/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.external.exceptions;

/**
 * Custom not found exception for REST API
 * 
 * @author miethaner
 */
public class NotFoundException extends Exception {

    private static final long serialVersionUID = -6302787421731299387L;

    public NotFoundException(Throwable e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return getCause().getMessage();
    }
}
