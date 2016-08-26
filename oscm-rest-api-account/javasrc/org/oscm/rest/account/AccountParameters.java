package org.oscm.rest.account;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.CommonParams;
import org.oscm.rest.common.RequestParameters;
import org.oscm.rest.common.WebException;

public class AccountParameters extends RequestParameters {

    @QueryParam("mId")
    private String marketplaceId;

    @PathParam("orgId")
    private String orgId;

    @Override
    public void validateParameters() throws WebApplicationException {
    }

    @Override
    public void update() {
    }

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public void validateId() throws WebApplicationException {
        if (orgId == null) {
            throw WebException.notFound().message(CommonParams.ERROR_INVALID_ID).build();
        }
    }

}
