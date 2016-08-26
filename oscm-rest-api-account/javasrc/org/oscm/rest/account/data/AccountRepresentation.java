package org.oscm.rest.account.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.vo.LdapProperties;
import org.oscm.internal.vo.Setting;
import org.oscm.rest.common.Representation;

public class AccountRepresentation extends Representation {

    private transient LdapProperties props;

    private OrganizationRepresentation organization;
    private UserRepresentation user;
    private String password;
    private Long serviceKey;
    private String sellerId;
    private Map<String, String> ldapProperties;
    private OrganizationRoleType[] organizationRoles;

    public AccountRepresentation() {
    }

    public AccountRepresentation(LdapProperties ldapProperties) {
        props = ldapProperties;
    }

    @Override
    public void validateContent() throws WebApplicationException {
        // nothing to do
    }

    @Override
    public void update() {
        organization.update();
        user.update();
        props = toProperties();
    }

    private LdapProperties toProperties() {
        if (ldapProperties == null) {
            return null;
        }
        Properties properties = new Properties();
        properties.putAll(ldapProperties);
        return new LdapProperties(properties);
    }

    @Override
    public void convert() {
        organization.convert();
        user.convert();
        ldapProperties = toMap();
    }

    private Map<String, String> toMap() {
        if (props == null) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        for (Setting setting : props.getSettings()) {
            map.put(setting.getKey(), setting.getValue());
        }
        return map;
    }

    public OrganizationRepresentation getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationRepresentation organization) {
        this.organization = organization;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(Long serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public Map<String, String> getLdapProperties() {
        return ldapProperties;
    }

    public void setLdapProperties(Map<String, String> ldapProperties) {
        this.ldapProperties = ldapProperties;
    }

    public LdapProperties getProps() {
        return props;
    }

    public OrganizationRoleType[] getOrganizationRoles() {
        return organizationRoles;
    }

    public void setOrganizationRoles(OrganizationRoleType[] organizationRoles) {
        this.organizationRoles = organizationRoles;
    }

    public boolean isSelfRegistration() {
        return (password != null && password.trim().length() > 0);
    }

    public boolean isCustomerRegistration() {
        return organizationRoles == null;
    }

}
