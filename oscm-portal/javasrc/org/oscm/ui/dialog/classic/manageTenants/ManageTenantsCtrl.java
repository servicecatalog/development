/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 29.08.2016
 *
 *******************************************************************************/
package org.oscm.ui.dialog.classic.manageTenants;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.oscm.converter.PropertiesLoader;
import org.oscm.internal.components.response.Response;
import org.oscm.internal.tenant.ManageTenantService;
import org.oscm.internal.tenant.POTenant;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.types.exception.SaaSSystemException;
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
        model.setClearExportAvailable(manageTenantService.doesSettingsForTenantExist(poTenant.getKey()));
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
                handleSuccessMessage(BaseBean.INFO_TENANT_SAVED, model.getTenantId().getValue());
                refreshModelAfterUpdate();
            } else {
                POTenant poTenant = new POTenant();
                poTenant.setTenantId(model.getTenantId().getValue());
                poTenant.setDescription(model.getTenantDescription().getValue());
                manageTenantService.addTenant(poTenant);
                handleSuccessMessage(BaseBean.INFO_TENANT_ADDED, model.getTenantId().getValue());
                refreshModel();
            }
        }  catch (SaaSApplicationException e) {
            ui.handleException(e);
        }
        return null;
    }

    private void handleSuccessMessage(String message, String tenantId) {
        ui.handle(new Response(), message, tenantId);
    }

    public String delete() {
        try {
            manageTenantService.removeTenant(model.getSelectedTenant());
            handleSuccessMessage(BaseBean.INFO_TENANT_DELETED, model.getSelectedTenantId());
            refreshModel();
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
                model.setClearExportAvailable(manageTenantService.doesSettingsForTenantExist(poTenant.getKey()));
                model.setTenantIdp(new FieldData<String>(poTenant.getIdp(), true, false));
                return;
            }
        }

    }

    private void refreshModel() {
        model.setSelectedTenant(null);
        model.setSelectedTenantId(null);
        model.setClearExportAvailable(false);
        initWithoutSelection();
    }

    public void addTenant() {
        model.setSelectedTenant(null);
        model.setSelectedTenantId(null);
        model.setClearExportAvailable(false);
        model.setTenantId(new FieldData<String>(null, false, true));
        model.setTenantDescription(new FieldData<String>(null, false, false));
        model.setTenantIdp(new FieldData<String>(null, true, false));
        model.setSaveDisabled(false);
        model.setDeleteDisabled(true);
    }

    public String importSettings() throws SaaSApplicationException {

        Properties propsToStore = new Properties();
        UploadedFile file = model.getFile();
        if (file == null) {
            ui.handleError(null, "error.organization.ldapsettings.nofile");
            return OUTCOME_ERROR;
        }
        try {
            propsToStore = PropertiesLoader.loadProperties(file
                .getInputStream());
            manageTenantService.setIdpSettingsForTenant(propsToStore, model.getSelectedTenantId());
            refreshModelAfterUpdate();
        } catch (IOException e) {
            addMessage(null, FacesMessage.SEVERITY_ERROR, ERROR_UPLOAD);
            return OUTCOME_ERROR;
        }
        addMessage(null, FacesMessage.SEVERITY_INFO, "info.organization.ldapsettings.imported");

        return OUTCOME_SUCCESS;
    }

    public String exportSettings() throws IOException {

        /*Properties props = new LdapSettingConverter().toProperties(
            model.getSettings(),
            Strings.isEmpty(model.getOrganizationIdentifier()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            props.store(baos, null);
            writeSettings(baos.toByteArray());
        } finally {
            baos.close();
        }*/

        return OUTCOME_SUCCESS;
    }
}
