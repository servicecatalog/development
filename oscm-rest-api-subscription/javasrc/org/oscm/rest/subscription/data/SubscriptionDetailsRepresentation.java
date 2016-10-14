package org.oscm.rest.subscription.data;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.vo.VOSubscriptionDetails;
import org.oscm.rest.common.Representation;

public class SubscriptionDetailsRepresentation extends Representation {

    private transient VOSubscriptionDetails vo;

    private BillingContactRepresentation billingContact;
    private PaymentInfoRepresentation paymentInfo;
    private List<UsageLicenseRepresentation> usageLicenses;
    private ServiceRepresentation service;
    private List<OperationRepresentation> operations;
    private PriceModelRepresentation priceModel;

    private Long activationDate;
    private Long creationDate;
    private Long deactivationDate;
    private String serviceAccessInfo;
    private String serviceLoginPath;
    private SubscriptionStatus status;
    private String serviceInstanceId;
    private boolean timeoutMailSent;
    private String purchaseOrderNumber;
    private String subscriptionId;
    private String provisioningProgress;
    private int numberOfAssignedUsers;
    private long unitKey;
    private String unitName;
    private String successInfo;

    public SubscriptionDetailsRepresentation() {
        this(new VOSubscriptionDetails());
    }

    public SubscriptionDetailsRepresentation(VOSubscriptionDetails sub) {
        vo = sub;
    }

    @Override
    public void validateContent() throws WebApplicationException {

    }

    @Override
    public void update() {
        // not needed, just used for GET
        vo.setKey(convertIdToKey());
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        setActivationDate(vo.getActivationDate());
        if (vo.getBillingContact() != null) {
            setBillingContact(new BillingContactRepresentation(vo.getBillingContact()));
            getBillingContact().convert();
        }
        setCreationDate(vo.getCreationDate());
        setDeactivationDate(vo.getDeactivationDate());
        setETag(Long.valueOf(vo.getVersion()));
        setId(Long.valueOf(vo.getKey()));
        setNumberOfAssignedUsers(vo.getNumberOfAssignedUsers());
        setOperations(OperationRepresentation.convert(vo.getTechnicalServiceOperations()));
        if (vo.getPaymentInfo() != null) {
            setPaymentInfo(new PaymentInfoRepresentation(vo.getPaymentInfo()));
            getPaymentInfo().convert();
        }
        setPriceModel(new PriceModelRepresentation(vo.getPriceModel()));
        getPriceModel().convert();
        setProvisioningProgress(vo.getProvisioningProgress());
        setPurchaseOrderNumber(vo.getPurchaseOrderNumber());
        setService(new ServiceRepresentation(vo.getSubscribedService()));
        getService().convert();
        setServiceAccessInfo(vo.getServiceAccessInfo());
        setServiceInstanceId(vo.getServiceInstanceId());
        setServiceLoginPath(vo.getServiceLoginPath());
        setStatus(vo.getStatus());
        setSubscriptionId(vo.getSubscriptionId());
        setSuccessInfo(vo.getSuccessInfo());
        setTimeoutMailSent(vo.isTimeoutMailSent());
        setUnitKey(vo.getUnitKey());
        setUnitName(vo.getUnitName());
        setUsageLicenses(UsageLicenseRepresentation.convert(vo.getUsageLicenses()));
    }

    public BillingContactRepresentation getBillingContact() {
        return billingContact;
    }

    public void setBillingContact(BillingContactRepresentation billingContact) {
        this.billingContact = billingContact;
    }

    public PaymentInfoRepresentation getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfoRepresentation paymentInfo) {
        this.paymentInfo = paymentInfo;
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

    public String getServiceAccessInfo() {
        return serviceAccessInfo;
    }

    public void setServiceAccessInfo(String serviceAccessInfo) {
        this.serviceAccessInfo = serviceAccessInfo;
    }

    public String getServiceLoginPath() {
        return serviceLoginPath;
    }

    public void setServiceLoginPath(String serviceLoginPath) {
        this.serviceLoginPath = serviceLoginPath;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public boolean isTimeoutMailSent() {
        return timeoutMailSent;
    }

    public void setTimeoutMailSent(boolean timeoutMailSent) {
        this.timeoutMailSent = timeoutMailSent;
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

    public String getProvisioningProgress() {
        return provisioningProgress;
    }

    public void setProvisioningProgress(String provisioningProgress) {
        this.provisioningProgress = provisioningProgress;
    }

    public int getNumberOfAssignedUsers() {
        return numberOfAssignedUsers;
    }

    public void setNumberOfAssignedUsers(int numberOfAssignedUsers) {
        this.numberOfAssignedUsers = numberOfAssignedUsers;
    }

    public long getUnitKey() {
        return unitKey;
    }

    public void setUnitKey(long unitKey) {
        this.unitKey = unitKey;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getSuccessInfo() {
        return successInfo;
    }

    public void setSuccessInfo(String successInfo) {
        this.successInfo = successInfo;
    }

    public List<UsageLicenseRepresentation> getUsageLicenses() {
        return usageLicenses;
    }

    public void setUsageLicenses(List<UsageLicenseRepresentation> usageLicenses) {
        this.usageLicenses = usageLicenses;
    }

    public ServiceRepresentation getService() {
        return service;
    }

    public void setService(ServiceRepresentation service) {
        this.service = service;
    }

    public List<OperationRepresentation> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationRepresentation> operations) {
        this.operations = operations;
    }

    public PriceModelRepresentation getPriceModel() {
        return priceModel;
    }

    public void setPriceModel(PriceModelRepresentation priceModel) {
        this.priceModel = priceModel;
    }

}
