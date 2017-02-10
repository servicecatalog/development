/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.TriggerService;
import org.oscm.vo.VOLocalizedText;

public class TriggerService_rejectAction implements
        IOperationDescriptor<TriggerService> {

    @Override
    public String getName() {
        return "TriggerService.rejectAction";
    }

    @Override
    public Class<TriggerService> getServiceType() {
        return TriggerService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("key", "reason");
    }

    @Override
    public void call(TriggerService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        VOLocalizedText text = new VOLocalizedText("en", params.get("reason"));
        service.rejectAction(Long.parseLong(params.get("key")),
                Collections.singletonList(text));
    }

    @Override
    public String getComment() {
        return null;
    }
}
