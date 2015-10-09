/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 2014-6-24                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.internal.usergroupmgmt;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.Product;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.UserGroup;
import org.oscm.interceptor.ExceptionMapper;
import org.oscm.interceptor.InvocationDateContainer;
import org.oscm.subscriptionservice.local.SubscriptionListServiceLocal;
import org.oscm.subscriptionservice.local.SubscriptionServiceLocal;
import org.oscm.usergroupservice.bean.UserGroupServiceLocalBean;
import org.oscm.internal.assembler.BasePOAssembler;
import org.oscm.internal.assembler.POUserGroupAssembler;
import org.oscm.internal.types.enumtypes.PerformanceHint;
import org.oscm.internal.types.exception.ConcurrentModificationException;
import org.oscm.internal.types.exception.DeletingUnitWithSubscriptionsNotPermittedException;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.ValidationException;
import org.oscm.internal.usermanagement.DataConverter;
import org.oscm.internal.usermanagement.POUserDetails;

/**
 * Manage user group Service
 * 
 * @author qiu
 * 
 */
@Stateless
@Remote(UserGroupService.class)
@Interceptors({ InvocationDateContainer.class, ExceptionMapper.class })
public class UserGroupServiceBean implements UserGroupService {

    @Inject
    UserGroupServiceLocalBean userGroupService;

    @EJB(beanInterface = DataService.class)
    DataService dm;

    @EJB(beanInterface = SubscriptionServiceLocal.class)
    SubscriptionServiceLocal ssl;

