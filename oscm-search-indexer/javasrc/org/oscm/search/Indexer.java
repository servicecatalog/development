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

package org.oscm.search;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.persistence.Query;

import org.apache.lucene.index.IndexReader;
import org.hibernate.*;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchFactory;
import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Marketplace;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.Subscription;
import org.oscm.internal.types.enumtypes.ServiceStatus;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;

/**
 * Message driven bean to handle the index request objects sent by the business
 * logic.
 */
@Singleton
public class Indexer {

    private static final int BATCH_SIZE = 1000;

    @EJB(beanInterface = DataService.class)
    public DataService dm;

    public void initIndexForFulltextSearch(final boolean force) {
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
            nativeQueryString.append(
                    "SELECT p FROM Product p WHERE EXISTS (SELECT c FROM CatalogEntry c WHERE c.marketplace.key = ")
                    .append(mp.getKey())
                    .append(" AND c.dataContainer.visibleInCatalog=TRUE AND c.product.key = p.key AND p.dataContainer.status = :status)");

            org.hibernate.Query productsOnMpQuery = fullTextSession
                    .createQuery(nativeQueryString.toString());
            productsOnMpQuery.setParameter("status", ServiceStatus.ACTIVE);
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
                "SELECT s FROM Subscription s WHERE s.dataContainer.status NOT IN (:statuses)");
        objectQuery.setParameterList("statuses", new Object[] {
                SubscriptionStatus.DEACTIVATED, SubscriptionStatus.INVALID });
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

    private Session getSession() {
        return dm.getSession();
    }

}
