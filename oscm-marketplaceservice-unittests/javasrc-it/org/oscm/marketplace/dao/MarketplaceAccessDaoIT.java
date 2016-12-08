/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *******************************************************************************/
package org.oscm.marketplace.dao;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Test;

import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Organization;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.test.EJBTestBase;
import org.oscm.test.data.Organizations;
import org.oscm.test.ejb.TestContainer;

/**
 * Created by PLGrubskiM on 2016-12-07.
 */
public class MarketplaceAccessDaoIT extends EJBTestBase{

    private MarketplaceAccessDao dao;
    private DataService ds;

    @Override
    protected void setup(TestContainer container) throws Exception {
        container.addBean(new DataServiceBean());
        ds = container.get(DataService.class);

        createOrg("orgDefaultTenant", OrganizationRoleType.SUPPLIER,
                OrganizationRoleType.TECHNOLOGY_PROVIDER);
    }

    @Test
    public void getOrganizationsWithMplAndSubscriptions() throws Exception {
        final long marketplaceKey = 0;
        final long tenantKey = 0;

        runTX(new Callable<List<Object[]>>() {
            @Override
            public List<Object[]> call() throws Exception {
                return dao.getOrganizationsWithMplAndSubscriptions(marketplaceKey, tenantKey);
            }
        });
    }

    private Organization createOrg(final String organizationId,
                                   final OrganizationRoleType... roles) throws Exception {
        return runTX(new Callable<Organization>() {
            @Override
            public Organization call() throws Exception {
                return Organizations.createOrganization(ds, organizationId,
                        roles);
            }
        });
    }

    private Organization createOrgWithTenant(final String organizationId,
                                             final String tenantId, final OrganizationRoleType... roles) throws Exception {
        return runTX(new Callable<Organization>() {
            @Override
            public Organization call() throws Exception {
                return Organizations.createOrganizationWithTenant(ds, organizationId, tenantId,
                        roles);
            }
        });
    }
}
