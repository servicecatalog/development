package org.oscm.tenant.bean;

import org.junit.Test;

import org.oscm.dataservice.local.DataService;
import org.oscm.tenant.local.TenantServiceLocal;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

/**
 * Created by PLGrubskiM on 2016-09-15.
 */
public class TenantServiceLocalBeanIT extends EJBTestBase{

    protected DataService dm;
    protected TenantServiceLocal tenantServiceLocal;

    @Override
    protected void setup(TestContainer container) throws Exception {
        container.enableInterfaceMocking(true);
        container.addBean(new TenantServiceLocalBean());

        dm = container.get(DataService.class);
        tenantServiceLocal = container.get(TenantServiceLocal.class);

        container.login("setup", ROLE_ORGANIZATION_ADMIN);
    }
//TODO tests
    @Test
    public void getAllTenants() {

    }

    @Test
    public void getTenantByTenantId() {

    }

    @Test
    public void saveTenant() {

    }

    @Test
    public void getTenantByKey() {

    }

    @Test
    public void removeTenant() {

    }

    @Test
    public void saveTenantSetting() {

    }

    @Test
    public void removeTenantSetting() {

    }

    @Test
    public void getAllTenantSettingsForTenant() {

    }

    @Test
    public void getTenantsByIdPattern() {

    }

    @Test
    public void doesOrganizationAssignedToTenantExist() {

    }

    @Test
    public void doesMarketplaceAssignedToTenantExist() {

    }
}
