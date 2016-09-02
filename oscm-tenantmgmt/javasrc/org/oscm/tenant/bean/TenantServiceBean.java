/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.bean;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.oscm.domobjects.Tenant;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.intf.TenantService;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOTenant;
import org.oscm.tenant.assembler.TenantAssembler;
import org.oscm.tenant.local.TenantServiceLocal;

import java.util.ArrayList;
import java.util.List;

@Stateless
@Remote(TenantService.class)
@RolesAllowed("PLATFORM_OPERATOR")
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class TenantServiceBean implements TenantService {

    @EJB
    TenantServiceLocal tenantServiceLocal;

    @Override
    public List<VOTenant> getTenants() {
        List<VOTenant> voTenants = new ArrayList<>();
        for (Tenant tenant : tenantServiceLocal.getAllTenants()) {
            voTenants.add(TenantAssembler.toVOTenant(tenant));
        }
        return voTenants;
    }

    @Override
    public VOTenant getTenantByTenantId(String tenantId) throws ObjectNotFoundException {
        return TenantAssembler.toVOTenant(tenantServiceLocal.getTenantByTenantId(tenantId));
    }

    @Override
    public void addTenant(VOTenant voTenant) throws NonUniqueBusinessKeyException {
        tenantServiceLocal.saveTenant(TenantAssembler.toTenant(voTenant));
    }

    @Override
    public void updateTenant(VOTenant voTenant)
        throws NonUniqueBusinessKeyException, ObjectNotFoundException, ConcurrentModificationException {
        Tenant tenantToUpdate = tenantServiceLocal.getTenantByKey(voTenant.getKey());
        tenantServiceLocal.saveTenant(TenantAssembler.updateTenantData(voTenant,
            tenantToUpdate));
    }

    @Override
    public void removeTenant(VOTenant voTenant) throws ObjectNotFoundException {
        Tenant tenantToRemove = tenantServiceLocal.getTenantByKey(voTenant.getKey());
        tenantServiceLocal.removeTenant(tenantToRemove);
    }
}
