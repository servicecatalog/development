/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016 
 *******************************************************************************/

package org.oscm.test.stubs;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oscm.internal.intf.OperatorService;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.enumtypes.UserAccountStatus;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.DistinguishedNameException;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.types.exception.OrganizationAuthorityException;
import org.oscm.internal.types.exception.PSPIdentifierForSellerException;
import org.oscm.internal.types.exception.PaymentDataException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.vo.LdapProperties;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOImageResource;
import org.oscm.internal.vo.VOOperatorOrganization;
import org.oscm.internal.vo.VOOrganization;
import org.oscm.internal.vo.VOPSP;
import org.oscm.internal.vo.VOPSPAccount;
import org.oscm.internal.vo.VOPaymentType;
import org.oscm.internal.vo.VOTimerInfo;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;

public class OperatorServiceStub implements OperatorService {

    @Override
    public void addAvailablePaymentTypes(VOOrganization supplier,
            Set<String> types) throws ObjectNotFoundException,
            OrganizationAuthorityException, PSPIdentifierForSellerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addOrganizationToRole(String organizationId,
            OrganizationRoleType role) throws OrganizationAuthorityException,
            ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOTimerInfo> getTimerExpirationInformation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOOrganization registerOrganization(VOOrganization organization,
            VOImageResource voImageResource, VOUserDetails orgInitialUser,
            LdapProperties organizationProperties, String marketplaceID,
            OrganizationRoleType... rolesToGrant)
            throws NonUniqueBusinessKeyException, ObjectNotFoundException,
            ValidationException, OrganizationAuthorityException,
            MailOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOTimerInfo> reInitTimers() throws ValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retryFailedPaymentProcesses() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserAccountStatus(VOUser user, UserAccountStatus newStatus)
            throws ObjectNotFoundException, ValidationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startBillingRun() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDistinguishedName(String organizationId,
            String distinguishedName) throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getOrganizationBillingData(long from, long to,
            String organizationId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resetPasswordForUser(String userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addCurrency(String currencyISOCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOConfigurationSetting> getConfigurationSettings()
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveConfigurationSetting(VOConfigurationSetting setting)
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOOperatorOrganization getOrganization(String organizationId)
            throws ObjectNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOOrganization> getOrganizations(String organizationIdPattern,
            List<OrganizationRoleType> organizationRoleTypes)
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOOrganization> getOrganizationsWithLimit(String organizationIdPattern, List<OrganizationRoleType> organizationRoleTypes, Integer queryLimit) throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOUserDetails> getUsers()
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOUserDetails> getSubscriptionOwnersForAssignment(Long organizationKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveConfigurationSettings(List<VOConfigurationSetting> settings)
            throws OrganizationAuthoritiesException {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOOperatorOrganization updateOrganization(
            VOOperatorOrganization organization, VOImageResource voImageResource)
            throws OrganizationAuthoritiesException, ObjectNotFoundException,
            ValidationException, ConcurrentModificationException,
            DistinguishedNameException, OrganizationAuthorityException,
            PSPIdentifierForSellerException, PaymentDataException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startPaymentProcessing() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getUserOperationLog(List<String> operationIds, long fromDate,
            long toDate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOPSP> getPSPs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOPSP savePSP(VOPSP psp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOPSPAccount> getPSPAccounts(VOOrganization organization) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOPSPAccount savePSPAccount(VOOrganization organization,
            VOPSPAccount account) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOPaymentType> getPaymentTypes(VOPSP psp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VOPaymentType savePaymentType(VOPSP psp, VOPaymentType paymentType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getSupplierRevenueList(long month) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getAvailableAuditLogOperations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String> getAvailableAuditLogOperationGroups() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VOUserDetails> getUnassignedUsersByOrg(Long subscriptionKey,
            Long organizationKey) {
        throw new UnsupportedOperationException();
    }

}
