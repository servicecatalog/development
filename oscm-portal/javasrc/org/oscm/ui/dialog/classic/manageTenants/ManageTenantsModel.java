/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 29.08.2016
 *
 *******************************************************************************/
package org.oscm.ui.dialog.classic.manageTenants;

import org.oscm.internal.tenant.POTenant;
import org.oscm.ui.beans.BaseModel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ViewScoped
@ManagedBean
public class ManageTenantsModel extends BaseModel implements Serializable {

    private List<POTenant> tenants;

    public List<POTenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<POTenant> tenants) {
        this.tenants = tenants;
    }
}
