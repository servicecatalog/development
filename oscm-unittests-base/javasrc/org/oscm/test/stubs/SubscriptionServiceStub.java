/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.test.stubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.RoleDefinition;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.TriggerProcess;
import org.oscm.domobjects.UsageLicense;
import org.oscm.internal.intf.SubscriptionService;
import org.oscm.internal.tables.Pagination;
import org.oscm.internal.types.enumtypes.OperationStatus;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.MandatoryUdaMissingException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.OperationStateException;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.types.exception.PaymentDataException;
import org.oscm.internal.types.exception.PaymentInformationException;
import org.oscm.internal.types.exception.PriceModelException;
import org.oscm.internal.types.exception.ServiceChangedException;
import org.oscm.internal.types.exception.ServiceParameterException;
import org.oscm.internal.types.exception.SubscriptionMigrationException;
import org.oscm.internal.types.exception.SubscriptionStateException;
import org.oscm.internal.types.exception.SubscriptionStillActiveException;
import org.oscm.internal.types.exception.TechnicalServiceNotAliveException;
import org.oscm.internal.types.exception.TechnicalServiceOperationException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.vo.VOBillingContact;
import org.oscm.internal.vo.VOInstanceInfo;
import org.oscm.internal.vo.VOLocalizedText;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOParameter;
import org.oscm.internal.vo.VOPaymentInfo;
import org.oscm.internal.vo.VORoleDefinition;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceOperationParameterValues;
import org.oscm.internal.vo.VOSubscription;
import org.oscm.internal.vo.VOSubscriptionDetails;
import org.oscm.internal.vo.VOSubscriptionIdAndOrganizations;
import org.oscm.internal.vo.VOTechnicalServiceOperation;
import org.oscm.internal.vo.VOUda;
import org.oscm.internal.vo.VOUsageLicense;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserSubscription;
import org.oscm.subscriptionservice.local.SubscriptionServiceLocal;
import org.oscm.types.exceptions.UserNotAssignedException;

