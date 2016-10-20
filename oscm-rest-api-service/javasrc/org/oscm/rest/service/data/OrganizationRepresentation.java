package org.oscm.rest.service.data;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOOrganization;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RepresentationCollection;

public class OrganizationRepresentation extends Representation {

    private transient VOOrganization voOrg = new VOOrganization();

    private String organizationId;

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
        voOrg.setKey(convertIdToKey());
        voOrg.setOrganizationId(getOrganizationId());
        voOrg.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setId(Long.valueOf(voOrg.getKey()));
        setOrganizationId(voOrg.getOrganizationId());
        setETag(Long.valueOf(voOrg.getVersion()));
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public VOOrganization getVO() {
        return voOrg;
    }

    public static RepresentationCollection<OrganizationRepresentation> toCollection(List<VOOrganization> list) {
        // TODO Auto-generated method stub
        return null;
    }

}
