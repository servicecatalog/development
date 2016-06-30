/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 25, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

/**
 * Data interface for trigger definitions
 * 
 * @author miethaner
 */
public interface TriggerDefinitionRest {

    public enum TargetType {
        WEB_SERVICE, REST_SERVICE
    }

    public enum Action {
        SUBSCRIBE_TO_SERVICE, UNSUBSCRIBE_FROM_SERVICE, MODIFY_SUBSCRIPTION
    };

    /**
     * Gets the entity id
     * 
     * @return the entity id
     */
    public Long getId();

    /**
     * Gets the entity concurrency tag
     * 
     * @return the entity tag
     */
    public String getTag();

    /**
     * Gets the name and description of the definition
     * 
     * @return the description string
     */
    public String getDescription();

    /**
     * Gets the target service type of the trigger
     * 
     * @return the target service type
     */
    public String getServiceType();

    /**
     * Gets the target url of the trigger
     * 
     * @return the target url string
     */
    public String getTargetURL();

    /**
     * Returns true if the trigger is suspending
     * 
     * @return true if suspending
     */
    public Boolean isSuspending();

    /**
     * Gets the organization id of the owner
     * 
     * @return the organization id
     */
    public Long getOwnerId();

    /**
     * Gets the corresponding organization object
     * 
     * @return
     */
    public OrganizationRest getOwner();

    /**
     * Gets the trigger action for the definition
     * 
     * @return the trigger action
     */
    public String getAction();

}
