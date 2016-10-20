package org.oscm.rest.account.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOBillingContact;
import org.oscm.rest.common.Representation;

public class BillingContactRepresentation extends Representation {

    private transient VOBillingContact vo;

    private String email;
    private String companyName;
    private String address;
    private boolean orgAddressUsed;
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
        vo.setAddress(getAddress());
        vo.setCompanyName(getCompanyName());
        vo.setId(getContactId());
        vo.setEmail(getEmail());
        vo.setKey(convertIdToKey());
        vo.setOrgAddressUsed(isOrgAddressUsed());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setAddress(vo.getAddress());
        setCompanyName(vo.getCompanyName());
        setContactId(vo.getId());
        setEmail(vo.getEmail());
        setId(Long.valueOf(vo.getKey()));
        setOrgAddressUsed(vo.isOrgAddressUsed());
        setETag(Long.valueOf(vo.getVersion()));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOrgAddressUsed() {
        return orgAddressUsed;
    }

    public void setOrgAddressUsed(boolean orgAddressUsed) {
        this.orgAddressUsed = orgAddressUsed;
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

    public static final Collection<BillingContactRepresentation> convert(List<VOBillingContact> billingContacts) {
        List<BillingContactRepresentation> result = new ArrayList<BillingContactRepresentation>();
        for (VOBillingContact bc : billingContacts) {
            result.add(new BillingContactRepresentation(bc));
        }
        return result;
    }

}
