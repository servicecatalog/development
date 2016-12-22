/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 05.02.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.WildcardQuery;
import org.hibernate.search.jpa.FullTextEntityManager;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.bridge.SubscriptionClassBridge;
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

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Long> searchSubscriptions(String searchPhrase)
            throws InvalidPhraseException, ObjectNotFoundException {
        ArgumentValidator.notEmptyString("searchPhrase", searchPhrase);

        FullTextEntityManager ftem = getFtem();

        List<Subscription> list;

        BooleanQuery booleanQuery = constructWildcardQuery(searchPhrase);

        javax.persistence.Query jpaQuery = ftem.createFullTextQuery(
                booleanQuery, Subscription.class);

        list = jpaQuery.getResultList();

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

    private BooleanQuery constructWildcardQuery(String searchPhrase) {

        BooleanQuery booleanQuery = new BooleanQuery();

        final List<String> fieldNames = Arrays.asList(
                SubscriptionClassBridge.NAME_SUBSCRIPTION_ID,
                SubscriptionClassBridge.NAME_REFERENCE,
                SubscriptionClassBridge.NAME_PARAMETER_VALUE,
                SubscriptionClassBridge.NAME_UDA_VALUE);

        for (String fieldName : fieldNames) {
            WildcardQuery queryPart = new WildcardQuery(
                    new Term(fieldName, "*" + searchPhrase.toLowerCase() + "*"));
            booleanQuery.add(queryPart, Occur.SHOULD);
        }

        return booleanQuery;
    }
}
