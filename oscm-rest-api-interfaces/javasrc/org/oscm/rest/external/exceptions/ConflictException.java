/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.external.exceptions;

/**
 * Custom conflict exception for REST API
 * 
 * @author miethaner
 */
public class ConflictException extends Exception {

    private static final long serialVersionUID = -5429039129594844438L;

    public ConflictException(Throwable e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return getCause().getMessage();
    }
}
