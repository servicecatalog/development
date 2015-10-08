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
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.UsageLicense;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.subscriptionservice.converter.SubscriptionListConverter;
import org.oscm.subscriptionservice.dao.SubscriptionDao;
import org.oscm.subscriptionservice.dao.UsageLicenseDao;
import org.oscm.subscriptionservice.local.SubscriptionListServiceLocal;
import org.oscm.subscriptionservice.local.SubscriptionWithRoles;
import org.oscm.internal.tables.Pagination;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;

/**
 * @author weiser
 * 
 */
@Stateless
@Local(SubscriptionListServiceLocal.class)
@Interceptors({ ExceptionMapper.class })
public class SubscriptionListServiceBean implements
        SubscriptionListServiceLocal {

    @EJB
    DataService ds;

    SubscriptionListConverter slc = new SubscriptionListConverter();

    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public List<SubscriptionWithRoles> getSubcsriptionsWithRoles(
            Organization owner, Set<SubscriptionStatus> states) {
        List<SubscriptionWithRoles> result = new ArrayList<SubscriptionWithRoles>();
        List<Object[]> list = getSubscriptionDao().getSubscriptionsWithRoles(
                owner, states);
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
    public List<Subscription> getSubscriptionsForOwner(PlatformUser owner) {
        return getSubscriptionDao().getSubscriptionsForOwner(owner);
    }

    public SubscriptionDao getSubscriptionDao() {
        return new SubscriptionDao(ds);
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

}
