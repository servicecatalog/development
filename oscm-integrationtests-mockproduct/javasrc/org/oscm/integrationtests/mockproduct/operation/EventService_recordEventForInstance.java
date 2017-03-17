/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2017
 *******************************************************************************/

package org.oscm.integrationtests.mockproduct.operation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.oscm.integrationtests.mockproduct.RequestLogEntry;
import org.oscm.intf.EventService;
import org.oscm.types.exceptions.DuplicateEventException;
import org.oscm.types.exceptions.ObjectNotFoundException;
import org.oscm.types.exceptions.OrganizationAuthoritiesException;
import org.oscm.types.exceptions.ValidationException;
import org.oscm.vo.VOGatheredEvent;

public class EventService_recordEventForInstance implements
        IOperationDescriptor<EventService> {

    @Override
    public String getName() {
        return "EventService.recordEventForInstance";
    }

    @Override
    public Class<EventService> getServiceType() {
        return EventService.class;
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("technicalServiceId", "instanceId", "eventId",
                "actor", "timestamp", "multiplier", "uniqueid");
    }

    @Override
    public void call(EventService service, RequestLogEntry logEntry,
            Map<String, String> params) throws DuplicateEventException,
            OrganizationAuthoritiesException, ObjectNotFoundException,
            ValidationException {
        final String technicalServiceId = params.get("technicalServiceId");
        final String instanceId = params.get("instanceId");
        final VOGatheredEvent event = new VOGatheredEvent();
        event.setEventId(params.get("eventId"));
        event.setActor(params.get("actor"));
        event.setOccurrenceTime(Long.parseLong(params.get("timestamp")));
        String uniqueId = params.get("uniqueid");
        if (uniqueId != null && uniqueId.length() > 0) {
            event.setUniqueId(uniqueId);
        }
        final String multiplier = params.get("multiplier");
        if (multiplier != null && multiplier.length() > 0) {
            event.setMultiplier(Long.parseLong(multiplier));
        }
        service.recordEventForInstance(technicalServiceId, instanceId, event);
    }

    @Override
    public String getComment() {
        return null;
    }

}
