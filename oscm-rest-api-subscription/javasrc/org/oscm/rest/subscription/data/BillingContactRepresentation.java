package org.oscm.rest.subscription.data;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOBillingContact;
import org.oscm.rest.common.Representation;

public class BillingContactRepresentation extends Representation {

    private transient VOBillingContact vo;

    private String contactId;

    public BillingContactRepresentation() {
        this(new VOBillingContact());
    }

    public BillingContactRepresentation(VOBillingContact bc) {
        vo = bc;
    }

    @Override
    public void validateContent() throws WebApplicationException {
        // nothing to do
    }

    @Override
    public void update() {
        vo.setId(getContactId());
        vo.setKey(convertIdToKey());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setContactId(vo.getId());
        setId(Long.valueOf(vo.getKey()));
        setETag(Long.valueOf(vo.getVersion()));
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public VOBillingContact getVO() {
        return vo;
    }
}
