package org.oscm.rest.identity.data;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.Representation;

public class RolesRepresentation extends Representation {

    private Set<UserRoleType> userRoles = new HashSet<UserRoleType>();

    transient VOUserDetails vo;

    public RolesRepresentation() {
        this(new VOUserDetails());
    }

    public RolesRepresentation(VOUserDetails details) {
        vo = details;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setKey(convertIdToKey());
        vo.setUserRoles(getUserRoles());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setId(Long.valueOf(vo.getKey()));
        setETag(Long.valueOf(vo.getVersion()));
        setUserRoles(vo.getUserRoles());
    }

    public Set<UserRoleType> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRoleType> userRoles) {
        this.userRoles = userRoles;
    }

    public VOUserDetails getVO() {
        return vo;
    }
}
