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

import org.oscm.internal.marketplace.POOrganization;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean
@ViewScoped
public class ManageAccessModel {

    private List<SelectItem> selectableMarketplaces;
    
    private String selectedMarketplaceId;
    
    private boolean selectedMarketplaceRestricted;

    private List<POOrganization> organizations;

    private boolean allOrganizationsSelected;

    public List<SelectItem> getSelectableMarketplaces() {
        return selectableMarketplaces;
    }

    public void setSelectableMarketplaces(List<SelectItem> selectableMarketplaces) {
        this.selectableMarketplaces = selectableMarketplaces;
    }

    public String getSelectedMarketplaceId() {
        return selectedMarketplaceId;
    }

    public void setSelectedMarketplaceId(String selectedMarketplaceId) {
        this.selectedMarketplaceId = selectedMarketplaceId;
    }

    public boolean isSelectedMarketplaceRestricted() {
        return selectedMarketplaceRestricted;
    }

    public void setSelectedMarketplaceRestricted(
            boolean selectedMarketplaceRestricted) {
        this.selectedMarketplaceRestricted = selectedMarketplaceRestricted;
    }

    public List<POOrganization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<POOrganization> organizations) {
        this.organizations = organizations;
    }

    public boolean isAllOrganizationsSelected() {
        return allOrganizationsSelected;
    }

    public void setAllOrganizationsSelected(boolean allOrganizationsSelected) {
        this.allOrganizationsSelected = allOrganizationsSelected;
    }

    public int getOrganizationsNumber() {
        return organizations.size();
    }
}
