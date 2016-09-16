package org.oscm.tenant.bean;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;

import org.oscm.dataservice.local.DataService;
import org.oscm.internal.intf.TenantService;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.TenantDeletionConstraintException;
import org.oscm.internal.vo.VOTenant;
import org.oscm.internal.vo.VOTenantSetting;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

/**
 * Created by PLGrubskiM on 2016-09-15.
 */
public class TenantServiceBeanIT extends EJBTestBase{

    protected DataService dm;
    protected TenantService tenantService;

    @Override
    protected void setup(TestContainer container) throws Exception {
        container.enableInterfaceMocking(true);
        container.addBean(new TenantServiceBean());

        dm = container.get(DataService.class);
        tenantService = container.get(TenantService.class);

        container.login("setup", ROLE_PLATFORM_OPERATOR);
    }
//TODO tests
    @Test
    public void getTenants() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() {
                    tenantService.getTenants();
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void getTenantByTenantId() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws ObjectNotFoundException {
                    tenantService.getTenantByTenantId("tenantId1");
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void addTenant() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws NonUniqueBusinessKeyException {
                    VOTenant tenant = null;
                    tenantService.addTenant(tenant);
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void updateTenant() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws ConcurrentModificationException, ObjectNotFoundException, NonUniqueBusinessKeyException {
                    VOTenant tenant = null;
                    tenantService.updateTenant(tenant);
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void removeTenant() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws TenantDeletionConstraintException, ObjectNotFoundException {
                    VOTenant tenant = null;
                    tenantService.removeTenant(tenant);
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void addTenantSettings() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws ObjectNotFoundException, NonUniqueBusinessKeyException {
                    List<VOTenantSetting> tenantSettings = null;
                    VOTenant tenant = null;
                    tenantService.addTenantSettings(tenantSettings, tenant);
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void removeTenantIdpProperties() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws ObjectNotFoundException {
                    tenantService.removeTenantIdpProperties(1L);
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void getSettingsForTenant() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws ObjectNotFoundException {
                    tenantService.getSettingsForTenant(1L);
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void getTenantsByIdPattern() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() {
                    tenantService.getTenantsByIdPattern("pattern");
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void findByTkey() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                @Override
                public Void call() throws ObjectNotFoundException {
                    tenantService.findByTkey("tkey");
                    return null;
                }
            });
        } catch (Exception e) {
            throw e.getCause();
        }
    }

}
