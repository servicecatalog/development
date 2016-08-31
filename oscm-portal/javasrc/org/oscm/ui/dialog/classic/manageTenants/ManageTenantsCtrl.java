/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 29.08.2016
 *
 *******************************************************************************/
package org.oscm.ui.dialog.classic.manageTenants;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.oscm.internal.tenant.ManageTenantService;
import org.oscm.internal.tenant.POTenant;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.internal.usermanagement.POUser;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.DataTableHandler;
import org.oscm.ui.dialog.classic.manageusers.ManageUsersModel;
import org.oscm.ui.profile.FieldData;

@ManagedBean
@ViewScoped
public class ManageTenantsCtrl extends BaseBean implements Serializable {

    @EJB
    private ManageTenantService manageTenantService;

    @ManagedProperty(value="#{manageTenantsModel}")
    private ManageTenantsModel model;

    @PostConstruct
    public void init() {
        model.setTenants(manageTenantService.getAllTenants());
        if (model.getSelectedTenantId() == null) {
            initWithoutSelection();
        }
    }

    public int getTenantsNumber() {
        return model.getTenants().size();
    }

    public ManageTenantsModel getModel() {
        return model;
    }

    public void setModel(ManageTenantsModel model) {
        this.model = model;
    }

    public List<String> getDataTableHeaders() {
        if (model.getDataTableHeaders() == null || model.getDataTableHeaders().isEmpty()) {
            try {
                model.setDataTableHeaders(DataTableHandler
                    .getTableHeaders(POTenant.class.getName()));
            } catch (Exception e) {
                throw new SaaSSystemException(e);
            }
        }
        return model.getDataTableHeaders();
    }

    private void initWithoutSelection() {
        model.setTenantId(new FieldData<String>(null, true, true));
        model.setTenantDescription(new FieldData<String>(null, true, false));
        model.setTenantIdp(new FieldData<String>(null, true, false));
    }
}
