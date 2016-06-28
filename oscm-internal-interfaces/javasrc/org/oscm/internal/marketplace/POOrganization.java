/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 20.05.2016
 *
 *******************************************************************************/
package org.oscm.internal.marketplace;

import org.oscm.internal.base.BasePO;

public class POOrganization extends BasePO {
    private String name;
    private String organizationId;
    private boolean selected;
    private boolean hasSubscriptions;
    
    public POOrganization() {}

    public POOrganization(String name, String organizationId) {
        this.name = name;
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public boolean isHasSubscriptions() {
        return hasSubscriptions;
    }

    public void setHasSubscriptions(boolean hasSubscriptions) {
        this.hasSubscriptions = hasSubscriptions;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        POOrganization that = (POOrganization) o;

        if (selected != that.selected) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return organizationId != null ? organizationId.equals(that.organizationId) : that.organizationId == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (organizationId != null ? organizationId.hashCode() : 0);
        result = 31 * result + (selected ? 1 : 0);
        return result;
    }
}
