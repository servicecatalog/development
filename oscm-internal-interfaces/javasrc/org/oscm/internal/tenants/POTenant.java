/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 29.08.2016
 *
 *******************************************************************************/
package org.oscm.internal.tenants;

import org.oscm.internal.base.BasePO;
import org.oscm.internal.types.enumtypes.IdpSettingType;
import org.oscm.internal.vo.VOTenant;

public class POTenant extends BasePO {

    private String tenantId;
    private String description;
    private String idp;

    public POTenant(VOTenant voTenant) {
        this.tenantId = voTenant.getTenantId();
        this.description = voTenant.getDescription();
        this.idp = voTenant.getTenantSettings().get(IdpSettingType.SSO_IDP_URL);
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String idp) {
        this.idp = idp;
    }
}
