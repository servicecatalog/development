/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 29.08.2016
 *
 *******************************************************************************/
package org.oscm.ui.dialog.classic.manageTenants;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.oscm.internal.tenant.POTenant;
import org.oscm.ui.beans.BaseModel;
import org.oscm.ui.profile.FieldData;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@ViewScoped
@ManagedBean
public class ManageTenantsModel extends BaseModel implements Serializable {

    private List<POTenant> tenants;
    private FieldData<String> tenantId;
    private FieldData<String> tenantName;
    private FieldData<String> tenantDescription;
    private FieldData<String> tenantIdp;
    private String selectedTenantId;
    private boolean saveDisabled;
    private boolean importDisabled;
    private boolean deleteDisabled;
    private POTenant selectedTenant;
    private UploadedFile file;
    private boolean clearExportAvailable;
    private Properties idpProperties;
    private boolean dirty;

    public List<POTenant> getTenants() {
        return tenants;
    }

    private List<String> dataTableHeaders = new ArrayList<>();

    public void setTenants(List<POTenant> tenants) {
        this.tenants = tenants;
    }

    public List<String> getDataTableHeaders() {
        return dataTableHeaders;
    }

    public void setDataTableHeaders(List<String> dataTableHeaders) {
        this.dataTableHeaders = dataTableHeaders;
    }

    public FieldData<String> getTenantId() {
        return tenantId;
    }

    public void setTenantId(FieldData<String> tenantId) {
        this.tenantId = tenantId;
    }

    public FieldData<String> getTenantIdp() {
        return tenantIdp;
    }

    public void setTenantIdp(FieldData<String> tenantIdp) {
        this.tenantIdp = tenantIdp;
    }

    public FieldData<String> getTenantDescription() {
        return tenantDescription;
    }

    public void setTenantDescription(FieldData<String> tenantDescription) {
        this.tenantDescription = tenantDescription;
    }

    public String getSelectedTenantId() {
        return selectedTenantId;
    }

    public void setSelectedTenantId(String selectedTenantId) {
        this.selectedTenantId = selectedTenantId;
    }

    public boolean isSaveDisabled() {
        return saveDisabled;
    }

    public void setSaveDisabled(boolean saveDisabled) {
        this.saveDisabled = saveDisabled;
    }

    public boolean isDeleteDisabled() {
        return deleteDisabled;
    }

    public void setDeleteDisabled(boolean deleteDisabled) {
        this.deleteDisabled = deleteDisabled;
    }

    public POTenant getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(POTenant selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public boolean isClearExportAvailable() {
        return clearExportAvailable;
    }

    public void setClearExportAvailable(boolean clearExportAvailable) {
        this.clearExportAvailable = clearExportAvailable;
    }

    public FieldData<String> getTenantName() {
        return tenantName;
    }

    public void setTenantName(FieldData<String> tenantName) {
        this.tenantName = tenantName;
    }

    public Properties getIdpProperties() {
        return idpProperties;
    }

    public void setIdpProperties(Properties idpProperties) {
        this.idpProperties = idpProperties;
    }

    public boolean isImportDisabled() {
        return importDisabled;
    }

    public void setImportDisabled(boolean importDisabled) {
        this.importDisabled = importDisabled;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
