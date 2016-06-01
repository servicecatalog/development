/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 05.02.2009                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.search.subscriptions;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.query.dsl.*;
import org.junit.Before;
import org.junit.Test;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.domobjects.*;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UdaConfigurationType;
import org.oscm.subscriptionservice.bean.SubscriptionSearchServiceBean;
import org.oscm.subscriptionservice.dao.SubscriptionDao;
import org.oscm.types.enumtypes.UdaTargetType;

public class SearchServiceBeanTest {

    SubscriptionSearchServiceBean ssb;
    private DataServiceBean bean;

    @Test
    public void searchSubscriptions() throws Exception {
        Collection<Long> results = ssb
                .searchSubscriptions("searchphrase phrase");
        assertTrue(results.contains(10L));
    }
    @Test
    public void searchSubscriptionsByUdaDefinitions() throws Exception {
        Collection<Long> results = ssb
                .searchSubscriptions("searchphrase");
        assertTrue(results.contains(10L));
        assertTrue(results.contains(1L));
    }

    @Before
    public void setup() {
        bean = spy(new DataServiceBean());
        ssb = spy(new SubscriptionSearchServiceBean());
        doReturn(bean).when(ssb).getDm();

        FullTextSession session = mock(FullTextSession.class);
        doReturn(mock(PlatformUser.class)).when(bean).getCurrentUser();
        doReturn(session).when(bean).getSession();
        SearchFactory searchFactory = mock(SearchFactory.class);
        doReturn(searchFactory).when(session).getSearchFactory();
        QueryContextBuilder contextBuilder = mock(QueryContextBuilder.class);
        doReturn(contextBuilder).when(searchFactory).buildQueryBuilder();
        SubscriptionDao subDaoMock = mock(SubscriptionDao.class);
        List<BigInteger> list = new ArrayList<>();
        list.add(BigInteger.ONE);
        doReturn(list).when(subDaoMock).getSubscriptionsWithDefaultUdaValuesAndVendor(any(PlatformUser.class), any(Set.class), any(Set.class));
        doReturn(subDaoMock).when(ssb).getSubscriptionDao();
        EntityContext entityContext = mock(EntityContext.class);
        doReturn(entityContext).when(contextBuilder).forEntity(
                Subscription.class);
        doReturn(entityContext).when(contextBuilder).forEntity(Parameter.class);
        doReturn(entityContext).when(contextBuilder).forEntity(Uda.class);
        QueryBuilder queryBuilder = mock(QueryBuilder.class);
        doReturn(queryBuilder).when(entityContext).get();
        TermContext termContext = mock(TermContext.class);
        doReturn(termContext).when(queryBuilder).keyword();
        WildcardContext wc = mock(WildcardContext.class);
        doReturn(wc).when(termContext).wildcard();
        TermMatchingContext termMatchingContext = mock(TermMatchingContext.class);
        doReturn(termMatchingContext).when(termContext).onFields(
                "dataContainer.purchaseOrderNumber",
                "dataContainer.subscriptionId");
        doReturn(termMatchingContext).when(termContext).onFields(
                "dataContainer.udaValue");
        doReturn(termMatchingContext).when(termContext).onFields(
                "dataContainer.value");
        doReturn(termMatchingContext).when(wc).onField(anyString());
        TermTermination termTermination = mock(TermTermination.class);
        doReturn(termMatchingContext).when(termMatchingContext).andField(
                anyString());
        doReturn(termTermination).when(termMatchingContext).matching(
                "searchphrase");
        doReturn(termTermination).when(termMatchingContext).matching("phrase");

        FullTextQuery subFTS = mock(FullTextQuery.class);
        FullTextQuery parFTS = mock(FullTextQuery.class);
        FullTextQuery udaFTS = mock(FullTextQuery.class);
        FullTextQuery udaDefFTS = mock(FullTextQuery.class);

        doReturn(subFTS).when(session).createFullTextQuery(any(Query.class),
                eq(Subscription.class));
        doReturn(parFTS).when(session).createFullTextQuery(any(Query.class),
                eq(Parameter.class));
        doReturn(udaFTS).when(session).createFullTextQuery(any(Query.class),
                eq(Uda.class));
        doReturn(udaDefFTS).when(session).createFullTextQuery(any(Query.class),
                eq(UdaDefinition.class));
        Collection<Long[]> longs = new ArrayList<>();
        Collection<Parameter[]> params = new ArrayList<>();
        Collection<Uda[]> udas = new ArrayList<>();
        Collection<UdaDefinition> udaDefs = new ArrayList<>();
        UdaDefinition udaDefinition = new UdaDefinition();
        udaDefinition.setDefaultValue("value");
        udaDefinition.setTargetType(UdaTargetType.CUSTOMER_SUBSCRIPTION);
        udaDefinition.setConfigurationType(UdaConfigurationType.SUPPLIER);
        udaDefinition.setKey(1000L);
        udaDefs.add(udaDefinition);udaDefs = new ArrayList<>();
        udaDefinition = new UdaDefinition();
        udaDefinition.setDefaultValue("value");
        udaDefinition.setTargetType(UdaTargetType.CUSTOMER_SUBSCRIPTION);
        udaDefinition.setConfigurationType(UdaConfigurationType.USER_OPTION_OPTIONAL);
        udaDefinition.setKey(1000L);
        udaDefs.add(udaDefinition);
        longs.add(new Long[] { 10L });
        doReturn(longs).when(subFTS).list();
        doReturn(params).when(parFTS).list();
        doReturn(udas).when(udaFTS).list();
        doReturn(udaDefs).when(udaDefFTS).list();
    }
}
