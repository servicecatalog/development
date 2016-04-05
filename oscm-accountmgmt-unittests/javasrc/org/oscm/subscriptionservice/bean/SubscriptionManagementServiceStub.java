/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.oscm.domobjects.Organization;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.RoleDefinition;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.TriggerProcess;
import org.oscm.domobjects.UsageLicense;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.internal.tables.Pagination;
import org.oscm.subscriptionservice.local.SubscriptionServiceLocal;
import org.oscm.types.exceptions.UserAlreadyAssignedException;
import org.oscm.types.exceptions.UserNotAssignedException;
import org.oscm.internal.intf.SubscriptionService;
import org.oscm.internal.types.enumtypes.OperationStatus;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.DomainObjectException.ClassEnum;
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

/**
 * Subscription Management Service Bean Stub
 */
@Stateless
@Remote(SubscriptionService.class)
@Local(SubscriptionServiceLocal.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class SubscriptionManagementServiceStub implements
        SubscriptionServiceLocal, SubscriptionService {

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public UsageLicense addUserToSubscription(Subscription subscription,
            PlatformUser user, RoleDefinition serviceRole)
            throws UserAlreadyAssignedException {

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<Subscription> getSubscriptionsForUserInt(PlatformUser user) {

        return new ArrayList<Subscription>();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void revokeUserFromSubscription(Subscription subscription,
            List<PlatformUser> users) throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

    }

    @Override
    public boolean addRevokeUser(String subscriptionId,
            List<VOUsageLicense> usersToBeAdded, List<VOUser> usersToBeRevoked)
            throws ObjectNotFoundException, ServiceParameterException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

        return true;
    }

    @Override
    public List<VOUserSubscription> getSubscriptionsForCurrentUser() {

        return new ArrayList<VOUserSubscription>();
    }

    @Override
    public List<VOUserSubscription> getSubscriptionsForUser(VOUser user)
            throws ObjectNotFoundException {

        return new ArrayList<VOUserSubscription>();
    }

    @Override
    public List<VOSubscription> getSubscriptionsForOrganization() {

        return new ArrayList<VOSubscription>();
    }

    @Override
    public List<VOService> getUpgradeOptions(String subscriptionId) {

        return new ArrayList<VOService>();
    }

    @Override
    public List<VOService> getUpgradeOptions(long subscriptionKey) throws ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    public VOSubscription subscribeToService(VOSubscription subscription,
            VOService product, List<VOUsageLicense> users,
            VOPaymentInfo paymentInfo, VOBillingContact billingContact,
            List<VOUda> udas) throws ObjectNotFoundException,
            NonUniqueBusinessKeyException, PaymentInformationException,
            ServiceParameterException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException, MandatoryUdaMissingException,
            SubscriptionStateException {

        return subscription;
    }

    @Override
    public boolean unsubscribeFromService(String subId)
            throws ObjectNotFoundException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

        return true;
    }

    @Override
    public VOSubscription upgradeSubscription(VOSubscription current,
            VOService newProduct, VOPaymentInfo paymentInfo,
            VOBillingContact billingContact, List<VOUda> udas)
            throws PaymentInformationException, MandatoryUdaMissingException {

        return null;
    }

    @Override
    public VOSubscriptionDetails getSubscriptionDetails(String subId)
            throws ObjectNotFoundException {

        return null;
    }

    @Override
    public VOSubscriptionDetails getSubscriptionDetails(long subscriptionKey) throws ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Subscription loadSubscription(long subscriptionKey)
            throws ObjectNotFoundException {
        if (subscriptionKey == 0) {
            throw new ObjectNotFoundException(ClassEnum.SUBSCRIPTION, "");
        }
        Organization organization = new Organization();
        organization.setOrganizationId("BMW");
        Subscription subscription = new Subscription();
        subscription.setKey(subscriptionKey);
        subscription.setOrganization(organization);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        return subscription;
    }

    @Override
    public VOSubscriptionDetails modifySubscription(
            VOSubscription subscription, List<VOParameter> modifiedParameters,
            List<VOUda> udas) throws NonUniqueBusinessKeyException,
            ObjectNotFoundException, OperationNotPermittedException,
            MandatoryUdaMissingException {

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public boolean expireOverdueSubscriptions(long currentTime) {

        return false;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public boolean expireSubscription(Subscription subscriptionToExpire) {

        return false;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void informProductAboutNewUsers(Subscription subscription,
            List<PlatformUser> usersToBeAdded)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

    }

    @Override
    public void completeAsyncSubscription(String subscriptionId,
            String customerId, VOInstanceInfo instance)
            throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {

    }

    @Override
    public void abortAsyncSubscription(String subscriptionId,
            String customerId, List<VOLocalizedText> reason)
            throws OrganizationAuthoritiesException,
            OperationNotPermittedException {

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public boolean notifyAboutTimedoutSubscriptions(long currentTime) {

        return false;
    }

    @Override
    public List<String> getSubscriptionIdentifiers() {

        return null;
    }

    @Override
    public List<VOOrganization> getCustomersForSubscriptionId(
            String subscriptionIdentifier) {

        return null;
    }

    @Override
    public List<VOSubscriptionIdAndOrganizations> getCustomerSubscriptions()
            throws OrganizationAuthoritiesException {

        return null;
    }

    @Override
    public VOSubscriptionDetails getSubscriptionForCustomer(
            String organizationId, String subscriptionId)
            throws ObjectNotFoundException, OperationNotPermittedException {

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void addRevokeUserInt(TriggerProcess triggerProcess)
            throws ObjectNotFoundException, ServiceParameterException,
            SubscriptionStateException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public VOSubscriptionDetails modifySubscriptionInt(TriggerProcess tp)
            throws ObjectNotFoundException, NonUniqueBusinessKeyException,
            ValidationException, OperationNotPermittedException,
            SubscriptionMigrationException {

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Subscription upgradeSubscriptionInt(TriggerProcess tp)
            throws ObjectNotFoundException, SubscriptionStateException,
            OperationNotPermittedException, ServiceChangedException,
            PriceModelException, PaymentInformationException,
            SubscriptionMigrationException {

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void unsubscribeFromServiceInt(TriggerProcess tp)
            throws ObjectNotFoundException, SubscriptionStateException,
            SubscriptionStillActiveException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Subscription subscribeToServiceInt(TriggerProcess tp)
            throws ObjectNotFoundException, ValidationException,
            OperationNotPermittedException, ServiceChangedException,
            PriceModelException, PaymentInformationException,
            NonUniqueBusinessKeyException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException, ServiceParameterException {

        return null;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void modifyUserRole(Subscription subscription, PlatformUser usr,
            RoleDefinition roleDef) throws SubscriptionStateException,
            UserNotAssignedException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

    }

    @Override
    public List<VORoleDefinition> getServiceRolesForSubscription(
            String subscription) throws ObjectNotFoundException,
            OperationNotPermittedException {

        return null;
    }

    @Override
    public List<VORoleDefinition> getServiceRolesForSubscription(long subscriptionKey) throws ObjectNotFoundException, OperationNotPermittedException {
        return null;
    }

    @Override
    public List<VORoleDefinition> getServiceRolesForService(VOService service)
            throws ObjectNotFoundException, OperationNotPermittedException {

        return null;
    }

    @Override
    public void updateAsyncSubscriptionProgress(String subscriptionId,
            String organizationId, List<VOLocalizedText> progress)
            throws ObjectNotFoundException, SubscriptionStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException {

    }

    @Override
    public void executeServiceOperation(VOSubscription subscription,
            VOTechnicalServiceOperation operation)
            throws ObjectNotFoundException, OperationNotPermittedException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, ValidationException {

    }

    @Override
    public void terminateSubscription(VOSubscription subscription, String reason)
            throws ObjectNotFoundException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, ConcurrentModificationException {

    }

    @Override
    public boolean hasCurrentUserSubscriptions() {

        return false;
    }

    @Override
    public VOSubscriptionDetails modifySubscriptionPaymentData(
            VOSubscription subscription, VOBillingContact billingContact,
            VOPaymentInfo paymentInfo) throws ObjectNotFoundException,
            ConcurrentModificationException, OperationNotPermittedException,
            PaymentInformationException, SubscriptionStateException,
            PaymentDataException, TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {
        return null;
    }

    @Override
    public List<VOSubscription> getSubscriptionsForOrganizationWithFilter(
            Set<SubscriptionStatus> requiredStatus) {

        return null;
    }

    @Override
    public void reportIssue(String subscriptionId, String subject,
            String issueText) throws ValidationException,
            ObjectNotFoundException, OperationNotPermittedException,
            MailOperationException, ValidationException {
    }

    @Override
    public List<VOSubscriptionIdAndOrganizations> getSubscriptionsForTerminate()
            throws OrganizationAuthoritiesException {
        return null;
    }

    @Override
    public List<Subscription> getSubscriptionsForManagers()
            throws OrganizationAuthoritiesException {
        return null;
    }

    @Override
    public List<Subscription> getSubscriptionsForManagers(Pagination pagination) throws OrganizationAuthoritiesException {
        return null;
    }

    @Override
    public List<Subscription> getSubscriptionsForCurrentUser(Pagination pagination) {
        return null;
    }

    @Override
    public UsageLicense getSubscriptionUsageLicense(PlatformUser user, Long subKey) {
        return null;
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
            throws ObjectNotFoundException, SubscriptionStateException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
    }

    @Override
    public void abortAsyncModifySubscription(String subscriptionId,
            String organizationId, List<VOLocalizedText> reason)
            throws ObjectNotFoundException, SubscriptionStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
    }

    @Override
    public void completeAsyncUpgradeSubscription(String subscriptionId,
            String organizationId, VOInstanceInfo instance)
            throws ObjectNotFoundException, SubscriptionStateException,
            TechnicalServiceNotAliveException,
            TechnicalServiceOperationException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
    }

    @Override
    public void abortAsyncUpgradeSubscription(String subscriptionId,
            String organizationId, List<VOLocalizedText> reason)
            throws ObjectNotFoundException, SubscriptionStateException,
            OrganizationAuthoritiesException, OperationNotPermittedException {
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

    }

    @Override
    public boolean validateSubscriptionIdForOrganization(String subscriptionId) {
        return false;
    }

    @Override
    public void revokeUserFromSubscriptionInt(Subscription subscription,
            List<PlatformUser> users) throws TechnicalServiceNotAliveException,
            TechnicalServiceOperationException {

    }

    @Override
    public void updateAsyncOperationProgress(String transactionId,
            OperationStatus status, List<VOLocalizedText> progress)
            throws OperationNotPermittedException, OperationStateException {
    }

    @Override
    public void updateAsyncSubscriptionStatus(String subscriptionId,
            String organizationId, VOInstanceInfo instanceInfo)
            throws ObjectNotFoundException {
    }

    /* (non-Javadoc)
     * @see org.oscm.internal.intf.SubscriptionService#getSubscriptionDetailsWithoutOwnerCheck(long)
     */
    @Override
    public VOSubscriptionDetails getSubscriptionDetailsWithoutOwnerCheck(
            long subscriptionKey) throws ObjectNotFoundException {
        return null;
    }

}
