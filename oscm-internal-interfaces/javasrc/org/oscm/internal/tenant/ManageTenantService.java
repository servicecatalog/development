/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 18.07.2012
 *
 *******************************************************************************/
package org.oscm.internal.tenant;

import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;

import javax.ejb.Remote;
import java.util.List;
import java.util.Properties;

@Remote
public interface ManageTenantService {
    public List<POTenant> getAllTenants();

    POTenant getTenantByTenantId(String tenantId) throws ObjectNotFoundException;

    void addTenant(POTenant poTenant) throws NonUniqueBusinessKeyException;

    void updateTenant(POTenant poTenant)
        throws ConcurrentModificationException, ObjectNotFoundException, NonUniqueBusinessKeyException;

    void removeTenant(POTenant poTenant) throws ObjectNotFoundException;

    void setIdpSettingsForTenant(Properties properties, String tenantId)
        throws ObjectNotFoundException, NonUniqueBusinessKeyException;

    Properties getTenantIdpSettings(long tenantKey);

    void removeTenantIdpSettings(long tenantKey) throws ObjectNotFoundException;
    
    List<POTenant> getTenantsByIdPattern(String tenantIdPattern);
}
