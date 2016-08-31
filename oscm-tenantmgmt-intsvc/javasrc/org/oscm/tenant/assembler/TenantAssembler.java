/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.assembler;

import org.oscm.domobjects.Tenant;
import org.oscm.domobjects.TenantSetting;
import org.oscm.internal.vo.VOTenant;
import org.oscm.vo.BaseAssembler;

public class TenantAssembler extends BaseAssembler {

    public static VOTenant toVOTenant(Tenant tenant) {
        VOTenant voTenant = new VOTenant();
        voTenant.setKey(tenant.getKey());
        voTenant.setTenantId(tenant.getDataContainer().getTenantId());
        voTenant.setDescription(tenant.getDataContainer().getDescription());
        for(TenantSetting tenantSetting : tenant.getTenantSettings()) {
            voTenant.getTenantSettings().put(tenantSetting.getName(), tenantSetting.getValue());
        }
        return voTenant;
    }
}
