/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: groch                                                      
 *                                                                              
 *  Creation Date: 18.07.2011                                                      
 *                                                                              
 *  Completion Time: 20.07.2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import org.apache.lucene.util.ReaderUtil;
import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.oscm.accountservice.bean.AccountServiceBean;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.CatalogEntry;
import org.oscm.domobjects.Category;
import org.oscm.domobjects.CategoryToCatalogEntry;
import org.oscm.domobjects.Marketplace;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.ParameterDefinition;
import org.oscm.domobjects.ParameterSet;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.PriceModel;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Tag;
import org.oscm.domobjects.TechnicalProduct;
import org.oscm.domobjects.TechnicalProductTag;
import org.oscm.domobjects.Uda;
import org.oscm.domobjects.UdaDefinition;
import org.oscm.domobjects.bridge.ProductClassBridge;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.domobjects.index.IndexRequestMessage;
import org.oscm.i18nservice.bean.LocalizerServiceBean;
import org.oscm.i18nservice.local.LocalizerServiceLocal;
import org.oscm.internal.intf.CategorizationService;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.ParameterType;
import org.oscm.internal.types.enumtypes.ParameterValueType;
import org.oscm.internal.types.enumtypes.ServiceAccessType;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.enumtypes.ServiceType;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;
import org.oscm.internal.vo.VOCategory;
import org.oscm.marketplace.bean.CategorizationServiceBean;
import org.oscm.marketplace.bean.MarketplaceServiceBean;
import org.oscm.serviceprovisioningservice.bean.SearchServiceInternalBean;
import org.oscm.serviceprovisioningservice.bean.ServiceProvisioningServiceBean;
import org.oscm.serviceprovisioningservice.bean.TagServiceBean;
import org.oscm.serviceprovisioningservice.local.TagServiceLocal;
import org.oscm.test.EJBTestBase;
import org.oscm.test.data.Marketplaces;
import org.oscm.test.data.Organizations;
import org.oscm.test.data.Products;
import org.oscm.test.data.Scenario;
import org.oscm.test.data.Subscriptions;
import org.oscm.test.data.TechnicalProducts;
import org.oscm.test.data.Udas;
import org.oscm.test.ejb.FifoJMSQueue;
import org.oscm.test.ejb.TestContainer;
import org.oscm.triggerservice.bean.TriggerQueueServiceBean;
import org.oscm.triggerservice.local.TriggerMessage;
import org.oscm.types.enumtypes.UdaTargetType;
import org.oscm.types.exceptions.InvalidUserSession;

public class IndexRequestMasterListenerIT extends EJBTestBase {

    private static final String TEMP_INDEX_BASE_DIR = "tempIndexDir";
    private DataService dm;
    private static FifoJMSQueue indexerQueue;
    private PlatformUser user;
    private LocalizerServiceLocal locSvc;
    private TagServiceLocal tagSvc;
    private Marketplace mpGlobal;
    private TechnicalProduct techProd;
    private int svcCounter;
    private long categoryKey;
    private static String sysPropertyBaseDir = null;
    private IndexRequestMasterListener irl;

    private static final String locale = "en";

    private static final List<LocalizedObjectTypes> localizedAttributes = Arrays
            .asList(LocalizedObjectTypes.PRODUCT_MARKETING_NAME,
                    LocalizedObjectTypes.PRODUCT_MARKETING_DESC,
                    LocalizedObjectTypes.PRODUCT_SHORT_DESCRIPTION,
                    LocalizedObjectTypes.PRICEMODEL_DESCRIPTION);

    private static final List<String> expectedIndexedAttributes = Arrays
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
    private static final List<String> expectedIndexedAttributesForSubscription = Arrays
            .asList("dataContainer.purchaseOrderNumber",
                    "dataContainer.subscriptionId");
    private static final List<String> expectedIndexedAttributesForParameter = Arrays
            .asList("dataContainer.value");
    private static final List<String> expectedIndexedAttributesForUda = Arrays
            .asList("dataContainer.udaValue");
    private static final List<String> expectedIndexedAttributesForUdaDefs = Arrays
            .asList("dataContainer.defaultValue");

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

    @Override
    protected void setup(final TestContainer container) throws Exception {
        enableHibernateSearchListeners(true);
        indexerQueue.clear();
        container.enableInterfaceMocking(true);
        container.addBean(new DataServiceBean() {
            @Override
            public PlatformUser getCurrentUser() {
                if (user == null) {
                    throw new InvalidUserSession("No user yet.");
                }
                return user;
            }
        });
        container.addBean(new TriggerQueueServiceBean() {
            @Override
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

        irl = new IndexRequestMasterListener();
        dm = container.get(DataService.class);
        irl.dm = dm;

        locSvc = container.get(LocalizerServiceLocal.class);
        tagSvc = container.get(TagServiceLocal.class);
        final CategorizationService cs = container
                .get(CategorizationService.class);

        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
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
                dm.createQuery("delete from ParameterDefinition")
                        .executeUpdate();
                dm.createQuery("delete from RoleDefinition").executeUpdate();
                dm.createQuery("delete from PricedEvent").executeUpdate();
                dm.createQuery("delete from Event").executeUpdate();
                dm.createQuery("delete from TechnicalProductOperation")
                        .executeUpdate();
                dm.createQuery("delete from TechnicalProduct").executeUpdate();

                return null;
            }
        });

