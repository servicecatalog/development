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
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.vo.VOTenant;
import org.oscm.tenant.assembler.TenantAssembler;
import org.oscm.tenant.local.TenantServiceLocal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BadziakP on 2016-08-31.
 */
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
}
