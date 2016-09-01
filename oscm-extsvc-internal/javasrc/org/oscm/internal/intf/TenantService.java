/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.internal.intf;

import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOTenant;

import javax.ejb.Remote;
import java.util.List;

@Remote
public interface TenantService {
    List<VOTenant> getTenants();

    VOTenant getTenantByTenantId(String tenantId) throws ObjectNotFoundException;
}
