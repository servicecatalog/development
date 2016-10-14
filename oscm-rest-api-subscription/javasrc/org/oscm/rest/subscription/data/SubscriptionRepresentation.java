package org.oscm.rest.subscription.data;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.vo.VOSubscription;
import org.oscm.internal.vo.VOUserSubscription;
import org.oscm.rest.common.Representation;
import org.oscm.rest.common.RepresentationCollection;

public class SubscriptionRepresentation extends Representation {

    private transient VOSubscription vo;

    private Long activationDate;
    private Long creationDate;
    private Long deactivationDate;
    private SubscriptionStatus status;
    private String subscriptionId;
    private Long unitKey;
    private String unitName;
    private UsageLicenseRepresentation usageLicense;

    public SubscriptionRepresentation() {
        this(new VOSubscription());
    }

    public SubscriptionRepresentation(VOSubscription sub) {
        vo = sub;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setActivationDate(getActivationDate());
        vo.setCreationDate(getCreationDate());
        vo.setDeactivationDate(getDeactivationDate());
        vo.setKey(convertIdToKey());
        vo.setStatus(getStatus());
        vo.setSubscriptionId(getSubscriptionId());
        if (getUnitKey() != null) {
            vo.setUnitKey(getUnitKey().longValue());
        }
        vo.setUnitName(getUnitName());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setActivationDate(vo.getActivationDate());
        setCreationDate(vo.getCreationDate());
        setDeactivationDate(vo.getDeactivationDate());
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setStatus(vo.getStatus());
        setSubscriptionId(vo.getSubscriptionId());
        if (vo.getUnitKey() != 0) {
            setUnitKey(Long.valueOf(vo.getUnitKey()));
        }
        setUnitName(vo.getUnitName());
        if (vo instanceof VOUserSubscription) {
            UsageLicenseRepresentation ulr = new UsageLicenseRepresentation(((VOUserSubscription) vo).getLicense());
            ulr.convert();
            setUsageLicense(ulr);
        }
    }

    public static RepresentationCollection<SubscriptionRepresentation> toCollection(List<? extends VOSubscription> subs) {
        List<SubscriptionRepresentation> result = new ArrayList<SubscriptionRepresentation>();
        for (VOSubscription sub : subs) {
            result.add(new SubscriptionRepresentation(sub));
        }
        return new RepresentationCollection<SubscriptionRepresentation>();
    }

    public Long getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Long activationDate) {
        this.activationDate = activationDate;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public Long getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Long deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getUnitKey() {
        return unitKey;
    }

    public void setUnitKey(Long unitKey) {
        this.unitKey = unitKey;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public UsageLicenseRepresentation getUsageLicense() {
        return usageLicense;
    }

    public void setUsageLicense(UsageLicenseRepresentation usageLicense) {
        this.usageLicense = usageLicense;
    }

}
