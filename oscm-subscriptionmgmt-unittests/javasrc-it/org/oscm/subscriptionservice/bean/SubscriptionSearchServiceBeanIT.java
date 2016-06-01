/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.lucene.search.Query;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.oscm.dataservice.bean.DataServiceBean;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Parameter;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.Uda;
import org.oscm.domobjects.UdaDefinition;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.internal.types.exception.InvalidPhraseException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionSearchServiceBeanIT extends EJBTestBase {

    @Spy
    private DataService ds = new DataServiceBean();

    @Spy
    @InjectMocks
    private SubscriptionSearchService sssb = new SubscriptionSearchServiceBean();

    @Mock
    private FullTextQuery query;
    @Mock
    private FullTextSession fullTextSession;


    @Override
    public void setup(TestContainer container) throws Exception {
        container.enableInterfaceMocking(true);
        container.addBean(ds);
        container.addBean(sssb);

        doReturn(fullTextSession).when(ds).getSession();
        doReturn(query).when(fullTextSession).createFullTextQuery(any(Query.class), any(Class.class));
    }

    @Test
    public void searchSub4SingleWord() throws Exception {
        runTX(new Callable<Void>(){
            @Override
            public Void call() {
                try {
                    sssb.searchSubscriptions("search");
                } catch (InvalidPhraseException e) {
                    e.printStackTrace();
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        verify(query, times(5)).list();
        ArgumentCaptor<Query> queryArgumentCaptor = forClass(Query.class);
        verify(fullTextSession, times(1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Parameter.class));
        verify(fullTextSession, times(2)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Subscription.class));
        verify(fullTextSession, times(1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Uda.class));
        verify(fullTextSession, times(1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(UdaDefinition.class));

        verifyQueryAttributes(queryArgumentCaptor,
                "(dataContainer.subscriptionId:search)",
                "(dataContainer.purchaseOrderNumber:search)",
                "(dataContainer.udaValue:search)",
                "(dataContainer.defaultValue:search)",
                "(dataContainer.value:search)");

    }

    @Test
    public void searchSub4MultipleWords() throws Exception {
        runTX(new Callable<Void>(){
            @Override
            public Void call() {
                try {
                    sssb.searchSubscriptions("search multiple");
                } catch (InvalidPhraseException e) {
                    e.printStackTrace();
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        verify(query, times(10)).list();
        ArgumentCaptor<Query> queryArgumentCaptor = forClass(Query.class);
        verify(fullTextSession, times(2)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Parameter.class));
        verify(fullTextSession, times(4)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Subscription.class));
        verify(fullTextSession, times(2)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Uda.class));
        verify(fullTextSession, times(2)).createFullTextQuery(queryArgumentCaptor.capture(), eq(UdaDefinition.class));

        verifyQueryAttributes(queryArgumentCaptor,
                "(dataContainer.subscriptionId:search)",
                "(dataContainer.purchaseOrderNumber:search)",
                "(dataContainer.udaValue:search)",
                "(dataContainer.defaultValue:search)",
                "(dataContainer.value:search)");

        verifyQueryAttributes(queryArgumentCaptor,
                "(dataContainer.subscriptionId:multiple)",
                "(dataContainer.purchaseOrderNumber:multiple)",
                "(dataContainer.udaValue:multiple)",
                "(dataContainer.value:multiple)");
    }

    private void verifyQueryAttributes(ArgumentCaptor<Query> queryArgumentCaptor, String... expectedValues) {
        boolean contains = false;
        for (String expectedValue : expectedValues) {
            for (Query query : queryArgumentCaptor.getAllValues()) {
                if (query.toString().contains(expectedValue)) {
                    contains = true;
                    break;
                }
            }
            assertTrue("Query captor does not contain mandatory values: " + expectedValue + " and looks like: "
                    + queryToString(queryArgumentCaptor.getAllValues()), contains);
            contains = false;
        }
    }

    private String queryToString(List<Query> allValues) {
        String retVal = "";
        for (Query query : allValues) {
            retVal += query.toString()+"\n";
        }
        return retVal;
    }

    @Test
    public void searchSub4MultipleWordsWithBlanks() throws Exception {
        runTX(new Callable<Void>(){
            @Override
            public Void call() {
                try {
                    sssb.searchSubscriptions("           search              multiple                   words        ");
                } catch (InvalidPhraseException e) {
                    e.printStackTrace();
                } catch (ObjectNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        int queryParts = 3;
        verify(query, times(queryParts * 5)).list();
        ArgumentCaptor<Query> queryArgumentCaptor = forClass(Query.class);
        verify(fullTextSession, times(queryParts *1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Parameter.class));
        verify(fullTextSession, times(queryParts *2)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Subscription.class));
        verify(fullTextSession, times(queryParts *1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Uda.class));
        verify(fullTextSession, times(queryParts *1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(UdaDefinition.class));

        verifyQueryAttributes(queryArgumentCaptor,
                "(dataContainer.subscriptionId:search)",
                "(dataContainer.purchaseOrderNumber:search)",
                "(dataContainer.udaValue:search)",
                "(dataContainer.defaultValue:search)",
                "(dataContainer.value:search)");

        verifyQueryAttributes(queryArgumentCaptor,
                "(dataContainer.subscriptionId:multiple)",
                "(dataContainer.purchaseOrderNumber:multiple)",
                "(dataContainer.udaValue:multiple)",
                "(dataContainer.defaultValue:search)",
                "(dataContainer.value:multiple)");

        verifyQueryAttributes(queryArgumentCaptor,
                "(dataContainer.subscriptionId:words)",
                "(dataContainer.purchaseOrderNumber:words)",
                "(dataContainer.udaValue:words)",
                "(dataContainer.defaultValue:search)",
                "(dataContainer.value:words)");
    }

//    @Test
//    public void searchSub4SpecialCharacters() throws Exception {
//        runTX(new Callable<Void>(){
//            @Override
//            public Void call() {
//                try {
//                    sssb.searchSubscriptions("search+-&&||!(){}[]^\"~*?:\\");
//                } catch (InvalidPhraseException e) {
//                    e.printStackTrace();
//                } catch (ObjectNotFoundException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        });
//        int queryParts = 1;
//        verify(query, times(queryParts *4)).list();
//        ArgumentCaptor<Query> queryArgumentCaptor = forClass(Query.class);
//        verify(fullTextSession, times(queryParts *1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Parameter.class));
//        verify(fullTextSession, times(queryParts *2)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Subscription.class));
//        verify(fullTextSession, times(queryParts *1)).createFullTextQuery(queryArgumentCaptor.capture(), eq(Uda.class));
//
//        verifyQueryAttributes(queryArgumentCaptor,
//                "(dataContainer.subscriptionId:" + QueryParser.escape("search+-&&||!(){}[]^~*?:\\") + ")",
//                "(dataContainer.purchaseOrderNumber:" + QueryParser.escape("search+-&&||!(){}[]^~*?:\\") + ")",
//                "(dataContainer.udaValue:" + QueryParser.escape("search+-&&||!(){}[]^~*?:\\") + ")",
//                "(dataContainer.value:" + QueryParser.escape("search+-&&||!(){}[]^~*?:\\") + ")");
//    }
}
