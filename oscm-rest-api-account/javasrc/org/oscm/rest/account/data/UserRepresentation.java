package org.oscm.rest.account.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.Salutation;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.rest.common.Representation;

public class UserRepresentation extends Representation {

    private transient VOUserDetails ud;

    private String email;
    private String firstName;
    private String additionalName;
    private String lastName;
    private String address;
    private String phone;
    private String locale;
    private Salutation salutation;
    private String realmUserId;
    private String organizationId;
    private String userId;

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
        ud.setKey(convertIdToKey());
        ud.setLastName(getLastName());
        ud.setLocale(getLocale());
        ud.setOrganizationId(getOrganizationId());
        ud.setPhone(getPhone());
        ud.setRealmUserId(getRealmUserId());
        ud.setSalutation(getSalutation());
        ud.setUserId(getUserId());
        ud.setVersion(convertETagToVersion());
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
        setPhone(ud.getPhone());
        setRealmUserId(ud.getRealmUserId());
        setSalutation(ud.getSalutation());
        setETag(Long.valueOf(ud.getVersion()));
        setUserId(ud.getUserId());
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

    public String getAddress() {
        return address;
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

    public VOUserDetails getVO() {
        return ud;
    }
}
