/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2015年4月30日                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.subscriptions;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.HashSet;
import java.util.concurrent.Callable;

import javax.ejb.EJBAccessException;
import javax.ejb.EJBException;

import org.junit.Test;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.PlatformUser;
import org.oscm.paginator.Pagination;
import org.oscm.test.EJBTestBase;
import org.oscm.test.data.Organizations;
import org.oscm.test.ejb.TestContainer;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UserRoleType;

/**
 * @author qiu
 * 
 */
public class SubscriptionsServiceBeanIT extends EJBTestBase {
    private DataService mgr;
    private SubscriptionsService service;
    private PlatformUser supplierUser;

    @Override
    protected void setup(TestContainer container) throws Exception {
        container.enableInterfaceMocking(true);
        mgr = mock(DataService.class);
        container.addBean(mgr);
        createOrganizationRoles(mgr);
        Organization tpAndSupplier = Organizations.createOrganization(mgr,
                OrganizationRoleType.SUPPLIER,
                OrganizationRoleType.TECHNOLOGY_PROVIDER);
        supplierUser = Organizations.createUserForOrg(mgr, tpAndSupplier, true,
                "admin");
        container.addBean(new SubscriptionsServiceBean());
        service = container.get(SubscriptionsService.class);
        doReturn(supplierUser).when(mgr).getCurrentUser();
    }

    @Test(expected = EJBAccessException.class)
    public void getSubscriptionsForOrg_NotAuthorized() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                container.login(String.valueOf(supplierUser.getKey()));
                try {
                    service.getSubscriptionsForOrg(null);
                } catch (EJBException e) {
                    throw e.getCausedByException();
                }
                return null;
            }
        });
    }

    @Test
    public void getSubscriptionsForOrg_subManager() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                container.login(String.valueOf(supplierUser.getKey()),
                        UserRoleType.SUBSCRIPTION_MANAGER.name());
                try {
                    service.getSubscriptionsForOrg(null);
                } catch (EJBException e) {
                    throw e.getCausedByException();
                }
                return null;
            }
        });
    }

    @Test
    public void getSubscriptionsForOrg_orgAdmin() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                container.login(String.valueOf(supplierUser.getKey()),
                        UserRoleType.ORGANIZATION_ADMIN.name());
                try {
                    service.getSubscriptionsForOrg(null);
                } catch (EJBException e) {
                    throw e.getCausedByException();
                }
                return null;
            }
        });
    }

    @Test(expected = EJBAccessException.class)
    public void getSubscriptionsForOrgSize_NotAuthorized() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                container.login(String.valueOf(supplierUser.getKey()));
                try {
                    service.getSubscriptionsForOrgSize(
                            new HashSet<SubscriptionStatus>(), new Pagination());
                } catch (EJBException e) {
                    throw e.getCausedByException();
                }
                return null;
            }
        });
    }

    @Test
    public void getSubscriptionsForOrgSize_subManager() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                container.login(String.valueOf(supplierUser.getKey()),
                        UserRoleType.SUBSCRIPTION_MANAGER.name());
                try {
                    service.getSubscriptionsForOrgSize(
                            new HashSet<SubscriptionStatus>(), new Pagination());
                } catch (EJBException e) {
                    throw e.getCausedByException();
                }
                return null;
            }
        });
    }

    @Test
    public void getSubscriptionsForOrgSize_orgAdmin() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                container.login(String.valueOf(supplierUser.getKey()),
                        UserRoleType.ORGANIZATION_ADMIN.name());
                try {
                    service.getSubscriptionsForOrgSize(
                            new HashSet<SubscriptionStatus>(), new Pagination());
                } catch (EJBException e) {
                    throw e.getCausedByException();
                }
                return null;
            }
        });
    }

}
