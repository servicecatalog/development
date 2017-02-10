/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: July 15, 2011                                                      
 *                                                                              
 *  Completion Time: July 19, 2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.domobjects.bridge;

import java.util.Currency;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.ejb.EJBException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.hibernate.search.bridge.LuceneOptions;
import org.junit.Assert;
import org.junit.Test;

import org.oscm.dataservice.local.QueryBasedObjectFactory;
import org.oscm.domobjects.CatalogEntry;
import org.oscm.domobjects.DomainObjectTestBase;
import org.oscm.domobjects.Marketplace;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.SupportedCurrency;
import org.oscm.domobjects.TechnicalProduct;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.i18nservice.bean.LocalizerServiceBean;
import org.oscm.i18nservice.local.LocalizerServiceLocal;
import org.oscm.test.data.Marketplaces;
import org.oscm.test.data.Organizations;
import org.oscm.test.data.Products;
import org.oscm.test.data.TechnicalProducts;
import org.oscm.test.ejb.TestContainer;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.ServiceAccessType;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;

/**
 * @author Dirk Bernsau
 * 
 */
public class ProductClassBridgeIT extends DomainObjectTestBase {

    private Organization tpAndSupplier;
    private long productKey;
    private LocalizerServiceLocal localizer;

    private Properties expectedFields;

    private final String RESELLER_MP = "RESELLER_MP";
    private Organization supplier;
    private Organization customer;

    @Override
    public void setup(TestContainer container) throws Exception {
        super.setup(container);
        container.addBean(new LocalizerServiceBean());
        localizer = container.get(LocalizerServiceLocal.class);
    }

    @Override
    protected void dataSetup() throws Exception {
        SupportedCurrency sc = new SupportedCurrency();
        sc.setCurrency(Currency.getInstance("EUR"));
        mgr.persist(sc);
        Organization operator = Organizations.createOrganization(mgr,
                OrganizationRoleType.PLATFORM_OPERATOR);
        supplier = Organizations.createOrganization(mgr,
                OrganizationRoleType.SUPPLIER);
        customer = Organizations.createCustomer(mgr, supplier);

        Marketplaces.createGlobalMarketplace(operator, "FUJITSU", mgr);
        Marketplaces.createGlobalMarketplace(operator, "EST", mgr);
        Marketplaces.createGlobalMarketplace(operator, RESELLER_MP, mgr);
        expectedFields = new Properties();
    }

    @Test
    public void test1() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    productKey = createProductAndExpectedFields().getKey();
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    verifyIndexedFieldsForProduct(productKey);
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    @Test
    public void test2() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    Product product = createProductAndExpectedFields();
                    expectedFields.clear();
                    productKey = createAndPublishPartnerCopy(product).getKey();
                    // Bug 9784
                    // The ID of the marketplace where the partner publishes
                    // must be written in the index.
                    expectedFields.put(ProductClassBridge.MP_ID,
                            RESELLER_MP.toLowerCase());
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    verifyIndexedFieldsForProduct(productKey);
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    @Test
    public void test3() throws Throwable {
        try {
            runTX(new Callable<Void>() {
                public Void call() throws Exception {
                    Product product = createProductAndExpectedFields();
                    expectedFields.clear();
                    productKey = createAndPublishCustomerCopy(product).getKey();
                    expectedFields.put(ProductClassBridge.MP_ID, "est");
                    return null;
                }
            });
            runTX(new Callable<Void>() {
                public Void call() {
                    verifyIndexedFieldsForProduct(productKey);
                    return null;
                }
            });
        } catch (EJBException e) {
            throw e.getCause();
        }
    }

