/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 10, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

/**
 * Data interface for trigger processes
 * 
 * @author miethaner
 */
public interface TriggerProcessRest {

    /**
     * Gets the entity id
     * 
     * @return the entity id
     */
    public Long getId();

    /**
     * Gets the comment for the process
     * 
     * @return the comment
     */
    public String getComment();

}
