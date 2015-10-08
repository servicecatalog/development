/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Author: Mike J&auml;ger                                                      
 *                                                                              
 *  Creation Date: 10.06.2009                                                      
 *                                                                              
 *  Completion Time: 17.06.2009                                             
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * History object of the {@link ProductReference}, used for auditing.
 * 
 * @author Mike J&auml;ger
 * 
 */
@Entity
@NamedQuery(name = "ProductReferenceHistory.findByObject", query = "select c from ProductReferenceHistory c where c.objKey=:objKey order by objversion")
public class ProductReferenceHistory extends
        DomainHistoryObjectWithEmptyDataContainer {

    private static final long serialVersionUID = 6339635897962794078L;

    /**
     * Reference to the source product (key only).
     */
    private long sourceProductTKey;

    /**
     * Reference to the target product (key only).
     */
    private long targetProductTKey;

    public ProductReferenceHistory() {

    }

    public ProductReferenceHistory(ProductReference prodReference) {
        super(prodReference);
        if (prodReference.getSourceProduct() != null) {
            sourceProductTKey = prodReference.getSourceProduct().getKey();
        }
        if (prodReference.getTargetProduct() != null) {
            targetProductTKey = prodReference.getTargetProduct().getKey();
        }
    }

    public long getSourceProductTKey() {
        return sourceProductTKey;
    }

    public long getTargetProductTKey() {
        return targetProductTKey;
    }

}
