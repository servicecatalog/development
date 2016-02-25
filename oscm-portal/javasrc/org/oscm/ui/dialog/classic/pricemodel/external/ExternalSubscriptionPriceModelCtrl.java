/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 19 lut 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.pricemodel.external;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.oscm.billing.external.pricemodel.service.PriceModel;
import org.oscm.internal.pricemodel.external.ExternalPriceModelException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.types.exception.SubscriptionStateException;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOSubscriptionDetails;

/**
 * This class is used to handle actions related to the subscription external
 * price model.
 *
 * @author BadziakP
 *
 */
@ManagedBean
@ViewScoped
public class ExternalSubscriptionPriceModelCtrl extends ExternalPriceModelCtrl {

    @Override
    public void upload() throws SaaSApplicationException {

        VOSubscriptionDetails subscription = validateSubscription(getPriceModelBean()
                .getSelectedSubscription());

        if (subscription == null) {
            return;
        }

        try {
            PriceModel priceModel = getExternalPriceModelService()
                    .getExternalPriceModelForSubscription(subscription);
            if (priceModel == null) {
                throw new ExternalPriceModelException();
            }
            loadPriceModelContent(priceModel);
            addMessage(null, FacesMessage.SEVERITY_INFO,
                    INFO_EXTERNAL_PRICE_UPLOADED);
            getPriceModelBean().setExternalPriceModelUploaded(true);
            getPriceModelBean().setDirty(true);
        } catch (ExternalPriceModelException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    ERROR_EXTERNAL_PRICEMODEL_NOT_AVAILABLE);
        }
    }

    public void display() throws IOException, SaaSApplicationException {
        
        VOSubscriptionDetails subscription = validateSubscription(getPriceModelBean()
                .getSelectedSubscription());

        if (subscription == null) {
            return;
        }
        VOService selectedService = subscription.getSubscribedService();
        // if an external price model has not been just uploaded then show the
        // price model stored in the database.
        if (!getPriceModelBean().isExternalPriceModelUploaded()) {
            showPersistedPriceModel(selectedService);
        }
        super.display();
    }

    public VOSubscriptionDetails validateSubscription(VOSubscriptionDetails subscription)
            throws SaaSApplicationException {
        if (subscription == null || subscription.getPriceModel() == null) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    ERROR_EXTERNAL_PRICEMODEL_NOT_AVAILABLE);
            return null;
        }
        if (!subscription.getPriceModel().isExternal()) {
            return null;
        }
        try {
            return getPriceModelBean()
                    .validateSubscription(subscription.getSubscribedService());
        } catch (SaaSApplicationException e) {
            if (e instanceof SubscriptionStateException) {
                addMessage(null, FacesMessage.SEVERITY_ERROR,
                        ERROR_SUBSCRIPTION_NOT_ACCESSIBLE,
                        new String[] { subscription.getSubscriptionId() });
                getPriceModelBean().setDirty(false);
                return null;
            }
            throw e;
        }
    }

}
