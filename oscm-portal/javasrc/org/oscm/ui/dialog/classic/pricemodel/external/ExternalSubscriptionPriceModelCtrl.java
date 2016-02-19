/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 19 lut 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.pricemodel.external;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.oscm.billing.external.pricemodel.service.PriceModel;
import org.oscm.internal.pricemodel.external.ExternalPriceModelException;
import org.oscm.internal.vo.VOSubscriptionDetails;

/**
 * This class is used to handle actions related to the subscription external price model.
 *
 * @author BadziakP
 *
 */
@ManagedBean
@ViewScoped
public class ExternalSubscriptionPriceModelCtrl extends ExternalPriceModelCtrl {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.oscm.ui.dialog.classic.pricemodel.external.ExternalPriceModelCtrl#
     * upload()
     */
    @Override
    public void upload() {

        VOSubscriptionDetails subscription = getPriceModelBean()
                .getSelectedSubscription();
        if (subscription.getPriceModel() == null) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    ERROR_EXTERNAL_PRICEMODEL_NOT_AVAILABLE);
            return;
        }
        if (!subscription.getPriceModel().isExternal()) {
            return;
        }
        try {
            PriceModel priceModel = getExternalPriceModelService()
                    .getExternalPriceModelForSubscription(subscription);
            loadPriceModelContent(priceModel);

            addMessage(null, FacesMessage.SEVERITY_INFO,
                    INFO_EXTERNAL_PRICE_UPLOADED);
        } catch (ExternalPriceModelException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    ERROR_EXTERNAL_PRICEMODEL_NOT_AVAILABLE);
        }
    }

}