public class SubscriptionServiceStub implements SubscriptionService,
        SubscriptionServiceLocal {

    @Override
    public void abortAsyncSubscription(String subscriptionId,
            String organizationId, List<VOLocalizedText> reason)
            throws OrganizationAuthoritiesException,
            OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addRevokeUser(String subscriptionId,
            List<VOUsageLicense> usersToBeAdded, List<VOUser> usersToBeRevoked)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void completeAsyncSubscription(String subscriptionId,
            String organizationId, VOInstanceInfo instance)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOUserSubscription> getSubscriptionsForCurrentUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOUserSubscription> getSubscriptionsForUser(VOUser user)
            throws OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOOrganization> getCustomersForSubscriptionId(
            String subscriptionIdentifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscriptionDetails getSubscriptionDetails(String subId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscriptionDetails getSubscriptionDetails(long subscriptionKey) throws ObjectNotFoundException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getSubscriptionIdentifiers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOSubscription> getSubscriptionsForOrganization() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOService> getUpgradeOptions(String subscriptionId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOService> getUpgradeOptions(long subscriptionKey) throws ObjectNotFoundException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscriptionDetails modifySubscription(
            VOSubscription subscription, List<VOParameter> modifiedParameters,
            List<VOUda> udas) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscription subscribeToService(VOSubscription subscription,
            VOService product, List<VOUsageLicense> users,
            VOPaymentInfo paymentInfo, VOBillingContact billingContact,
            List<VOUda> udas) throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException, MandatoryUdaMissingException,
            SubscriptionStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean unsubscribeFromService(String subId)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscription upgradeSubscription(VOSubscription current,
            VOService newProduct, VOPaymentInfo paymentInfo,
            VOBillingContact billingContact, List<VOUda> udas) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UsageLicense addUserToSubscription(Subscription subscription,
            PlatformUser user, RoleDefinition serviceRole) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean expireOverdueSubscriptions(long currentTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean expireSubscription(Subscription subscriptionToExpire) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Subscription> getSubscriptionsForUserInt(PlatformUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void informProductAboutNewUsers(Subscription subscription,
            List<PlatformUser> usersToBeAdded)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Subscription loadSubscription(long subscriptionKey)
            throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean notifyAboutTimedoutSubscriptions(long currentTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void revokeUserFromSubscription(Subscription subscription,
            List<PlatformUser> users) throws SubscriptionStateException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscriptionDetails getSubscriptionForCustomer(
            String organizationId, String subscriptionId)
            throws ObjectNotFoundException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addRevokeUserInt(TriggerProcess triggerProcess)
            throws ObjectNotFoundException, ServiceParameterException,
            SubscriptionStateException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscriptionDetails modifySubscriptionInt(TriggerProcess tp)
            throws ObjectNotFoundException, NonUniqueBusinessKeyException,
            ValidationException, OperationNotPermittedException,
            SubscriptionMigrationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Subscription upgradeSubscriptionInt(TriggerProcess tp)
            throws ObjectNotFoundException, SubscriptionStateException,
            OperationNotPermittedException, ServiceChangedException,
            PriceModelException, PaymentInformationException,
            SubscriptionMigrationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unsubscribeFromServiceInt(TriggerProcess tp)
            throws ObjectNotFoundException, SubscriptionStateException,
            SubscriptionStillActiveException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Subscription subscribeToServiceInt(TriggerProcess tp)
            throws ObjectNotFoundException, ValidationException,
            OperationNotPermittedException, ServiceChangedException,
            PriceModelException, PaymentInformationException,
            NonUniqueBusinessKeyException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException, ServiceParameterException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyUserRole(Subscription subscription, PlatformUser usr,
            RoleDefinition roleDef) throws SubscriptionStateException,
            UserNotAssignedException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VORoleDefinition> getServiceRolesForSubscription(
            String subscription) throws ObjectNotFoundException,
            OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VORoleDefinition> getServiceRolesForSubscription(long subscriptionKey) throws ObjectNotFoundException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VORoleDefinition> getServiceRolesForService(VOService service)
            throws ObjectNotFoundException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsyncSubscriptionProgress(String subscriptionId,
            String organizationId, List<VOLocalizedText> localizedProgress)
            throws ObjectNotFoundException, SubscriptionStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void executeServiceOperation(VOSubscription subscription,
            VOTechnicalServiceOperation operation)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, ValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void terminateSubscription(VOSubscription subscription, String reason)
            throws ObjectNotFoundException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, ConcurrentModificationException {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean hasCurrentUserSubscriptions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOSubscriptionDetails modifySubscriptionPaymentData(
            VOSubscription subscription, VOBillingContact billingContact,
            VOPaymentInfo paymentInfo) throws ObjectNotFoundException,
            ConcurrentModificationException, OperationNotPermittedException,
            PaymentInformationException, SubscriptionStateException,
            PaymentDataException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOSubscription> getSubscriptionsForOrganizationWithFilter(
            Set<SubscriptionStatus> requiredStatus) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reportIssue(String subscriptionId, String subject,
            String issueText) throws ValidationException,
            ObjectNotFoundException, OperationNotPermittedException,
            MailOperationException, ValidationException {
        throw new UnsupportedOperationException();

    }

    @Override
    public List<VOSubscriptionIdAndOrganizations> getCustomerSubscriptions()
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOSubscriptionIdAndOrganizations> getSubscriptionsForTerminate()
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Subscription> getSubscriptionsForManagers()
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Subscription> getSubscriptionsForManagers(Pagination pagination) throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Subscription> getSubscriptionsForCurrentUser(Pagination pagination) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UsageLicense getSubscriptionUsageLicense(PlatformUser user, Long subKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSubscriptionOwner(Subscription sub) {

    }

    @Override
    public Subscription getMySubscriptionDetails(long key) {
        return null;
    }

    @Override
    public void completeAsyncModifySubscription(String subscriptionId,
            String organizationId, VOInstanceInfo instanceId)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abortAsyncModifySubscription(String subscriptionId,
            String organizationId, List<VOLocalizedText> reason)
            throws OrganizationAuthoritiesException,
            OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void completeAsyncUpgradeSubscription(String subscriptionId,
            String organizationId, VOInstanceInfo instance)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abortAsyncUpgradeSubscription(String subscriptionId,
            String organizationId, List<VOLocalizedText> reason)
            throws OrganizationAuthoritiesException,
            OperationNotPermittedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOServiceOperationParameterValues> getServiceOperationParameterValues(
            VOSubscription subscription, VOTechnicalServiceOperation operation)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TechnicalServiceNotAliveException {
        return new ArrayList<>();
    }

    @Override
    public void updateAccessInformation(String subscriptionId,
            String organizationId, VOInstanceInfo instanceInfo)
            throws ObjectNotFoundException, SubscriptionStateException,
            OperationNotPermittedException, ValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean validateSubscriptionIdForOrganization(String subscriptionId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void revokeUserFromSubscriptionInt(Subscription subscription,
            List<PlatformUser> users) throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsyncOperationProgress(String transactionId,
            OperationStatus status, List<VOLocalizedText> progress)
            throws OperationNotPermittedException, OperationStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateAsyncSubscriptionStatus(String subscriptionId,
            String organizationId, VOInstanceInfo instanceInfo)
            throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.oscm.internal.intf.SubscriptionService#getSubscriptionDetailsWithoutOwnerCheck(long)
     */
    @Override
    public VOSubscriptionDetails getSubscriptionDetailsWithoutOwnerCheck(
            long subscriptionKey) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

}
