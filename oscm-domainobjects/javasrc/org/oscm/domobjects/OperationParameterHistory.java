/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 22.01.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.oscm.types.enumtypes.OperationParameterType;

/**
 * @author weiser
 * 
 */
@NamedQueries({ @NamedQuery(name = "OperationParameterHistory.findByObject", query = "select oph from OperationParameterHistory oph where oph.objKey=:objKey order by objversion") })
@Entity
public class OperationParameterHistory extends
        DomainHistoryObject<OperationParameterData> {

    private static final long serialVersionUID = -4106425427241808219L;

    @Column(nullable = false)
    private long technicalProductOperationObjKey;

    public OperationParameterHistory() {
        dataContainer = new OperationParameterData();
    }

    public OperationParameterHistory(OperationParameter op) {
        super(op);
        if (op.getTechnicalProductOperation() != null) {
            setTechnicalProductOperationObjKey(op
                    .getTechnicalProductOperation().getKey());
        }
    }

    public long getTechnicalProductOperationObjKey() {
        return technicalProductOperationObjKey;
    }

    public void setTechnicalProductOperationObjKey(
            long technicalProductOperationObjKey) {
        this.technicalProductOperationObjKey = technicalProductOperationObjKey;
    }

    public String getId() {
        return dataContainer.getId();
    }

    public OperationParameterType getType() {
        return dataContainer.getType();
    }

    public boolean isMandatory() {
        return dataContainer.isMandatory();
    }
}
