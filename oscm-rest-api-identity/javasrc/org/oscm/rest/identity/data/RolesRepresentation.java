package org.oscm.rest.identity.data;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.Representation;

public class RolesRepresentation extends Representation {

    private Set<UserRoleType> userRoles = new HashSet<UserRoleType>();

    transient VOUserDetails ud;

    public RolesRepresentation() {
        this(new VOUserDetails());
    }

    public RolesRepresentation(VOUserDetails details) {
        ud = details;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        if (getId() != null) {
            ud.setKey(getId().longValue());
        }
        ud.setUserRoles(getUserRoles());
        if (getTag() != null) {
            ud.setVersion(Integer.parseInt(getTag()));
        }
    }

    @Override
    public void convert() {
        setId(Long.valueOf(ud.getKey()));
        setTag(String.valueOf(ud.getVersion()));
        setUserRoles(ud.getUserRoles());
    }

    public Set<UserRoleType> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRoleType> userRoles) {
        this.userRoles = userRoles;
    }

    public VOUserDetails getVO() {
        return ud;
    }
}
