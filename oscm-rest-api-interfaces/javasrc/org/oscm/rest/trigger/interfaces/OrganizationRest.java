/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 1, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

/**
 * Data interface for organizations
 * 
 * @author miethaner
 */
public interface OrganizationRest {

    /**
     * Gets the entity id
     * 
     * @return the entity id
     */
    public Long getId();

    /**
     * Gets the name of the organization
     * 
     * @return the description string
     */
    public String getName();

}
