/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: May 25, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

import java.util.Date;

/**
 * @author miethaner
 *
 */
public interface TriggerProcessRest {

    public enum Status {
        APPROVED, FAILED, REJECTED, CANCELED, WAITING_FOR_APPROVEL
    }

    public String getResourceId();

    public Status getStatus();

    public String getComment();

    public Date getActivitionTime();

    public String getDefinitionId();

    public String getAuthorId();

    public UserRest getAuthor();

}
