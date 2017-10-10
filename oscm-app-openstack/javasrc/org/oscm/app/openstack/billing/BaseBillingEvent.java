package org.oscm.app.openstack.billing;

import java.util.Calendar;

import org.oscm.app.openstack.controller.PropertyHandler;
import org.oscm.intf.EventService;
import org.oscm.types.exceptions.DuplicateEventException;
import org.oscm.vo.VOGatheredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for implementing the billing event processing.
 */
public abstract class BaseBillingEvent {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BaseBillingEvent.class);

    public enum BILLING_EVENT {
        EVENT_DISK_GIGABYTE_HOURS,
        EVENT_CPU_HOURS,
        EVENT_RAM_MEGABYTE_HOURS,
        EVENT_TOTAL_HOURS
    }

    PropertyHandler paramHandler;

    BaseBillingEvent(PropertyHandler paramHandler) {
        this.paramHandler = paramHandler;
    }

    /**
     * Returns current date as calendar
     */
    Calendar getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Returns number of months between two dates
     */
    int getMonthsDifference(Calendar c1, Calendar c2) {
        int m1 = c1.get(Calendar.YEAR) * 12 + c1.get(Calendar.MONTH);
        int m2 = c2.get(Calendar.YEAR) * 12 + c2.get(Calendar.MONTH);
        return m2 - m1;
    }

    /**
     * Creates a new billing event
     */
    void submitBillingEvent(String userId, BILLING_EVENT eventId,
            long multiplier, long occurenceTime) throws Exception {
        String instanceId = paramHandler.getSettings().getParameters().get(OpenstackBilling.INSTANCE_ID).getValue();
        String technicalServiceInstanceId = paramHandler.getSettings().getParameters().get(PropertyHandler.TECHNICAL_SERVICE_INSTANCE_ID).getValue();
        LOGGER.debug("submitBillingEvent( instanceId: " + instanceId + " technicalServiceInstanceId: " + technicalServiceInstanceId  + "  userId: "
                + userId + "  eventId: " + eventId.toString() + "  multiplier: "
                + Long.toString(multiplier) + "  occurenceTime: "
                + Long.toString(occurenceTime) + ")");

        EventService svc = paramHandler.getWebService(EventService.class);

        VOGatheredEvent evt = new VOGatheredEvent();
        evt.setActor(userId);
        evt.setEventId(eventId.toString());
        evt.setMultiplier(multiplier);
        evt.setOccurrenceTime(occurenceTime);
        evt.setUniqueId("" + occurenceTime);
        try {
            svc.recordEventForInstance(technicalServiceInstanceId, instanceId, evt);
        } catch (DuplicateEventException e) {
            // It is okay as we collect the events more often
            LOGGER.debug("Event already inserted");

        }
    }

}
