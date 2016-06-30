/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.external.exceptions;

/**
 * Custom data exception for REST API
 * 
 * @author miethaner
 */
public class DataException extends Exception {

    private static final long serialVersionUID = 8739180922201638068L;

    public DataException(Throwable e) {
        super(e);
    }

    @Override
    public String getMessage() {
        return getCause().getMessage();
    }
}
