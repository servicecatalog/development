/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import javax.persistence.EntityManager;

import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.oscm.dataservice.local.DataService;
import org.oscm.internal.intf.SubscriptionSearchService;
import org.oscm.test.EJBTestBase;
import org.oscm.test.ejb.TestContainer;

@RunWith(MockitoJUnitRunner.class)
public class SubscriptionSearchServiceBeanIT extends EJBTestBase {

    @Spy
    @InjectMocks
    private SubscriptionSearchService sssb = new SubscriptionSearchServiceBean();

    @Mock
    private FullTextQuery query;
    @Mock
    private FullTextSession fullTextSession;

    @Override
    public void setup(TestContainer container) throws Exception {
        DataService ds = Mockito.mock(DataService.class);
        EntityManager em = container.getPersistenceUnit("oscm-domainobjects");
        Mockito.doReturn(em).when(ds).getEntityManager();

        container.addBean(ds);
        container.addBean(sssb);
    }

    // @Test
    // public void searchSub4SingleWord() throws Exception {
    // runTX(new Callable<Void>() {
    // @Override
    // public Void call() {
    // try {
    // sssb.searchSubscriptions("search");
    // } catch (InvalidPhraseException e) {
    // e.printStackTrace();
    // } catch (ObjectNotFoundException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    // });
    //
    // }
    //
    // @Test
    // public void searchSub4MultipleWords() throws Exception {
    // runTX(new Callable<Void>() {
    // @Override
    // public Void call() {
    // try {
    // sssb.searchSubscriptions("search multiple");
    // } catch (InvalidPhraseException e) {
    // e.printStackTrace();
    // } catch (ObjectNotFoundException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    // });
    // }
    //
    // @Test
    // public void searchSub4MultipleWordsWithBlanks() throws Exception {
    // runTX(new Callable<Void>() {
    // @Override
    // public Void call() {
    // try {
    // sssb.searchSubscriptions("           search              multiple                   words        ");
    // } catch (InvalidPhraseException e) {
    // e.printStackTrace();
    // } catch (ObjectNotFoundException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    // });
    //
    // }
    //
    // @Test
    // public void searchSub4SpecialCharacters() throws Exception {
    // runTX(new Callable<Void>() {
    // @Override
    // public Void call() {
    // try {
    // sssb.searchSubscriptions("search+-&&||!(){}[]^\"~*?:\\");
    // } catch (InvalidPhraseException e) {
    // e.printStackTrace();
    // } catch (ObjectNotFoundException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }
    // });
    //
    // }
}
