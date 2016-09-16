/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 30.08.2016
 *
 *******************************************************************************/
package org.oscm.tenant.dao;

import org.oscm.dataservice.local.DataService;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

/**
 * Created by PLGrubskiM on 2016-09-15.
 */
public class TenantDaoIT extends EJBTestBase{

    protected DataService dm;
    protected TenantDao tenantService;

    @Override
    protected void setup(TestContainer container) throws Exception {
        container.enableInterfaceMocking(true);
        container.addBean(new TenantDao());

        dm = container.get(DataService.class);
        tenantService = container.get(TenantDao.class);

        container.login("setup", ROLE_ORGANIZATION_ADMIN);
    }

    //TODO tests
}