        // run scenario setup
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // perform base setup
                Scenario.setup(container, true);
                techProd = Scenario.createTechnicalService(dm, "techProdId");
                tagSvc.updateTags(techProd, "en",
                        Arrays.asList(new Tag("en", "english tag")));
                return null;
            }
        });

        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                createOrganizationRoles(dm);
                Organization operatorOrg = Organizations.createOrganization(dm,
                        OrganizationRoleType.PLATFORM_OPERATOR);
                mpGlobal = Marketplaces.createGlobalMarketplace(operatorOrg,
                        GLOBAL_MARKETPLACE_NAME, dm);
                return null;
            }
        });

        // set temp index base directory and empty possibly existing index
        System.setProperty("hibernate.search.default.indexBase",
                TEMP_INDEX_BASE_DIR);
        user = new PlatformUser();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                container.login("admin", ROLE_MARKETPLACE_OWNER);
                VOCategory cat = new VOCategory();
                cat.setName("name en");
                cat.setCategoryId("id");
                cat.setMarketplaceId(mpGlobal.getMarketplaceId());
                user.setOrganization(dm.getReference(Marketplace.class,
                        mpGlobal.getKey()).getOrganization());
                cs.saveCategories(Arrays.asList(cat), null, "en");
                List<VOCategory> list = cs.getCategories(
                        mpGlobal.getMarketplaceId(), "en");
                categoryKey = list.get(0).getKey();
                return null;
            }
        });
        emptyCurrentIndex();
        assertDocsInIndex("Index must contain 0 document at test start", 0);
        svcCounter = 0;
    }

    @Test
    public void testInitIndexForFulltextSearch_NoIndexPresentEmptyDB()
            throws Throwable {
        assertDocsInIndex("Index must have no documents before indexing", 0);
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(true);
                return null;
            }
        });
        assertDocsInIndex("Index must have no documents after indexing", 0);
    }

    @Test
    public void testInitIndexForFulltextSearch_NoIndexPresentDataAvailable()
            throws Throwable {
        addItemsToDatabase(3);
        // manually delete the index
        emptyCurrentIndex();
        assertDocsInIndex("Index must have no documents before indexing", 0);
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(false);
                return null;
            }
        });
        assertDocsInIndex("Index must contain 3 documents after indexing", 3);
    }

    @Ignore
    @Test
    public void testInitIndexForFulltextSearch_IndexPresentNoForceSet()
            throws Throwable {

        // first have a dummy call to setup the scenario etc.
        emptyCurrentIndex();
        addItemsToDatabase(1);
        emptyCurrentIndex();
        // now create 3 elements on the empty index
        addItemsToDatabase(3);
        assertDocsInIndex(
                "Index must contain 3 documents due to automatic indexing", 3);
        // manually delete the index
        emptyCurrentIndex();
        addItemsToDatabase(1);
        assertDocsInIndex(
                "Index must contain 1 document due to automatic indexing", 1);
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(false);
                return null;
            }
        });
        assertDocsInIndex(
                "Index must still contain 1 document after indexing (although the db contains 4 items)",
                1);
    }

    @Ignore
    @Test
    public void testInitIndexForFulltextSearch_IndexPresentForceSet()
            throws Throwable {
        // first have a dummy call to setup the scenario etc.
        emptyCurrentIndex();
        addItemsToDatabase(1); // db contains 1 product
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(true);
                return null;
            }
        });

        emptyCurrentIndex();
        // now create 3 elements on the empty index, db contains 4 products
        addItemsToDatabase(3);
        assertDocsInIndex(
                "Index must contain 3 documents due to automatic indexing", 3);
        // manually delete the index
        emptyCurrentIndex();
        addItemsToDatabase(1); // db contains 5 products
        assertDocsInIndex(
                "Index must contain 1 document due to automatic indexing", 1);
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(true);
                return null;
            }
        });

        assertDocsInIndex("Index must contain " + 5
                + " documents after indexing (and so does the db)", 5);
    }

    /**
     * When deleting a catalog entry we expect that the system requests the
     * respective product to be indexed again.
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteCatalogEntry() throws Exception {

        final List<String> ids = addItemsToDatabase(1);
        final List<Long> keys = new ArrayList<Long>();

        assertNotNull(ids);
        assertEquals(1, ids.size());

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                Product prod = new Product();
                prod.setProductId(ids.get(0));
                prod.setVendor(Scenario.getSupplier());
                prod = (Product) dm.getReferenceByBusinessKey(prod);
                keys.add(Long.valueOf(prod.getKey()));
                CatalogEntry ce = prod.getCatalogEntries().get(0);
                dm.remove(ce);
                dm.flush();
                return null;
            }
        });

        assertNotNull(indexerQueue);
        Object object = indexerQueue.remove();
        assertNotNull(object);
        assertTrue(object instanceof ObjectMessage);
        Serializable message = ((ObjectMessage) object).getObject();
        assertNotNull(message);
        assertTrue(message instanceof IndexRequestMessage);
        IndexRequestMessage irm = (IndexRequestMessage) message;
        assertEquals("Wrong key expected - ", keys.get(0),
                Long.valueOf(irm.getKey()));
        assertEquals("Wrong class - ", Product.class.getName(), irm
                .getObjectClass().getName());
    }

    @Test
    public void testProductUpdateWithCustomerCopy_B9670() throws Exception {

        final List<String> ids = addItemsToDatabase(1);

        assertNotNull(ids);
        assertEquals(1, ids.size());

        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(true);
                return null;
            }
        });

        createProductCopy(ids.get(0), ServiceType.CUSTOMER_TEMPLATE);

        emptyCurrentIndex();

        // when
        modifyShortDescription(ids.get(0));

        // then two products in index
        assertDocsInIndex("Index must contain 2 entries", 2);
    }

    @Test
    public void getProductAndCopiesForIndexUpdate_InactivePartnerCopy_B9670()
            throws Exception {
        final List<String> ids = addItemsToDatabase(1);

        assertNotNull(ids);
        assertEquals(1, ids.size());

        createProductCopy(ids.get(0), ServiceType.PARTNER_TEMPLATE,
                ServiceStatus.INACTIVE);

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                Product prod = new Product();
                prod.setProductId(ids.get(0));
                prod.setVendor(Scenario.getSupplier());
                prod = (Product) dm.getReferenceByBusinessKey(prod);

                List<Product> products = irl
                        .getProductAndCopiesForIndexUpdate(prod);
                assertEquals(2, products.size());
                assertTrue("Keys equal.", products.get(0).getKey() != products
                        .get(1).getKey());
                return null;
            }
        });

    }

    @Test
    public void getProductAndCopiesForIndexUpdate_SuspendedCustomerCopy_B9670()
            throws Exception {
        final List<String> ids = addItemsToDatabase(1);

        assertNotNull(ids);
        assertEquals(1, ids.size());

        createProductCopy(ids.get(0), ServiceType.CUSTOMER_TEMPLATE,
                ServiceStatus.SUSPENDED);

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                Product prod = new Product();
                prod.setProductId(ids.get(0));
                prod.setVendor(Scenario.getSupplier());
                prod = (Product) dm.getReferenceByBusinessKey(prod);

                List<Product> products = irl
                        .getProductAndCopiesForIndexUpdate(prod);
                assertEquals(2, products.size());
                return null;
            }
        });

    }

    @Test
    public void getProductAndCopiesForIndexUpdate_OtherCopy_B9670()
            throws Exception {
        final List<String> ids = addItemsToDatabase(1);

        assertNotNull(ids);
        assertEquals(1, ids.size());

        createProductCopy(ids.get(0), ServiceType.TEMPLATE);

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                Product prod = new Product();
                prod.setProductId(ids.get(0));
                prod.setVendor(Scenario.getSupplier());
                prod = (Product) dm.getReferenceByBusinessKey(prod);

                List<Product> products = irl
                        .getProductAndCopiesForIndexUpdate(prod);
                assertEquals(1, products.size());
                assertEquals(prod.getKey(), products.get(0).getKey());
                return null;
            }
        });

    }

    @Test
    public void testProductUpdateWithPartnerCopy() throws Exception {

        final List<String> ids = addItemsToDatabase(1);

        assertNotNull(ids);
        assertEquals(1, ids.size());

        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(true);
                return null;
            }
        });

        createProductCopy(ids.get(0), ServiceType.PARTNER_TEMPLATE);

        emptyCurrentIndex();

        // when
        modifyShortDescription(ids.get(0));

        // then two products in index
        assertDocsInIndex("Index must contain 2 entries", 2);
    }

    @Test
    public void testProductUpdateWithNormalCopy() throws Exception {

        final List<String> ids = addItemsToDatabase(1);

        assertNotNull(ids);
        assertEquals(1, ids.size());

        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                irl.initIndexForFulltextSearch(true);
                return null;
            }
        });

        createProductCopy(ids.get(0), ServiceType.TEMPLATE);

        emptyCurrentIndex();

        // when
        modifyShortDescription(ids.get(0));

        // then one product in index
        assertDocsInIndex("Index must contain 1 entries", 1);
    }

    @Test
    public void testDeleteProductForPriceModel_Bug9893() throws Exception {
        // given
        // create price model and product
        final PriceModel pm = new PriceModel();
        runTX(new Callable<Product>() {
            @Override
            public Product call() throws Exception {
                String prodId = "prodId";
                final Product prod = Products.createProduct(
                        Scenario.getSupplier(), techProd, true, prodId, "pMId"
                                + svcCounter, mpGlobal, dm);
                pm.setProduct(prod);
                dm.persist(pm);
                dm.flush();
                return prod;
            }
        });

        // delete product
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Product pd = dm.getReference(Product.class, pm.getProduct()
                        .getKey());
                dm.remove(pd);
                dm.flush();
                return null;
            }
        });

        // verify the price model is not null
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                PriceModel priceModel = dm.getReference(PriceModel.class,
                        pm.getKey());
                assertNotNull(priceModel);
                assertNull(priceModel.getProduct());
                try {
                    // when
                    irl.handleIndexing(priceModel, ModificationType.ADD);
                    // then: no exception printed
                } catch (IllegalArgumentException e) {
                    fail();
                }
                return null;
            }
        });

    }

    /**
     * When deleting a technical product tag we expect that the system requests
     * the respective technical product (resulting in this marketable products)
     * to be indexed again.
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteTag() throws Exception {

        assertNotNull("No technical product present", techProd);
        final List<Long> keys = new ArrayList<Long>();

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                Tag tag = new Tag("es", "justTest");

                // Read or create tag
                Tag foundTag = (Tag) dm.find(tag);
                if (foundTag == null) {
                    dm.persist(tag);
                    foundTag = tag;
                }

                TechnicalProductTag tpt = new TechnicalProductTag();
                tpt.setTag(foundTag);
                tpt.setTechnicalProduct(techProd);
                dm.persist(tpt);
                dm.flush();
                flushQueue(indexerQueue, irl, 1000);
                keys.add(Long.valueOf(tpt.getKey()));
                return null;
            }
        });

        assertTrue("tag key expected", keys.size() > 0);

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                TechnicalProductTag tpt = dm.getReference(
                        TechnicalProductTag.class, keys.get(0).longValue());
                assertNotNull("Tag expected", tpt);
                // access getters to avoid lazy loading problems
                tpt.getTechnicalProduct().getOrganization();
                dm.remove(tpt);
                dm.flush();
                return null;
            }
        });

        assertNotNull(indexerQueue);
        Object object = indexerQueue.remove();
        assertNotNull(object);
        assertTrue(object instanceof ObjectMessage);
        Serializable message = ((ObjectMessage) object).getObject();
        assertNotNull(message);
        assertTrue(message instanceof IndexRequestMessage);
        IndexRequestMessage irm = (IndexRequestMessage) message;
        assertEquals("Wrong key - ", techProd.getKey(), irm.getKey());
        assertEquals("Wrong class - ", TechnicalProduct.class.getName(), irm
                .getObjectClass().getName());
    }

    private void assertDocsInIndex(final String comment,
            final int expectedNumDocs) throws Exception {
        Boolean evaluationTookPlace = runTX(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean evaluatedIndex = false;
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    SearchFactory searchFactory = fullTextSession
                            .getSearchFactory();
                    IndexReader reader = searchFactory.getIndexReaderAccessor()
                            .open(Product.class);

                    try {
                        assertEquals(comment, expectedNumDocs, reader.numDocs());
                        if (expectedNumDocs > 0) {
                            final FieldInfos indexedFieldNames = ReaderUtil
                                    .getMergedFieldInfos(reader);
                            for (String expectedAttr : expectedIndexedAttributes) {
                                assertNotNull("attribute " + expectedAttr
                                        + " does not exist in index: "
                                        + indexedFieldNames,
                                        indexedFieldNames
                                                .fieldInfo(expectedAttr));
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
                                    expectedIndexedAttributes.size() + 2,
                                    indexedFieldNames.size());
                            evaluatedIndex = true;
                        }
                    } finally {
                        searchFactory.getIndexReaderAccessor().close(reader);
                    }
                }

                return Boolean.valueOf(evaluatedIndex);
            }
        });

        if (expectedNumDocs > 0) {
            Assert.assertTrue("Index not found, no evaluation took place",
                    evaluationTookPlace.booleanValue());
        }
    }

    private void emptyCurrentIndex() throws Exception {
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    fullTextSession.purgeAll(Product.class);
                }

                return null;
            }
        });

    }

    private List<String> addItemsToDatabase(final int num) throws Exception {

        final List<String> createdProductIds = new ArrayList<String>();

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                for (int i = 0; i < num; i++) {
                    svcCounter++;
                    String prodId = "prodId" + svcCounter;
                    Product prod = Products.createProduct(
                            Scenario.getSupplier(), techProd, true, prodId,
                            "pMId" + svcCounter, mpGlobal, dm);
                    dm.persist(prod);
                    createdProductIds.add(prodId);
                }
                dm.flush();

                return null;
            }
        });

        // also, set visible in catalog to true for every created product
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

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
                        locSvc.storeLocalizedResource(locale, prod.getKey(),
                                type,
                                "MY_PRODUCT_" + prodId + " " + type.toString());
                    }
                }
                dm.flush();

                return null;
            }
        });
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                flushQueue(indexerQueue, irl, 1000);
                return null;
            }
        });
        return createdProductIds;
    }

    private void flushQueue(FifoJMSQueue queue,
            IndexRequestMasterListener reciever, int limit) {
        Assert.assertNotNull(queue);
        Assert.assertNotNull(reciever);
        try {
            Object message = null;
            do {
                message = queue.remove();
                if (message instanceof Message) {
                    reciever.onMessage((Message) message);
                }
            } while (message != null && (--limit > 0));
        } catch (NoSuchElementException e) {
            // ignore
        }
    }

    private void createProductCopy(final String srcProductId,
            final ServiceType serviceType) throws Exception {
        createProductCopy(srcProductId, serviceType, ServiceStatus.ACTIVE);
    }

    private void createProductCopy(final String srcProductId,
            final ServiceType serviceType, final ServiceStatus serviceStatus)
            throws Exception {
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                Product prod = new Product();
                prod.setProductId(srcProductId);
                prod.setVendor(Scenario.getSupplier());
                prod = (Product) dm.getReferenceByBusinessKey(prod);
                Organization customer = (serviceType == ServiceType.CUSTOMER_TEMPLATE) ? Scenario
                        .getCustomer() : null;
                Product copy = prod.copyForCustomer(customer);
                copy.setStatus(serviceStatus);
                copy.setType(serviceType);
                dm.persist(copy);

                dm.flush();
                flushQueue(indexerQueue, irl, 1000);
                return null;
            }
        });
    }

    private void modifyShortDescription(final String id) throws Exception {
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Product prod = new Product();
                prod.setProductId(id);
                prod.setVendor(Scenario.getSupplier());
                prod = (Product) dm.getReferenceByBusinessKey(prod);

                locSvc.storeLocalizedResource(locale, prod.getKey(),
                        LocalizedObjectTypes.PRODUCT_SHORT_DESCRIPTION,
                        "Short description");
                dm.flush();
                flushQueue(indexerQueue, irl, 1000);
                return null;
            }
        });
    }

    @Test
    public void testIndexSubscriptions_validStatus() throws Throwable {
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        addSubscriptionToDatabase(SubscriptionStatus.EXPIRED);
        addSubscriptionToDatabase(SubscriptionStatus.PENDING);
        addSubscriptionToDatabase(SubscriptionStatus.PENDING_UPD);
        addSubscriptionToDatabase(SubscriptionStatus.SUSPENDED);
        addSubscriptionToDatabase(SubscriptionStatus.SUSPENDED_UPD);
        emptySubscriptionIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexSubscriptions(fullTextSession);
                }
                return null;
            }
        });
        // One subscription is created during the common setup, and so the
        // actual number of subscriptions is one greater than that of
        // subscriptions that are created and eligible for indexing.
        assertSubscriptionDocsInIndex(
                "Index must contain 7 document due to automatic indexing", 7,
                expectedIndexedAttributesForSubscription.size(),
                expectedIndexedAttributesForSubscription);
    }

    @Test
    public void testIndexSubscriptions_invalidStatus() throws Throwable {
        addSubscriptionToDatabase(SubscriptionStatus.DEACTIVATED);
        addSubscriptionToDatabase(SubscriptionStatus.INVALID);
        emptySubscriptionIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexSubscriptions(fullTextSession);
                }
                return null;
            }
        });
        // One subscription is created during the common setup, and so the
        // actual number of subscriptions is one greater than that of
        // subscriptions that are created and eligible for indexing.
        assertSubscriptionDocsInIndex(
                "Index must contain 1 document due to automatic indexing", 1,
                expectedIndexedAttributesForSubscription.size(),
                expectedIndexedAttributesForSubscription);
    }

    @Test
    public void testIndexSubscriptions_variousStatus() throws Throwable {
        addSubscriptionToDatabase(SubscriptionStatus.ACTIVE);
        addSubscriptionToDatabase(SubscriptionStatus.DEACTIVATED);
        addSubscriptionToDatabase(SubscriptionStatus.EXPIRED);
        addSubscriptionToDatabase(SubscriptionStatus.INVALID);
        addSubscriptionToDatabase(SubscriptionStatus.PENDING);
        addSubscriptionToDatabase(SubscriptionStatus.PENDING_UPD);
        addSubscriptionToDatabase(SubscriptionStatus.SUSPENDED);
        addSubscriptionToDatabase(SubscriptionStatus.SUSPENDED_UPD);
        emptySubscriptionIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexSubscriptions(fullTextSession);
                }
                return null;
            }
        });
        // One subscription is created during the common setup, and so the
        // actual number of subscriptions is one greater than that of
        // subscriptions that are created and eligible for indexing.
        assertSubscriptionDocsInIndex(
                "Index must contain 7 document due to automatic indexing", 7,
                expectedIndexedAttributesForSubscription.size(),
                expectedIndexedAttributesForSubscription);
    }

    private void addSubscriptionToDatabase(final SubscriptionStatus status)
            throws Exception {

        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Organization seller = Organizations.createOrganization(dm,
                        OrganizationRoleType.SUPPLIER);
                Product product = Products.createProduct(
                        seller.getOrganizationId(), "prodId", "techProd", dm);
                Subscription sub = Subscriptions.createSubscription(dm,
                        seller.getOrganizationId(), product);
                sub.setStatus(status);
                dm.persist(sub);
                dm.flush();

                return null;
            }
        });
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                flushQueue(indexerQueue, irl, 1000);
                return null;
            }
        });
    }

    private void emptySubscriptionIndex() throws Exception {
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    fullTextSession.purgeAll(Subscription.class);
                }

                return null;
            }
        });

    }

    private void assertSubscriptionDocsInIndex(final String comment,
            final int expectedNumDocs, final int expectedNumIndexedAttributes,
            final List<String> expectedAttributes) throws Exception {
        Boolean evaluationTookPlace = runTX(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean evaluatedIndex = false;
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    SearchFactory searchFactory = fullTextSession
                            .getSearchFactory();
                    IndexReader reader = searchFactory.getIndexReaderAccessor()
                            .open(Subscription.class);

                    try {
                        assertEquals(comment, expectedNumDocs, reader.numDocs());
                        if (expectedNumDocs > 0) {
                            final FieldInfos indexedFieldNames = ReaderUtil
                                    .getMergedFieldInfos(reader);
                            for (String expectedAttr : expectedAttributes) {
                                assertNotNull("attribute " + expectedAttr
                                        + " does not exist in index: "
                                        + indexedFieldNames,
                                        indexedFieldNames
                                                .fieldInfo(expectedAttr));
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
            }
        });

        if (expectedNumDocs > 0) {
            Assert.assertTrue("Index not found, no evaluation took place",
                    evaluationTookPlace.booleanValue());
        }
    }

    @Test
    public void testIndexParameters_ParameterDefinitionValueType()
            throws Throwable {
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.ACTIVE);
        createParameter(ParameterValueType.BOOLEAN, true,
                SubscriptionStatus.ACTIVE);
        createParameter(ParameterValueType.DURATION, true,
                SubscriptionStatus.ACTIVE);
        createParameter(ParameterValueType.ENUMERATION, true,
                SubscriptionStatus.ACTIVE);
        createParameter(ParameterValueType.INTEGER, true,
                SubscriptionStatus.ACTIVE);
        createParameter(ParameterValueType.LONG, true,
                SubscriptionStatus.ACTIVE);
        emptyParameterIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexParameters(fullTextSession);
                }
                return null;
            }
        });
        // One parameter is created during the common setup, and so the actual
        // number of parameters is one greater than that of parameters that are
        // created and eligible for indexing.
        assertParameterDocsInIndex(
                "Index must contain 2 document due to automatic indexing", 2,
                expectedIndexedAttributesForParameter.size(),
                expectedIndexedAttributesForParameter);
    }

    @Test
    public void testIndexParameters_SubscriptionStatus() throws Throwable {
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.ACTIVE);
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.DEACTIVATED);
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.EXPIRED);
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.INVALID);
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.PENDING);
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.PENDING_UPD);
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.SUSPENDED);
        createParameter(ParameterValueType.STRING, true,
                SubscriptionStatus.SUSPENDED_UPD);
        emptyParameterIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexParameters(fullTextSession);
                }
                return null;
            }
        });
        // One parameter is created during the common setup, and so the actual
        // number of parameters is one greater than that of parameters that are
        // created and eligible for indexing.
        assertParameterDocsInIndex(
                "Index must contain 7 document due to automatic indexing", 7,
                expectedIndexedAttributesForParameter.size(),
                expectedIndexedAttributesForParameter);
    }

    @Test
    public void testIndexParameters_withoutSubscription() throws Throwable {
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.ACTIVE);
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.DEACTIVATED);
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.EXPIRED);
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.INVALID);
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.PENDING);
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.PENDING_UPD);
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.SUSPENDED);
        createParameter(ParameterValueType.STRING, false,
                SubscriptionStatus.SUSPENDED_UPD);
        emptyParameterIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexParameters(fullTextSession);
                }
                return null;
            }
        });
        // One parameter is created during the common setup, and so the actual
        // number of parameters is one greater than that of parameters that are
        // created and eligible for indexing.
        assertParameterDocsInIndex(
                "Index must contain 1 document due to automatic indexing", 1,
                expectedIndexedAttributesForParameter.size(),
                expectedIndexedAttributesForParameter);
    }

    private void emptyParameterIndex() throws Exception {
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    fullTextSession.purgeAll(Parameter.class);
                }

                return null;
            }
        });

    }

    private Parameter createParameter(
            final ParameterValueType parameterValueType,
            final boolean createSubscription,
            final SubscriptionStatus subscriptionStatus) throws Exception {
        final Parameter parameter = runTX(new Callable<Parameter>() {
            @Override
            public Parameter call() throws Exception {

                Organization tp = Organizations.createOrganization(dm,
                        OrganizationRoleType.TECHNOLOGY_PROVIDER);
                ParameterDefinition pd = new ParameterDefinition();
                pd.setParameterId("parameterIndexingTest");
                pd.setParameterType(ParameterType.PLATFORM_PARAMETER);
                pd.setValueType(parameterValueType);

                Parameter parameter = new Parameter();
                parameter.setValue("parameterIndexingTest");

                ParameterSet parameterSet = new ParameterSet();
                parameter.setParameterDefinition(pd);
                parameter.setParameterSet(parameterSet);
                dm.persist(pd);
                Product product = Products.createProduct(
                        tp.getOrganizationId(), "parameterIndexingTest",
                        "techProd", dm);
                if (createSubscription) {
                    Subscription subscription = Subscriptions
                            .createSubscription(dm, tp.getOrganizationId(),
                                    product.getProductId(),
                                    "parameterIndexingTest", tp);
                    subscription.setStatus(subscriptionStatus);
                    Product subscriptionProduct = subscription.getProduct();
                    parameterSet.setProduct(subscriptionProduct);
                    subscriptionProduct.setParameterSet(parameterSet);
                    dm.persist(subscriptionProduct);
                    dm.persist(subscription);
                }
                dm.persist(parameterSet);
                dm.persist(parameter);
                dm.flush();
                return parameter;
            }
        });
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                flushQueue(indexerQueue, irl, 1000);
                return null;
            }
        });
        return parameter;
    }

    private void assertParameterDocsInIndex(final String comment,
            final int expectedNumDocs, final int expectedNumIndexedAttributes,
            final List<String> expectedAttributes) throws Exception {
        Boolean evaluationTookPlace = runTX(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean evaluatedIndex = false;
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    SearchFactory searchFactory = fullTextSession
                            .getSearchFactory();
                    IndexReader reader = searchFactory.getIndexReaderAccessor()
                            .open(Parameter.class);

                    try {
                        assertEquals(comment, expectedNumDocs, reader.numDocs());
                        if (expectedNumDocs > 0) {
                            final FieldInfos indexedFieldNames = ReaderUtil
                                    .getMergedFieldInfos(reader);
                            for (String expectedAttr : expectedAttributes) {
                                assertNotNull("attribute " + expectedAttr
                                        + " does not exist in index: "
                                        + indexedFieldNames,
                                        indexedFieldNames
                                                .fieldInfo(expectedAttr));
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
            }
        });

        if (expectedNumDocs > 0) {
            Assert.assertTrue("Index not found, no evaluation took place",
                    evaluationTookPlace.booleanValue());
        }
    }

    @Test
    public void testindexUdas_Customer() throws Throwable {
        createUdaOnCustomer("UDA1", "UDA2", "UDA3");
        emptyUdaIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexUdas(fullTextSession);
                }
                return null;
            }
        });
        // Four Udas are created during the common setup, and so the actual
        // number of Udas is one greater than that of Udas that are created and
        // eligible for indexing.
        assertUdaDocsInIndex(
                "Index must contain 7 document due to automatic indexing", 7,
                expectedIndexedAttributesForUda.size(),
                expectedIndexedAttributesForUda);
    }

    @Test
    public void testindexUdas_Subscription() throws Throwable {
        createUdaOnSubscription("UDA1", "UDA2", "UDA3", "UDA4");
        emptyUdaIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexUdas(fullTextSession);
                }
                return null;
            }
        });
        // Four Udas are created during the common setup, and so the actual
        // number of Udas is one greater than that of Udas that are created and
        // eligible for indexing.
        assertUdaDocsInIndex(
                "Index must contain 8 document due to automatic indexing", 8,
                expectedIndexedAttributesForUda.size(),
                expectedIndexedAttributesForUda);
    }

    @Test
    public void testindexUdas_CustomerAndSubscription() throws Throwable {
        createUdaOnCustomer("UDA_C-1", "UDA_C-2", "UDA_C-3", "UDA_C-4");
        createUdaOnSubscription("UDA_S-1", "UDA_S-2", "UDA_S-3", "UDA_S-4",
                "UDA_S-5");
        emptyUdaIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexUdas(fullTextSession);
                }
                return null;
            }
        });
        // Four Udas are created during the common setup, and so the actual
        // number of Udas is one greater than that of Udas that are created and
        // eligible for indexing.
        assertUdaDocsInIndex(
                "Index must contain 13 document due to automatic indexing", 13,
                expectedIndexedAttributesForUda.size(),
                expectedIndexedAttributesForUda);
    }

    @Test
    public void testindexUdaDefs() throws Throwable {
        createUdaDefOnCustomer("UDADef1", "UDADef2", "UDADef3");
        emptyUdaDefIndex();
        runTX(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    irl.indexUdaDefs(fullTextSession);
                }
                return null;
            }
        });
        assertUdaDefsDocsInIndex(
                "Index must contain 3 document due to automatic indexing", 3,
                expectedIndexedAttributesForUdaDefs.size(),
                expectedIndexedAttributesForUdaDefs);
    }

    private List<Uda> createUdaOnCustomer(final String... udaId)
            throws Exception {
        List<Uda> result = runTX(new Callable<List<Uda>>() {

            @Override
            public List<Uda> call() throws Exception {
                Organization supplier = Organizations.createOrganization(dm,
                        OrganizationRoleType.SUPPLIER);
                Organization customer = Organizations.createOrganization(dm,
                        OrganizationRoleType.CUSTOMER);

                List<Uda> result = new ArrayList<Uda>();
                for (String id : udaId) {
                    UdaDefinition def = Udas.createUdaDefinition(dm, supplier,
                            UdaTargetType.CUSTOMER, id, null,
                            UdaConfigurationType.USER_OPTION_MANDATORY);
                    Uda uda = Udas.createUda(dm, customer, def, "42");
                    result.add(uda);
                }
                return result;
            }
        });
        return result;
    }

    private List<Uda> createUdaDefOnCustomer(final String... udaId)
            throws Exception {
        List<Uda> result = runTX(new Callable<List<Uda>>() {

            @Override
            public List<Uda> call() throws Exception {
                Organization supplier = Organizations.createOrganization(dm,
                        OrganizationRoleType.SUPPLIER);
                Organization customer = Organizations.createOrganization(dm,
                        OrganizationRoleType.CUSTOMER);

                List<Uda> result = new ArrayList<>();
                for (String id : udaId) {
                    UdaDefinition def = Udas.createUdaDefinition(dm, supplier,
                            UdaTargetType.CUSTOMER_SUBSCRIPTION, id, "default definition value",
                            UdaConfigurationType.USER_OPTION_MANDATORY);
                    Uda uda = Udas.createUda(dm, customer, def, null);
                    result.add(uda);
                }
                return result;
            }
        });
        return result;
    }

    private List<Uda> createUdaOnSubscription(final String... udaId)
            throws Exception {
        List<Uda> result = runTX(new Callable<List<Uda>>() {

            @Override
            public List<Uda> call() throws Exception {
                Organization provider = Organizations.createOrganization(dm,
                        OrganizationRoleType.TECHNOLOGY_PROVIDER);
                Organization supplier = Organizations.createOrganization(dm,
                        OrganizationRoleType.SUPPLIER);
                Organization customer = Organizations.createOrganization(dm,
                        OrganizationRoleType.CUSTOMER);

                TechnicalProduct tp = TechnicalProducts.createTechnicalProduct(
                        dm, provider, "TPID", false, ServiceAccessType.LOGIN);
                Product prod = Products.createProduct(supplier, tp, false,
                        "PRODID", null, dm);
                Subscription sub = Subscriptions.createSubscription(dm,
                        customer.getOrganizationId(), prod.getProductId(),
                        "SUBID", supplier);
                List<Uda> result = new ArrayList<Uda>();
                for (String id : udaId) {
                    UdaDefinition def = Udas.createUdaDefinition(dm, supplier,
                            UdaTargetType.CUSTOMER_SUBSCRIPTION, id, null,
                            UdaConfigurationType.USER_OPTION_MANDATORY);
                    Uda uda = Udas.createUda(dm, sub, def, "42");
                    result.add(uda);
                }
                return result;
            }
        });
        return result;
    }

    private void emptyUdaIndex() throws Exception {
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    fullTextSession.purgeAll(Uda.class);
                }

                return null;
            }
        });

    }

    private void emptyUdaDefIndex() throws Exception {
        runTX(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    fullTextSession.purgeAll(UdaDefinition.class);
                }

                return null;
            }
        });

    }

    private void assertUdaDocsInIndex(final String comment,
            final int expectedNumDocs, final int expectedNumIndexedAttributes,
            final List<String> expectedAttributes) throws Exception {
        Boolean evaluationTookPlace = runTX(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean evaluatedIndex = false;
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    SearchFactory searchFactory = fullTextSession
                            .getSearchFactory();
                    IndexReader reader = searchFactory.getIndexReaderAccessor()
                            .open(Uda.class);

                    try {
                        assertEquals(comment, expectedNumDocs, reader.numDocs());
                        if (expectedNumDocs > 0) {
                            final FieldInfos indexedFieldNames = ReaderUtil
                                    .getMergedFieldInfos(reader);
                            for (String expectedAttr : expectedAttributes) {
                                assertNotNull("attribute " + expectedAttr
                                        + " does not exist in index: "
                                        + indexedFieldNames,
                                        indexedFieldNames
                                                .fieldInfo(expectedAttr));
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
            }
        });

        if (expectedNumDocs > 0) {
            Assert.assertTrue("Index not found, no evaluation took place",
                    evaluationTookPlace.booleanValue());
        }
    }

    private void assertUdaDefsDocsInIndex(final String comment,
            final int expectedNumDocs, final int expectedNumIndexedAttributes,
            final List<String> expectedAttributes) throws Exception {
        Boolean evaluationTookPlace = runTX(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                boolean evaluatedIndex = false;
                Session session = dm.getSession();
                if (session != null) {
                    FullTextSession fullTextSession = Search
                            .getFullTextSession(session);
                    SearchFactory searchFactory = fullTextSession
                            .getSearchFactory();
                    IndexReader reader = searchFactory.getIndexReaderAccessor()
                            .open(UdaDefinition.class);

                    try {
                        assertEquals(comment, expectedNumDocs, reader.numDocs());
                        if (expectedNumDocs > 0) {
                            final FieldInfos indexedFieldNames = ReaderUtil
                                    .getMergedFieldInfos(reader);
                            for (String expectedAttr : expectedAttributes) {
                                assertNotNull("attribute " + expectedAttr
                                        + " does not exist in index: "
                                        + indexedFieldNames,
                                        indexedFieldNames
                                                .fieldInfo(expectedAttr));
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
            }
        });

        if (expectedNumDocs > 0) {
            Assert.assertTrue("Index not found, no evaluation took place",
                    evaluationTookPlace.booleanValue());
        }
    }

}