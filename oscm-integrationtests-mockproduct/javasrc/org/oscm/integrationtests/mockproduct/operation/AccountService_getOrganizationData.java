/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.AccountService;

public class AccountService_getOrganizationData implements
        IOperationDescriptor<AccountService> {

    @Override
    public String getName() {
        return "AccountService.getOrganizationData";
    }

    @Override
    public Class<AccountService> getServiceType() {
        return AccountService.class;
    }

    @Override
    public List<String> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public void call(AccountService service, RequestLogEntry logEntry,
            Map<String, String> params) {
        logEntry.setResult(service.getOrganizationData());
    }

    @Override
    public String getComment() {
        return null;
    }
}
