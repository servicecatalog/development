/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.SubscriptionService;
import org.oscm.vo.VOLocalizedText;

public class SubscriptionService_abortAsyncUpgradeSubscription implements
        IOperationDescriptor<SubscriptionService> {

    @Override
    public String getName() {
        return "SubscriptionService.abortAsyncUpgradeSubscription";
    }

    @Override
    public Class<SubscriptionService> getServiceType() {
        return SubscriptionService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("subscriptionId", "organizationId", "locale",
                "text");
    }

    @Override
    public void call(SubscriptionService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        final String subscriptionId = params.get("subscriptionId");
        final String organizationId = params.get("organizationId");
        final VOLocalizedText localizedText = new VOLocalizedText();
        localizedText.setLocale(params.get("locale"));
        localizedText.setText(params.get("text"));
        List<VOLocalizedText> list = new ArrayList<VOLocalizedText>();
        list.add(localizedText);
        service.abortAsyncUpgradeSubscription(subscriptionId, organizationId,
                list);
    }

    @Override
    public String getComment() {
        return null;
    }

}
