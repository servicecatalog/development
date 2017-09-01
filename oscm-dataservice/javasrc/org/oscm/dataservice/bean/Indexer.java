/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Author: Dirk Bernsau
 *
 *  Creation Date: July 26, 2011
 *
 *  Completion Time: July 26, 2011
 *
 *******************************************************************************/

package org.oscm.dataservice.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.oscm.converter.ParameterizedTypes;
import org.oscm.domobjects.*;
import org.oscm.domobjects.enums.ModificationType;
import org.oscm.internal.types.enumtypes.*;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.types.enumtypes.UdaTargetType;

/**
 * Message driven bean to handle the index request objects sent by the business
 * logic.
 */
public class Indexer {

    private final static Log4jLogger logger = LoggerFactory
            .getLogger(Indexer.class);

    /**
     * It must be stupid simply
     * indexing the passed domain objects without additional BL and additional
     * queries.<br>
     *
     * Returns the list of products to be indexed. In case if the template
     * product is updated, beside of the index fields for the this product also
     * these for broker, reseller and customer specific copies must be updated
     * as well.
     */
    List<Product> getProductAndCopiesForIndexUpdate(Product product,
            EntityPersister persister) {
        List<Product> productsToUpdate = new ArrayList<>();
        if (!product.isCopy()) {
            Session session = getSession(persister);
            org.hibernate.Query query = session
                    .getNamedQuery("Product.getProductsForTemplateIndexUpdate");
            query.setParameter("template", product);
            query.setParameter("state",
                    EnumSet.of(ServiceStatus.ACTIVE, ServiceStatus.INACTIVE,
                            ServiceStatus.SUSPENDED, ServiceStatus.OBSOLETE));
            query.setParameter("type", EnumSet.of(ServiceType.PARTNER_TEMPLATE,
                    ServiceType.CUSTOMER_TEMPLATE));
            productsToUpdate.addAll(
                    ParameterizedTypes.list(query.list(), Product.class));
            session.close();
        }
        productsToUpdate.add(product);
        return productsToUpdate;
    }

    public void handleIndexing(DomainObject<?> object, ModificationType modType,
            EntityPersister persister) {
        if (object instanceof Product) {
            Product product = (Product) object;
            // Bug 9670: In case if a template of a partner or customer product
            // is modified we must also write the copies to the index
            if (modType == ModificationType.MODIFY) {
                List<Product> productsToUpdate = getProductAndCopiesForIndexUpdate(
                        product, persister);
                handleListIndexing(ParameterizedTypes.list(productsToUpdate,
                        Product.class), persister);
                return;
            }
            handleObjectIndexing(object, persister);
            return;
        }
        if (object instanceof PriceModel) {
            handleObjectIndexing(((PriceModel) object).getProduct(), persister);
            return;
        }
        if (object instanceof CatalogEntry) {
            handleObjectIndexing(((CatalogEntry) object).getProduct(),
                    persister);
            return;
        }
        if (object instanceof TechnicalProductTag) {
            TechnicalProduct tp = ((TechnicalProductTag) object)
                    .getTechnicalProduct();
            handleListIndexing(tp.getProducts(), persister);
            return;
        }
        if (object instanceof TechnicalProduct) {
            handleListIndexing(((TechnicalProduct) object).getProducts(),
                    persister);
            return;
        }
        if (object instanceof Category) {
            // This only happens when categories are "renamed". It will NOT be
            // invoked when categories are deleted.
            Session tmpSession = getSession(persister);
            final org.hibernate.Query servicesQuery = tmpSession
                    .getNamedQuery("Category.findServices");
            servicesQuery.setParameter("categoryKey", object.getKey());
            handleListIndexing(ParameterizedTypes.list(servicesQuery.list(),
                    Product.class), persister);
            tmpSession.close();
            return;
        }
        if (object instanceof Subscription) {
            Subscription subscription = (Subscription) object;
            if (isSubscriptionNotDeactivatedOrInvalid(subscription)) {
                handleObjectIndexing(object, persister);
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
                        handleObjectIndexing(subscription, persister);
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
                Session session = getSession(persister);
                Subscription sub = session.get(Subscription.class,
                        uda.getTargetObjectKey());
                if (sub == null) {
                    logger.logDebug("uda target didn't match any subscription",
                            Log4jLogger.SYSTEM_LOG);
                } else {
                    handleObjectIndexing(sub, persister);
                }
                session.close();
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
            handleListIndexing(subList, persister);
            return;
        }
    }

    private boolean isSubscriptionNotDeactivatedOrInvalid(
            Subscription subscription) {
        return subscription.getStatus() != SubscriptionStatus.DEACTIVATED
                && subscription.getStatus() != SubscriptionStatus.INVALID;
    }

    private void handleListIndexing(Collection<? extends DomainObject<?>> list,
            EntityPersister persister) {
        Session session = getSession(persister);
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
        session.close();
    }

    private void handleObjectIndexing(Object parameter,
            EntityPersister persister) {

        Session session = getSession(persister);
        if (parameter == null || session == null) {
            return;
        }

        FullTextSession fts = Search.getFullTextSession(session);
        Transaction tx = fts.beginTransaction();

        fts.index(parameter);

        tx.commit();
        session.close();
    }

    private Session getSession(EntityPersister persister) {
        return persister.getFactory().openTemporarySession();
    }
}
