/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 02.06.17 14:22
 *
 ******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.SubscriptionService;
import org.oscm.vo.VOInstanceInfo;

public class SubscriptionService_notifySubscriptionVmsNumber implements
        IOperationDescriptor<SubscriptionService> {

    @Override
    public String getName() {
        return "SubscriptionService.notifySubscriptionVmsNumber";
    }

    @Override
    public Class<SubscriptionService> getServiceType() {
        return SubscriptionService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("subscriptionId", "organizationId", "vmsNumber");
    }

    @Override
    public void call(SubscriptionService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        final String subscriptionId = params.get("subscriptionId");
        final String organizationId = params.get("organizationId");

        VOInstanceInfo instanceInfo = new VOInstanceInfo();
        instanceInfo.setVmsNumber(Integer.parseInt(params.get("vmsNumber")));
        service.notifySubscriptionAboutVmsNumber(subscriptionId, organizationId,
                instanceInfo);
    }

    @Override
    public String getComment() {
        return null;
    }

}
