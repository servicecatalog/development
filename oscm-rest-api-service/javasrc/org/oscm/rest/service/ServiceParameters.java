package org.oscm.rest.service;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.RequestParameters;

public class ServiceParameters extends RequestParameters {

    @PathParam("orgId")
    private Long orgKey;

    @Override
    public void validateParameters() throws WebApplicationException {

    }

    @Override
    public void update() {

    }

    public Long getOrgKey() {
        return orgKey;
    }

    public void setOrgKey(Long orgKey) {
        this.orgKey = orgKey;
    }

}
