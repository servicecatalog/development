/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: kowalczyka                                                      
 *                                                                              
 *  Creation Date: 18.05.2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.marketplace;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.JSFUtils;

@ManagedBean
@ViewScoped
public class ManageAccessCtrl {

    @ManagedProperty(value = "#{manageAccessModel}")
    private ManageAccessModel model;

    @EJB
    private MarketplaceService marketplaceService;

    @PostConstruct
    public void initialize() {
        initSelectableMarketplaces();
    }

    public void initSelectableMarketplaces() {

        List<VOMarketplace> marketplaces = marketplaceService
                .getMarketplacesOwned();

        List<SelectItem> selectableMarketplaces = new ArrayList<SelectItem>();

        for (VOMarketplace mp : marketplaces) {
            selectableMarketplaces
                    .add(new SelectItem(mp.getMarketplaceId(), String.format(
                            "%s (%s)", mp.getName(), mp.getMarketplaceId())));
        }

        model.setSelectableMarketplaces(selectableMarketplaces);
    }

    public void marketplaceChanged() {
        String marketplaceId = model.getSelectedMarketplaceId();

        if (marketplaceId != null) {
            try {
                VOMarketplace marketplace = marketplaceService
                        .getMarketplaceById(marketplaceId);
                model.setSelectedMarketplaceRestricted(
                        marketplace.isRestricted());
            } catch (ObjectNotFoundException e) {
                // TODO: handle non existing marketplace
            }
        } else {
            model.setSelectedMarketplaceId(null);
            model.setSelectedMarketplaceRestricted(false);
        }
    }

    public String save() {
        
        //TODO: saving logic
        
        JSFUtils.addMessage(null, FacesMessage.SEVERITY_INFO, BaseBean.INFO_MARKETPLACE_ACCESS_SAVED, null);
        return BaseBean.OUTCOME_SUCCESS;
    }

    public ManageAccessModel getModel() {
        return model;
    }

    public void setModel(ManageAccessModel model) {
        this.model = model;
    }

    public MarketplaceService getMarketplaceService() {
        return marketplaceService;
    }

    public void setMarketplaceService(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }
}
