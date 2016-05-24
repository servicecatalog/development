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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.oscm.internal.intf.MarketplaceService;
import org.oscm.internal.marketplace.POOrganization;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.common.UiDelegate;

@ManagedBean
@ViewScoped
public class ManageAccessCtrl {

    private UiDelegate ui;

    @ManagedProperty(value = "#{manageAccessModel}")
    private ManageAccessModel model;

    @EJB
    private MarketplaceService marketplaceService;

    public ManageAccessCtrl() {
        ui = new UiDelegate();
    }

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

        if (marketplaceId == null) {
            model.setSelectedMarketplaceId(null);
            model.setSelectedMarketplaceRestricted(false);
            return;
        }
        try {
            VOMarketplace marketplace = marketplaceService
                    .getMarketplaceById(marketplaceId);
            model.setSelectedMarketplaceRestricted(marketplace.isRestricted());
            populateOrganizations(marketplaceId);
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
            ui.handleException(e);
        }
    }

    public void accessChanged() {
        populateOrganizations(model.getSelectedMarketplaceId());
    }

    private void populateOrganizations(String marketplaceId) {
        List<POOrganization> organizations = new ArrayList<>();
        Map<Long, Boolean> accesses = new HashMap<>();
        for (VOOrganization voOrganization : marketplaceService
                .getAllOrganizations(marketplaceId)) {
            POOrganization poOrganization = toPOOrganization(voOrganization);
            organizations.add(poOrganization);
            accesses.put(new Long(poOrganization.getKey()),
                    new Boolean(poOrganization.isSelected()));
        }
        model.setOrganizations(organizations);
        model.setOrganizationsAccesses(accesses);
    }

    public String save() {
        try {
            if (model.isSelectedMarketplaceRestricted()) {
                prepareOrganizationsListsForUpdate();
                getMarketplaceService().closeMarketplace(
                        model.getSelectedMarketplaceId(),
                        model.getAuthorizedOrganizations(),
                        model.getUnauthorizedOrganizations());
            } else {
                getMarketplaceService()
                        .openMarketplace(model.getSelectedMarketplaceId());
            }
        } catch (SaaSApplicationException e) {
            e.printStackTrace();
            ui.handleException(e);
        }
        addMessage(BaseBean.INFO_MARKETPLACE_ACCESS_SAVED);
        return BaseBean.OUTCOME_SUCCESS;
    }

    private void prepareOrganizationsListsForUpdate() {
        model.getAuthorizedOrganizations().clear();
        model.getUnauthorizedOrganizations().clear();
        for (POOrganization poOrganization : model.getOrganizations()) {
            if (model.getOrganizationsAccesses().get(poOrganization.getKey())
                    && !poOrganization.isSelected()) {
                model.getUnauthorizedOrganizations()
                        .add(toVOOrganization(poOrganization));
                continue;
            }
            if (!model.getOrganizationsAccesses().get(poOrganization.getKey())
                    && poOrganization.isSelected()) {
                model.getAuthorizedOrganizations()
                        .add(toVOOrganization(poOrganization));
            }
        }
    }

    public void addMessage(final String messageText) {
        JSFUtils.addMessage(null, FacesMessage.SEVERITY_INFO, messageText, null);
    }

    private POOrganization toPOOrganization(VOOrganization voOrganization) {
        POOrganization poOrganization = new POOrganization(
                voOrganization.getName(), voOrganization.getOrganizationId());
        poOrganization.setKey(voOrganization.getKey());
        poOrganization
                .setSelected(voOrganization.isHasGrantedAccessToMarketplace());
        return poOrganization;
    }

    private VOOrganization toVOOrganization(POOrganization poOrganization) {
        VOOrganization voOrganization = new VOOrganization();
        voOrganization.setKey(poOrganization.getKey());
        voOrganization.setOrganizationId(poOrganization.getOrganizationId());
        return voOrganization;
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
