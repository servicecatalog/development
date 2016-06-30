/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.external.exceptions;

/**
 * Custom bad data exception for REST API
 * 
 * @author miethaner
 */
public class BadDataException extends Exception {

    private static final long serialVersionUID = -1236420958680280L;

    public BadDataException(Throwable e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return getCause().getMessage();
    }
}
