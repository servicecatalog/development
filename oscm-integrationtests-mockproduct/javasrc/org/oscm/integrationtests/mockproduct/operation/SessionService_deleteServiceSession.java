/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.SessionService;

public class SessionService_deleteServiceSession implements
        IOperationDescriptor<SessionService> {

    @Override
    public String getName() {
        return "SessionService.deleteServiceSession";
    }

    @Override
    public Class<SessionService> getServiceType() {
        return SessionService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("bssId", "subKey");
    }

    @Override
    public void call(SessionService service, RequestLogEntry logEntry,
            Map<String, String> params) {
        final long subKey = Long.parseLong(params.get("subKey"));
        service.deleteServiceSession(subKey, params.get("bssId"));
    }

    @Override
    public String getComment() {
        return null;
    }

}
