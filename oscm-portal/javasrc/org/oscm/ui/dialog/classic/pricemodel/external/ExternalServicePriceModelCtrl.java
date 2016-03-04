/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2015 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 24.02.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.pricemodel.external;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.oscm.billing.external.pricemodel.service.PriceModel;
import org.oscm.internal.pricemodel.external.ExternalPriceModelException;
import org.oscm.internal.vo.VOServiceDetails;

/**
 * @author iversen
 *
 */
@ManagedBean
@ViewScoped
public class ExternalServicePriceModelCtrl extends ExternalPriceModelCtrl {

    @Override
    public void upload() {
        if (!getPriceModelBean().isExternalServiceSelected()) {
            return;
        }

        VOServiceDetails service = getPriceModelBean().getSelectedService();

        try {
            PriceModel priceModel = getExternalPriceModelService()
                    .getExternalPriceModelForService(service);
            loadPriceModelContent(priceModel);

            getPriceModelBean().setDirty(true);
            addMessage(null, FacesMessage.SEVERITY_INFO,
                    INFO_EXTERNAL_PRICE_UPLOADED);
        } catch (ExternalPriceModelException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    ERROR_EXTERNAL_PRICEMODEL_NOT_AVAILABLE);
        }
    }
}
