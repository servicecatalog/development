/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 24.04.17 15:08
 *
 ******************************************************************************/

package org.oscm.operatorsvc.client.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.oscm.internal.vo.VOSubscriptionUsageEntry;
import org.oscm.operatorsvc.client.CommandContext;

/**
 * @author tokoda
 * 
 */
public class GetSubscriptionUsage extends GetUserOperationLogCommand {

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList(ARG_FILE_NAME);
    }

    @Override
    public String getDescription() {
        return "Returns generated a CSV report.\n"
                + "Each line in the report represents one subscription and consists of:\n"
                + "\n" + "-    Customer organization id\n"
                + "-    Customer organization name\n"
                + "-    Subscription name\n" + "-    Marketable service name\n"
                + "-    Technical service name\n"
                + "-    Supplier organization id\n"
                + "-    Supplier organization name\n" + "-    Number of users\n"
                + "-    Number of VMs ";
    }

    @Override
    public String getName() {
        return "getsubscriptionusage";
    }

    @Override
    public boolean run(CommandContext ctx) throws Exception {
        // read parameters
        String outputFileName = ctx.getString(ARG_FILE_NAME);
        String validateError = validateFileName(outputFileName);
        if (validateError != null) {
            ctx.err().print(validateError);
            ctx.err().flush();
            return false;
        }
        Collection<VOSubscriptionUsageEntry> entries = ctx.getService()
                .getSubscriptionUsageReport();

        String csv = "";
        for (VOSubscriptionUsageEntry entry : entries) {
            csv += toCSV(entry);
        }
        return writeResults(ctx, outputFileName, csv);


    }

    private String toCSV(VOSubscriptionUsageEntry entry) {
        return entry.getCustomerOrgId() + "," + entry.getCustomerOrgName() + ","
                + entry.getSubscriptionName() + ","
                + entry.getMarketableServiceName() + ","
                + entry.getTechnicalServiceName() + ","
                + entry.getSupplierOrganizationId() + ","
                + entry.getSupplierOrganizationName() + ","
                + entry.getNumberOfusers() + "," + entry.getNumberOfVMs() + "\n";
    }

}
