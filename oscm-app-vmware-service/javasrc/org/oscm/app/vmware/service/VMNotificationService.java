/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) FUJITSU LIMITED - ALL RIGHTS RESERVED.                  
 *       
 *  Creation Date: 2013-05-12                                                       
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.service;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.oscm.app.vmware.business.trigger.ServiceValidationTask;
import org.oscm.notification.intf.NotificationService;
import org.oscm.notification.vo.VONotification;
import org.oscm.types.enumtypes.UserRoleType;
import org.oscm.vo.VOOrganization;
import org.oscm.vo.VOOrganizationPaymentConfiguration;
import org.oscm.vo.VOParameter;
import org.oscm.vo.VOPaymentType;
import org.oscm.vo.VOService;
import org.oscm.vo.VOServicePaymentConfiguration;
import org.oscm.vo.VOSubscription;
import org.oscm.vo.VOTriggerProcess;
import org.oscm.vo.VOUsageLicense;
import org.oscm.vo.VOUser;
import org.oscm.vo.VOUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(serviceName = "VMwareNotificationService", portName = "StubServicePort", targetNamespace = "http://oscm.org/xsd", endpointInterface = "org.oscm.notification.intf.NotificationService")
public class VMNotificationService implements NotificationService {

    private final static Logger log = LoggerFactory
            .getLogger(VMNotificationService.class);

    @Override
    public void billingPerformed(String xmlBillingData) {
        log.debug("");
    }

    @Override
    public void onActivateProduct(VOTriggerProcess process, VOService product) {
        log.debug("product: " + product.getNameToDisplay() + " productId: "
                + product.getServiceId() + " technicalId: "
                + product.getTechnicalId() + " sellerId: "
                + product.getSellerId());
        ServiceValidationTask task = new ServiceValidationTask();
        task.validate(process, product);
    }

    @Override
    public void onAddRevokeUser(VOTriggerProcess process, String subscriptionId,
            List<VOUsageLicense> usersToBeAdded,
            List<VOUser> usersToBeRevoked) {
    }

    @Override
    public void onAddSupplier(VOTriggerProcess process, String supplierId) {
        log.debug("supplierId: " + supplierId);
    }

    @Override
    public void onDeactivateProduct(VOTriggerProcess process,
            VOService product) {
        log.debug("product: " + product.getNameToDisplay());
    }

    @Override
    public void onModifySubscription(VOTriggerProcess process,
            VOSubscription subscription, List<VOParameter> parameters) {
        log.debug("subscriptionId:" + subscription.getSubscriptionId());
    }

    @Override
    public void onRegisterCustomer(VOTriggerProcess process,
            VOOrganization organization, VOUserDetails user,
            Properties properties) {
        log.debug("");
    }

    @Override
    public void onRegisterUserInOwnOrganization(VOTriggerProcess process,
            VOUserDetails user, List<UserRoleType> roles,
            String marketplaceId) {
        log.debug("marketplaceId: " + marketplaceId);
    }

    @Override
    public void onRemoveSupplier(VOTriggerProcess process, String supplierId) {
        log.debug("supplierId: " + supplierId);
    }

    @Override
    public void onSaveCustomerPaymentConfiguration(VOTriggerProcess process,
            VOOrganizationPaymentConfiguration configuration) {
        log.debug("");
    }

    @Override
    public void onSaveDefaultPaymentConfiguration(VOTriggerProcess process,
            Set<VOPaymentType> configuration) {
        log.debug("");
    }

    @Override
    public void onSaveServiceDefaultPaymentConfiguration(
            VOTriggerProcess process, Set<VOPaymentType> configuration) {
        log.debug("");
    }

    @Override
    public void onSaveServicePaymentConfiguration(VOTriggerProcess process,
            VOServicePaymentConfiguration configuration) {
        log.debug("");
    }

    @Override
    public void onSubscribeToProduct(VOTriggerProcess process,
            VOSubscription subscription, VOService product,
            List<VOUsageLicense> users) {
        log.debug("product: " + product.getNameToDisplay());
    }

    @Override
    public void onSubscriptionCreation(VOTriggerProcess process,
            VOService product, List<VOUsageLicense> usersToBeAdded,
            VONotification notification) {
        log.debug("product: " + product.getNameToDisplay());
    }

    @Override
    public void onSubscriptionModification(VOTriggerProcess process,
            List<VOParameter> parameter, VONotification notification) {
        log.debug("");
    }

    @Override
    public void onSubscriptionTermination(VOTriggerProcess process,
            VONotification notification) {
        log.debug("");
    }

    @Override
    public void onUnsubscribeFromProduct(VOTriggerProcess process,
            String subscriptionId) {
        log.debug("subscriptionId: " + subscriptionId);
    }

    @Override
    public void onUpgradeSubscription(VOTriggerProcess process,
            VOSubscription subscription, VOService product) {
        log.debug("");
    }

    @Override
    public void onCancelAction(@WebParam(name = "actionKey") long arg0) {
        log.debug("actionKey: " + arg0);
    }

}
