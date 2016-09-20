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
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.vo.VOTenant;
import org.oscm.internal.vo.VOTenantSetting;
import org.oscm.vo.BaseAssembler;

public class TenantAssembler extends BaseAssembler {

    public static VOTenant toVOTenant(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        VOTenant voTenant = new VOTenant();
        voTenant.setKey(tenant.getKey());
        voTenant.setVersion(tenant.getVersion());
        voTenant.setTenantId(tenant.getDataContainer().getTenantId());
        voTenant.setDescription(tenant.getDataContainer().getDescription());
        for(TenantSetting tenantSetting : tenant.getTenantSettings()) {
            voTenant.getTenantSettings().put(tenantSetting.getName(), tenantSetting.getValue());
        }
        return voTenant;
    }

    public static Tenant updateTenantData(VOTenant voTenant, Tenant tenant) throws ConcurrentModificationException {
        verifyVersionAndKey(tenant, voTenant);
        tenant.getDataContainer().setTenantId(voTenant.getTenantId());
        tenant.getDataContainer().setDescription(voTenant.getDescription());
        return tenant;
    }

    public static Tenant toTenant(VOTenant voTenant) {
        Tenant tenant = new Tenant();
        tenant.getDataContainer().setTenantId(voTenant.getTenantId());
        tenant.getDataContainer().setDescription(voTenant.getDescription());
        tenant.setKey(voTenant.getKey());
        return tenant;
    }

    public static TenantSetting toTenantSetting(VOTenantSetting voTenantSetting) {
        TenantSetting tenantSetting = new TenantSetting();
        tenantSetting.getDataContainer().setName(voTenantSetting.getName());
        tenantSetting.getDataContainer().setValue(voTenantSetting.getValue());
        Tenant t = new Tenant();
        t.setKey(voTenantSetting.getVoTenant().getKey());
        tenantSetting.setTenant(t);
        return tenantSetting;
    }

    public static VOTenantSetting toVOTenantSetting(TenantSetting tenantSetting) {
        VOTenantSetting voTenantSetting = new VOTenantSetting();
        voTenantSetting.setKey(tenantSetting.getKey());
        voTenantSetting.setVersion(tenantSetting.getVersion());
        voTenantSetting.setName(tenantSetting.getDataContainer().getName());
        voTenantSetting.setValue(tenantSetting.getDataContainer().getValue());
        return voTenantSetting;
    }
}
