/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *       
 *  Creation Date: 2012-10-12                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.v1_0.data;

import java.io.Serializable;
import java.util.List;

/**
 * Provides information on the current status of an application instance with
 * respect to actions controlled by APP, optionally including a list of users
 * with their IDs as used in the application.
 */
public class InstanceStatusUsers extends InstanceStatus implements Serializable {

    private static final long serialVersionUID = 224174303180232503L;

    private List<ServiceUser> users;

    /**
     * Returns a list of platform users that are to be mapped to the given
     * application users in the future.
     * 
     * @return the list of users
     */
    public List<ServiceUser> getChangedUsers() {
        return users;
    }

    /**
     * Sets a list of platform users that are to be mapped to the given
     * application users in the future.
     * 
     * @param users
     *            the list of users
     */
    public void setChangedUsers(List<ServiceUser> users) {
        this.users = users;
    }

}
