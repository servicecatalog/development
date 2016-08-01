/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Marketplace;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.TechnicalProduct;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.ServiceAccessType;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.test.EJBTestBase;
import org.oscm.test.data.Marketplaces;
import org.oscm.test.data.Organizations;
import org.oscm.test.data.Products;
import org.oscm.test.data.Subscriptions;
import org.oscm.test.data.TechnicalProducts;
import org.oscm.test.ejb.TestContainer;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionSearchServiceBeanIT extends EJBTestBase {

    private SubscriptionSearchService sssb = new SubscriptionSearchServiceBean();

    @Override
    public void setup(TestContainer container) throws Exception {
        DataService ds = new DataServiceBean();

        enableHibernateSearchListeners(true);
        container.addBean(ds);
        container.addBean(sssb);

        Organization tpAndSupplier = Organizations.createOrganization(ds,
                OrganizationRoleType.SUPPLIER,
                OrganizationRoleType.TECHNOLOGY_PROVIDER);
        Marketplace mp = Marketplaces.ensureMarketplace(tpAndSupplier,
                tpAndSupplier.getOrganizationId(), ds);
        Marketplaces.grantPublishing(tpAndSupplier, mp, ds, false);

        Organization org = Organizations.createOrganization(ds,
                OrganizationRoleType.TECHNOLOGY_PROVIDER,
                OrganizationRoleType.SUPPLIER);
        org.addPlatformUser(Organizations.createUserForOrg(ds, org, true,
                "admin"));
        TechnicalProduct techProd = TechnicalProducts.createTechnicalProduct(
                ds, org, "techProd", false, ServiceAccessType.DIRECT);
        Products.createProduct(org, techProd, false, "prodId", null, ds);

        Organization customer = Organizations.createCustomer(ds, org);
        customer.addPlatformUser(Organizations.createUserForOrg(ds, customer,
                true, "admin"));
        Subscription sub = Subscriptions.createSubscription(ds,
                customer.getOrganizationId(), "prodId", "search", org);
        sub.setMarketplace(mp);

        Subscription sub2 = Subscriptions.createSubscription(ds,
                customer.getOrganizationId(), "prodId", "multiple", org);
        sub.setMarketplace(mp);

        Subscription sub3 = Subscriptions.createSubscription(ds,
                customer.getOrganizationId(), "prodId",
                "search+-&&||!(){}[]^\"~*?:\\", org);
        sub.setMarketplace(mp);
    }

    @Test
    public void searchSub4SingleWord() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() {
                try {
                    Collection<Long> col = sssb.searchSubscriptions("search");
                    assertEquals(2, col.size());
                } catch (InvalidPhraseException | ObjectNotFoundException e) {
                    fail();
                }

                return null;
            }
        });

    }

    @Test
    public void searchSub4MultipleWords() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() {
                try {
                    Collection<Long> col = sssb
                            .searchSubscriptions("search multiple");
                    assertEquals(3, col.size());
                } catch (InvalidPhraseException | ObjectNotFoundException e) {
                    fail();
                }
                return null;
            }
        });
    }

    @Test
    public void searchSub4SpecialCharacters() throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() {
                try {
                    Collection<Long> col = sssb
                            .searchSubscriptions("search+-&&||!(){}[]^\"~*?:\\");
                    assertEquals(2, col.size());
                } catch (InvalidPhraseException | ObjectNotFoundException e) {
                    fail();
                }
                return null;
            }
        });

    }
}
