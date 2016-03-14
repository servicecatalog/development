package org.oscm.subscriptionservice.bean;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.Subscription;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.subscriptionservice.search.LuceneQueryBuilder;
import org.oscm.validation.ArgumentValidator;

/**
 * Service for full text subscription searching
 */
@Stateless
@Local(SubscriptionSearchService.class)
public class SubscriptionSearchServiceBean implements SubscriptionSearchService {

    private static final Log4jLogger logger = LoggerFactory
            .getLogger(SubscriptionSearchServiceBean.class);

    @EJB
    private DataService dm;

    private static String DEFAULT_LOCALE = "en";

    @Override
    public Collection<Long> searchSubscriptions(String searchPhrase)
            throws InvalidPhraseException, ObjectNotFoundException {

        ArgumentValidator.notEmptyString("searchPhrase", searchPhrase);

        String locale = dm.getCurrentUser().getLocale();
        List<Long> voList = new ArrayList<>(100);

        try {
            Session session = getDm().getSession();
            if (session != null) {
                FullTextSession fts = Search.getFullTextSession(session);

                // (1) search in actual locale
                org.apache.lucene.search.Query query = getLuceneQuery(
                        LuceneQueryBuilder.getSubscriptionQuery(searchPhrase,
                                locale, DEFAULT_LOCALE, false), locale, fts);
                voList.addAll(searchViaLucene(query, fts));

                if (!DEFAULT_LOCALE.equals(locale)) {
                    // (2) search in default locale
                    query = getLuceneQuery(
                            LuceneQueryBuilder.getSubscriptionQuery(searchPhrase,
                                locale, DEFAULT_LOCALE, true), DEFAULT_LOCALE, fts);
                    voList.addAll(searchViaLucene(query, fts));
                }
            }
        } catch (ParseException e) {
            InvalidPhraseException ipe = new InvalidPhraseException(e,
                    searchPhrase);
            logger.logDebug(ipe.getMessage());
            throw ipe;
        }
        return voList;
    }

    private DataService getDm() {
        return dm;
    }

    private org.apache.lucene.search.Query getLuceneQuery(String searchString,
                                                          String locale, FullTextSession fts)
            throws ParseException {
        Analyzer analyzer = fts.getSearchFactory().getAnalyzer(Product.class);
        try {
            // try to find the correct analyzer for the locale
            analyzer = fts.getSearchFactory().getAnalyzer(locale);
        } catch (SearchException e) {
            // default will hold
        }

        // use analyzer for actual text part of query
        org.apache.lucene.search.Query purchaseOrderNumber;
        org.apache.lucene.search.Query subIdQuery;


        purchaseOrderNumber = getQuery(searchString, locale, analyzer, "dataContainer.purchaseOrderNumber");

        subIdQuery = getQuery(searchString, locale, analyzer, "dataContainer.subscriptionId");


        // now construct final query
        BooleanQuery query = new BooleanQuery();
        query.add(purchaseOrderNumber, BooleanClause.Occur.MUST);
        query.add(subIdQuery, BooleanClause.Occur.MUST);
        return query;
    }

    private Query getQuery(String searchString, String locale, Analyzer analyzer, String field) throws ParseException {
        Query purchaseOrderNumber;QueryParser parser = new QueryParser(Version.LUCENE_31,
                field + locale, analyzer);
        purchaseOrderNumber = parser.parse(searchString);
        return purchaseOrderNumber;
    }

    private Set<Long> searchViaLucene(org.apache.lucene.search.Query query,
                                 FullTextSession fts)
            throws HibernateException {
        Set<Long> set = new LinkedHashSet<>();
        FullTextQuery ftQuery = fts.createFullTextQuery(query, Subscription.class);
        ftQuery.setProjection("key");
        List<?> result = ftQuery.list();
        if (result != null) {
            for (Object item : result) {
                set.add((Long) ((Object[]) item)[0]);
            }
        }
        return set;
    }
}
