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
import java.util.Collections;
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
import org.oscm.domobjects.*;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.domobjects.index.IndexReinitRequestMessage;
import org.oscm.domobjects.index.IndexRequestMessage;
import org.oscm.internal.types.enumtypes.ParameterValueType;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.enumtypes.ServiceType;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.LogMessageIdentifier;

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
                    DomainObject<?> object = dm.getReference(
                            msg.getObjectClass(), msg.getKey());
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
        List<Product> productsToUpdate = new ArrayList<Product>();
        if (!product.isCopy()) {
            Query query = dm
                    .createNamedQuery("Product.getProductsForTemplateIndexUpdate");
            query.setParameter("template", product);
            query.setParameter("state", EnumSet.of(ServiceStatus.ACTIVE,
                    ServiceStatus.INACTIVE, ServiceStatus.SUSPENDED,
                    ServiceStatus.OBSOLETE));
            query.setParameter("type", EnumSet.of(ServiceType.PARTNER_TEMPLATE,
                    ServiceType.CUSTOMER_TEMPLATE));
            productsToUpdate.addAll(ParameterizedTypes.list(
                    query.getResultList(), Product.class));
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
                List<Product> productsToUpdate = getProductAndCopiesForIndexUpdate(product);
                handleProductIndexing(ParameterizedTypes.list(productsToUpdate,
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
            handleProductIndexing(tp.getProducts());
            return;
        }
        if (object instanceof TechnicalProduct) {
            handleProductIndexing(((TechnicalProduct) object).getProducts());
            return;
        }
        if (object instanceof Category) {
            // This only happens when categories are "renamed". It will NOT be
            // invoked when categories are deleted.
            final Query servicesQuery = dm
                    .createNamedQuery("Category.findServices");
            servicesQuery.setParameter("categoryKey",
                    Long.valueOf(((Category) object).getKey()));
            handleProductIndexing(ParameterizedTypes.list(
                    servicesQuery.getResultList(), Product.class));
            return;
        }
        if (object instanceof Subscription) {
            Subscription subscription = (Subscription) object;
            if (isSubscriptionDeactivatedOrInvalid(subscription)) {
                handleObjectIndexing(object);
            }
            return;
        }
        if (object instanceof Parameter) {
            Parameter parameter = (Parameter) object;
            if (parameter.getParameterDefinition().getValueType() == ParameterValueType.STRING) {
                Product product = parameter.getParameterSet().getProduct();
                if (product != null) {
                    Subscription subscription = product.getOwningSubscription();
                    if (subscription != null
                            && isSubscriptionDeactivatedOrInvalid(subscription)) {
                        handleObjectIndexing(parameter);
                    }
                }
                return;
            }
        }
        if (object instanceof Uda) {
            handleObjectIndexing(object);
            return;
        }
        if (object instanceof UdaDefinition) {
            handleObjectIndexing(object);
            return;
        }
    }

    private boolean isSubscriptionDeactivatedOrInvalid(Subscription subscription) {
        return subscription.getStatus() != SubscriptionStatus.DEACTIVATED
                && subscription.getStatus() != SubscriptionStatus.INVALID;
    }

    private void handleProductIndexing(Collection<Product> products) {
        Session session = getSession();
        if (session != null) {
            FullTextSession fts = Search.getFullTextSession(session);
            for (Product p : products) {
                if (p != null) {
                    fts.index(p);
                }
            }
        }
    }

    private void handleObjectIndexing(Object parameter) {
        if (parameter == null) {
            return;
        }
        Session session = getSession();
        if (session != null) {
            FullTextSession fts = Search.getFullTextSession(session);
            fts.index(parameter);
        }
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
        IndexReader reader = searchFactory.getIndexReaderAccessor().open(
                Product.class);

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
            }
        }

        // index all entities relevant for full text search
        // for it: get all products from all global marketplaces
        // (full text search only available for global marketplaces
        // by definition)

        Query query = dm.createNamedQuery("Marketplace.getAll");
        List<Marketplace> globalMps = ParameterizedTypes.list(
                query.getResultList(), Marketplace.class);

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
        indexSubscriptions(fullTextSession);
        indexParameters(fullTextSession);
        indexUdas(fullTextSession);
        tx.commit(); // index is written at commit time
    }

    protected void indexSubscriptions(FullTextSession fullTextSession) {
        org.hibernate.Query objectQuery = fullTextSession
                .createQuery("SELECT s FROM Subscription s WHERE s.dataContainer.status NOT IN ('"
                        + SubscriptionStatus.DEACTIVATED.name()
                        + "','"
                        + SubscriptionStatus.INVALID.name() + "')");
        ScrollableResults results = objectQuery.scroll(ScrollMode.FORWARD_ONLY);
        indexObject(fullTextSession, results);
        results.close();
    }

    protected void indexParameters(FullTextSession fullTextSession) {
        org.hibernate.Query objectQuery = fullTextSession
                .createQuery("SELECT parameter FROM Parameter parameter, ParameterSet ps, Product product, Subscription s WHERE parameter.parameterDefinition.dataContainer.valueType = '"
                        + ParameterValueType.STRING.name()
                        + "' AND parameter.parameterSet.key = ps.key AND product.key = ps.product.key AND s.product.key = product.key AND s.dataContainer.status NOT IN ('"
                        + SubscriptionStatus.DEACTIVATED.name()
                        + "','"
                        + SubscriptionStatus.INVALID.name() + "')");
        ScrollableResults results = objectQuery.scroll(ScrollMode.FORWARD_ONLY);
        indexObject(fullTextSession, results);
        results.close();
    }

    protected void indexUdas(FullTextSession fullTextSession) {
        org.hibernate.Query objectQuery = fullTextSession
                .createQuery("SELECT uda FROM Uda uda");
        ScrollableResults results = objectQuery.scroll(ScrollMode.FORWARD_ONLY);
        indexObject(fullTextSession, results);
        results.close();
    }

    private void indexObject(FullTextSession fullTextSession,
            ScrollableResults results) {
        int index = 0;
        while (results.next()) {
            index++;
            fullTextSession.index(results.get(0));
            if (index % BATCH_SIZE == 0) {
                fullTextSession.flushToIndexes();
                fullTextSession.clear();
            }
        }
    }

}
