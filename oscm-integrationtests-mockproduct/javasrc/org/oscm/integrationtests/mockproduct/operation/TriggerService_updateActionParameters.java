/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                  
 *  Creation Date: 23.06.15 17:17
 *
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.TriggerService;
import org.oscm.types.enumtypes.TriggerProcessParameterType;
import org.oscm.vo.VOTriggerProcessParameter;

public class TriggerService_updateActionParameters implements
        IOperationDescriptor<TriggerService> {

    @Override
    public String getName() {
        return "TriggerService.updateActionParameters";
    }

    @Override
    public Class<TriggerService> getServiceType() {
        return TriggerService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("triggerProcessKey", "triggerProcessParamType",
                "serializedValue");
    }

    @SuppressWarnings("boxing")
    @Override
    public void call(TriggerService service, RequestLogEntry logEntry,
            Map<String, String> parameters) throws Exception {
        VOTriggerProcessParameter parameter = new VOTriggerProcessParameter();

        parameter.setTriggerProcessKey(Long.parseLong(parameters
                .get("triggerProcessKey")));
        parameter.setType(TriggerProcessParameterType.valueOf(parameters
                .get("triggerProcessParamType")));
        parameter.setValue(parameters.get("serializedValue"));

        service.updateActionParameters(
                Long.parseLong(parameters.get("triggerProcessKey")),
                Collections.singletonList(parameter));
    }

    @Override
    public String getComment() {
        return "Call getActionParameter() method first. Copy the parameter value in XML editor, edit the parameter values and copy the changed XML string into the 'serializedValue' input box.";
    }
}
