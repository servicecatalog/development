package org.oscm.rest.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.RepresentationCollection;
import org.oscm.rest.common.RestBackend;
import org.oscm.rest.identity.data.OnBehalfUserRepresentation;
import org.oscm.rest.identity.data.RolesRepresentation;
import org.oscm.rest.identity.data.UserRepresentation;

@Stateless
public class UserBackend {

    @EJB
    IdentityService is;

    public RestBackend.GetCollection<UserRepresentation, UserParameters> getLdapUsers() {
        return new RestBackend.GetCollection<UserRepresentation, UserParameters>() {

            @Override
            public RepresentationCollection<UserRepresentation> getCollection(UserParameters params) throws Exception {
                Collection<UserRepresentation> list = UserRepresentation
                        .convert(is.searchLdapUsers(params.getPattern()));
                return new RepresentationCollection<UserRepresentation>(list);
            }
        };
    }

    public RestBackend.Post<UserRepresentation, UserParameters> postLdapUser() {
        return new RestBackend.Post<UserRepresentation, UserParameters>() {

            @Override
            public Object post(UserRepresentation content, UserParameters params) throws Exception {
                VOUserDetails vo = content.getVO();
                is.importLdapUsers(Collections.singletonList(vo), params.getMarketplaceId());
                return vo.getUserId();
            }
        };
    }

    public RestBackend.GetCollection<UserRepresentation, UserParameters> getUsers() {
        return new RestBackend.GetCollection<UserRepresentation, UserParameters>() {

            @Override
            public RepresentationCollection<UserRepresentation> getCollection(UserParameters params) throws Exception {
                Collection<UserRepresentation> list = UserRepresentation.convert(is.getUsersForOrganization());
                return new RepresentationCollection<UserRepresentation>(list);
            }

        };
    }

    public RestBackend.Post<UserRepresentation, UserParameters> postUser() {
        return new RestBackend.Post<UserRepresentation, UserParameters>() {

            @Override
            public Object post(UserRepresentation content, UserParameters params) throws Exception {
                VOUserDetails vo = content.getVO();
                vo = is.createUser(vo, new ArrayList<UserRoleType>(vo.getUserRoles()), params.getMarketplaceId());
                if (vo == null) {
                    return null;
                }
                return vo.getUserId();
            }
        };
    }

    public RestBackend.Get<UserRepresentation, UserParameters> getUser() {
        return new RestBackend.Get<UserRepresentation, UserParameters>() {

            @Override
            public UserRepresentation get(UserParameters params) throws Exception {
                VOUser vo = new VOUser();
                vo.setUserId(params.getUserId());
                return new UserRepresentation(is.getUserDetails(vo));
            }

        };
    }

    public RestBackend.Put<UserRepresentation, UserParameters> putUser() {
        return new RestBackend.Put<UserRepresentation, UserParameters>() {

            @Override
            public boolean put(UserRepresentation content, UserParameters params) throws Exception {
                // TODO: handle id change?
                is.updateUser(content.getVO()).getUserId();
                return true;
            }

        };
    }

    public RestBackend.Delete<UserParameters> deleteUser() {
        return new RestBackend.Delete<UserParameters>() {

            @Override
            public boolean delete(UserParameters params) throws Exception {
                VOUser vo = new VOUser();
                vo.setUserId(params.getUserId());
                vo.setVersion(params.eTagToVersion());
                is.deleteUser(vo, params.getMarketplaceId());
                return true;
            }
        };
    }

    public RestBackend.Get<RolesRepresentation, UserParameters> getRoles() {
        return new RestBackend.Get<RolesRepresentation, UserParameters>() {

            @Override
            public RolesRepresentation get(UserParameters params) throws Exception {
                VOUser vo = new VOUser();
                vo.setUserId(params.getUserId());
                return new RolesRepresentation(is.getUserDetails(vo));
            }
        };
    }

    public RestBackend.Put<RolesRepresentation, UserParameters> putRoles() {
        return new RestBackend.Put<RolesRepresentation, UserParameters>() {

            @Override
            public boolean put(RolesRepresentation content, UserParameters params) throws Exception {
                VOUserDetails vo = content.getVO();
                vo.setUserId(params.getUserId());
                is.setUserRoles(vo, new ArrayList<UserRoleType>(content.getUserRoles()));
                return true;
            }
        };
    }

    public RestBackend.Post<OnBehalfUserRepresentation, UserParameters> postOnBehalfUser() {
        return new RestBackend.Post<OnBehalfUserRepresentation, UserParameters>() {

            @Override
            public Object post(OnBehalfUserRepresentation content, UserParameters params) throws Exception {
                return is.createOnBehalfUser(content.getOrganizationId(), content.getPassword()).getUserId();
            }
        };
    }

    public RestBackend.Delete<UserParameters> deleteOBehalfUser() {
        return new RestBackend.Delete<UserParameters>() {

            @Override
            public boolean delete(UserParameters params) throws Exception {
                is.cleanUpCurrentUser();
                return true;
            }
        };
    }
}
