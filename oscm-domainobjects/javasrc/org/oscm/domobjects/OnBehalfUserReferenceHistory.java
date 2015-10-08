/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: tokoda                                                    
 *                                                                              
 *  Creation Date: 26.05.2011                                                      
 *                                                                              
 *  Completion Time: 26.05.2011                                         
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * History-Object of OnBehalfUserReference, used for auditing. Will be
 * automatically created during persist, save or remove operations (if performed
 * via DataManager)
 * 
 * @author tokoda
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "OnBehalfUserReferenceHistory.findByObject", query = "select c from OnBehalfUserReferenceHistory c where c.objKey=:objKey order by objVersion") })
public class OnBehalfUserReferenceHistory extends
        DomainHistoryObject<OnBehalfUserReferenceData> {

    private static final long serialVersionUID = 2884498818924036805L;

    private long masterUserObjKey;

    private long slaveUserObjKey;

    public OnBehalfUserReferenceHistory() {
        dataContainer = new OnBehalfUserReferenceData();
    }

    /**
     * Constructs OnBehalfUserReferenceHistory from an OnBehalfUserReference
     * domain object
     * 
     * @param c
     *            The OnBehalfUserReferenceHistory
     */
    public OnBehalfUserReferenceHistory(OnBehalfUserReference c) {
        super(c);
        if (c.getMasterUser() != null) {
            setMasterUserObjKey(c.getMasterUser().getKey());
        }
        if (c.getSlaveUser() != null) {
            setSlaveUserObjKey(c.getSlaveUser().getKey());
        }
    }

    public long getMasterUserObjKey() {
        return masterUserObjKey;
    }

    public void setMasterUserObjKey(long masterUserObjKey) {
        this.masterUserObjKey = masterUserObjKey;
    }

    public long getSlaveUserObjKey() {
        return slaveUserObjKey;
    }

    public void setSlaveUserObjKey(long slaveUserObjKey) {
        this.slaveUserObjKey = slaveUserObjKey;
    }

}
