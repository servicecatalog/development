/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: brandstetter                                                      
 *                                                                              
 *  Creation Date: 07.10.2011                                                      
 *                                                                              
 *  Completion Time: 07.10.2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * @author brandstetter
 * 
 */
@Entity
@NamedQuery(name = "ProductToPaymentTypeHistory.findByObject", query = "SELECT c FROM ProductToPaymentTypeHistory c WHERE c.objKey=:objKey ORDER BY objversion")
public class ProductToPaymentTypeHistory extends
        DomainHistoryObject<EmptyDataContainer> {

    private static final long serialVersionUID = 7126118192245455555L;

    private long productObjKey;

    private long paymentTypeObjKey;

    // ------------------------------------------------------------------------
    public ProductToPaymentTypeHistory() {
        dataContainer = new EmptyDataContainer();
    }

    // ------------------------------------------------------------------------
    public ProductToPaymentTypeHistory(ProductToPaymentType x) {
        super(x);
        if (x.getProduct() != null) {
            setProductObjKey(x.getProduct().getKey());
        }
        if (x.getPaymentType() != null) {
            setPaymentTypeObjKey(x.getPaymentType().getKey());
        }
    }

    // ------------------------------------------------------------------------
    public long getProductObjKey() {
        return productObjKey;
    }

    public void setProductObjKey(long productObjKey) {
        this.productObjKey = productObjKey;
    }

    // ------------------------------------------------------------------------
    public long getPaymentTypeObjKey() {
        return paymentTypeObjKey;
    }

    public void setPaymentTypeObjKey(long paymentTypeObjKey) {
        this.paymentTypeObjKey = paymentTypeObjKey;
    }
}
