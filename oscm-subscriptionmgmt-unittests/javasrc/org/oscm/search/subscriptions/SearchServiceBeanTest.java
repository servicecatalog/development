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
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.query.dsl.*;
import org.junit.Before;
import org.junit.Test;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.domobjects.UdaDefinition;
import org.oscm.subscriptionservice.bean.SubscriptionSearchServiceBean;

public class SearchServiceBeanTest {

    SubscriptionSearchServiceBean ssb;
    private DataServiceBean bean;

    @Test
    public void searchSubscriptions() throws Exception {
        setup();
        Collection<Long> results = ssb
                .searchSubscriptions("searchphrase phrase");
        assertTrue(results.contains(10L));
    }

    @Before
    public void setup() {
        bean = spy(new DataServiceBean());
        ssb = spy(new SubscriptionSearchServiceBean());
        doReturn(bean).when(ssb).getDm();

        FullTextSession session = mock(FullTextSession.class);
        doReturn(session).when(bean).getSession();
        SearchFactory searchFactory = mock(SearchFactory.class);
        doReturn(searchFactory).when(session).getSearchFactory();
        QueryContextBuilder contextBuilder = mock(QueryContextBuilder.class);
        doReturn(contextBuilder).when(searchFactory).buildQueryBuilder();
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
        Collection<Uda[]> udaDefs = new ArrayList<>();
        longs.add(new Long[] { 10L });
        doReturn(longs).when(subFTS).list();
        doReturn(params).when(parFTS).list();
        doReturn(udas).when(udaFTS).list();
        doReturn(udaDefs).when(udaDefFTS).list();
    }
}
