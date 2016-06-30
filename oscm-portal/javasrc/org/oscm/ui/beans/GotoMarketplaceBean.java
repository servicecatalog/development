/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: Apr 17, 2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.oscm.internal.vo.VOMarketplace;

@ViewScoped
@ManagedBean(name = "gotoMarketplaceBean")
public class GotoMarketplaceBean extends BaseBean implements Serializable {

    private static final long serialVersionUID = 6745716919639233847L;

    private List<SelectItem> cachedMarketplaces;
    private String selectedMarketplace;

    public String getSelectedMarketplace() {
        return selectedMarketplace;
    }

    public void setSelectedMarketplace(String selectedMarketplace) {
        this.selectedMarketplace = selectedMarketplace;
    }

    /**
     * @return all marketplaces which are owned and to which the supplier can
     *         publish
     */
    public List<SelectItem> getMarketplaces() {
        if (cachedMarketplaces == null) {
            cachedMarketplaces = new ArrayList<SelectItem>();
            cachedMarketplaces = convertToUIModel(loadMarketplaces());
        }
        return cachedMarketplaces;
    }

    Set<VOMarketplace> loadMarketplaces() {

        Set<VOMarketplace> marketplaces = new HashSet<VOMarketplace>();
        List<VOMarketplace> restrictedMarketplaces = getMarketplaceService()
                .getRestrictedMarketplaces();

        if (isLoggedInAndMarketplaceOwner()) {
            List<VOMarketplace> marketplacesOwned = getMarketplaceService()
                    .getMarketplacesOwned();

            for (VOMarketplace marketplace : marketplacesOwned) {
                if (!marketplace.isRestricted()) {
                    marketplaces.add(marketplace);
                } else if (restrictedMarketplaces.contains(marketplace)) {
                    marketplaces.add(marketplace);
                }
            }
        }

        if (isLoggedInAndVendorManager()) {
            List<VOMarketplace> marketplacesForOrganization = getMarketplaceService()
                    .getMarketplacesForOrganization();

            for (VOMarketplace marketplace : marketplacesForOrganization) {
                if (!marketplace.isRestricted()) {
                    marketplaces.add(marketplace);
                } else if (restrictedMarketplaces.contains(marketplace)) {
                    marketplaces.add(marketplace);
                }
            }
        }

        return marketplaces;
    }

    private List<SelectItem> convertToUIModel(Set<VOMarketplace> marketplaces) {
        List<SelectItem> uiMarketplaces = new ArrayList<SelectItem>();
        for (VOMarketplace mp : marketplaces) {
            uiMarketplaces
                    .add(new SelectItem(mp.getMarketplaceId(), getLabel(mp)));
        }
        return uiMarketplaces;
    }

    private String getLabel(VOMarketplace marketplace) {
        return marketplace.getName() + "(" + marketplace.getMarketplaceId()
                + ")";
    }

    /**
     * updates the session's mid attribute and forwards to the selected
     * marketplace
     */
    public String gotoMarketplace() {
        setMarketplaceId(selectedMarketplace);
        return OUTCOME_SUCCESS;
    }

    /*
     * value change listener for marketplace chooser
     */
    public void processValueChange(ValueChangeEvent event) {
        selectedMarketplace = (String) event.getNewValue();
    }

    public boolean isButtonEnabled() {
        return getSelectedMarketplace() != null
                && getSelectedMarketplace().length() > 0;
    }

}
