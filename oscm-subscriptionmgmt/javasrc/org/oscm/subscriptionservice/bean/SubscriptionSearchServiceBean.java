/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 05.02.2015                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.DomainObject;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.domobjects.UdaDefinition;
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
    public static final String SUBSCRIPTION_PURCHASE_ORDER_NUMBER = "dataContainer.purchaseOrderNumber";
    public static final String SUBSCRIPTION_ID = "dataContainer.subscriptionId";
    public static final String UDA_VALUE = "dataContainer.udaValue";
    public static final String PARAMETER_VALUE = "dataContainer.value";
    public static final String UDA_DEFINITION_VALUE = "dataContainer.defaultValue";

    @EJB
    private DataService dm;

    @Override
    public Collection<Long> searchSubscriptions(String searchPhrase)
            throws InvalidPhraseException, ObjectNotFoundException {
        ArgumentValidator.notEmptyString("searchPhrase", searchPhrase);

        Set<Long> result = new TreeSet<Long>();

        List<Subscription> subList = searchSubscriptionsForFields(
                Subscription.class, searchPhrase, SUBSCRIPTION_ID,
                SUBSCRIPTION_PURCHASE_ORDER_NUMBER);
        result.addAll(extractKeysForSubscriptions(subList));
        logger.logDebug("I have found " + result.size()
                + " subscriptions by referenceId and subscriptionId");

        List<Parameter> paramList = searchSubscriptionsForFields(
                Parameter.class, searchPhrase, PARAMETER_VALUE);
        result.addAll(extractKeysForParameters(paramList));
        logger.logDebug("I have found " + result.size()
                + " subscriptions by parameters value");

        List<UdaDefinition> udefList = searchSubscriptionsForFields(
                UdaDefinition.class, searchPhrase, UDA_DEFINITION_VALUE);
        result.addAll(extractKeysForUdaDefinition(udefList));
        logger.logDebug("I have found " + result.size()
                + " subscriptions by uda definitions value");

        List<Uda> udaList = searchSubscriptionsForFields(Uda.class,
                searchPhrase, UDA_VALUE);
        result.addAll(extractKeysForUda(udaList));
        logger.logDebug("I have found " + result.size()
                + " subscriptions by uda value");

        return result;
    }

    private <D extends DomainObject<?>> List<D> searchSubscriptionsForFields(
            Class<D> clazz, String phrase, String... fields) {

        FullTextEntityManager ftem = getFtem();

        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder()
                .forEntity(clazz).get();

        org.apache.lucene.search.Query luceneQuery = qb.keyword()
                .onFields(fields).matching(phrase).createQuery();

        javax.persistence.Query jpaQuery = ftem.createFullTextQuery(
                luceneQuery, clazz);

        @SuppressWarnings("unchecked")
        List<D> list = jpaQuery.getResultList();

        return list;
    }

    private Set<Long> extractKeysForSubscriptions(List<Subscription> list) {
        Set<Long> set = new TreeSet<Long>();
        for (Subscription sub : list) {
            set.add(new Long(sub.getKey()));
        }

        return set;
    }

    private Set<Long> extractKeysForParameters(List<Parameter> list) {
        Set<Long> set = new TreeSet<Long>();
        for (Parameter param : list) {
            set.add(new Long(param.getParameterSet().getProduct()
                    .getOwningSubscription().getKey()));
        }

        return set;
    }

    private Set<Long> extractKeysForUdaDefinition(List<UdaDefinition> list) {
        Set<Long> set = new TreeSet<>();

        SubscriptionDao subscriptionDao = getSubscriptionDao();
        Set<Long> udaDefsFound = new HashSet<>();

        for (UdaDefinition udef : list) {
            if (UdaConfigurationType.SUPPLIER.equals(udef
                    .getConfigurationType())) {
                continue;
            }
            if (UdaTargetType.CUSTOMER_SUBSCRIPTION
                    .equals(udef.getTargetType())) {
                udaDefsFound.add(new Long(udef.getKey()));
            }
        }

        if (!udaDefsFound.isEmpty()) {
            List<BigInteger> subs = subscriptionDao
                    .getSubscriptionsWithDefaultUdaValuesAndVendor(getDm()
                            .getCurrentUser(), getStates(), udaDefsFound);
            for (BigInteger subIds : subs) {
                set.add(new Long(subIds.longValue()));
            }
        }
        return set;
    }

    private Set<Long> extractKeysForUda(List<Uda> list) {
        Set<Long> set = new TreeSet<>();
        for (Uda uda : list) {
            if (UdaConfigurationType.SUPPLIER.equals(uda.getUdaDefinition()
                    .getConfigurationType())) {
                continue;
            }
            if (UdaTargetType.CUSTOMER_SUBSCRIPTION.equals(uda
                    .getUdaDefinition().getTargetType())) {
                set.add(new Long(uda.getTargetObjectKey()));
            }
        }
        return set;
    }

    public DataService getDm() {
        return dm;
    }

    public FullTextEntityManager getFtem() {
        EntityManager em = getDm().getEntityManager();

        return org.hibernate.search.jpa.Search.getFullTextEntityManager(em);
    }

    private Set<SubscriptionStatus> getStates() {
        Set<SubscriptionStatus> retVal = new TreeSet<>();
        retVal.addAll(Subscription.ASSIGNABLE_SUBSCRIPTION_STATUS);
        retVal.addAll(Subscription.VISIBLE_SUBSCRIPTION_STATUS);
        return retVal;
    }

    public SubscriptionDao getSubscriptionDao() {
        return new SubscriptionDao(dm);
    }
}
