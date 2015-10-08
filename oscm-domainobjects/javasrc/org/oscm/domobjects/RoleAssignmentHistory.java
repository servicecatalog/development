/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: cheld                                                     
 *                                                                              
 *  Creation Date: 29.04.2011                                                      
 *                                                                              
 *  Completion Time: 29.04.2011                                                
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;


@Entity
@NamedQuery(name = "RoleAssignmentHistory.findByObject", query = "select c from RoleAssignmentHistory c where c.objKey=:objKey order by objversion")
public class RoleAssignmentHistory extends
        DomainHistoryObjectWithEmptyDataContainer {

    private static final long serialVersionUID = -4296384837043039948L;

    /**
     * Reference to the target user (key-only).
     */
    private long userObjKey;

    /**
     * Reference to the target user role (key-only).
     */
    private long userRoleObjKey;

    public RoleAssignmentHistory() {
    }

    public RoleAssignmentHistory(RoleAssignment assignment) {
        super(assignment);
        if (assignment.getUser() != null) {
            this.userObjKey = assignment.getUser().getKey();
        }
        if (assignment.getRole() != null) {
            this.userRoleObjKey = assignment.getRole().getKey();
        }
    }

    public long getUserObjKey() {
        return userObjKey;
    }

    public long getRoleObjKey() {
        return userRoleObjKey;
    }
}
