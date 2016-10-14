package org.oscm.rest.subscription.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.EventType;
import org.oscm.internal.vo.VOEventDefinition;
import org.oscm.rest.common.Representation;

public class EventDefinitionRepresentation extends Representation {

    private transient VOEventDefinition vo;

    private EventType eventType;
    private String eventId;
    private String eventDescription;

    public EventDefinitionRepresentation() {
        this(new VOEventDefinition());
    }

    public EventDefinitionRepresentation(VOEventDefinition e) {
        vo = e;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setEventDescription(eventDescription);
        vo.setEventId(eventId);
        vo.setEventType(eventType);
        vo.setKey(convertIdToKey());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setEventDescription(vo.getEventDescription());
        setEventId(vo.getEventId());
        setEventType(vo.getEventType());
        setId(Long.valueOf(vo.getKey()));
        setETag(Long.valueOf(vo.getVersion()));
    }

    public VOEventDefinition getVO() {
        return vo;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

}
