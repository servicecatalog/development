/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                              
 *  Author: Dirk Bernsau                                                      
 *                                                                              
 *  Creation Date: July 26, 2011                                                      
 *                                                                              
 *  Completion Time: July 26, 2011                                              
 *                                                                              
 *******************************************************************************/

package org.oscm.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.Query;

import org.apache.lucene.index.IndexReader;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.backend.impl.jms.AbstractJMSHibernateSearchController;
import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.CatalogEntry;
import org.oscm.domobjects.Category;
import org.oscm.domobjects.DomainObject;
import org.oscm.domobjects.Marketplace;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.PriceModel;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.TechnicalProduct;
import org.oscm.domobjects.TechnicalProductTag;
import org.oscm.domobjects.Uda;
import org.oscm.domobjects.UdaDefinition;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.domobjects.index.IndexReinitRequestMessage;
import org.oscm.domobjects.index.IndexRequestMessage;
import org.oscm.internal.types.enumtypes.ParameterValueType;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.enumtypes.ServiceType;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.types.enumtypes.UdaTargetType;

/**
 * Message driven bean to handle the index request objects sent by the business
 * logic.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "UserName", propertyValue = "jmsuser"),
        @ActivationConfigProperty(propertyName = "Password", propertyValue = "jmsuser") }, name = "jmsQueue", mappedName = "jms/bss/masterIndexerQueue")
public class IndexRequestMasterListener extends
        AbstractJMSHibernateSearchController implements MessageListener {

    private final static Log4jLogger logger = LoggerFactory
            .getLogger(IndexRequestMasterListener.class);

    // batch size might have to be adapted (as big as possible, but small enough
    // to avoid OutOfMemoryException)
    private static final int BATCH_SIZE = 1000;

    @EJB(beanInterface = DataService.class)
    public DataService dm;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void onMessage(Message message) {
        if (!(message instanceof ObjectMessage)) {
            logger.logError(
                    LogMessageIdentifier.ERROR_RECEIVE_MESSAGE_INTERPRETED_FAILED,
                    String.valueOf(message));
            return;
        }

        logger.logDebug("Received object message from queue",
                Log4jLogger.SYSTEM_LOG);
        try {
            // obtain the actual request object
            ObjectMessage om = (ObjectMessage) message;
            Serializable messageObject = om.getObject();
            if (messageObject instanceof IndexRequestMessage) {
                IndexRequestMessage msg = (IndexRequestMessage) messageObject;
                try {
                    DomainObject<?> object = dm
                            .getReference(msg.getObjectClass(), msg.getKey());
                    handleIndexing(object, msg.getType());
                } catch (ObjectNotFoundException e) {
                    logger.logDebug(
                            "Cannot find requested object " + msg.toString(),
                            Log4jLogger.SYSTEM_LOG);
                }
            } else if (messageObject instanceof IndexReinitRequestMessage) {
                IndexReinitRequestMessage msg = (IndexReinitRequestMessage) messageObject;
                initIndexForFulltextSearch(msg.isForceIndexCreation());
            } else {
                super.onMessage(message);
            }
        } catch (Throwable e) {
            // we cannot abort here, no exception can be thrown either. So just
            // log the exception and put the process to error state.
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR_EVALUATE_MESSAGE_FAILED);
        }
    }

    /**
     * FIXME: Master indexer needs to be re-factored. It must be stupid simply
     * indexing the passed domain objects without additional BL and additional
     * queries.<br>
     * 
     * Returns the list of products to be indexed. In case if the template
     * product is updated, beside of the index fields for the this product also
     * these for broker, reseller and customer specific copies must be updated
     * as well.
     */
    List<Product> getProductAndCopiesForIndexUpdate(Product product) {
        List<Product> productsToUpdate = new ArrayList<>();
        if (!product.isCopy()) {
            Query query = dm.createNamedQuery(
                    "Product.getProductsForTemplateIndexUpdate");
            query.setParameter("template", product);
            query.setParameter("state",
                    EnumSet.of(ServiceStatus.ACTIVE, ServiceStatus.INACTIVE,
                            ServiceStatus.SUSPENDED, ServiceStatus.OBSOLETE));
            query.setParameter("type", EnumSet.of(ServiceType.PARTNER_TEMPLATE,
                    ServiceType.CUSTOMER_TEMPLATE));
            productsToUpdate.addAll(ParameterizedTypes
                    .list(query.getResultList(), Product.class));
        }
        productsToUpdate.add(product);
        return productsToUpdate;
    }

    void handleIndexing(DomainObject<?> object, ModificationType modType) {
        if (object instanceof Product) {
            Product product = (Product) object;
            // Bug 9670: In case if a template of a partner or customer product
            // is modified we must also write the copies to the index
            if (modType == ModificationType.MODIFY) {
                List<Product> productsToUpdate = getProductAndCopiesForIndexUpdate(
                        product);
                handleListIndexing(ParameterizedTypes.list(productsToUpdate,
                        Product.class));
                return;
            }
            handleObjectIndexing(object);
            return;
        }
        if (object instanceof PriceModel) {
            handleObjectIndexing(((PriceModel) object).getProduct());
            return;
        }
        if (object instanceof CatalogEntry) {
            handleObjectIndexing(((CatalogEntry) object).getProduct());
            return;
        }
        if (object instanceof TechnicalProductTag) {
            TechnicalProduct tp = ((TechnicalProductTag) object)
                    .getTechnicalProduct();
            handleListIndexing(tp.getProducts());
            return;
        }
        if (object instanceof TechnicalProduct) {
            handleListIndexing(((TechnicalProduct) object).getProducts());
            return;
        }
        if (object instanceof Category) {
            // This only happens when categories are "renamed". It will NOT be
            // invoked when categories are deleted.
            final Query servicesQuery = dm
                    .createNamedQuery("Category.findServices");
            servicesQuery.setParameter("categoryKey",
                    Long.valueOf(((Category) object).getKey()));
            handleListIndexing(ParameterizedTypes
                    .list(servicesQuery.getResultList(), Product.class));
            return;
        }
        if (object instanceof Subscription) {
            Subscription subscription = (Subscription) object;
            if (isSubscriptionNotDeactivatedOrInvalid(subscription)) {
                handleObjectIndexing(object);
            }
            return;
        }
        if (object instanceof Parameter) {
            Parameter parameter = (Parameter) object;
            if (parameter.getParameterDefinition()
                    .getValueType() == ParameterValueType.STRING) {
                Product product = parameter.getParameterSet().getProduct();
                if (product != null) {
                    Subscription subscription = product.getOwningSubscription();
                    if (subscription != null
                            && isSubscriptionNotDeactivatedOrInvalid(
                                    subscription)) {
                        handleObjectIndexing(subscription);
                    }
                }
                return;
            }
        }
        if (object instanceof Uda) {
            Uda uda = (Uda) object;
            UdaDefinition udaDef = uda.getUdaDefinition();

            if (udaDef.getTargetType() == UdaTargetType.CUSTOMER_SUBSCRIPTION
                    && udaDef
                            .getConfigurationType() != UdaConfigurationType.SUPPLIER) {
                try {
                    Subscription sub = dm.getReference(Subscription.class,
                            uda.getTargetObjectKey());
                    handleObjectIndexing(sub);
                } catch (ObjectNotFoundException e) {
                    logger.logDebug("uda target didn't match any subscription",
                            Log4jLogger.SYSTEM_LOG);
                }
            }
            return;
        }
        if (object instanceof UdaDefinition) {
            UdaDefinition udaDef = (UdaDefinition) object;

            List<Product> prodList = udaDef.getOrganization().getProducts();
            List<Subscription> subList = new ArrayList<>();
            for (Product prod : prodList) {
                subList.add(prod.getOwningSubscription());
            }
            handleListIndexing(subList);
            return;
        }
    }

    private boolean isSubscriptionNotDeactivatedOrInvalid(
            Subscription subscription) {
        return subscription.getStatus() != SubscriptionStatus.DEACTIVATED
                && subscription.getStatus() != SubscriptionStatus.INVALID;
    }

    private void handleListIndexing(
            Collection<? extends DomainObject<?>> list) {
        Session session = getSession();
        if (list == null || session == null) {
            return;
        }

        FullTextSession fts = Search.getFullTextSession(session);
        Transaction tx = fts.beginTransaction();

        for (DomainObject<?> obj : list) {
            if (obj != null) {
                fts.index(obj);
            }
        }

        tx.commit();
    }

    private void handleObjectIndexing(Object parameter) {

        Session session = getSession();
        if (parameter == null || session == null) {
            return;
        }

        FullTextSession fts = Search.getFullTextSession(session);
        Transaction tx = fts.beginTransaction();

        fts.index(parameter);

        tx.commit();
    }

    @Override
    protected Session getSession() {
        return dm.getSession();
    }

    @Override
    protected void cleanSessionIfNeeded(Session session) {
        // nothing to do as we use container management
    }

    protected void initIndexForFulltextSearch(final boolean force) {
        dm.find(PlatformUser.class, Long.valueOf(1000));
        FullTextSession fullTextSession = Search
                .getFullTextSession(getSession());

        // check if index is already present (via query)
        boolean isIndexEmpty = true;
        SearchFactory searchFactory = fullTextSession.getSearchFactory();
        IndexReader reader = searchFactory.getIndexReaderAccessor()
                .open(Product.class, Subscription.class);

        Transaction tx = fullTextSession.beginTransaction();

        try {
            isIndexEmpty = reader.numDocs() == 0;
        } finally {
            searchFactory.getIndexReaderAccessor().close(reader);
        }

        if (!isIndexEmpty) {
            if (!force) {
                // if so and force is NOT set, return without
                // indexing
                return;
            } else {
                // otherwise delete previous index
                fullTextSession.purgeAll(Product.class);
                fullTextSession.purgeAll(Subscription.class);
            }
        }

        // index all entities relevant for full text search
        // for it: get all products from all global marketplaces
        // (full text search only available for global marketplaces
        // by definition)

        Query query = dm.createNamedQuery("Marketplace.getAll");
        List<Marketplace> globalMps = ParameterizedTypes
                .list(query.getResultList(), Marketplace.class);

        fullTextSession.setFlushMode(FlushMode.MANUAL);
        fullTextSession.setCacheMode(CacheMode.IGNORE);

        for (Marketplace mp : globalMps) {
            // call find to ensure entity manager is registered
            dm.find(mp);

            StringBuffer nativeQueryString = new StringBuffer();
            nativeQueryString
                    .append("SELECT p FROM Product p WHERE EXISTS (SELECT c FROM CatalogEntry c WHERE c.marketplace.key = ")
                    .append(mp.getKey())
                    .append(" AND c.dataContainer.visibleInCatalog=TRUE AND c.product.key = p.key AND p.dataContainer.status = '")
                    .append(ServiceStatus.ACTIVE.name()).append("')");

            org.hibernate.Query productsOnMpQuery = fullTextSession
                    .createQuery(nativeQueryString.toString());

            ScrollableResults results = productsOnMpQuery
                    .scroll(ScrollMode.FORWARD_ONLY);

            int index = 0;
            while (results.next()) {
                index++;
                fullTextSession.index(results.get(0));
                if (index % BATCH_SIZE == 0) {
                    fullTextSession.flushToIndexes();
                    fullTextSession.clear();
                }
            }
            results.close();

        }

        // index all active subscriptions
        org.hibernate.Query objectQuery = fullTextSession.createQuery(
                "SELECT s FROM Subscription s WHERE s.dataContainer.status NOT IN ('"
                        + SubscriptionStatus.DEACTIVATED.name() + "','"
                        + SubscriptionStatus.INVALID.name() + "')");
        ScrollableResults results = objectQuery.scroll(ScrollMode.FORWARD_ONLY);

        int index = 0;
        while (results.next()) {
            index++;
            fullTextSession.index(results.get(0));
            if (index % BATCH_SIZE == 0) {
                fullTextSession.flushToIndexes();
                fullTextSession.clear();
            }
        }

        results.close();

        tx.commit(); // index is written at commit time
    }

}
