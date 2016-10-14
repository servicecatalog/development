package org.oscm.rest.subscription.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOPricedRole;
import org.oscm.rest.common.Representation;

public class PricedRoleRepresentation extends Representation {

    private transient VOPricedRole vo;

    private BigDecimal pricePerUser = BigDecimal.ZERO;
    private RoleDefinitionRepresentation role;

    public PricedRoleRepresentation() {
        this(new VOPricedRole());
    }

    public PricedRoleRepresentation(VOPricedRole pr) {
        vo = pr;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setKey(convertIdToKey());
        vo.setPricePerUser(getPricePerUser());
        if (getRole() != null) {
            getRole().update();
            vo.setRole(getRole().getVO());
        }
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setPricePerUser(vo.getPricePerUser());
        setRole(new RoleDefinitionRepresentation(vo.getRole()));
        getRole().convert();
    }

    public BigDecimal getPricePerUser() {
        return pricePerUser;
    }

    public void setPricePerUser(BigDecimal pricePerUser) {
        this.pricePerUser = pricePerUser;
    }

    public RoleDefinitionRepresentation getRole() {
        return role;
    }

    public void setRole(RoleDefinitionRepresentation role) {
        this.role = role;
    }

    public VOPricedRole getVO() {
        return vo;
    }

    public static List<VOPricedRole> update(List<PricedRoleRepresentation> pricedRoles) {
        List<VOPricedRole> result = new ArrayList<VOPricedRole>();
        if (pricedRoles == null) {
            return result;
        }
        for (PricedRoleRepresentation prr : pricedRoles) {
            prr.update();
            result.add(prr.getVO());
        }
        return result;
    }

    public static List<PricedRoleRepresentation> convert(List<VOPricedRole> pricedRoles) {
        if (pricedRoles == null || pricedRoles.isEmpty()) {
            return null;
        }
        List<PricedRoleRepresentation> result = new ArrayList<PricedRoleRepresentation>();
        for (VOPricedRole pr : pricedRoles) {
            PricedRoleRepresentation prr = new PricedRoleRepresentation(pr);
            prr.convert();
            result.add(prr);
        }
        return result;
    }

}
