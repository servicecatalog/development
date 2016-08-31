/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 29.08.2016
 *
 *******************************************************************************/
package org.oscm.ui.dialog.classic.manageTenants;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.oscm.internal.tenant.ManageTenantService;
import org.oscm.ui.beans.BaseBean;

@ManagedBean
@ViewScoped
public class ManageTenantsCtrl extends BaseBean implements Serializable {

    @EJB
    private ManageTenantService manageTenantService;

    @ManagedProperty(value="#{manageTenantsModel}")
    private ManageTenantsModel manageTenantsModel;

    @PostConstruct
    public void init() {
        manageTenantService.getAllTenants();
    }

    public ManageTenantsModel getManageTenantsModel() {
        return manageTenantsModel;
    }

    public void setManageTenantsModel(ManageTenantsModel manageTenantsModel) {
        this.manageTenantsModel = manageTenantsModel;
    }

}
