/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Registry of all known operations.
 * 
 * @author hoffmann
 */
public class OperationRegistry {

    private Map<String, IOperationDescriptor<Object>> operations = new TreeMap<>();

    @SuppressWarnings("unchecked")
    private void register(IOperationDescriptor<?> op) {
        operations.put(op.getName(), (IOperationDescriptor<Object>) op);
    }

    public OperationRegistry() {
        register(new AccountService_getOrganizationData());
        register(new AccountService_registerCustomer());
        register(new AccountService_saveBillingContact());
        register(new AccountService_updateAccountInformation());
        register(new BillingService_getCustomerBillingData());
        register(new EventService_recordEventForSubscription());
        register(new EventService_recordEventForInstance());
        register(new IdentityService_getUsersForOrganization());
        register(new ReportingService_getAvailableReports());
        register(new SessionService_resolveUserToken());
        register(new SessionService_deleteServiceSession());
        register(new SubscriptionService_abortAsyncSubscription());
        register(new SubscriptionService_completeAsyncSubscription());
        register(new SubscriptionService_updateAsyncSubscriptionProgress());
        register(new SubscriptionService_abortAsyncModifySubscription());
        register(new SubscriptionService_completeAsyncModifySubscription());
        register(new SubscriptionService_abortAsyncUpgradeSubscription());
        register(new SubscriptionService_completeAsyncUpgradeSubscription());
        register(new SubscriptionService_notifySubscriptionVmsNumber());
        register(new TriggerService_approveAction());
        register(new TriggerService_rejectAction());
        register(new TriggerService_getActionParameter());
        register(new TriggerService_updateActionParameters());
        register(new OrganizationalUnitService_createUnit());
        register(new OrganizationalUnitService_getVisibleServices());
        register(new OrganizationalUnitService_getAccessibleServices());
        register(new OrganizationalUnitService_addAccessibleServices());
        register(new OrganizationalUnitService_addVisibleServices());
        register(new OrganizationalUnitService_revokeAccessibleServices());
        register(new OrganizationalUnitService_revokeVisibleServices());
        register(new OrganizationalUnitService_getOrganizationalUnits());
        register(new OrganizationalUnitService_grantUserRoles());
        register(new OrganizationalUnitService_revokeUserRoles());
        register(new OrganizationalUnitService_deleteUnit());
    }

    public List<String> getOperations() {
        return new ArrayList<>(operations.keySet());
    }

    public IOperationDescriptor<Object> getOperation(String name) {
        final IOperationDescriptor<Object> op = operations.get(name);
        if (op == null) {
            throw new NoSuchElementException("No operation " + name);
        }
        return op;
    }

}
