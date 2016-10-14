package org.oscm.rest.subscription.data;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.PriceModelType;
import org.oscm.internal.types.enumtypes.PricingPeriod;
import org.oscm.internal.vo.VOPriceModel;
import org.oscm.rest.common.Representation;

public class PriceModelRepresentation extends Representation {

    private transient VOPriceModel vo;

    private String description;
    private PricingPeriod period;
    private BigDecimal pricePerPeriod = BigDecimal.ZERO;
    private BigDecimal pricePerUserAssignment = BigDecimal.ZERO;
    private String currencyISOCode;
    private BigDecimal oneTimeFee = BigDecimal.ZERO;
    private PriceModelType type = PriceModelType.FREE_OF_CHARGE;
    private List<PricedParameterRepresentation> selectedParameters;
    private List<PricedRoleRepresentation> roleSpecificUserPrices;
    private List<SteppedPriceRepresentation> steppedPrices;
    private List<PricedEventRepresentation> consideredEvents;

    public PriceModelRepresentation() {
        this(new VOPriceModel());
    }

    public PriceModelRepresentation(VOPriceModel pm) {
        vo = pm;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setConsideredEvents(PricedEventRepresentation.update(getConsideredEvents()));
        vo.setCurrencyISOCode(getCurrencyISOCode());
        vo.setDescription(getDescription());
        vo.setKey(convertIdToKey());
        vo.setOneTimeFee(getOneTimeFee());
        vo.setPeriod(getPeriod());
        vo.setPricePerPeriod(getPricePerPeriod());
        vo.setPricePerUserAssignment(getPricePerUserAssignment());
        vo.setRoleSpecificUserPrices(PricedRoleRepresentation.update(getRoleSpecificUserPrices()));
        vo.setSelectedParameters(PricedParameterRepresentation.update(getSelectedParameters()));
        vo.setSteppedPrices(SteppedPriceRepresentation.update(getSteppedPrices()));
        vo.setType(getType());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setConsideredEvents(PricedEventRepresentation.convert(vo.getConsideredEvents()));
        setCurrencyISOCode(vo.getCurrencyISOCode());
        setDescription(vo.getDescription());
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setOneTimeFee(vo.getOneTimeFee());
        setPeriod(vo.getPeriod());
        setPricePerPeriod(vo.getPricePerPeriod());
        setPricePerUserAssignment(vo.getPricePerUserAssignment());
        setRoleSpecificUserPrices(PricedRoleRepresentation.convert(vo.getRoleSpecificUserPrices()));
        setSelectedParameters(PricedParameterRepresentation.convert(vo.getSelectedParameters()));
        setSteppedPrices(SteppedPriceRepresentation.convert(vo.getSteppedPrices()));
        setType(vo.getType());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PricingPeriod getPeriod() {
        return period;
    }

    public void setPeriod(PricingPeriod period) {
        this.period = period;
    }

    public BigDecimal getPricePerPeriod() {
        return pricePerPeriod;
    }

    public void setPricePerPeriod(BigDecimal pricePerPeriod) {
        this.pricePerPeriod = pricePerPeriod;
    }

    public BigDecimal getPricePerUserAssignment() {
        return pricePerUserAssignment;
    }

    public void setPricePerUserAssignment(BigDecimal pricePerUserAssignment) {
        this.pricePerUserAssignment = pricePerUserAssignment;
    }

    public String getCurrencyISOCode() {
        return currencyISOCode;
    }

    public void setCurrencyISOCode(String currencyISOCode) {
        this.currencyISOCode = currencyISOCode;
    }

    public BigDecimal getOneTimeFee() {
        return oneTimeFee;
    }

    public void setOneTimeFee(BigDecimal oneTimeFee) {
        this.oneTimeFee = oneTimeFee;
    }

    public PriceModelType getType() {
        return type;
    }

    public void setType(PriceModelType type) {
        this.type = type;
    }

    public List<PricedParameterRepresentation> getSelectedParameters() {
        return selectedParameters;
    }

    public void setSelectedParameters(List<PricedParameterRepresentation> selectedParameters) {
        this.selectedParameters = selectedParameters;
    }

    public List<PricedRoleRepresentation> getRoleSpecificUserPrices() {
        return roleSpecificUserPrices;
    }

    public void setRoleSpecificUserPrices(List<PricedRoleRepresentation> roleSpecificUserPrices) {
        this.roleSpecificUserPrices = roleSpecificUserPrices;
    }

    public List<SteppedPriceRepresentation> getSteppedPrices() {
        return steppedPrices;
    }

    public void setSteppedPrices(List<SteppedPriceRepresentation> steppedPrices) {
        this.steppedPrices = steppedPrices;
    }

    public List<PricedEventRepresentation> getConsideredEvents() {
        return consideredEvents;
    }

    public void setConsideredEvents(List<PricedEventRepresentation> consideredEvents) {
        this.consideredEvents = consideredEvents;
    }

}
