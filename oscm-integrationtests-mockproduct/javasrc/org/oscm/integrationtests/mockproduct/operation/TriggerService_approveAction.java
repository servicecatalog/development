/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.TriggerService;

public class TriggerService_approveAction implements
        IOperationDescriptor<TriggerService> {

    @Override
    public String getName() {
        return "TriggerService.approveAction";
    }

    @Override
    public Class<TriggerService> getServiceType() {
        return TriggerService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("key");
    }

    @Override
    public void call(TriggerService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        service.approveAction(Long.parseLong(params.get("key")));
    }

    @Override
    public String getComment() {
        return null;
    }
}