    @EJB(beanInterface = SubscriptionListServiceLocal.class)
    SubscriptionListServiceLocal slsl;

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN" })
    public POUserGroup createGroup(POUserGroup group, String marketplaceId)
            throws ValidationException, NonUniqueBusinessKeyException,
            OperationNotPermittedException, MailOperationException,
            ObjectNotFoundException, ConcurrentModificationException {
        List<Product> invisibleProducts = verifyProducts(group
                .getInvisibleServices());
        List<Product> visibleProducts = verifyProducts(group
                .getVisibleServices());
        return POUserGroupAssembler.toPOUserGroup(userGroupService
                .createUserGroup(POUserGroupAssembler.toUserGroup(group),
                        visibleProducts, invisibleProducts, marketplaceId));

    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public POUserGroup updateGroup(POUserGroup group, String marketplaceId,
            List<POUserDetails> usersToAssign,
            List<POUserDetails> usersToDeassign) throws ValidationException,
            OperationNotPermittedException, ConcurrentModificationException,
            ObjectNotFoundException, NonUniqueBusinessKeyException,
            MailOperationException {
        verifyGroupVersionAndKey(group);
        List<Product> invisibleProducts = verifyProducts(group
                .getInvisibleServices());
        List<Product> visibleProducts = verifyProducts(group
                .getVisibleServices());
        return POUserGroupAssembler.toPOUserGroup(userGroupService
                .updateUserGroup(POUserGroupAssembler.toUserGroup(group),
                        visibleProducts, invisibleProducts, marketplaceId,
                        convertToPlatformUsers(usersToAssign),
                        convertToPlatformUsers(usersToDeassign)));
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public boolean handleRemovingCurrentUserFromGroup() {
        if (!userGroupService.handleRemovingCurrentUserFromGroup()) {
            return false;
        }
        PlatformUser currentUser = dm.getCurrentUser();
        if (currentUser.hasSubscriptionOwnerRole()) {
            return true;
        }
        List<Subscription> subscriptions = slsl
                .getSubscriptionsForOwner(currentUser);
        for (Subscription subscription : subscriptions) {
            ssl.removeSubscriptionOwner(subscription);
        }
        return true;

    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN" })
    public boolean deleteGroup(POUserGroup group) throws ValidationException,
            OperationNotPermittedException, ObjectNotFoundException,
            ConcurrentModificationException, MailOperationException,
            DeletingUnitWithSubscriptionsNotPermittedException {
        UserGroup storedGroup = verifyGroupVersionAndKey(group);
        return userGroupService.deleteUserGroup(storedGroup);
    }

    private List<PlatformUser> convertToPlatformUsers(
            List<POUserDetails> poUsers) throws ValidationException {
        if (poUsers == null) {
            return null;
        }
        DataConverter dc = new DataConverter();
        List<PlatformUser> users = new ArrayList<>();
        for (POUserDetails poUser : poUsers) {
            users.add(dc.toPlatformUser(poUser));
        }
        return users;
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public List<POUserGroup> getGroupsForOrganization() {
        return POUserGroupAssembler.toPOUserGroups(userGroupService
                .getUserGroupsForOrganization());
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN" })
    public List<POUserGroup> getGroupsForOrganizationWithoutDefault() {
        return POUserGroupAssembler.toPOUserGroups(userGroupService
                .getUserGroupsForOrganizationWithoutDefault());
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN" })
    public List<POUserGroup> getGroupListForOrganizationWithoutDefault() {
        return POUserGroupAssembler.toPOUserGroups(
                userGroupService.getUserGroupsForOrganizationWithoutDefault(),
                PerformanceHint.ONLY_FIELDS_FOR_LISTINGS);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN" })
    public POUserGroup getUserGroupDetails(long groupKey)
            throws ObjectNotFoundException {
        return POUserGroupAssembler.toPOUserGroup(userGroupService
                .getUserGroupDetails(groupKey));
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public POUserGroup getUserGroupDetailsForList(long groupKey)
            throws ObjectNotFoundException {
        return POUserGroupAssembler.toPOUserGroup(
                userGroupService.getUserGroupDetails(groupKey),
                PerformanceHint.ONLY_FIELDS_FOR_LISTINGS);
    }

    @Override
    public List<POUserGroup> getUserGroupsForUserWithoutDefault(String userId) {
        return POUserGroupAssembler.toPOUserGroups(userGroupService
                .getUserGroupsForUserWithoutDefault(userId));
    }

    @Override
    public List<POUserGroup> getUserGroupListForUserWithoutDefault(String userId) {
        return POUserGroupAssembler.toPOUserGroups(
                userGroupService.getUserGroupsForUserWithoutDefault(userId),
                PerformanceHint.ONLY_FIELDS_FOR_LISTINGS);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN" })
    public List<POUserGroup> getUserGroupListForUserWithRoles(String userId) {
        return POUserGroupAssembler.toPOUserGroups(
                userGroupService.getUserGroupsForUserWithRoles(userId),
                PerformanceHint.ONLY_FIELDS_FOR_LISTINGS);
    }
    
    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN" })
    public List<POUserGroup> getUserGroupListForUserWithRolesWithoutDefault(String userId) {
        return POUserGroupAssembler.toPOUserGroups(
                userGroupService.getUserGroupsForUserWithRolesWithoutDefault(userId),
                PerformanceHint.ONLY_FIELDS_FOR_LISTINGS);
    }

    @Override
    public List<Long> getInvisibleProductKeysForUser(long userKey)
            throws ObjectNotFoundException {
        return userGroupService.getInvisibleProductKeysForUser(userKey);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public List<POUserGroup> getGroupListForOrganization() {
        return POUserGroupAssembler.toPOUserGroups(
                userGroupService.getUserGroupsForOrganization(),
                PerformanceHint.ONLY_FIELDS_FOR_LISTINGS);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public long getUserCountForGroup(long groupKey, boolean isDefaultGroup) {
        return userGroupService.getUserCountForGroup(groupKey, isDefaultGroup);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public List<String> getAssignedUserIdsForUserGroup(long groupKey) {
        return userGroupService.getAssignedUserIdsForUserGroup(groupKey);
    }

    @Override
    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public List<Long> getInvisibleProductKeysForGroup(long groupKey) {
        return userGroupService.getInvisibleProductKeysForGroup(groupKey);
    }

    private List<Product> verifyProducts(List<POService> services)
            throws ObjectNotFoundException, ConcurrentModificationException {
        List<Product> products = new ArrayList<>();
        for (POService service : services) {
            Product product = dm.getReference(Product.class, service.getKey());
            BasePOAssembler.verifyVersionAndKey(product, service);
            products.add(product);
        }
        return products;
    }

    private UserGroup verifyGroupVersionAndKey(POUserGroup group)
            throws ObjectNotFoundException, ConcurrentModificationException {
        UserGroup storedGroup = userGroupService.getUserGroupDetails(group
                .getKey());
        BasePOAssembler.verifyVersionAndKey(storedGroup, group);
        return storedGroup;
    }

    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public List<POUserGroup> getUserGroupsForUserWithRole(long userKey,
            long userRoleKey) {
        return POUserGroupAssembler.toPOUserGroups(userGroupService
                .getUserGroupsForUserWithRole(userKey, userRoleKey));
    }

    @RolesAllowed({ "ORGANIZATION_ADMIN", "UNIT_ADMINISTRATOR" })
    public List<POUserGroup> getUserGroupsForUserWithRoleWithoutDefault(long userKey,
            long userRoleKey) {
        return POUserGroupAssembler.toPOUserGroups(userGroupService
                .getUserGroupsForUserWithRoleWithoutDefault(userKey, userRoleKey));
    }

}
