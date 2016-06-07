/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 7, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.external.exceptions;

/**
 * @author miethaner
 *
 */
public class DatabaseErrorException extends Exception {

    private static final long serialVersionUID = 8739180922201638068L;

    public DatabaseErrorException(Throwable e) {
        super(e);
    }
}
