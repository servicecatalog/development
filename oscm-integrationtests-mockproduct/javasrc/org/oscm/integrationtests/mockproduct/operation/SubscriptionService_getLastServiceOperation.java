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
 * {@link SubscriptionService#getLastUsedServiceOperation(String)}
 * 
 * @author goebel
 *
 */
public class SubscriptionService_getLastServiceOperation implements
        IOperationDescriptor<SubscriptionService> {

    @Override
    public String getName() {
        return "SubscriptionService.getLastUsedServiceOperation";
    }

    @Override
    public Class<SubscriptionService> getServiceType() {
        return SubscriptionService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("subscriptionId");
    }

    @Override
    public void call(SubscriptionService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        final String subscriptionId = params.get("subscriptionId");
        String result = service.getLastUsedServiceOperation(subscriptionId);
        logEntry.setResult(result);
    }

    @Override
    public String getComment() {
        return null;
    }

}
