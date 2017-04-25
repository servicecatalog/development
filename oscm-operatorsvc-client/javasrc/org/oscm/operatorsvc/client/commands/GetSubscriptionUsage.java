/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 24.04.17 15:08
 *
 ******************************************************************************/

package org.oscm.operatorsvc.client.commands;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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





        outputFileName = "c:/cyce.txt";







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

        // write result
        File outputFile = new File(outputFileName);
        PrintWriter pw = null;
        try {
            pw = createPrintWriter(outputFile);
            pw.print(csv);
            pw.flush();
        } catch (Exception e) {
            ctx.err().print("The file can not be created.\n");
            ctx.err().flush();
            return false;
        } finally {
            if (pw != null) {
                pw.close();
            }
        }

        ctx.out().print(String.format("Successfully created the file: %s%n",
                outputFile.getCanonicalPath()));
        ctx.out().flush();
        return true;
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
