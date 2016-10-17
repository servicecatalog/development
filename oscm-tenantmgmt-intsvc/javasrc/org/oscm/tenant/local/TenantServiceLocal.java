/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 31.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.local;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.oscm.domobjects.Tenant;
import org.oscm.domobjects.TenantSetting;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;

@Local
public interface TenantServiceLocal {

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    List<Tenant> getAllTenants();

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    Tenant getTenantByTenantId(String tenantId) throws ObjectNotFoundException;

    void saveTenant(Tenant tenant) throws NonUniqueBusinessKeyException;

    Tenant getTenantByKey(long tkey) throws ObjectNotFoundException;

    void removeTenant(Tenant tenant);

    void saveTenantSetting(TenantSetting tenantSetting) throws NonUniqueBusinessKeyException;

    void removeTenantSetting(TenantSetting tenantSetting) throws ObjectNotFoundException;

    List<TenantSetting> getAllTenantSettingsForTenant(Tenant tenant);

    List<Tenant> getTenantsByIdPattern(String tenantIdPattern);

    boolean doesOrganizationAssignedToTenantExist(Tenant tenant);

    boolean doesMarketplaceAssignedToTenantExist(Tenant tenant);
    
    boolean doOrgUsersExistInTenant(String orgId, long tenantKey);
}
