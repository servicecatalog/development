package org.oscm.rest.service;

import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.RequestParameters;

public class ServiceParameters extends RequestParameters {

    @PathParam("orgKey")
    private Long orgKey;

    @PathParam("orgId")
    private String orgId;

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

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

}
