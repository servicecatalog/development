/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2013-12-5                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.oscm.domobjects.annotations.BusinessKey;
import org.oscm.domobjects.enums.ModifiedEntityType;

/**
 * ModifiedUda stores uda value which has been asynchronous updated but not
 * complete.
 * 
 * @author Zhou
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "ModifiedUda.findByBusinessKey", query = "SELECT m FROM ModifiedUda m WHERE m.dataContainer.targetObjectKey = :targetObjectKey AND m.dataContainer.targetObjectType = :targetObjectType AND m.dataContainer.subscriptionKey = :subscriptionKey"),
        @NamedQuery(name = "ModifiedUda.deleteBySubscription", query = "DELETE FROM ModifiedUda m WHERE m.dataContainer.subscriptionKey = :subscriptionKey") })
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "targetObjectKey",
        "targetObjectType", "subscriptionKey" }))
@BusinessKey(attributes = { "targetObjectKey", "targetObjectType",
        "subscriptionKey" })
public class ModifiedUda extends DomainObjectWithVersioning<ModifiedUdaData> {

    private static final long serialVersionUID = -983947573071480232L;

    public ModifiedUda() {
        super();
        dataContainer = new ModifiedUdaData();
    }

    public ModifiedUda(long targetObjectKey,
            ModifiedEntityType targetObjectType, long subscriptionKey) {
        super();
        setTargetObjectKey(targetObjectKey);
        setTargetObjectType(targetObjectType);
        setSubscriptionKey(subscriptionKey);
    }

    public long getTargetObjectKey() {
        return dataContainer.getTargetObjectKey();
    }

    public void setTargetObjectKey(long targetObjectKey) {
        dataContainer.setTargetObjectKey(targetObjectKey);
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

    public void setTargetObjectType(ModifiedEntityType targetObjectType) {
        dataContainer.setTargetObjectType(targetObjectType);
    }

    public long getSubscriptionKey() {
        return dataContainer.getSubscriptionKey();
    }

    public void setSubscriptionKey(long subscriptionKey) {
        dataContainer.setSubscriptionKey(subscriptionKey);
    }

}
