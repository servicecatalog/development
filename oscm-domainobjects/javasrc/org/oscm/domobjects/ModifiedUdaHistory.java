/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2013-12-5                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import org.oscm.domobjects.enums.ModifiedEntityType;

/**
 * History-Object of ModifiedUda, used for auditing. Will be automatically
 * created during persist, save or remove operations (if performed via
 * DataManager)
 * 
 * @author Zhou
 */
@Entity
@NamedQuery(name = "ModifiedUdaHistory.findByObject", query = "select c from ModifiedUdaHistory c where c.objKey=:objKey order by objversion")
public class ModifiedUdaHistory extends DomainHistoryObject<ModifiedUdaData> {

    private static final long serialVersionUID = -9210916078104787863L;

    public ModifiedUdaHistory() {
        dataContainer = new ModifiedUdaData();
    }

    public ModifiedUdaHistory(ModifiedUda c) {
        super(c);
        setTargetObjectKey(c.getTargetObjectKey());
        setTargetObjectType(c.getTargetObjectType());
        setValue(c.getValue());
        setSubscriptionKey(c.getSubscriptionKey());
    }

    public long getTargetObjectKey() {
        return dataContainer.getTargetObjectKey();
    }

    public void setTargetObjectKey(long objectKey) {
        dataContainer.setTargetObjectKey(objectKey);
    }

    public String getValue() {
        return dataContainer.getValue();
    }

    public void setValue(String value) {
        dataContainer.setValue(value);
    }

    public ModifiedEntityType getTargetObjectType() {
        return dataContainer.getTargetObjectType();
    }

    public void setTargetObjectType(ModifiedEntityType objectType) {
        dataContainer.setTargetObjectType(objectType);
    }

    public long getSubscriptionKey() {
        return dataContainer.getSubscriptionKey();
    }

    public void setSubscriptionKey(long subscriptionKey) {
        dataContainer.setSubscriptionKey(subscriptionKey);
    }

}
