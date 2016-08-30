package org.oscm.internal.tenant;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.oscm.domobjects.Tenant;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;

import org.oscm.internal.tenants.ManageTenantService;
import org.oscm.internal.tenants.POTenant;
import org.oscm.tenant.bean.TenantServiceLocalBean;

import java.util.List;

/**
 * Created by BadziakP on 2016-08-30.
 */

@Stateless
@Remote(ManageTenantService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class ManageTenantServiceBean implements ManageTenantService {

    @Inject
    TenantServiceLocalBean tenantServiceLocalBean;

    @Override
    public List<POTenant> getAllTenants() {
        List<Tenant> allTenants = tenantServiceLocalBean.getAllTenants();
        /*List<POTenant> poTenants = new ArrayList<>();
        for (VOTenant voTenant : tenantService.getAllTenants()) {
            poTenants.add(new POTenant(voTenant));
        }*/
        return null;
    }
}
