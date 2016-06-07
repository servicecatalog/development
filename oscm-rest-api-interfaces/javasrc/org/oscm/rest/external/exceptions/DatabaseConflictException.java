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
public class DatabaseConflictException extends Exception {

    private static final long serialVersionUID = -5429039129594844438L;

    public DatabaseConflictException(Throwable e) {
        super(e);
    }
}
