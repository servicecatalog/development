/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: Jun 1, 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.rest.trigger.interfaces;

import java.util.UUID;

/**
 * @author miethaner
 *
 */
public interface UserRest {

    public UUID getId();

    public String getEmail();
}
