package org.oscm.rest.identity.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.Representation;

public class OnBehalfUserRepresentation extends Representation {

    private String organizationId;
    private String password;
    private String userId;

    public OnBehalfUserRepresentation() {
    }

    public OnBehalfUserRepresentation(VOUserDetails vo) {
        setId(Long.valueOf(vo.getKey()));
        setOrganizationId(vo.getOrganizationId());
        setETag(Long.valueOf(vo.getVersion()));
        setUserId(vo.getUserId());
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        // only used as input
    }

    @Override
    public void convert() {

    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
