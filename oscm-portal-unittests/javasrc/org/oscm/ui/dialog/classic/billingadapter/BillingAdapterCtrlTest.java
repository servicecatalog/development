/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                           
 *                                                                                                                                 
 *  Creation Date: 4 maj 2016                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.billingadapter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.oscm.internal.billingadapter.BillingAdapterService;
import org.oscm.internal.billingadapter.ConnectionPropertyItem;
import org.oscm.internal.billingadapter.POBillingAdapter;
import org.oscm.internal.components.response.Response;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.DuplicateAdapterException;
import org.oscm.internal.types.exception.DuplicatePropertyKeyException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.ui.common.UiDelegate;

/**
 * @author BadziakP
 *
 */
public class BillingAdapterCtrlTest {

    private BillingAdapterCtrl bean;
    private BillingAdapterModel model;
    private BillingAdapterService billingAdapterService;
    private UiDelegate ui;

    @Before
    public void setup() throws Exception {
        bean = spy(new BillingAdapterCtrl());
        model = spy(new BillingAdapterModel());
        model.addBillingAdapter(createBillingAdapter());
        bean.setModel(model);
        billingAdapterService = mock(BillingAdapterService.class);
        doReturn(billingAdapterService).when(bean).getBillingAdapterService();
        ui = mock(UiDelegate.class);
        bean.setUiDelegate(ui);

        doNothing().when(ui).handle(any(String.class));
        List<POBillingAdapter> billingAdapters = new ArrayList<>();
        billingAdapters.add(createBillingAdapter());
        doReturn(billingAdapters).when(bean).getBillingAdapters();
    }

    @Test
    public void addBillingAdapterTest() throws SaaSApplicationException {
        // given
        doNothing().when(model).addBillingAdapter(any(POBillingAdapter.class));
        doReturn(1).when(bean).reinitializeAdapters();
        // when
        bean.addBillingAdapter();
        // then
        verify(bean, times(1)).setDisabledAddBtn(true);
        verify(model, times(1)).setSelectedIndex(1);
    }

    @Test
    public void saveTest()
            throws ObjectNotFoundException, NonUniqueBusinessKeyException,
            DuplicateAdapterException, DuplicatePropertyKeyException,
            ConcurrentModificationException, SaaSApplicationException {
        // given
        doReturn(new Response()).when(billingAdapterService)
                .saveBillingAdapter(any(POBillingAdapter.class));
        doReturn(createBillingAdapter()).when(bean)
                .getBillingAdapter(any(String.class));

        // when
        String result = bean.save();

        // then
        assertEquals(result, "success");
        verify(bean, times(1)).setDisabledAddBtn(false);
    }

    @Test
    public void setDefaultAdapterTest()
            throws ObjectNotFoundException, NonUniqueBusinessKeyException,
            DuplicateAdapterException, SaaSApplicationException {
        // given
        doReturn(new Response()).when(billingAdapterService)
                .setDefaultBillingAdapter(any(POBillingAdapter.class));

        // when
        String result = bean.setDefaultAdapter();

        // then
        assertEquals(result, "success");
        verify(bean, times(1)).setDisabledAddBtn(false);
    }

    @Test
    public void deleteBillingAdapterTest() throws SaaSApplicationException {
        // given
        doReturn(new Response()).when(billingAdapterService)
                .deleteAdapter(any(POBillingAdapter.class));

        // when
        String result = bean.deleteAdapter();

        // then
        assertEquals(result, "success");
        verify(bean, times(1)).setDisabledAddBtn(false);
    }

    private POBillingAdapter createBillingAdapter() {
        POBillingAdapter adapter = new POBillingAdapter();
        adapter.setBillingIdentifier("BILLING_ADAPTER1");
        adapter.setName("BILLING_ADAPTER1");
        adapter.setNativeBilling(false);
        adapter.setDefaultAdapter(false);
        Set<ConnectionPropertyItem> connectionProperties = new HashSet<>();
        ConnectionPropertyItem connectionPropertyItem = new ConnectionPropertyItem(
                "Key1", "Value1");
        connectionProperties.add(connectionPropertyItem);
        adapter.setConnectionProperties(connectionProperties);
        return adapter;
    }
}
