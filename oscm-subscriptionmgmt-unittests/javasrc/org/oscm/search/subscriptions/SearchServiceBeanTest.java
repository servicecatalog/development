/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 05.02.2009                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.search.subscriptions;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.junit.Test;
import org.mockito.Mockito;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.domobjects.Subscription;
import org.oscm.subscriptionservice.bean.SubscriptionSearchServiceBean;

public class SearchServiceBeanTest {

    @Test
    public void searchSubscriptions() throws Exception {
        DataServiceBean bean = spy(new DataServiceBean());
        SubscriptionSearchServiceBean ssb = spy(new SubscriptionSearchServiceBean());
        doReturn(bean).when(ssb).getDm();

        FullTextEntityManager ftem = mock(FullTextEntityManager.class,
                Mockito.RETURNS_DEEP_STUBS);
        doReturn(ftem).when(ssb).getFtem();

        Analyzer analyzer = mock(Analyzer.class);
        when(ftem.getSearchFactory().getAnalyzer(anyString())).thenReturn(
                analyzer);

        MultiFieldQueryParser parser = mock(MultiFieldQueryParser.class);
        doReturn(parser).when(ssb).getParser(analyzer);

        org.apache.lucene.search.Query lq = mock(org.apache.lucene.search.Query.class);
        doReturn(lq).when(parser).parse(anyString());

        Subscription sub = new Subscription();
        sub.setKey(1L);
        FullTextQuery jqSub = mock(FullTextQuery.class);
        doReturn(jqSub).when(ftem).createFullTextQuery(lq, Subscription.class);
        doReturn(Arrays.asList(sub)).when(jqSub).getResultList();

        Collection<Long> result = ssb.searchSubscriptions("searchphrase");
        assertTrue(result.contains(new Long(1L)));
    }
}
