package org.oscm.subscriptionservice.bean;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.lucene.queryParser.ParseException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
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

    @Override
    public Collection<Long> searchSubscriptions(String searchPhrase)
            throws InvalidPhraseException, ObjectNotFoundException {

        ArgumentValidator.notEmptyString("searchPhrase", searchPhrase);
        List<Long> voList = new ArrayList<>(100);

        try {
            Session session = getDm().getSession();
            if (session != null) {
                FullTextSession fts = Search.getFullTextSession(session);
                org.apache.lucene.search.Query query = getLuceneQueryForSubscription(
                        searchPhrase, fts);
                voList.addAll(searchSubscriptionViaLucene(query, fts));
                query = getLuceneQueryForParameter(searchPhrase, fts);
                voList.addAll(searchParametersViaLucene(query, fts));
                query = getLuceneQueryForUda(searchPhrase, fts);
                voList.addAll(searchUdasViaLucene(query, fts));
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

    private org.apache.lucene.search.Query getLuceneQueryForSubscription(String searchString, FullTextSession fts)
            throws ParseException {
        QueryBuilder qb = fts.getSearchFactory().buildQueryBuilder().forEntity(Subscription.class).get();
        return qb.keyword().onFields(getSearchFieldsForSubscription()).
                matching(searchString).createQuery();
    }

    private org.apache.lucene.search.Query getLuceneQueryForParameter(String searchString, FullTextSession fts)
            throws ParseException {
        QueryBuilder qb = fts.getSearchFactory().buildQueryBuilder().forEntity(Parameter.class).get();
        return qb.keyword().onFields(getSearchFieldsForParameter()).
                matching(searchString).createQuery();
    }

    private org.apache.lucene.search.Query getLuceneQueryForUda(String searchString, FullTextSession fts)
            throws ParseException {
        QueryBuilder qb = fts.getSearchFactory().buildQueryBuilder().forEntity(Uda.class).get();
        return qb.keyword().onFields(getSearchFieldsForUda()).
                matching(searchString).createQuery();
    }

    private Set<Long> searchSubscriptionViaLucene(org.apache.lucene.search.Query query,
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

    private Set<Long> searchParametersViaLucene(org.apache.lucene.search.Query query,
                                      FullTextSession fts)
            throws HibernateException {
        Set<Long> set = new LinkedHashSet<>();
        FullTextQuery ftQuery = fts.createFullTextQuery(query, Parameter.class);
        List<Parameter> result = ftQuery.list();
        for (Parameter item : result) {
            set.add(item.getParameterSet().getProduct().getOwningSubscription().getKey());
        }
        return set;
    }

    private Set<Long> searchUdasViaLucene(org.apache.lucene.search.Query query,
                                      FullTextSession fts)
            throws HibernateException {
        Set<Long> set = new LinkedHashSet<>();
        FullTextQuery ftQuery = fts.createFullTextQuery(query, Uda.class);
        List<Uda> result = ftQuery.list();
        for (Uda item : result) {
            set.add(item.getTargetObjectKey());
        }
        return set;
    }

    private String[] getSearchFieldsForSubscription() {
        return new String[]{"dataContainer.purchaseOrderNumber", "dataContainer.subscriptionId"};
    }

    private String[] getSearchFieldsForUda() {
        return new String[]{"dataContainer.udaValue"};
    }

    private String[] getSearchFieldsForParameter() {
        return new String[]{"dataContainer.value"};
    }
}
