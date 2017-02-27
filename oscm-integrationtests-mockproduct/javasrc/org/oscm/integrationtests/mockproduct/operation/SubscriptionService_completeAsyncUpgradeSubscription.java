/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.SubscriptionService;
import org.oscm.vo.VOInstanceInfo;

public class SubscriptionService_completeAsyncUpgradeSubscription implements
        IOperationDescriptor<SubscriptionService> {

    @Override
    public String getName() {
        return "SubscriptionService.completeAsyncUpgradeSubscription";
    }

    @Override
    public Class<SubscriptionService> getServiceType() {
        return SubscriptionService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("subscriptionId", "organizationId", "instanceId",
                "accessInfo", "baseUrl", "loginPath");
    }

    @Override
    public void call(SubscriptionService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        final String subscriptionId = params.get("subscriptionId");
        final String organizationId = params.get("organizationId");
        final VOInstanceInfo instance = new VOInstanceInfo();
        instance.setInstanceId(params.get("instanceId"));
        instance.setAccessInfo(params.get("accessInfo"));
        instance.setBaseUrl(params.get("baseUrl"));
        instance.setLoginPath(params.get("loginPath"));
        service.completeAsyncUpgradeSubscription(subscriptionId,
                organizationId, instance);
    }

    @Override
    public String getComment() {
        return null;
    }

}
