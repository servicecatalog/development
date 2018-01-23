/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2018                                           
 *                                                                                                                                 
 *  Creation Date: 23.01.2018                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.TechnicalProductOperation;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.subscriptionservice.dao.SubscriptionDao;

/**
 * @author goebel
 */
public class GetLastUsedServiceOperationTest
        extends SubscriptionServiceMockBase {

    private static final String EN = "en";
    private static final String TEXT = "some localized text";

    private SubscriptionServiceBean bean;

    private Subscription subscription = new Subscription();
    private TechnicalProductOperation operation = new TechnicalProductOperation();
    private SubscriptionDao subscriptionDao;
    private PlatformUser user = new PlatformUser();

    @Before
    public void setup() throws Exception {
        subscriptionDao = mock(SubscriptionDao.class);

        bean = spy(new SubscriptionServiceBean());
        DataService dsMock = mock(DataService.class);
        bean.dataManager = dsMock;

        doReturn(subscriptionDao).when(bean).getSubscriptionDao();

        bean.manageBean = mock(ManageSubscriptionBean.class);

        user.setLocale(EN);

        when(bean.dataManager.getReference(eq(TechnicalProductOperation.class),
                anyLong())).thenReturn(operation);

        when(bean.manageBean.loadSubscription(anyString(), anyLong()))
                .thenReturn(subscription);

        when(bean.manageBean.checkSubscriptionOwner(anyString(), anyLong()))
                .thenReturn(subscription);

        doReturn(operation).when(subscriptionDao)
                .getTechnicalProductionOperation(anyObject(), anyString());

        when(subscriptionDao.getTechnicalProductionOperation(anyObject(),
                anyString())).thenReturn(operation);

    }

    private void givenLastUsedOperation(String id) {
        subscription.setLastUsedOperation(id);
    }

    @Test
    public void getLastUsedServiceOperation()
            throws ObjectNotFoundException, OperationNotPermittedException {

        givenLastUsedOperation("my_operation");

        String op = bean.getLastUsedServiceOperation("s1");

        assertEquals("my_operation", op);
    }

    @Test
    public void setLastUsedServiceOperation()
            throws ObjectNotFoundException, OperationNotPermittedException {

        // given
        givenLastUsedOperation("old_operation");

        // when
        bean.setLastUsedServiceOperation("s1", "my_operation");

        // then
        assertEquals("my_operation", subscription.getLastUsedOperation());

    }

}
