/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                    
 *                                                                                                                                 
 *  Creation Date: 24.02.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.pricemodel.external;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.oscm.billing.external.pricemodel.service.PriceModel;
import org.oscm.internal.pricemodel.external.ExternalPriceModelException;
import org.oscm.internal.types.enumtypes.ServiceType;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOServiceDetails;

/**
 * @author iversen
 *
 */
@ManagedBean
@ViewScoped
public class ExternalCustomerPriceModelCtrl extends ExternalPriceModelCtrl {

    @PostConstruct
    public void initBean() {
        initPersistedPriceModel(ServiceType.CUSTOMER_TEMPLATE);
    }

    @Override
    public void upload() {

        if (!getPriceModelBean().isExternalServiceSelected()) {
            return;
        }

        VOServiceDetails service = getPriceModelBean().getSelectedService();
        VOOrganization customer = getPriceModelBean().getCustomer()
                .getVOOrganization();

        try {
            PriceModel priceModel = getExternalPriceModelService()
                    .getExternalPriceModelForCustomer(service, customer);

            if (priceModel == null) {
                throw new ExternalPriceModelException();
            }
            loadPriceModelContent(priceModel);
            getPriceModelBean().setDirty(true);
            addMessage(null, FacesMessage.SEVERITY_INFO,
                    INFO_EXTERNAL_PRICE_UPLOADED);
        } catch (ExternalPriceModelException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    ERROR_EXTERNAL_PRICEMODEL_NOT_AVAILABLE);
        }
    }

    public void reloadPriceModel() {
        reloadPriceModel(ServiceType.CUSTOMER_TEMPLATE);
    }

}
