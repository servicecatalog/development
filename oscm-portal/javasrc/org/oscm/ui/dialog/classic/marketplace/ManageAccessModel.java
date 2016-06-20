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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.oscm.internal.marketplace.POOrganization;
import org.oscm.internal.vo.VOMarketplace;
import org.oscm.internal.vo.VOOrganization;

@ManagedBean
@ViewScoped
public class ManageAccessModel {

    private List<SelectItem> selectableMarketplaces;
    
    private String selectedMarketplaceId;
    
    private boolean selectedMarketplaceRestricted;

    private List<POOrganization> organizations;

    private Map<Long, Boolean> organizationsAccesses = new HashMap<>();

    private List<VOOrganization> authorizedOrganizations = new ArrayList<>();

    private List<VOOrganization> unauthorizedOrganizations = new ArrayList<>();

    private boolean allOrganizationsSelected;
    
    private Set<String> organizationsToBeRemoved = new HashSet<>(); 
    
    private VOMarketplace selectedMarketplace;
    
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

    public Map<Long, Boolean> getOrganizationsAccesses() {
        return organizationsAccesses;
    }

    public void setOrganizationsAccesses(Map<Long, Boolean> organizationsAccesses) {
        this.organizationsAccesses = organizationsAccesses;
    }

    public List<VOOrganization> getAuthorizedOrganizations() {
        return authorizedOrganizations;
    }

    public void setAuthorizedOrganizations(List<VOOrganization> authorizedOrganizations) {
        this.authorizedOrganizations = authorizedOrganizations;
    }

    public List<VOOrganization> getUnauthorizedOrganizations() {
        return unauthorizedOrganizations;
    }

    public void setUnauthorizedOrganizations(List<VOOrganization> unauthorizedOrganizations) {
        this.unauthorizedOrganizations = unauthorizedOrganizations;
    }

    public Set<String> getOrganizationsToBeRemoved() {
        return organizationsToBeRemoved;
    }

    public void setOrganizationsToBeRemoved(
            Set<String> organizationsToBeRemoved) {
        this.organizationsToBeRemoved = organizationsToBeRemoved;
    }

    public VOMarketplace getSelectedMarketplace() {
        return selectedMarketplace;
    }

    public void setSelectedMarketplace(VOMarketplace selectedMarketplace) {
        this.selectedMarketplace = selectedMarketplace;
    }
}
