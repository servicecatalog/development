/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.ReportingService;
import org.oscm.types.enumtypes.ReportType;

public class ReportingService_getAvailableReports implements
        IOperationDescriptor<ReportingService> {

    @Override
    public String getName() {
        return "ReportingService.getAvailableReports";
    }

    @Override
    public Class<ReportingService> getServiceType() {
        return ReportingService.class;
    }

    @Override
    public List<String> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public void call(ReportingService service, RequestLogEntry logEntry,
            Map<String, String> params) {
        logEntry.setResult(service.getAvailableReports(ReportType.ALL));
    }

    @Override
    public String getComment() {
        return null;
    }

}
