/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: groch                                                      
 *
 *  Creation Date: 18.07.2011                                                      
 *
 *  Completion Time: 20.07.2011                                              
 *
 *******************************************************************************/

package org.oscm.search;

import static org.junit.Assert.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.junit.*;
import org.oscm.accountservice.bean.AccountServiceBean;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.*;
import org.oscm.domobjects.bridge.ProductClassBridge;
import org.oscm.domobjects.bridge.SubscriptionClassBridge;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.domobjects.index.IndexRequestMessage;
import org.oscm.i18nservice.bean.LocalizerServiceBean;
import org.oscm.i18nservice.local.LocalizerServiceLocal;
import org.oscm.internal.intf.CategorizationService;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.enumtypes.ServiceType;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.vo.VOCategory;
import org.oscm.marketplace.bean.CategorizationServiceBean;
import org.oscm.marketplace.bean.MarketplaceServiceBean;
import org.oscm.serviceprovisioningservice.bean.SearchServiceInternalBean;
import org.oscm.serviceprovisioningservice.bean.ServiceProvisioningServiceBean;
import org.oscm.serviceprovisioningservice.bean.TagServiceBean;
import org.oscm.serviceprovisioningservice.local.TagServiceLocal;
import org.oscm.test.EJBTestBase;
import org.oscm.test.data.*;
import org.oscm.test.ejb.FifoJMSQueue;
import org.oscm.test.ejb.TestContainer;
import org.oscm.test.stubs.ConfigurationServiceStub;
import org.oscm.triggerservice.bean.TriggerQueueServiceBean;
import org.oscm.triggerservice.local.TriggerMessage;
import org.oscm.types.exceptions.InvalidUserSession;

public class IndexerIT extends EJBTestBase {

    private static final String TEMP_INDEX_BASE_DIR = "tempIndexDir";
    private static final String locale = "en";
    private static final List<LocalizedObjectTypes> localizedAttributes = Arrays
            .asList(LocalizedObjectTypes.PRODUCT_MARKETING_NAME,
                    LocalizedObjectTypes.PRODUCT_MARKETING_DESC,
                    LocalizedObjectTypes.PRODUCT_SHORT_DESCRIPTION,
                    LocalizedObjectTypes.PRICEMODEL_DESCRIPTION,
                    LocalizedObjectTypes.PRODUCT_CUSTOM_TAB_NAME);

    private static final List<String> expectedIndexedAttributesProduct = Arrays
            .asList(ProductClassBridge.SERVICE_NAME + locale,
                    ProductClassBridge.SERVICE_DESCRIPTION + locale,
                    ProductClassBridge.SERVICE_SHORT_DESC + locale,
                    ProductClassBridge.TAGS + locale,
                    ProductClassBridge.PRICEMODEL_DESCRIPTION + locale,
                    ProductClassBridge.CATEGORY_NAME + locale,
                    ProductClassBridge.SERVICE_NAME
                            + ProductClassBridge.DEFINED_LOCALES_SUFFIX,
                    ProductClassBridge.SERVICE_DESCRIPTION
                            + ProductClassBridge.DEFINED_LOCALES_SUFFIX,
                    ProductClassBridge.SERVICE_SHORT_DESC
                            + ProductClassBridge.DEFINED_LOCALES_SUFFIX,
                    ProductClassBridge.PRICEMODEL_DESCRIPTION
                            + ProductClassBridge.DEFINED_LOCALES_SUFFIX,
                    ProductClassBridge.CATEGORY_NAME
                            + ProductClassBridge.DEFINED_LOCALES_SUFFIX,
                    ProductClassBridge.MP_ID);
    private static final List<String> expectedIndexedAttributesSubscription = Arrays
            .asList(SubscriptionClassBridge.NAME_SUBSCRIPTION_ID,
                    SubscriptionClassBridge.NAME_REFERENCE,
                    SubscriptionClassBridge.NAME_PARAMETER_VALUE,
                    SubscriptionClassBridge.NAME_UDA_VALUE);
    private static FifoJMSQueue indexerQueue;
    private static String sysPropertyBaseDir = null;
    private DataService dm;
    private PlatformUser user;
    private LocalizerServiceLocal locSvc;
    private TagServiceLocal tagSvc;
    private Marketplace mpGlobal;
    private TechnicalProduct techProd;
    private int svcCounter;
    private long categoryKey;
    private Indexer irl;

    @BeforeClass
    public static void setupOnce() throws Exception {
        // store property base directory to be able to reset it later
        sysPropertyBaseDir = System
                .getProperty("hibernate.search.default.indexBase");

        // for all succeeding tests, put index into temp directory
        deleteTempIndexDir();
        new File(TEMP_INDEX_BASE_DIR).mkdir();
        System.setProperty("hibernate.search.default.indexBase",
                TEMP_INDEX_BASE_DIR);

        indexerQueue = createIndexerQueue();
        PERSISTENCE.clearEntityManagerFactoryCache();
    }

