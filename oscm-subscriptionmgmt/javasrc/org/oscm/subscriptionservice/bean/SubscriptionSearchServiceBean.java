/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 05.02.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.bridge.SubscriptionClassBridge;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.subscriptionservice.dao.SubscriptionDao;
import org.oscm.types.enumtypes.LogMessageIdentifier;
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

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Long> searchSubscriptions(String searchPhrase)
            throws InvalidPhraseException, ObjectNotFoundException {
        ArgumentValidator.notEmptyString("searchPhrase", searchPhrase);

        FullTextEntityManager ftem = getFtem();

        Analyzer analyzer = ftem.getSearchFactory().getAnalyzer(
                "customanalyzer");
        MultiFieldQueryParser parser = getParser(analyzer);

        List<Subscription> list;
        try {
            org.apache.lucene.search.Query luceneQuery = parser
                    .parse(QueryParser.escape(searchPhrase));

            javax.persistence.Query jpaQuery = ftem.createFullTextQuery(
                    luceneQuery, Subscription.class);

            list = jpaQuery.getResultList();
        } catch (ParseException e) {
            logger.logError(
                    Log4jLogger.SYSTEM_LOG,
                    e,
                    LogMessageIdentifier.ERROR_SUBSCRIPTION_SEARCH_QUERY_PARSER_FAILED,
                    searchPhrase);
            throw new InvalidPhraseException(e, searchPhrase);
        }

        List<Long> result = new ArrayList<Long>();

        for (Subscription sub : list) {
            result.add(new Long(sub.getKey()));
        }

        return result;
    }

    public DataService getDm() {
        return dm;
    }

    public FullTextEntityManager getFtem() {
        EntityManager em = getDm().getEntityManager();

        return org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    }

    public MultiFieldQueryParser getParser(Analyzer analyzer) {
        MultiFieldQueryParser parser = new MultiFieldQueryParser(
                Version.LUCENE_36, new String[] {
                        SubscriptionClassBridge.NAME_SUBSCRIPTION_ID,
                        SubscriptionClassBridge.NAME_REFERENCE,
                        SubscriptionClassBridge.NAME_PARAMETER_VALUE,
                        SubscriptionClassBridge.NAME_UDA_VALUE }, analyzer);
        parser.setDefaultOperator(Operator.AND);

        return parser;
    }

    public SubscriptionDao getSubscriptionDao() {
        return new SubscriptionDao(dm);
    }
}
