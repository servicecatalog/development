package org.oscm.app.openstack.billing;

import java.util.Calendar;

import org.oscm.app.openstack.controller.PropertyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for implementing the pre-paid billing event processing.
 */
public class UsageEvent extends BaseBillingEvent {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(UsageEvent.class);

    public UsageEvent(PropertyHandler paramHandler) {
        super(paramHandler);
    }

    /**
     * Submit a pre-paid billing event for the given type (from "now" until end
     * of billing period)
     */
    public void submit(BILLING_EVENT eventId, long multiplier) throws Exception {
        LOGGER.info("USAGE_event: " + eventId.toString() + " x "
                + Long.toString(multiplier));
        // occurenceTime will be last day of previous month
        Calendar x = Calendar.getInstance();
        int month = x.get(Calendar.MONTH);
        int year = x.get(Calendar.YEAR);
        Calendar y = Calendar.getInstance();
        y.clear();
        y.set(year, month, 0);
        long occTime = y.getTimeInMillis();

        submitBillingEvent(paramHandler.getTPAuthentication().getUserName(), eventId, multiplier,
                occTime);
    }
}
