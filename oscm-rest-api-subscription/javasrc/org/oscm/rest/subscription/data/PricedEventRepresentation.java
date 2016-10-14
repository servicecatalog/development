package org.oscm.rest.subscription.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOPricedEvent;
import org.oscm.rest.common.Representation;

public class PricedEventRepresentation extends Representation {

    private transient VOPricedEvent vo;

    private List<SteppedPriceRepresentation> steppedPrices;
    private EventDefinitionRepresentation eventDefinition;
    private BigDecimal eventPrice = BigDecimal.ZERO;

    public PricedEventRepresentation() {
        this(new VOPricedEvent());
    }

    public PricedEventRepresentation(VOPricedEvent pe) {
        vo = pe;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        if (getEventDefinition() != null) {
            getEventDefinition().update();
            vo.setEventDefinition(getEventDefinition().getVO());
        }
        vo.setEventPrice(getEventPrice());
        vo.setKey(convertIdToKey());
        vo.setSteppedPrices(SteppedPriceRepresentation.update(getSteppedPrices()));
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setETag(Long.valueOf(vo.getVersion()));
        setEventDefinition(new EventDefinitionRepresentation(vo.getEventDefinition()));
        getEventDefinition().convert();
        setEventPrice(vo.getEventPrice());
        setId(Long.valueOf(vo.getKey()));
        setSteppedPrices(SteppedPriceRepresentation.convert(vo.getSteppedPrices()));
    }

    public List<SteppedPriceRepresentation> getSteppedPrices() {
        return steppedPrices;
    }

    public void setSteppedPrices(List<SteppedPriceRepresentation> steppedPrices) {
        this.steppedPrices = steppedPrices;
    }

    public EventDefinitionRepresentation getEventDefinition() {
        return eventDefinition;
    }

    public void setEventDefinition(EventDefinitionRepresentation eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    public BigDecimal getEventPrice() {
        return eventPrice;
    }

    public void setEventPrice(BigDecimal eventPrice) {
        this.eventPrice = eventPrice;
    }

    public VOPricedEvent getVO() {
        return vo;
    }

    public static List<PricedEventRepresentation> convert(List<VOPricedEvent> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        List<PricedEventRepresentation> result = new ArrayList<PricedEventRepresentation>();
        for (VOPricedEvent pe : events) {
            PricedEventRepresentation per = new PricedEventRepresentation(pe);
            per.convert();
            result.add(per);
        }
        return result;
    }

    public static List<VOPricedEvent> update(List<PricedEventRepresentation> events) {
        List<VOPricedEvent> result = new ArrayList<VOPricedEvent>();
        if (events == null) {
            return result;
        }
        for (PricedEventRepresentation per : events) {
            per.update();
            result.add(per.getVO());
        }
        return result;
    }

}
