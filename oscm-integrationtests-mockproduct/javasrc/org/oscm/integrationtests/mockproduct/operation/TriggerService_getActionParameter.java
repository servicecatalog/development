/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                  
 *  Creation Date: 23.06.15 17:18
 *
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.TriggerService;
import org.oscm.types.enumtypes.TriggerProcessParameterType;
import org.oscm.vo.VOTriggerProcessParameter;

public class TriggerService_getActionParameter implements
        IOperationDescriptor<TriggerService> {

    @Override
    public String getName() {
        return "TriggerService.getActionParameter";
    }

    @Override
    public Class<TriggerService> getServiceType() {
        return TriggerService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("triggerProcessKey", "paramType");
    }

    @Override
    public void call(TriggerService service, RequestLogEntry logEntry,
            Map<String, String> parameters) throws Exception {
        VOTriggerProcessParameter parameter = service
                .getActionParameter(Long.parseLong(parameters
                        .get("triggerProcessKey")), TriggerProcessParameterType
                        .valueOf(parameters.get("paramType")));
        logEntry.setResult(parameter);
    }

    @Override
    public String getComment() {
        return null;
    }
}
