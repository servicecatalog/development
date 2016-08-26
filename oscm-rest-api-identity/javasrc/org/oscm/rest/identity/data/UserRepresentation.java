package org.oscm.rest.identity.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.Salutation;
import org.oscm.internal.types.enumtypes.UserAccountStatus;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.Representation;

public class UserRepresentation extends Representation {

    private String email;
    private String firstName;
    private String additionalName;
    private String lastName;
    private String address;
    private String phone;
    private String locale;
    private Salutation salutation;
    private String realmUserId;
    private boolean remoteLdapActive;
    private String organizationId;
    private String userId;
    private UserAccountStatus status;
    private Set<OrganizationRoleType> organizationRoles = new HashSet<OrganizationRoleType>();
    private Set<UserRoleType> userRoles = new HashSet<UserRoleType>();

    transient VOUserDetails ud;

    public UserRepresentation() {
        this(new VOUserDetails());
    }

    public UserRepresentation(VOUserDetails details) {
        ud = details;
    }

    @Override
    public void validateContent() throws WebApplicationException {
        // nothing right now
    }

    @Override
    public void update() {
        ud.setAdditionalName(getAdditionalName());
        ud.setAddress(getAddress());
        ud.setEMail(getEmail());
        ud.setFirstName(getFirstName());
        if (getId() != null) {
            ud.setKey(getId().longValue());
        }
        ud.setLastName(getLastName());
        ud.setLocale(getLocale());
        ud.setOrganizationId(getOrganizationId());
        ud.setOrganizationRoles(getOrganizationRoles());
        ud.setPhone(getPhone());
        ud.setRealmUserId(getRealmUserId());
        ud.setRemoteLdapActive(isRemoteLdapActive());
        ud.setSalutation(getSalutation());
        ud.setStatus(getStatus());
        ud.setUserId(getUserId());
        ud.setUserRoles(getUserRoles());
        if (getTag() != null) {
            ud.setVersion(Integer.parseInt(getTag()));
        }
    }

    @Override
    public void convert() {
        setAdditionalName(ud.getAdditionalName());
        setAddress(ud.getAddress());
        setEmail(ud.getEMail());
        setFirstName(ud.getFirstName());
        setId(Long.valueOf(ud.getKey()));
        setLastName(ud.getLastName());
        setLocale(ud.getLocale());
        setOrganizationId(ud.getOrganizationId());
        setOrganizationRoles(ud.getOrganizationRoles());
        setPhone(ud.getPhone());
        setRealmUserId(ud.getRealmUserId());
        setRemoteLdapActive(ud.isRemoteLdapActive());
        setSalutation(ud.getSalutation());
        setStatus(ud.getStatus());
        setTag(String.valueOf(ud.getVersion()));
        setUserId(ud.getUserId());
        setUserRoles(ud.getUserRoles());
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getRealmUserId() {
        return realmUserId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public void setRealmUserId(String realmUserId) {
        this.realmUserId = realmUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(UserAccountStatus status) {
        this.status = status;
    }

    public UserAccountStatus getStatus() {
        return status;
    }

    public Set<UserRoleType> getUserRoles() {
        return userRoles;
    }

    public String getAddress() {
        return address;
    }

    public void setUserRoles(Set<UserRoleType> userRoles) {
        this.userRoles = userRoles;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<OrganizationRoleType> getOrganizationRoles() {
        return organizationRoles;
    }

    public void setOrganizationRoles(Set<OrganizationRoleType> organizationRoles) {
        this.organizationRoles = organizationRoles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAdditionalName() {
        return additionalName;
    }

    public void setAdditionalName(String additionalName) {
        this.additionalName = additionalName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Salutation getSalutation() {
        return salutation;
    }

    public void setSalutation(Salutation salutation) {
        this.salutation = salutation;
    }

    public boolean isRemoteLdapActive() {
        return remoteLdapActive;
    }

    public void setRemoteLdapActive(boolean remoteLdapActive) {
        this.remoteLdapActive = remoteLdapActive;
    }

    public VOUserDetails getVO() {
        return ud;
    }

    public static final Collection<UserRepresentation> convert(List<VOUserDetails> list) {
        Collection<UserRepresentation> result = new ArrayList<UserRepresentation>();
        for (VOUserDetails vo : list) {
            result.add(new UserRepresentation(vo));
        }
        return result;
    }
}
