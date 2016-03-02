/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) 2014 FUJITSU Limited - ALL RIGHTS RESERVED.                  
 *                                                                                                                                 
 *  Creation Date: 18.12.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.pricemodel.external;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;

import org.oscm.billing.external.pricemodel.service.PriceModel;
import org.oscm.billing.external.pricemodel.service.PriceModelContent;
import org.oscm.internal.pricemodel.external.ExternalPriceModelException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.vo.VOPriceModel;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceDetails;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.PriceModelBean;
import org.oscm.ui.beans.SessionBean;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.common.UiDelegate;

/**
 * @author stavreva
 * 
 */
public abstract class ExternalPriceModelCtrl extends BaseBean {

    UiDelegate ui = new UiDelegate();

    @ManagedProperty(value = "#{externalPriceModelModel}")
    private ExternalPriceModelModel model;

    private PriceModelBean priceModelBean;

    private ApplicationBean appBean;

    private SessionBean sessionBean;

    final String PRICEMODELBEAN = "priceModelBean";
    final String APPBEAN = "appBean";
    final String SESSION_BEAN = "sessionBean";

    public ExternalPriceModelModel getModel() {
        return model;
    }

    public void setModel(ExternalPriceModelModel model) {
        this.model = model;
    }

    public void setAppBean(ApplicationBean appBean) {
        this.appBean = appBean;
    }

    public ApplicationBean getAppBean() {
        return appBean;
    }

    public PriceModelBean getPriceModelBean() {
        return priceModelBean;
    }

    public void setPriceModelBean(PriceModelBean priceModelBean) {
        this.priceModelBean = priceModelBean;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public abstract void upload() throws SaaSApplicationException;

    @PostConstruct
    public void initialize() {

        initBeans();

        if (getPriceModelBean().isExternalServiceSelected()) {

            VOServiceDetails selectedService = getPriceModelBean()
                    .getSelectedService();

            showPersistedPriceModel(selectedService);
        }
    }

    public void loadPriceModelContent(PriceModel priceModel)
            throws ExternalPriceModelException {
        Locale locale = JSFUtils.getViewLocale();

        PriceModelContent priceModelContent = getLocalizedPriceModelContent(
                locale, priceModel);

        model.setSelectedPriceModelContent(priceModelContent);
        model.setSelectedPriceModel(priceModel);
        model.setSelectedPriceModelId(priceModel.getId().toString());
    }

    protected void initBeans() {
        if (getPriceModelBean() == null) {
            this.setPriceModelBean(
                    (PriceModelBean) ui.findBean(PRICEMODELBEAN));
        }
        if (getAppBean() == null) {
            this.setAppBean((ApplicationBean) ui.findBean(APPBEAN));
        }
        if (getSessionBean() == null) {
            this.setSessionBean((SessionBean) ui.findBean(SESSION_BEAN));
        }
    }


    public void showPersistedPriceModel(VOService selectedService) {
        
        VOPriceModel priceModel = selectedService.getPriceModel();

        if (priceModel != null && priceModel.isExternal()) {
            PriceModelContent selectedPriceModelContent = new PriceModelContent(
                    priceModel.getPresentationDataType(),
                    priceModel.getPresentation());
            PriceModel selectedPriceModel = new PriceModel(priceModel.getUuid());
            getModel().setSelectedPriceModel(selectedPriceModel);
            getModel().setSelectedPriceModelContent(selectedPriceModelContent);
            getModel().setSelectedPriceModelId(priceModel.getUuid().toString());
        } else {
            getModel().setSelectedPriceModel(null);
            getModel().setSelectedPriceModelContent(null);
            getModel().setSelectedPriceModelId("");
        }
    }

    private PriceModelContent getLocalizedPriceModelContent(Locale locale,
            PriceModel externalPriceModel) {
        PriceModelContent priceModelContent = externalPriceModel.get(locale);
        if (priceModelContent == null) {
            priceModelContent = externalPriceModel
                    .get(getAppBean().getDefaultLocale());
        }
        return priceModelContent;
    }

    public void display() throws IOException, SaaSApplicationException {

        PriceModelContent priceModelContent = getModel()
                .getSelectedPriceModelContent();

        if (priceModelContent == null) {
            addMessage(null, FacesMessage.SEVERITY_ERROR,
                    ERROR_EXTERNAL_PRICEMODEL_NOT_AVAILABLE);
            return;

        }
        ExternalPriceModelDisplayHandler displayHandler = new ExternalPriceModelDisplayHandler();
        displayHandler.setContent(priceModelContent.getContent());
        displayHandler.setContentType(priceModelContent.getContentType());
        displayHandler.setFilename(priceModelContent.getFilename());
        displayHandler.display();
        return;
    }

}