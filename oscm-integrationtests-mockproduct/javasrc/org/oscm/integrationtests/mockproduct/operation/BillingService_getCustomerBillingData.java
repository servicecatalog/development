/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.BillingService;

public class BillingService_getCustomerBillingData implements
        IOperationDescriptor<BillingService> {

    @Override
    public String getName() {
        return "BillingService.getCustomerBillingData";
    }

    @Override
    public Class<BillingService> getServiceType() {
        return BillingService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("from", "to", "customerIdList");
    }

    @Override
    public void call(BillingService service, RequestLogEntry logEntry,
            Map<String, String> params) throws Exception {
        final DateFormat df = DateFormat.getInstance();
        Long from = Long.valueOf(df.parse(params.get("from")).getTime());
        Long to = Long.valueOf(df.parse(params.get("to")).getTime());
        final String[] ids = params.get("to").split(",");
        byte[] xml = service.getCustomerBillingData(from, to,
                Arrays.asList(ids));
        logEntry.setResult(new String(xml, "UTF-8"));
    }

    @Override
    public String getComment() {
        return null;
    }
}