    private Product createProductAndExpectedFields()
            throws NonUniqueBusinessKeyException, ObjectNotFoundException {
        tpAndSupplier = Organizations.createOrganization(mgr,
                OrganizationRoleType.SUPPLIER,
                OrganizationRoleType.TECHNOLOGY_PROVIDER);

        // insert some products
        TechnicalProduct tProd = TechnicalProducts.createTechnicalProduct(mgr,
                tpAndSupplier, "TP_ID", false, ServiceAccessType.LOGIN);
        Product product = Products.createProduct(tpAndSupplier, tProd, true,
                "Product_1", null, mgr);

        productKey = product.getKey();

        mgr.flush();

        Marketplace marketplace = Marketplaces.findMarketplace(mgr, "FUJITSU");
        CatalogEntry entry = QueryBasedObjectFactory.createCatalogEntry(
                product, marketplace);
        entry.setVisibleInCatalog(true);
        mgr.persist(entry);

        marketplace = Marketplaces.findMarketplace(mgr, "EST");
        entry = QueryBasedObjectFactory
                .createCatalogEntry(product, marketplace);
        entry.setVisibleInCatalog(true);
        mgr.persist(entry);

        mgr.flush();

        String value = "Name_de";
        localizer.storeLocalizedResource("de", productKey,
                LocalizedObjectTypes.PRODUCT_MARKETING_NAME, value);
        expectedFields.put(ProductClassBridge.SERVICE_NAME + "de", value);

        value = "Name_en";
        localizer.storeLocalizedResource("en", productKey,
                LocalizedObjectTypes.PRODUCT_MARKETING_NAME, value);
        expectedFields.put(ProductClassBridge.SERVICE_NAME + "en", value);

        value = "Beschreibung";
        localizer.storeLocalizedResource("de", productKey,
                LocalizedObjectTypes.PRODUCT_MARKETING_DESC, value);
        expectedFields
                .put(ProductClassBridge.SERVICE_DESCRIPTION + "de", value);

        value = "Description";
        localizer.storeLocalizedResource("en", productKey,
                LocalizedObjectTypes.PRODUCT_MARKETING_DESC, value);
        expectedFields
                .put(ProductClassBridge.SERVICE_DESCRIPTION + "en", value);

        value = "Kurz";
        localizer.storeLocalizedResource("de", productKey,
                LocalizedObjectTypes.PRODUCT_SHORT_DESCRIPTION, value);
        expectedFields.put(ProductClassBridge.SERVICE_SHORT_DESC + "de", value);

        value = "Short";
        localizer.storeLocalizedResource("en", productKey,
                LocalizedObjectTypes.PRODUCT_SHORT_DESCRIPTION, value);
        expectedFields.put(ProductClassBridge.SERVICE_SHORT_DESC + "en", value);

        long pmKey = product.getPriceModel().getKey();
        value = "Free";
        localizer.storeLocalizedResource("en", pmKey,
                LocalizedObjectTypes.PRICEMODEL_DESCRIPTION, value);
        expectedFields.put(ProductClassBridge.PRICEMODEL_DESCRIPTION + "en",
                value);

        value = "Umasonst";
        localizer.storeLocalizedResource("de", pmKey,
                LocalizedObjectTypes.PRICEMODEL_DESCRIPTION, value);
        expectedFields.put(ProductClassBridge.PRICEMODEL_DESCRIPTION + "de",
                value);
        return product;

    }

    private void verifyIndexedFieldsForProduct(long productKey) {
        ProductClassBridge bridge = new ProductClassBridge();
        Document doc = new Document();

        Product product = mgr.find(Product.class, productKey);
        bridge.set("name", product, doc, mockLuceneOptions());
        Assert.assertNotNull("Indexed fields expected", doc.getFields());
        Properties fields = new Properties();
        for (Object o : doc.getFields()) {
            Assert.assertTrue("Field is not actually a field object",
                    o instanceof Field);
            Field field = (Field) o;
            Assert.assertNotNull("Field has no name", field.name());
            Assert.assertNotNull("Field " + field.name() + " has no value",
                    field.stringValue());
            fields.put(field.name(), field.stringValue());
        }
        for (Object key : expectedFields.keySet()) {
            Assert.assertTrue("Field " + key + " expected",
                    fields.containsKey(key));
            Assert.assertEquals("Wrong value for field " + key,
                    expectedFields.get(key), fields.get(key));
        }

    }

    private LuceneOptions mockLuceneOptions() {
        return new LuceneOptions() {

            public TermVector getTermVector() {
                return TermVector.NO;
            }

            public Store getStore() {
                return Store.NO;
            }

            public Index getIndex() {
                return Index.NOT_ANALYZED;
            }

            public float getBoost() {
                return 1F;
            }

            @Override
            public void addFieldToDocument(String arg0, String arg1,
                    Document arg2) {
            }

            @Override
            public void addNumericFieldToDocument(String arg0, Object arg1,
                    Document arg2) {
            }

            @Override
            public String indexNullAs() {
                return null;
            }

            @Override
            public boolean isCompressed() {
                return false;
            }
        };

    }

    /**
     * @throws ObjectNotFoundException
     * @throws NonUniqueBusinessKeyException
     */
    private Product createAndPublishPartnerCopy(Product srcProduct)
            throws NonUniqueBusinessKeyException, ObjectNotFoundException {
        // Publish on reseller marketplace
        Marketplace marketplace = Marketplaces
                .findMarketplace(mgr, RESELLER_MP);

        Product copy = Products.createProductResaleCopy(srcProduct, supplier,
                marketplace, mgr);
        mgr.flush();
        return copy;
    }

    /**
     * @throws ObjectNotFoundException
     * @throws NonUniqueBusinessKeyException
     */
    private Product createAndPublishCustomerCopy(Product srcProduct)
            throws NonUniqueBusinessKeyException, ObjectNotFoundException {
        Product copy = Products.createCustomerSpecifcProduct(mgr, customer,
                srcProduct, ServiceStatus.ACTIVE);
        mgr.flush();
        return copy;
    }
}
