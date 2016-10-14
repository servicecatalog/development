package org.oscm.rest.subscription;

import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.oscm.rest.common.RequestParameters;

public class SubscriptionParameters extends RequestParameters {

    @QueryParam("userId")
    private String userId;

    @Override
    public void validateParameters() throws WebApplicationException {

    }

    @Override
    public void update() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
