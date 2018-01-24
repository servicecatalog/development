/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2018
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.SubscriptionService;

/**
 * Test is a mockproduct operation for testing
 *   
 * {@link SubscriptionService#setLastUsedServiceOperation(String, String))}
 * 
 * @author goebel
 *
 */
public class SubscriptionService_setLastServiceOperation implements
        IOperationDescriptor<SubscriptionService> {

    @Override
    public String getName() {
        return "SubscriptionService.setLastUsedServiceOperation";
    }

    @Override
    public Class<SubscriptionService> getServiceType() {
        return SubscriptionService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("subscriptionId", "operationId");
    }

    @Override
    public void call(SubscriptionService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        final String subscriptionId = params.get("subscriptionId");
        final String operationId = params.get("operationId");
        service.setLastUsedServiceOperation(subscriptionId, operationId);
    }

    @Override
    public String getComment() {
        return null;
    }

}
