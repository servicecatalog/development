package org.oscm.rest.service.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOPricedOption;
import org.oscm.rest.common.Representation;

public class PricedOptionRepresentation extends Representation {

    private transient VOPricedOption vo;

    private BigDecimal pricePerUser = BigDecimal.ZERO;
    private BigDecimal pricePerSubscription = BigDecimal.ZERO;
    private Long parameterOptionKey;
    private String optionId;
    private List<PricedRoleRepresentation> roleSpecificUserPrices;

    public PricedOptionRepresentation() {
        this(new VOPricedOption());
    }

    public PricedOptionRepresentation(VOPricedOption po) {
        vo = po;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setKey(convertIdToKey());
        vo.setOptionId(getOptionId());
        if (getParameterOptionKey() != null) {
            vo.setParameterOptionKey(getParameterOptionKey().longValue());
        }
        vo.setPricePerSubscription(getPricePerSubscription());
        vo.setPricePerUser(getPricePerUser());
        vo.setRoleSpecificUserPrices(PricedRoleRepresentation.update(getRoleSpecificUserPrices()));
        vo.setVersion(convertETagToVersion());

    }

    @Override
    public void convert() {
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setOptionId(vo.getOptionId());
        setParameterOptionKey(Long.valueOf(vo.getParameterOptionKey()));
        setPricePerSubscription(vo.getPricePerSubscription());
        setPricePerUser(vo.getPricePerUser());
        setRoleSpecificUserPrices(PricedRoleRepresentation.convert(vo.getRoleSpecificUserPrices()));
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

    public Long getParameterOptionKey() {
        return parameterOptionKey;
    }

    public void setParameterOptionKey(Long parameterOptionKey) {
        this.parameterOptionKey = parameterOptionKey;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public List<PricedRoleRepresentation> getRoleSpecificUserPrices() {
        return roleSpecificUserPrices;
    }

    public void setRoleSpecificUserPrices(List<PricedRoleRepresentation> roleSpecificUserPrices) {
        this.roleSpecificUserPrices = roleSpecificUserPrices;
    }

    public VOPricedOption getVO() {
        return vo;
    }

    public static List<PricedOptionRepresentation> convert(List<VOPricedOption> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        List<PricedOptionRepresentation> result = new ArrayList<PricedOptionRepresentation>();
        for (VOPricedOption po : options) {
            PricedOptionRepresentation por = new PricedOptionRepresentation(po);
            por.convert();
            result.add(por);
        }
        return result;
    }

    public static List<VOPricedOption> update(List<PricedOptionRepresentation> pricedOptions) {
        List<VOPricedOption> result = new ArrayList<VOPricedOption>();
        if (pricedOptions == null) {
            return result;
        }
        for (PricedOptionRepresentation por : pricedOptions) {
            por.update();
            result.add(por.getVO());
        }
        return result;
    }

}