    private static void deleteTempIndexDir() {
        final File indexBaseDir = new File(TEMP_INDEX_BASE_DIR);
        if (indexBaseDir.exists()) {
            final File[] childDirs = new File(TEMP_INDEX_BASE_DIR).listFiles();
            if (childDirs != null) {
                for (int i = childDirs.length - 1; i >= 0; i--) {
                    final File[] files = childDirs[i].listFiles();
                    if (files != null) {
                        for (int j = files.length - 1; j >= 0; j--) {
                            files[j].delete();
                        }
                    }
                    childDirs[i].delete();
                }
            }
            indexBaseDir.delete();
        }
    }

    @AfterClass
    public static void tearDownClass() {
        if (sysPropertyBaseDir != null && sysPropertyBaseDir.length() > 0)
            System.setProperty("hibernate.search.default.indexBase",
                    sysPropertyBaseDir);
        else {
            System.clearProperty("hibernate.search.default.indexBase");
        }
        // delete the created temp directory and all of its content
        deleteTempIndexDir();
        PERSISTENCE.clearEntityManagerFactoryCache();
    }

    protected void setup(final TestContainer container) throws Exception {
        enableHibernateSearchListeners(true);
        indexerQueue.clear();
        container.enableInterfaceMocking(true);
        container.addBean(new ConfigurationServiceStub());
        container.addBean(new DataServiceBean() {

            public PlatformUser getCurrentUser() {
                if (user == null) {
                    throw new InvalidUserSession("No user yet.");
                }
                return user;
            }
        });
        container.addBean(new TriggerQueueServiceBean() {

            public void sendAllNonSuspendingMessages(
                    List<TriggerMessage> messageData) {

            }

        });
        container.addBean(new LocalizerServiceBean());
        container.addBean(new TagServiceBean());
        container.addBean(new ServiceProvisioningServiceBean());
        container.addBean(new AccountServiceBean());
        container.addBean(new MarketplaceServiceBean());
        container.addBean(new SearchServiceInternalBean());
        container.addBean(new CategorizationServiceBean());

        irl = new Indexer();
        dm = container.get(DataService.class);
        irl.dm = dm;

        locSvc = container.get(LocalizerServiceLocal.class);
        tagSvc = container.get(TagServiceLocal.class);
        final CategorizationService cs = container
                .get(CategorizationService.class);

        runTX(() -> {
            dm.createQuery("delete from LocalizedResource").executeUpdate();
            dm.createQuery("delete from CatalogEntry").executeUpdate();
            dm.createQuery("delete from UsageLicense").executeUpdate();
            dm.createQuery("delete from Subscription").executeUpdate();
            dm.createQuery("delete from Product").executeUpdate();
            dm.createQuery("delete from PricedProductRole").executeUpdate();
            dm.createQuery("delete from PricedOption").executeUpdate();
            dm.createQuery("delete from SteppedPrice").executeUpdate();
            dm.createQuery("delete from PricedParameter").executeUpdate();
            dm.createQuery("delete from ParameterOption").executeUpdate();
            dm.createQuery("delete from Parameter").executeUpdate();
            dm.createQuery("delete from ParameterDefinition").executeUpdate();
            dm.createQuery("delete from RoleDefinition").executeUpdate();
            dm.createQuery("delete from PricedEvent").executeUpdate();
            dm.createQuery("delete from Event").executeUpdate();
            dm.createQuery("delete from TechnicalProductOperation")
                    .executeUpdate();
            dm.createQuery("delete from TechnicalProduct").executeUpdate();

            return null;
        });

        // run scenario setup
        runTX(() -> {
            // perform base setup
            Scenario.setup(container, true);
            techProd = Scenario.createTechnicalService(dm, "techProdId");
            tagSvc.updateTags(techProd, "en",
                    Arrays.asList(new Tag("en", "english tag")));
            return null;
        });

        runTX(() -> {

            createOrganizationRoles(dm);
            Organization operatorOrg = Organizations.createOrganization(dm,
                    OrganizationRoleType.PLATFORM_OPERATOR);
            mpGlobal = Marketplaces.createGlobalMarketplace(operatorOrg,
                    GLOBAL_MARKETPLACE_NAME, dm);
            return null;
        });

        // set temp index base directory and empty possibly existing index
        System.setProperty("hibernate.search.default.indexBase",
                TEMP_INDEX_BASE_DIR);
        user = new PlatformUser();
        runTX(() -> {

            container.login("admin", ROLE_MARKETPLACE_OWNER);
            VOCategory cat = new VOCategory();
            cat.setName("name en");
            cat.setCategoryId("id");
            cat.setMarketplaceId(mpGlobal.getMarketplaceId());
            user.setOrganization(
                    dm.getReference(Marketplace.class, mpGlobal.getKey())
                            .getOrganization());
            cs.saveCategories(Arrays.asList(cat), null, "en");
            List<VOCategory> list = cs
                    .getCategories(mpGlobal.getMarketplaceId(), "en");
            categoryKey = list.get(0).getKey();
            return null;
        });
        emptyProductIndex();
        emptySubscriptionIndex();
        assertDocsInIndex(Product.class,
                "Index must contain 0 document at test start", 0,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must contain 0 document at test start", 0,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
        svcCounter = 0;
    }

    @Test
    public void testInitIndexForFulltextSearch_NoIndexPresentEmptyDB()
            throws Throwable {
        assertDocsInIndex(Product.class,
                "Index must have no documents before indexing", 0,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must have no document before indexing", 0,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
        runTX(() -> {

            irl.initIndexForFulltextSearch(true);
            return null;
        });
        assertDocsInIndex(Product.class,
                "Index must have no documents after indexing", 0,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must have 1 document after indexing", 1,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
    }

    @Test
    public void testInitIndexForFulltextSearch_NoIndexPresentDataAvailable()
            throws Throwable {

        addProductsToDatabase(3);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);

        // manually delete the index
        emptyProductIndex();
        emptySubscriptionIndex();
        assertDocsInIndex(Product.class,
                "Index must have no documents before indexing", 0,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must have no documents before indexing", 0,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
        runTX(() -> {

            irl.initIndexForFulltextSearch(false);
            return null;
        });
        assertDocsInIndex(Product.class,
                "Index must contain 3 documents after indexing", 3,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must contain 3 document after indexing", 3,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
    }

    @Ignore
    @Test
    public void testInitIndexForFulltextSearch_IndexPresentNoForceSet()
            throws Throwable {

        // first have a dummy call to setup the scenario etc.
        emptyProductIndex();
        emptySubscriptionIndex();
        addProductsToDatabase(1);

        emptyProductIndex();
        emptySubscriptionIndex();
        // now create 3 elements on the empty index
        addProductsToDatabase(3);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        assertDocsInIndex(Product.class,
                "Index must contain 3 documents due to automatic indexing", 3,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must contain 3 documents due to automatic indexing", 3,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
        // manually delete the index
        emptyProductIndex();
        emptySubscriptionIndex();
        addProductsToDatabase(1);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        assertDocsInIndex(Product.class,
                "Index must contain 1 document due to automatic indexing", 1,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must contain 1 document due to automatic indexing", 1,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
        runTX(() -> {

            irl.initIndexForFulltextSearch(false);
            return null;
        });
        assertDocsInIndex(Product.class,
                "Index must still contain 1 document after indexing (although the db contains 4 items)",
                1, expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must still contain 1 document after indexing (although the db contains 4 items)",
                1, expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
    }

    @Ignore
    @Test
    public void testInitIndexForFulltextSearch_IndexPresentForceSet()
            throws Throwable {
        // first have a dummy call to setup the scenario etc.
        emptyProductIndex();
        emptySubscriptionIndex();
        addProductsToDatabase(1); // db contains 1 product
        runTX(() -> {

            irl.initIndexForFulltextSearch(true);
            return null;
        });
        emptyProductIndex();
        emptySubscriptionIndex();
        // now create 3 elements on the empty index, db contains 4 products
        addProductsToDatabase(3);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        assertDocsInIndex(Product.class,
                "Index must contain 3 documents due to automatic indexing", 3,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must contain 3 documents due to automatic indexing", 3,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
        // manually delete the index
        emptyProductIndex();
        emptySubscriptionIndex();
        addProductsToDatabase(1); // db contains 5 products
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        assertDocsInIndex(Product.class,
                "Index must contain 1 document due to automatic indexing", 1,
                expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must contain 1 document due to automatic indexing", 1,
                expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
        runTX(() -> {

            irl.initIndexForFulltextSearch(true);
            return null;
        });
        assertDocsInIndex(Product.class,
                "Index must contain " + 5
                        + " documents after indexing (and so does the db)",
                5, expectedIndexedAttributesProduct.size(),
                expectedIndexedAttributesProduct);
        assertDocsInIndex(Subscription.class,
                "Index must contain " + 5
                        + " documents after indexing (and so does the db)",
                5, expectedIndexedAttributesSubscription.size(),
                expectedIndexedAttributesSubscription);
    }


    private void assertDocsInIndex(final Class<?> clazz, final String comment,
            final int expectedNumDocs, final int expectedNumIndexedAttributes,
            final List<String> expectedAttributes) throws Exception {
        Boolean evaluationTookPlace = runTX(() -> {
            boolean evaluatedIndex = false;
            Session session = dm.getSession();
            if (session != null) {
                FullTextSession fullTextSession = Search
                        .getFullTextSession(session);
                SearchFactory searchFactory = fullTextSession
                        .getSearchFactory();
                IndexReader reader = searchFactory.getIndexReaderAccessor()
                        .open(clazz);

                try {
                    assertEquals(comment, expectedNumDocs, reader.numDocs());
                    if (expectedNumDocs > 0) {
                        final FieldInfos indexedFieldNames = MultiFields
                                .getMergedFieldInfos(reader);
                        for (String expectedAttr : expectedAttributes) {
                            assertNotNull(
                                    "attribute " + expectedAttr
                                            + " does not exist in index: "
                                            + indexedFieldNames,
                                    indexedFieldNames.fieldInfo(expectedAttr));
                        }
                        assertNotNull(
                                "attribute \"key\" does not exist in index: "
                                        + indexedFieldNames,
                                indexedFieldNames.fieldInfo("key"));
                        assertNotNull(
                                "attribute \"_hibernate_class\" does not exist in index: "
                                        + indexedFieldNames,
                                indexedFieldNames
                                        .fieldInfo("_hibernate_class"));
                        assertEquals(
                                "More or less attributes indexed than expected, attributes retrieved from index: "
                                        + indexedFieldNames,
                                expectedNumIndexedAttributes + 2,
                                indexedFieldNames.size());
                        evaluatedIndex = true;
                    }
                } finally {
                    searchFactory.getIndexReaderAccessor().close(reader);
                }
            }

            return Boolean.valueOf(evaluatedIndex);
        });

        if (expectedNumDocs > 0) {
            Assert.assertTrue("Index not found, no evaluation took place",
                    evaluationTookPlace.booleanValue());
        }
    }

    private void emptyProductIndex() throws Exception {
        runTX(() -> {

            Session session = dm.getSession();
            if (session != null) {
                FullTextSession fullTextSession = Search
                        .getFullTextSession(session);
                fullTextSession.purgeAll(Product.class);
            }

            return null;
        });
    }

    private List<String> addProductsToDatabase(final int num) throws Exception {

        final List<String> createdProductIds = new ArrayList<>();

        runTX(() -> {

            for (int i = 0; i < num; i++) {
                svcCounter++;
                String prodId = "prodId" + svcCounter;
                Product prod = Products.createProduct(Scenario.getSupplier(),
                        techProd, true, prodId, "pMId" + svcCounter, mpGlobal,
                        dm);
                dm.persist(prod);
                createdProductIds.add(prodId);
            }
            dm.flush();

            return null;
        });
        // also, set visible in catalog to true for every created product
        runTX(() -> {

            for (String prodId : createdProductIds) {
                Product prod = new Product();
                prod.setProductId(prodId);
                prod.setVendor(Scenario.getSupplier());
                prod = (Product) dm.getReferenceByBusinessKey(prod);
                CatalogEntry ce = prod.getCatalogEntries().get(0);
                ce.setVisibleInCatalog(true);
                dm.persist(ce);
                CategoryToCatalogEntry cc = new CategoryToCatalogEntry();
                cc.setCatalogEntry(ce);
                cc.setCategory(dm.getReference(Category.class, categoryKey));
                dm.persist(cc);
                // also store given localized resources for created product
                for (LocalizedObjectTypes type : localizedAttributes) {
                    locSvc.storeLocalizedResource(locale, prod.getKey(), type,
                            "MY_PRODUCT_" + prodId + " " + type.toString());
                }
            }
            dm.flush();

            return null;
        });
        return createdProductIds;
    }

    private void addSubscriptionToDatabase(final SubscriptionStatus status)
            throws Exception {

        runTX(() -> {

            Organization seller = Organizations.createOrganization(dm,
                    OrganizationRoleType.SUPPLIER);
            Product product = Products.createProduct(seller.getOrganizationId(),
                    "prodId", "techProd", dm);
            Subscription sub = Subscriptions.createSubscription(dm,
                    seller.getOrganizationId(), product);
            sub.setStatus(status);
            dm.persist(sub);
            dm.flush();

            return null;
        });
    }

    private void emptySubscriptionIndex() throws Exception {
        runTX(() -> {

            Session session = dm.getSession();
            if (session != null) {
                FullTextSession fullTextSession = Search
                        .getFullTextSession(session);
                fullTextSession.purgeAll(Subscription.class);
            }

            return null;
        });
    }
}