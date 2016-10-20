package org.oscm.rest.account.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOOrganization;
import org.oscm.rest.common.Representation;

public class OrganizationRepresentation extends Representation {

    private transient VOOrganization voOrg = new VOOrganization();

    private String organizationId;
    private String address;
    private String email;
    private String locale;
    private String name;
    private String phone;
    private String url;
    private String description;
    private String domicileCountry;
    private String supportEmail;

    public OrganizationRepresentation() {
        this(new VOOrganization());
    }

    public OrganizationRepresentation(VOOrganization org) {
        voOrg = org;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        voOrg.setAddress(getAddress());
        voOrg.setDescription(getDescription());
        voOrg.setDomicileCountry(getDomicileCountry());
        voOrg.setEmail(getEmail());
        voOrg.setKey(convertIdToKey());
        voOrg.setLocale(getLocale());
        voOrg.setName(getName());
        voOrg.setOrganizationId(getOrganizationId());
        voOrg.setPhone(getPhone());
        voOrg.setSupportEmail(getSupportEmail());
        voOrg.setVersion(convertETagToVersion());
        voOrg.setUrl(getUrl());
    }

    @Override
    public void convert() {
        setAddress(voOrg.getAddress());
        setDescription(voOrg.getDescription());
        setDomicileCountry(voOrg.getDomicileCountry());
        setEmail(voOrg.getEmail());
        setId(Long.valueOf(voOrg.getKey()));
        setLocale(voOrg.getLocale());
        setName(voOrg.getName());
        setOrganizationId(voOrg.getOrganizationId());
        setPhone(voOrg.getPhone());
        setSupportEmail(voOrg.getSupportEmail());
        setETag(Long.valueOf(voOrg.getVersion()));
        setUrl(voOrg.getUrl());
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomicileCountry() {
        return domicileCountry;
    }

    public void setDomicileCountry(String domicileCountry) {
        this.domicileCountry = domicileCountry;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public VOOrganization getVO() {
        return voOrg;
    }

}
