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
public class InternalException extends Exception {

    private static final long serialVersionUID = -1236420958680280L;

    public InternalException(Throwable e) {
        super(e);
    }
}
