/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 15.05.15 09:34
 *
 *******************************************************************************/

package org.oscm.ui.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.oscm.paginator.PaginationInt;
import org.richfaces.component.SortOrder;
import org.richfaces.model.ArrangeableState;
import org.richfaces.model.FilterField;
import org.richfaces.model.SortField;

import org.oscm.ui.dialog.mp.subscriptions.SubscriptionListsLazyDataModel;
import org.oscm.internal.components.response.Response;
import org.oscm.internal.subscriptions.POSubscriptionForList;
import org.oscm.internal.subscriptions.SubscriptionsService;
import org.oscm.internal.tables.Pagination;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;

public class SubscriptionListsLazyDataModelTest {

    private SubscriptionListsLazyDataModel beanUnderTheTest = spy(new SubscriptionListsLazyDataModel());

    private SubscriptionsService subscriptionsService = mock(SubscriptionsService.class);
    private Set<SubscriptionStatus> states = EnumSet.of(SubscriptionStatus.ACTIVE,
            SubscriptionStatus.EXPIRED, SubscriptionStatus.PENDING,
            SubscriptionStatus.SUSPENDED, SubscriptionStatus.PENDING_UPD,
            SubscriptionStatus.SUSPENDED_UPD);

    @Test
    public void testInit() throws Exception {
        //when
        beanUnderTheTest.init();
        //then
        assertEquals(beanUnderTheTest.getColumnNamesMapping().size(), 6);
        assertEquals(beanUnderTheTest.getSortOrders().size(), 6);
    }

    @Test
    public void bug11725() {
        //given
        //when
        beanUnderTheTest.init();
        //then
        assertEquals(SortOrder.descending, beanUnderTheTest.getSortOrders().get(beanUnderTheTest.getACTIVATION()));
    }

    @Test
    public void testGetDataList() throws Exception {
        //given
        int firstRow = 0;
        int numRows = 10;
        int totalCount = 1;
        List<POSubscriptionForList> expectedList = prepareList();
        Response resp = new Response(expectedList);
        when(subscriptionsService.getSubscriptionsForOrgWithFiltering(anySet(), any(org.oscm.paginator.Pagination.class))).thenReturn(resp);
        when(subscriptionsService.getSubscriptionsForOrgSizeWithFiltering(anySet(), any(org.oscm.paginator.Pagination.class))).thenReturn(totalCount);
        ArrangeableState arrangeable = new ArrangeableState() {
            @Override
            public List<FilterField> getFilterFields() {
                return Collections.emptyList();
            }

            @Override
            public List<SortField> getSortFields() {
                return Collections.emptyList();
            }

            @Override
            public Locale getLocale() {
                return Locale.JAPAN;
            }
        };
        when(beanUnderTheTest.getArrangeable()).thenReturn(arrangeable);
        doNothing().when(beanUnderTheTest).applyFilters(anyList(), any(Pagination.class));
        doNothing().when(beanUnderTheTest).decorateWithLocalizedStatuses(any(Pagination.class));
        doNothing().when(beanUnderTheTest).applyFilters(anyList(), any(PaginationInt.class));
        doNothing().when(beanUnderTheTest).decorateWithLocalizedStatuses(any(org.oscm.paginator.Pagination.class));
        beanUnderTheTest.setSubscriptionsService(subscriptionsService);

        //when
        List<POSubscriptionForList> result = beanUnderTheTest.getDataList(firstRow, numRows,
                Collections.<FilterField>emptyList(), Collections.<SortField>emptyList(), true);
        //then
        assertArrayEquals(expectedList.toArray(), result.toArray());
        assertEquals(beanUnderTheTest.getTotalCount(), totalCount);
    }

    @Test
    public void testGetTotalCount() throws Exception {
        //given
        beanUnderTheTest.setTotalCount(-1);
        beanUnderTheTest.setSubscriptionsService(subscriptionsService);
        when(subscriptionsService.getSubscriptionsForOrgSizeWithFiltering(anySet(), any(org.oscm.paginator.Pagination.class))).thenReturn(2);
        ArrangeableState arrangeable = new ArrangeableState() {
            @Override
            public List<FilterField> getFilterFields() {
                return Collections.emptyList();
            }

            @Override
            public List<SortField> getSortFields() {
                return Collections.emptyList();
            }

            @Override
            public Locale getLocale() {
                return Locale.JAPAN;
            }
        };
        doNothing().when(beanUnderTheTest).applyFilters(anyList(), any(Pagination.class));
        doNothing().when(beanUnderTheTest).applyFilters(anyList(), any(PaginationInt.class));
        doNothing().when(beanUnderTheTest).decorateWithLocalizedStatuses(any(org.oscm.paginator.Pagination.class));
        doNothing().when(beanUnderTheTest).decorateWithLocalizedStatuses(any(Pagination.class));
        when(beanUnderTheTest.getArrangeable()).thenReturn(arrangeable);
        beanUnderTheTest.setSubscriptionsService(subscriptionsService);
        //when
        int totalCount = beanUnderTheTest.getTotalCount();
        //then
        assertEquals(2, totalCount);
        verify(subscriptionsService, atLeastOnce()).getSubscriptionsForOrgSizeWithFiltering(anySet(), any(org.oscm.paginator.Pagination.class));
    }

    private List<POSubscriptionForList> prepareList() {
        List<POSubscriptionForList> cachedList = new ArrayList<>();
        cachedList.add(new POSubscriptionForList());
        return cachedList;
    }
}
