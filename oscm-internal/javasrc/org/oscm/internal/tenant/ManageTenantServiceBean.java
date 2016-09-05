/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.internal.tenant;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.interceptor.Interceptors;

import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.TenantService;
import org.oscm.internal.types.enumtypes.IdpSettingType;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOTenant;
import org.oscm.internal.vo.VOTenantSetting;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;

@Stateless
@RolesAllowed("PLATFORM_OPERATOR")
@Remote(ManageTenantService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class ManageTenantServiceBean implements ManageTenantService {
    private static final Log4jLogger logger = LoggerFactory
        .getLogger(ManageTenantServiceBean.class);

    @EJB
    TenantService tenantService;

    @Override
    public List<POTenant> getAllTenants() {
        List<POTenant> poTenants = new ArrayList<>();
        for (VOTenant voTenant : tenantService.getTenants()) {
            poTenants.add(new POTenant(voTenant));
        }
        return poTenants;
    }

    @Override
    public POTenant getTenantByTenantId(String tenantId) throws ObjectNotFoundException {
        return new POTenant(tenantService.getTenantByTenantId(tenantId));
    }

    @Override
    public void addTenant(POTenant poTenant) throws NonUniqueBusinessKeyException {
        tenantService.addTenant(poTenant.toVOTenanat());
    }

    @Override
    public void updateTenant(POTenant poTenant)
        throws ConcurrentModificationException, ObjectNotFoundException, NonUniqueBusinessKeyException {
        tenantService.updateTenant(poTenant.toVOTenanat());
    }

    @Override
    public void removeTenant(POTenant poTenant) throws ObjectNotFoundException {
        tenantService.removeTenant(poTenant.toVOTenanat());
    }

    @Override
    public void setIdpSettingsForTenant(Properties properties, String tenantId)
        throws ObjectNotFoundException, NonUniqueBusinessKeyException {
        VOTenant voTenant = tenantService.getTenantByTenantId(tenantId);
        List<VOTenantSetting> tenantSettings = new ArrayList<>();
        for (Object propertyKey : properties.keySet()) {
            String key = (String) propertyKey;
            if (!IdpSettingType.contains(key)) {
                logger.logWarn(
                    Log4jLogger.SYSTEM_LOG,
                    LogMessageIdentifier.WARN_IGNORE_ILLEGAL_PLATFORM_SETTING,
                    key);
            }
            String value = properties.getProperty(key);
            VOTenantSetting voTenantSetting = new VOTenantSetting();
            voTenantSetting.setName(IdpSettingType.valueOf(key));
            voTenantSetting.setValue(value);
            voTenantSetting.setVoTenant(voTenant);
            tenantSettings.add(voTenantSetting);
        }
        tenantService.addTenantSettings(tenantSettings, voTenant);
    }

    @Override
    public boolean doesSettingsForTenantExist(long tenantKey) {
        return !tenantService.getSettingsForTenant(tenantKey).isEmpty();
    }
}
