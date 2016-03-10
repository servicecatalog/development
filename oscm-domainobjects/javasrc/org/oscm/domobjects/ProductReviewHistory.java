/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Creation Date: May 12, 2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 * History-Object of ProductReview, used for auditing. Will be automatically
 * created during persist, save or remove operations (if performed via
 * DataManager)
 * 
 * @author barzu
 */
@Entity
@NamedQuery(name = "ProductReviewHistory.findByObject", query = "select c from ProductReviewHistory c where c.objKey=:objKey order by objversion, modDate")
public class ProductReviewHistory extends
        DomainHistoryObject<ProductReviewData> {

    private static final long serialVersionUID = -142696515808286900L;

    private long platformUserObjKey;

    private long productFeedbackObjKey;

    public ProductReviewHistory() {
        dataContainer = new ProductReviewData();
    }

    public ProductReviewHistory(ProductReview pr) {
        super(pr);
        if (pr.getPlatformUser() != null) {
            setPlatformUserObjKey(pr.getPlatformUser().getKey());
        }
        if (pr.getProductFeedback() != null) {
            setPlatformUserObjKey(pr.getProductFeedback().getKey());
        }
    }

    public long getPlatformUserObjKey() {
        return platformUserObjKey;
    }

    public void setPlatformUserObjKey(long platformUserObjKey) {
        this.platformUserObjKey = platformUserObjKey;
    }

    public long getProductFeedbackObjKey() {
        return productFeedbackObjKey;
    }

    public void setProductFeedbackObjKey(long productFeedbackObjKey) {
        this.productFeedbackObjKey = productFeedbackObjKey;
    }
}
