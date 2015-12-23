/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 21.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.subscriptionservice.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.Organization;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.RoleDefinition;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.UsageLicense;
import org.oscm.i18nservice.bean.LocalizerFacade;
import org.oscm.i18nservice.local.LocalizerServiceLocal;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.internal.tables.Pagination;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.usermanagement.POSubscription;
import org.oscm.pagination.PaginationInt;
import org.oscm.subscriptionservice.converter.SubscriptionListConverter;
import org.oscm.subscriptionservice.dao.SubscriptionDao;
import org.oscm.subscriptionservice.dao.UsageLicenseDao;
import org.oscm.subscriptionservice.dao.UserSubscriptionDao;
import org.oscm.subscriptionservice.local.SubscriptionListServiceLocal;
import org.oscm.subscriptionservice.local.SubscriptionWithRoles;

/**
 * @author weiser
 * 
 */
@Stateless
@Local(SubscriptionListServiceLocal.class)
@Interceptors({ ExceptionMapper.class })
public class SubscriptionListServiceBean
        implements SubscriptionListServiceLocal {

    @EJB
    DataService ds;

    @EJB(beanInterface = LocalizerServiceLocal.class)
    LocalizerServiceLocal lsl;

    SubscriptionListConverter slc = new SubscriptionListConverter();

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<SubscriptionWithRoles> getSubcsriptionsWithRoles(
            Organization owner, Set<SubscriptionStatus> states) {
        List<SubscriptionWithRoles> result = new ArrayList<SubscriptionWithRoles>();
        List<Object[]> list = getSubscriptionDao()
                .getSubscriptionsWithRoles(owner, states);
        result = slc.convert(list);
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<UsageLicense> getSubscriptionAssignments(PlatformUser user,
            Set<SubscriptionStatus> states) {
        return getUsageLicenseDao().getSubscriptionAssignments(user, states);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public boolean isUsableSubscriptionExistForTemplate(PlatformUser user,
            Set<SubscriptionStatus> states, Product template) {
        return getSubscriptionDao().isUsableSubscriptionsExistForTemplate(user,
                states, template);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<Subscription> getSubscriptionsForOrganization(
            Set<SubscriptionStatus> states) {

        List<Subscription> result = new ArrayList<Subscription>();

        List<Subscription> subscriptionsList = new ArrayList<Subscription>();

        PlatformUser currentUser = ds.getCurrentUser();
        if (currentUser.isOrganizationAdmin()) {
            subscriptionsList = currentUser.getOrganization()
                    .getSubscriptions();
        } else if (currentUser.hasRole(UserRoleType.SUBSCRIPTION_MANAGER)) {
            subscriptionsList = getSubscriptionsForOwner(currentUser);
        }

        for (Subscription sub : subscriptionsList) {
            if (states != null && !states.contains(sub.getStatus())) {
                continue;
            }
            result.add(sub);
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<Subscription> getAllSubscriptionsForOrganization() {
        PlatformUser currentUser = ds.getCurrentUser();
        return new ArrayList<Subscription>(
                currentUser.getOrganization().getSubscriptions());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<Subscription> getSubscriptionsForOwner(PlatformUser owner) {
        return getSubscriptionDao().getSubscriptionsForOwner(owner);
    }

    public SubscriptionDao getSubscriptionDao() {
        return new SubscriptionDao(ds);
    }

    public UserSubscriptionDao getUserSubscriptionDao() {
        return new UserSubscriptionDao(ds);
    }

    public UsageLicenseDao getUsageLicenseDao() {
        return new UsageLicenseDao(ds);
    }

    @Override
    public List<Subscription> getSubscriptionsForOrganization(
            Set<SubscriptionStatus> states, Pagination pagination)
                    throws OrganizationAuthoritiesException {
        List<Subscription> result = new ArrayList<>();
        PlatformUser currentUser = ds.getCurrentUser();
        if (currentUser.isOrganizationAdmin()) {
            result = getSubscriptionsForOrg(currentUser, pagination, states);
        } else {
            Set<UserRoleType> userRoleTypes = currentUser
                    .getAssignedRoleTypes();
            userRoleTypes.retainAll(getValidRoleTypes());
            result = getSubscriptionDao().getSubscriptionsForUserWithRoles(
                    userRoleTypes, currentUser, pagination, states);
        }
        return result;
    }

    private Set<UserRoleType> getValidRoleTypes() {
        Set<UserRoleType> validRoleTypes = new HashSet<>();
        validRoleTypes.add(UserRoleType.SUBSCRIPTION_MANAGER);
        validRoleTypes.add(UserRoleType.UNIT_ADMINISTRATOR);
        return validRoleTypes;
    }

    private List<Subscription> getSubscriptionsForOrg(PlatformUser user,
            Pagination pagination, Set<SubscriptionStatus> states) {
        return getSubscriptionDao().getSubscriptionsForOrg(user, pagination,
                states);

    }

    @Override
    public List<Subscription> getSubscriptionsForOwner(PlatformUser owner,
            Pagination pagination) throws OrganizationAuthoritiesException {
        return getSubscriptionDao().getSubscriptionsForOwner(owner, pagination);
    }

    @Override
    public List<POSubscription> getUserAssignableSubscriptions(
            org.oscm.pagination.Pagination pagination, PlatformUser user,
            Set<SubscriptionStatus> states) {

        List<Object[]> list = getUserSubscriptionDao()
                .getUserAssignableSubscriptions(pagination,
                        user.getOrganization(), user.getKey(), states);

        LocalizerFacade lf = new LocalizerFacade(lsl, user.getLocale());
        List<POSubscription> subscriptions = new ArrayList<>();

        for (Object[] sub : list) {

            String subId = (String) sub[0];
            List<RoleDefinition> roles = getSubscriptionDao()
                    .getSubscriptionRoles(user.getOrganization(), subId);

            POSubscription subscription = slc.toPOSubscription(sub, roles, lf);

            subscriptions.add(subscription);
        }

        return subscriptions;
    }

    @Override
    public Long getUserAssignableSubscriptionsNumber(
            PaginationInt pagination, PlatformUser user,
            Set<SubscriptionStatus> states) {

        Long numberOfSubs = getUserSubscriptionDao()
                .getCountUserAssignableSubscriptions(pagination,
                        user.getOrganization(), user.getKey(), states);
        return numberOfSubs;
    }

}
