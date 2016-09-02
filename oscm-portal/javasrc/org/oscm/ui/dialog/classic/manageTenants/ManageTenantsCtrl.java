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

import org.oscm.internal.components.response.Response;
import org.oscm.internal.tenant.ManageTenantService;
import org.oscm.internal.tenant.POTenant;
import org.oscm.internal.types.exception.*;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.DataTableHandler;
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
        if (model.getSelectedTenant() == null) {
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
        model.setTenants(manageTenantService.getAllTenants());
        model.setTenantId(new FieldData<String>(null, true, true));
        model.setTenantDescription(new FieldData<String>(null, true, false));
        model.setTenantIdp(new FieldData<String>(null, true, false));
        model.setSaveDisabled(true);
        model.setDeleteDisabled(true);
    }

    public void setSelectedTenantId(String tenantId) {
        model.setSelectedTenantId(tenantId);
    }

    public void setSelectedTenant() {
        POTenant poTenant = getSelectedTenant();
        model.setSelectedTenant(poTenant);
        model.setTenantId(new FieldData<String>(poTenant.getTenantId(), false, true));
        model.setTenantDescription(new FieldData<String>(poTenant.getDescription(), false, false));
        model.setTenantIdp(new FieldData<String>(poTenant.getIdp(), true, false));
        model.setSaveDisabled(false);
        model.setDeleteDisabled(false);
    }

    private POTenant getSelectedTenant() {
        POTenant poTenant = null;
        try {
            poTenant = manageTenantService.getTenantByTenantId(model.getSelectedTenantId());
        } catch (SaaSApplicationException e) {
            ui.handleException(e);
        }
        return poTenant;
    }

    public String save() {
        try {
            if (model.getSelectedTenant() != null) {
                model.getSelectedTenant().setTenantId(model.getTenantId().getValue());
                model.getSelectedTenant().setDescription(model.getTenantDescription().getValue());
                manageTenantService.updateTenant(model.getSelectedTenant());
                refreshModelAfterUpdate();
            } else {
                POTenant poTenant = new POTenant();
                poTenant.setTenantId(model.getTenantId().getValue());
                poTenant.setDescription(model.getTenantDescription().getValue());
                manageTenantService.addTenant(poTenant);
                refreshModelAfterDelete();
            }
            ui.handle(new Response(), BaseBean.INFO_USER_SAVED, model.getSelectedTenantId());
        }  catch (SaaSApplicationException e) {
            ui.handleException(e);
        }
        return null;
    }

    public String delete() {
        try {
            manageTenantService.removeTenant(model.getSelectedTenant());
            refreshModelAfterDelete();
            ui.handle(new Response(), BaseBean.INFO_USER_DELETED, model.getSelectedTenantId());
        } catch (SaaSApplicationException e) {
            ui.handleException(e);
        }
        return null;
    }

    private void refreshModelAfterUpdate() {
        model.setTenants(manageTenantService.getAllTenants());
        for (POTenant poTenant : manageTenantService.getAllTenants()) {
            if (poTenant.getKey() == model.getSelectedTenant().getKey()) {
                model.setSelectedTenant(poTenant);
            }
        }
    }

    private void refreshModelAfterDelete() {
        model.setSelectedTenant(null);
        model.setSelectedTenantId(null);
        initWithoutSelection();
    }

    public void addTenant() {
        model.setSelectedTenant(null);
        model.setSelectedTenantId(null);
        model.setTenantId(new FieldData<String>(null, false, true));
        model.setTenantDescription(new FieldData<String>(null, false, false));
        model.setTenantIdp(new FieldData<String>(null, true, false));
        model.setSaveDisabled(false);
        model.setDeleteDisabled(true);
    }
}
