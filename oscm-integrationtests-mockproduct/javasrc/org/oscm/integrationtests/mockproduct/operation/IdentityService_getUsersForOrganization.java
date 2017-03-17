/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.IdentityService;

public class IdentityService_getUsersForOrganization implements
        IOperationDescriptor<IdentityService> {

    @Override
    public String getName() {
        return "IdentityService.getUsersForOrganization";
    }

    @Override
    public Class<IdentityService> getServiceType() {
        return IdentityService.class;
    }

    @Override
    public List<String> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public void call(IdentityService service, RequestLogEntry logEntry,
            Map<String, String> params) {
        logEntry.setResult(service.getUsersForOrganization());
    }

    @Override
    public String getComment() {
        return null;
    }

}
