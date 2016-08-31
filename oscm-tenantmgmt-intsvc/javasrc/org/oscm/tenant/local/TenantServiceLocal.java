/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 31.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.local;

import org.oscm.domobjects.Tenant;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

@Local
public interface TenantServiceLocal {
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    List<Tenant> getAllTenants();
}
