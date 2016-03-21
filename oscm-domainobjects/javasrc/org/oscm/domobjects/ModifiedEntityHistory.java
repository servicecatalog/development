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
 * History-Object of ModifiedEntity, used for auditing. Will be automatically
 * created during persist, save or remove operations (if performed via
 * DataManager)
 * 
 * @author Qiu
 */
@Entity
@NamedQuery(name = "ModifiedEntityHistory.findByObject", query = "select c from ModifiedEntityHistory c where c.objKey=:objKey order by objversion")
public class ModifiedEntityHistory extends
        DomainHistoryObject<ModifiedEntityData> {

    private static final long serialVersionUID = 2359726754581238567L;

    public ModifiedEntityHistory() {
        dataContainer = new ModifiedEntityData();
    }

    public ModifiedEntityHistory(ModifiedEntity c) {
        super(c);
        setTargetObjectKey(c.getTargetObjectKey());
        setTargetObjectType(c.getTargetObjectType());
        setValue(c.getValue());
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

}
