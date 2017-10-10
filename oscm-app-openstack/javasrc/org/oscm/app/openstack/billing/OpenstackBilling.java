package org.oscm.app.openstack.billing;

import java.util.Calendar;

import org.openstack4j.model.compute.SimpleTenantUsage;
import org.oscm.app.openstack.OpenstackClient;
import org.oscm.app.openstack.billing.BaseBillingEvent.BILLING_EVENT;
import org.oscm.app.openstack.controller.PropertyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to handle all billing relevant operations.
 */
public class OpenstackBilling {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(OpenstackBilling.class);

    public static final String INSTANCE_ID = "INSTANCE_ID";
    
    private PropertyHandler paramHandler;

    public OpenstackBilling(PropertyHandler paramHandler) {
        this.paramHandler = paramHandler;
    }

    /**
     * Charge all monthly exceeded credit costs.
     */
    public void chargeMonthlyFees() throws Exception {
        Calendar x = Calendar.getInstance();
        String month = String.format("%02d",
                Integer.valueOf(x.get(Calendar.MONTH) + 1));
        int year = x.get(Calendar.YEAR);
        String billperiod = year + month;
        OpenstackClient osClient = new OpenstackClient(paramHandler);
        String startTime = "2017-07-23T00:00:00";
        String endTime = "2017-07-23T23:59:59";
        SimpleTenantUsage usage = osClient.getUsage(paramHandler.getTenantId(), startTime, endTime);
        UsageEvent evt = new UsageEvent(paramHandler);
        evt.submit(BILLING_EVENT.EVENT_TOTAL_HOURS, Long.parseLong(usage.getTotalHours()));
        evt.submit(BILLING_EVENT.EVENT_RAM_MEGABYTE_HOURS, usage.getTotalMemoryMbUsage().longValue());
        evt.submit(BILLING_EVENT.EVENT_CPU_HOURS, usage.getTotalVcpusUsage().longValue());
        evt.submit(BILLING_EVENT.EVENT_DISK_GIGABYTE_HOURS, usage.getTotalLocalGbUsage().longValue());
    }

}
