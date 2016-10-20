package org.oscm.rest.account.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.oscm.internal.vo.VOPaymentInfo;
import org.oscm.rest.common.Representation;

public class PaymentInfoRepresentation extends Representation {

    private transient VOPaymentInfo vo;

    private PaymentTypeRepresentation paymentType;
    private String infoId;
    private String providerName;
    private String accountNumber;

    public PaymentInfoRepresentation() {
        this(new VOPaymentInfo());
        setPaymentType(new PaymentTypeRepresentation());
    }

    public PaymentInfoRepresentation(VOPaymentInfo pi) {
        vo = pi;
        setPaymentType(new PaymentTypeRepresentation(pi.getPaymentType()));
    }

    @Override
    public void validateContent() throws WebApplicationException {
        // nothing to do
    }

    @Override
    public void update() {
        paymentType.update();
        vo.setAccountNumber(accountNumber);
        vo.setId(infoId);
        vo.setKey(convertIdToKey());
        vo.setPaymentType(paymentType.getVO());
        vo.setProviderName(providerName);
        vo.setVersion(convertETagToVersion());
    }

    @Override
    public void convert() {
        paymentType.convert();
        setAccountNumber(vo.getAccountNumber());
        setId(Long.valueOf(vo.getKey()));
        setInfoId(vo.getId());
        setProviderName(vo.getProviderName());
        setETag(Long.valueOf(vo.getVersion()));
    }

    public PaymentTypeRepresentation getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentTypeRepresentation paymentType) {
        this.paymentType = paymentType;
    }

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public VOPaymentInfo getVO() {
        return vo;
    }

    public static Collection<PaymentInfoRepresentation> convert(List<VOPaymentInfo> paymentInfos) {
        List<PaymentInfoRepresentation> result = new ArrayList<PaymentInfoRepresentation>();
        for (VOPaymentInfo pi : paymentInfos) {
            result.add(new PaymentInfoRepresentation(pi));
        }
        return result;
    }

}
