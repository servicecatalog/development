package org.oscm.rest.service.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOPricedParameter;
import org.oscm.rest.common.Representation;

public class PricedParameterRepresentation extends Representation {

    private transient VOPricedParameter vo;

    private ParameterDefinitionRepresentation parameterDef;
    private BigDecimal pricePerUser = BigDecimal.ZERO;
    private BigDecimal pricePerSubscription = BigDecimal.ZERO;
    private Long parameterKey;
    private List<PricedRoleRepresentation> roleSpecificUserPrices;
    private List<SteppedPriceRepresentation> steppedPrices;
    private List<PricedOptionRepresentation> pricedOptions;

    public PricedParameterRepresentation() {
        this(new VOPricedParameter());
    }

    public PricedParameterRepresentation(VOPricedParameter pp) {
        vo = pp;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setKey(convertIdToKey());
        if (getParameterKey() != null) {
            vo.setParameterKey(getParameterKey().longValue());
        }
        vo.setPricedOptions(PricedOptionRepresentation.update(getPricedOptions()));
        vo.setPricePerSubscription(getPricePerSubscription());
        vo.setPricePerUser(getPricePerUser());
        vo.setRoleSpecificUserPrices(PricedRoleRepresentation.update(getRoleSpecificUserPrices()));
        vo.setSteppedPrices(SteppedPriceRepresentation.update(getSteppedPrices()));
        vo.setVersion(convertETagToVersion());
        if (getParameterDef() != null) {
            getParameterDef().update();
            vo.setVoParameterDef(getParameterDef().getVO());
        }
    }

    @Override
    public void convert() {
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setParameterDef(new ParameterDefinitionRepresentation(vo.getVoParameterDef()));
        getParameterDef().convert();
        setParameterKey(Long.valueOf(vo.getParameterKey()));
        setPricedOptions(PricedOptionRepresentation.convert(vo.getPricedOptions()));
        setPricePerSubscription(vo.getPricePerSubscription());
        setPricePerUser(vo.getPricePerUser());
        setRoleSpecificUserPrices(PricedRoleRepresentation.convert(vo.getRoleSpecificUserPrices()));
        setSteppedPrices(SteppedPriceRepresentation.convert(vo.getSteppedPrices()));
    }

    public BigDecimal getPricePerUser() {
        return pricePerUser;
    }

    public void setPricePerUser(BigDecimal pricePerUser) {
        this.pricePerUser = pricePerUser;
    }

    public BigDecimal getPricePerSubscription() {
        return pricePerSubscription;
    }

    public void setPricePerSubscription(BigDecimal pricePerSubscription) {
        this.pricePerSubscription = pricePerSubscription;
    }

    public Long getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(Long parameterKey) {
        this.parameterKey = parameterKey;
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

    public List<PricedOptionRepresentation> getPricedOptions() {
        return pricedOptions;
    }

    public void setPricedOptions(List<PricedOptionRepresentation> pricedOptions) {
        this.pricedOptions = pricedOptions;
    }

    public ParameterDefinitionRepresentation getParameterDef() {
        return parameterDef;
    }

    public void setParameterDef(ParameterDefinitionRepresentation parameterDef) {
        this.parameterDef = parameterDef;
    }

    public VOPricedParameter getVO() {
        return vo;
    }

    public static List<PricedParameterRepresentation> convert(List<VOPricedParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return null;
        }
        List<PricedParameterRepresentation> result = new ArrayList<PricedParameterRepresentation>();
        for (VOPricedParameter pp : parameters) {
            PricedParameterRepresentation ppr = new PricedParameterRepresentation(pp);
            ppr.convert();
            result.add(ppr);
        }
        return result;
    }

    public static List<VOPricedParameter> update(List<PricedParameterRepresentation> parameters) {
        List<VOPricedParameter> result = new ArrayList<VOPricedParameter>();
        if (parameters == null) {
            return result;
        }
        for (PricedParameterRepresentation ppr : parameters) {
            ppr.update();
            result.add(ppr.getVO());

        }
        return result;
    }

}
