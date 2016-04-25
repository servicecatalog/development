package org.oscm.subscriptionservice.bean;

import java.math.BigInteger;
import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.TermMatchingContext;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.subscriptionservice.dao.SubscriptionDao;
import org.oscm.types.enumtypes.UdaTargetType;
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
        searchPhrase = searchPhrase.toLowerCase();
        try {
            Session session = getDm().getSession();
            if (session != null) {
                FullTextSession fts = Search.getFullTextSession(session);
                org.apache.lucene.search.Query query = getLuceneQueryForClass(
                        searchPhrase, fts, Subscription.class, getSearchFieldsForSubscription());
                voList.addAll(searchSubscriptionViaLucene(query, fts));
                query = getLuceneQueryForClass(
                        searchPhrase, fts, Parameter.class, getSearchFieldsForParameter());
                voList.addAll(searchParametersViaLucene(query, fts));
                query = getLuceneQueryForClass(
                        searchPhrase, fts, Uda.class, getSearchFieldsForUda());
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

    public DataService getDm() {
        return dm;
    }

    private org.apache.lucene.search.Query getLuceneQueryForClass(String searchString, FullTextSession fts, Class clazz, String[] fieldNames)
            throws ParseException {
        QueryBuilder qb = fts.getSearchFactory().buildQueryBuilder().forEntity(clazz).get();
        String[] split = searchString.split(" ");
        BooleanQuery bq = new BooleanQuery();
        for (String s : split) {
            TermMatchingContext termMatchingContext = qb.keyword().wildcard().onField(fieldNames[0]);
            int counter = 1;
            while(counter < fieldNames.length) {
                termMatchingContext = termMatchingContext.andField(fieldNames[counter++]);
            }
            Query query = termMatchingContext.
                    matching("*" + QueryParser.escape(s) + "*").createQuery();
            bq.add(query, BooleanClause.Occur.MUST);
        }
        return bq;
    }

    private Set<Long> searchSubscriptionViaLucene(org.apache.lucene.search.Query query,
                                                  FullTextSession fts)
            throws HibernateException {
        Set<Long> set = new LinkedHashSet<>();
        FullTextQuery ftQuery = fts.createFullTextQuery(query, Subscription.class);
        ftQuery.setProjection("key");
        List<?> result = ftQuery.list();
        for (Object item : result) {
            set.add((Long) ((Object[]) item)[0]);
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
        SubscriptionDao subscriptionDao = getSubscriptionDao();
        for (Uda item : result) {
            if(UdaTargetType.CUSTOMER.equals(item.getUdaDefinition().getTargetType())) {
                //Uda for organization, so get all subscription for organization
                List<Object[]> subs = subscriptionDao.getSubscriptionIdsForOrg(
                        dm.getCurrentUser(), getStates());
                for (Object[] subColumns : subs) {
                    set.add(((BigInteger) subColumns[0]).longValue());
                }
                break;
            } else {
                set.add(item.getTargetObjectKey());
            }
        }
        return set;
    }

    private Set<SubscriptionStatus> getStates() {
        Set<SubscriptionStatus> retVal = new HashSet<>(20);
        retVal.addAll(Subscription.ASSIGNABLE_SUBSCRIPTION_STATUS);
        retVal.addAll(Subscription.VISIBLE_SUBSCRIPTION_STATUS);
        return retVal;
    }

    public SubscriptionDao getSubscriptionDao() {
        return new SubscriptionDao(dm);
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
