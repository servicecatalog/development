package org.oscm.rest.subscription.data;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOBillingContact;
import org.oscm.internal.vo.VOPaymentInfo;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOSubscription;
import org.oscm.internal.vo.VOUda;
import org.oscm.internal.vo.VOUsageLicense;
import org.oscm.rest.common.Representation;

public class SubscriptionCreationRepresentation extends Representation {

    private transient VOSubscription vo;

    private Long billingContactKey;
    private Long paymentInfoKey;
    private List<UsageLicenseRepresentation> users;
    private List<UdaRepresentation> udas;
    private ServiceRepresentation service;

    private String purchaseOrderNumber;
    private String subscriptionId;
    private Long unitKey;
    private String unitName;

    public SubscriptionCreationRepresentation() {
        this(new VOSubscription());
    }

    public SubscriptionCreationRepresentation(VOSubscription sub) {
        vo = sub;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        vo.setPurchaseOrderNumber(getPurchaseOrderNumber());
        vo.setSubscriptionId(getSubscriptionId());
        if (getUnitKey() != null) {
            vo.setUnitKey(getUnitKey().longValue());
        }
        vo.setUnitName(getUnitName());
    }

    @Override
    public void convert() {
        // not really needed as it is only input for creating a subscription
        setPurchaseOrderNumber(vo.getPurchaseOrderNumber());
        setSubscriptionId(vo.getSubscriptionId());
    }

    public VOSubscription getVO() {
        return vo;
    }

    public List<VOUsageLicense> getUsageLicenses() {
        if (users == null) {
            return null;
        }
        List<VOUsageLicense> result = new ArrayList<VOUsageLicense>();
        for (UsageLicenseRepresentation ulr : users) {
            result.add(ulr.getVO());
        }
        return result;
    }

    public List<VOUda> getUdas() {
        if (udas == null) {
            return null;
        }
        List<VOUda> result = new ArrayList<VOUda>();
        for (UdaRepresentation uda : udas) {
            result.add(uda.getVO());
        }
        return result;
    }

    public VOBillingContact getBillingContact() {
        if (billingContactKey == null) {
            return null;
        }
        VOBillingContact bc = new VOBillingContact();
        bc.setKey(billingContactKey.longValue());
        return bc;
    }

    public VOPaymentInfo getPaymentInfo() {
        if (paymentInfoKey == null) {
            return null;
        }
        VOPaymentInfo pi = new VOPaymentInfo();
        pi.setKey(paymentInfoKey.longValue());
        return pi;
    }

    public VOService getVOService() {
        if (service == null) {
            return null;
        }
        return service.getVO();
    }

    public Long getBillingContactKey() {
        return billingContactKey;
    }

    public void setBillingContactKey(Long billingContactKey) {
        this.billingContactKey = billingContactKey;
    }

    public Long getPaymentInfoKey() {
        return paymentInfoKey;
    }

    public void setPaymentInfoKey(Long paymentInfoKey) {
        this.paymentInfoKey = paymentInfoKey;
    }

    public List<UsageLicenseRepresentation> getUsers() {
        return users;
    }

    public void setUsers(List<UsageLicenseRepresentation> users) {
        this.users = users;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
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

    public void setUdas(List<UdaRepresentation> udas) {
        this.udas = udas;
    }

    public ServiceRepresentation getService() {
        return service;
    }

    public void setService(ServiceRepresentation service) {
        this.service = service;
    }

}
