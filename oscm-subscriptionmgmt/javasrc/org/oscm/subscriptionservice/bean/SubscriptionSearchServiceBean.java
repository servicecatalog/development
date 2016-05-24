package org.oscm.subscriptionservice.bean;

import java.math.BigInteger;
import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;
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
        searchPhrase = searchPhrase.trim();
        Set<Long> voList = new HashSet<>(100);
        try {
            Session session = getDm().getSession();
            if (session != null) {
                FullTextSession fts = Search.getFullTextSession(session);
                searchPhrase = searchPhrase.toLowerCase();
                String[] split = searchPhrase.replaceAll("\"", "").split(" ");
                Set<Long> runResult;
                for (int i = 0; i < split.length; i++) {
                    String singleString = split[i];
                    singleString = singleString.trim();
                    if (singleString.length() == 0) {
                        continue;
                    }
                    org.apache.lucene.search.Query query = getLuceneQueryForFields(
                            singleString, getSearchFieldsForSubscription()[0]);
                    runResult = searchSubscriptionViaLucene(query, fts);
                    logger.logDebug("I have found " + voList.size()
                            + " subscriptions by referenceId");

                    query = getLuceneQueryForFields(singleString,
                            getSearchFieldsForSubscription()[1]);
                    runResult.addAll(searchSubscriptionViaLucene(query, fts));
                    logger.logDebug("I have found "
                            + voList.size()
                            + " subscriptions by referenceId and subscriptionId");

                    query = getLuceneQueryForFields(singleString,
                            getSearchFieldsForParameter());
                    runResult.addAll(searchParametersViaLucene(query, fts));
                    logger.logDebug("I have found " + voList.size()
                            + " subscriptions by parameters value");

                    query = getLuceneQueryForFields(singleString,
                            getSearchFieldsForUda());
                    runResult.addAll(searchUdasViaLucene(query, fts));
                    logger.logDebug("I have found " + voList.size()
                            + " subscriptions by uda value");

                    if (i == 0) {
                        voList.addAll(runResult);
                    } else {
                        voList = findCommonIds(voList, runResult);
                    }
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

    private Set<Long> findCommonIds(Set<Long> voList, Set<Long> runResult) {
        Set<Long> result = new HashSet<>();
        Set<Long> shorter = voList.size() < runResult.size() ? voList
                : runResult;
        Set<Long> longer = voList.size() >= runResult.size() ? voList
                : runResult;
        for (Long aLong : shorter) {
            if (longer.contains(aLong)) {
                result.add(aLong);
            }
        }
        return result;
    }

    public DataService getDm() {
        return dm;
    }

    private org.apache.lucene.search.Query getLuceneQueryForFields(
            String searchString, String... fieldNames) throws ParseException {
        BooleanQuery bq = new BooleanQuery();
        if (isPhraseQuery(searchString)) {
            getPhraseQuery(searchString, fieldNames, bq);
        } else {
            getTermQuery(searchString, fieldNames, bq);
        }
        return bq;
    }

    private boolean isPhraseQuery(String searchString) {
        // Uncomment if you want to phrase querying
        // return searchString.startsWith("\"") && searchString.endsWith("\"");
        return false;
    }

    private void getTermQuery(String searchString, String[] fieldNames,
            BooleanQuery bq) {
        String[] split = searchString.replaceAll("\"", "").split(" ");
        TermQuery wq;
        int counter = 0;
        BooleanQuery internal;
        for (String singleString : split) {
            internal = new BooleanQuery();
            while (counter < fieldNames.length) {
                wq = new TermQuery(new Term(fieldNames[counter++],
                        QueryParser.escape(singleString)));
                internal.add(wq, Occur.SHOULD);
            }
            bq.add(internal, Occur.SHOULD);
            counter = 0;
        }
    }

    private void getPhraseQuery(String searchString, String[] fieldNames,
            BooleanQuery bq) {
        String[] split = searchString.replaceAll("\"", "").split(" ");
        PhraseQuery phraseQuery;
        int counter = 0;
        for (String singleString : split) {
            phraseQuery = new PhraseQuery();
            while (counter < fieldNames.length) {
                phraseQuery.add(new Term(fieldNames[counter++], QueryParser
                        .escape(singleString)));
            }
            bq.add(phraseQuery, Occur.MUST);
            counter = 0;
        }
    }

    private Set<Long> searchSubscriptionViaLucene(
            org.apache.lucene.search.Query query, FullTextSession fts)
            throws HibernateException {
        Set<Long> set = new LinkedHashSet<>();
        FullTextQuery ftQuery = fts.createFullTextQuery(query,
                Subscription.class);
        ftQuery.setProjection("key");
        List<?> result = ftQuery.list();
        for (Object item : result) {
            set.add((Long) ((Object[]) item)[0]);
        }
        return set;
    }

    private Set<Long> searchParametersViaLucene(
            org.apache.lucene.search.Query query, FullTextSession fts)
            throws HibernateException {
        Set<Long> set = new LinkedHashSet<>();
        FullTextQuery ftQuery = fts.createFullTextQuery(query, Parameter.class);
        List<Parameter> result = ftQuery.list();
        for (Parameter item : result) {
            set.add(item.getParameterSet().getProduct().getOwningSubscription()
                    .getKey());
        }
        return set;
    }

    private Set<Long> searchUdasViaLucene(org.apache.lucene.search.Query query,
            FullTextSession fts) throws HibernateException {
        Set<Long> set = new LinkedHashSet<>();
        FullTextQuery ftQuery = fts.createFullTextQuery(query, Uda.class);
        List<Uda> result = ftQuery.list();
        SubscriptionDao subscriptionDao = getSubscriptionDao();
        for (Uda item : result) {
            if (!UdaConfigurationType.SUPPLIER.equals(item.getUdaDefinition()
                    .getConfigurationType())) {
                if (UdaTargetType.CUSTOMER.equals(item.getUdaDefinition()
                        .getTargetType())) {
                    // Uda for organization, so get all subscription for
                    // organization
                    List<Object[]> subs = subscriptionDao
                            .getSubscriptionIdsForOrg(dm.getCurrentUser(),
                                    getStates(), item.getUdaDefinition()
                                            .getOrganizationKey());
                    for (Object[] subColumns : subs) {
                        set.add(((BigInteger) subColumns[0]).longValue());
                    }
                    break;
                } else {
                    set.add(item.getTargetObjectKey());
                }
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
        return new String[] { "dataContainer.purchaseOrderNumber",
                "dataContainer.subscriptionId" };
    }

    private String[] getSearchFieldsForUda() {
        return new String[] { "dataContainer.udaValue" };
    }

    private String[] getSearchFieldsForParameter() {
        return new String[] { "dataContainer.value" };
    }
}
