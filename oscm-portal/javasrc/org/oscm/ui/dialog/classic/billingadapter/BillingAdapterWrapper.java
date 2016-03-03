/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                 
 *                                                                                                                                 
 *  Creation Date: Dec 23, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.billingadapter;

import java.util.Set;

import org.oscm.ui.common.JSFUtils;
import org.oscm.internal.billingadapter.ConnectionPropertyItem;
import org.oscm.internal.billingadapter.POBillingAdapter;

/**
 * @author farmaki
 * 
 */
public class BillingAdapterWrapper {

    private static final String MESSAGE_NEW_ADAPTER = "operator.manageBillingAdapters.newAdapter";

    private POBillingAdapter adapter;
    private boolean active;

    private int rowIndex;

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public BillingAdapterWrapper(POBillingAdapter adapter) {
        this.adapter = adapter;
        this.active = adapter.isActive();
    }

    public POBillingAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(POBillingAdapter adapter) {
        this.adapter = adapter;
    }

    public String getBillingIdentifier() {
        return adapter.getBillingIdentifier();
    }

    public Set<ConnectionPropertyItem> getConnectionProperties() {
        return adapter.getConnectionProperties();
    }

    public String getName() {
        return adapter.getName();
    }

    public void setBillingIdentifier(String billingIdentifier) {
        adapter.setBillingIdentifier(billingIdentifier);
    }

    public void setName(String name) {
        adapter.setName(name);
    }

    public void setConnectionProperties(
            Set<ConnectionPropertyItem> connectionProperties) {
        adapter.setConnectionProperties(connectionProperties);
    }

    public boolean isDefaultAdapter() {
        return adapter.isDefaultAdapter();
    }

    public String getDisplayName() {
        return getDisplayName(adapter);
    }

    public boolean isIdFieldReadOnly() {
        return adapter.getKey() > 0;
    }

    public boolean isDefaultEnabled() {
        boolean isPersisted = adapter.getKey() > 0;
        return !isDefaultAdapter() && isPersisted;

    }

    public boolean isDeleteEnabled() {
        boolean isPersisted = adapter.getKey() > 0;
        return !isDefaultAdapter() && isPersisted;

    }

    private String getDisplayName(POBillingAdapter adapter) {
        String adapterName = adapter.getBillingIdentifier();

        if (adapterName != null && adapterName.trim().length() != 0) {
            return adapterName;
        } else {
            return JSFUtils.getText(MESSAGE_NEW_ADAPTER, null);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void addItem() {
        if (adapter.getConnectionProperties() != null) {
            adapter.getConnectionProperties().add(
                    new ConnectionPropertyItem(null, null));
        }
    }

    public void removeItem(ConnectionPropertyItem rowItem) {
        if (adapter.getConnectionProperties() != null) {
            adapter.getConnectionProperties().remove(rowItem);
        }
    }

    public boolean isNativeBilling() {
        return adapter.isNativeBilling();
    }

}
