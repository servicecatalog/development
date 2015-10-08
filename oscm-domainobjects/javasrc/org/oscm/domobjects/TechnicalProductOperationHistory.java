/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 16.08.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * @author weiser
 * 
 */
@Entity
@NamedQueries({ @NamedQuery(name = "TechnicalProductOperationHistory.findByObject", query = "select c from TechnicalProductOperationHistory c where c.objKey=:objKey order by objversion") })
public class TechnicalProductOperationHistory extends
        DomainHistoryObject<TechnicalProductOperationData> {

    private static final long serialVersionUID = -829143995586572099L;

    /**
     * Technical product key.
     */
    private long technicalProductObjKey;

    /**
     * Default constructor.
     */
    public TechnicalProductOperationHistory() {
        super();
        dataContainer = new TechnicalProductOperationData();
    }

    public TechnicalProductOperationHistory(
            TechnicalProductOperation productOperation) {
        super(productOperation);
        if (productOperation.getTechnicalProduct() != null) {
            setTechnicalProductObjKey(productOperation.getTechnicalProduct()
                    .getKey());
        }
    }

    public void setTechnicalProductObjKey(long technicalProductObjKey) {
        this.technicalProductObjKey = technicalProductObjKey;
    }

    public long getTechnicalProductObjKey() {
        return technicalProductObjKey;
    }

    public String getOperationId() {
        return dataContainer.getOperationId();
    }

    public String getActionUrl() {
        return dataContainer.getActionUrl();
    }

}
