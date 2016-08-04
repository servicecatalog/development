/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 05.02.2009                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.search.subscriptions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.ParameterSet;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.domobjects.UdaDefinition;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;
import org.oscm.subscriptionservice.bean.SubscriptionSearchServiceBean;
import org.oscm.subscriptionservice.dao.SubscriptionDao;
import org.oscm.types.enumtypes.UdaTargetType;

public class SearchServiceBeanTest {

    @SuppressWarnings({ "boxing", "unchecked" })
    @Test
    public void searchSubscriptions() throws Exception {
        DataServiceBean bean = spy(new DataServiceBean());
        SubscriptionSearchServiceBean ssb = spy(new SubscriptionSearchServiceBean());
        doReturn(bean).when(ssb).getDm();

        FullTextEntityManager ftem = mock(FullTextEntityManager.class,
                Mockito.RETURNS_DEEP_STUBS);
        doReturn(ftem).when(ssb).getFtem();

        QueryBuilder qb = mock(QueryBuilder.class, Mockito.RETURNS_DEEP_STUBS);
        when(
                ftem.getSearchFactory().buildQueryBuilder()
                        .forEntity(any(Class.class)).get()).thenReturn(qb);

        org.apache.lucene.search.Query lq = mock(
                org.apache.lucene.search.Query.class,
                Mockito.RETURNS_DEEP_STUBS);
        when(
                qb.keyword().onFields(Matchers.<String> anyVararg())
                        .matching(anyString()).createQuery()).thenReturn(lq);

        Subscription sub = new Subscription();
        sub.setKey(1L);
        FullTextQuery jqSub = mock(FullTextQuery.class);
        doReturn(jqSub).when(ftem).createFullTextQuery(lq, Subscription.class);
        doReturn(Arrays.asList(sub)).when(jqSub).getResultList();

        Parameter param = new Parameter();
        ParameterSet paramSet = new ParameterSet();
        Product prod = new Product();
        Subscription pSub = new Subscription();
        pSub.setKey(2L);
        prod.setOwningSubscription(pSub);
        paramSet.setProduct(prod);
        param.setParameterSet(paramSet);
        FullTextQuery jqParam = mock(FullTextQuery.class);
        doReturn(jqParam).when(ftem).createFullTextQuery(lq, Parameter.class);
        doReturn(Arrays.asList(param)).when(jqParam).getResultList();

        SubscriptionDao subDaoMock = mock(SubscriptionDao.class);
        doReturn(subDaoMock).when(ssb).getSubscriptionDao();
        doReturn(mock(PlatformUser.class)).when(bean).getCurrentUser();

        List<UdaDefinition> udaDefs = new ArrayList<>();
        UdaDefinition udaDefinition = new UdaDefinition();
        udaDefinition.setDefaultValue("value");
        udaDefinition.setTargetType(UdaTargetType.CUSTOMER_SUBSCRIPTION);
        udaDefinition.setConfigurationType(UdaConfigurationType.SUPPLIER);
        udaDefinition.setKey(1000L);
        udaDefs.add(udaDefinition);
        udaDefinition = new UdaDefinition();
        udaDefinition.setDefaultValue("value");
        udaDefinition.setTargetType(UdaTargetType.CUSTOMER_SUBSCRIPTION);
        udaDefinition
                .setConfigurationType(UdaConfigurationType.USER_OPTION_OPTIONAL);
        udaDefinition.setKey(1000L);
        udaDefs.add(udaDefinition);

        List<BigInteger> list = new ArrayList<>();
        list.add(BigInteger.valueOf(3L));
        doReturn(list)
                .when(subDaoMock)
                .getSubscriptionsWithDefaultUdaValuesAndVendor(
                        any(PlatformUser.class), any(Set.class), any(Set.class));

        FullTextQuery jqUdaDef = mock(FullTextQuery.class);
        doReturn(jqUdaDef).when(ftem).createFullTextQuery(lq,
                UdaDefinition.class);
        doReturn(udaDefs).when(jqUdaDef).getResultList();

        List<Uda> udas = new ArrayList<>();
        Uda uda = new Uda();
        udaDefinition = new UdaDefinition();
        udaDefinition.setDefaultValue("value");
        udaDefinition.setTargetType(UdaTargetType.CUSTOMER_SUBSCRIPTION);
        udaDefinition.setConfigurationType(UdaConfigurationType.SUPPLIER);
        udaDefinition.setKey(1000L);
        uda.setUdaDefinition(udaDefinition);
        uda.setTargetObjectKey(4L);
        udas.add(uda);
        udaDefinition = new UdaDefinition();
        uda = new Uda();
        udaDefinition.setDefaultValue("value");
        udaDefinition.setTargetType(UdaTargetType.CUSTOMER_SUBSCRIPTION);
        udaDefinition
                .setConfigurationType(UdaConfigurationType.USER_OPTION_OPTIONAL);
        udaDefinition.setKey(1000L);
        uda.setUdaDefinition(udaDefinition);
        uda.setTargetObjectKey(5L);
        udas.add(uda);

        FullTextQuery jqUda = mock(FullTextQuery.class);
        doReturn(jqUda).when(ftem).createFullTextQuery(lq, Uda.class);
        doReturn(udas).when(jqUda).getResultList();

        Collection<Long> result = ssb.searchSubscriptions("searchphrase");
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
        assertFalse(result.contains(4L));
        assertTrue(result.contains(5L));
    }
}
