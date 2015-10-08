/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                              
 *  Creation Date: 26.01.2010                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * History object for the payment result domain object.
 * 
 * @author Mike J&auml;ger
 * 
 */
@Entity
@NamedQuery(name = "PaymentResultHistory.findByObject", query = "SELECT c FROM PaymentResultHistory c WHERE c.objKey=:objKey ORDER BY objVersion")
public class PaymentResultHistory extends
        DomainHistoryObject<PaymentResultData> {

    private static final long serialVersionUID = -321559673578498540L;

    @Column(nullable = false)
    private long billingResultObjKey;

    public PaymentResultHistory() {
        dataContainer = new PaymentResultData();
    }

    /**
     * Constructs PaymentResultHistory from a PaymentResult domain object
     * 
     * @param c
     *            - the payment result
     */
    public PaymentResultHistory(PaymentResult c) {
        super(c);
        if (c.getBillingResult() != null) {
            setBillingResultObjKey(c.getBillingResult().getKey());
        }
    }

    public long getBillingResultObjKey() {
        return billingResultObjKey;
    }

    public void setBillingResultObjKey(long billingResultObjKey) {
        this.billingResultObjKey = billingResultObjKey;
    }

}
