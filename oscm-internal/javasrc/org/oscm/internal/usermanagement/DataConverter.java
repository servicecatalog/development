/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 03.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.internal.usermanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.oscm.domobjects.PlatformUser;
import org.oscm.domobjects.RoleAssignment;
import org.oscm.domobjects.RoleDefinition;
import org.oscm.domobjects.Subscription;
import org.oscm.domobjects.UsageLicense;
import org.oscm.domobjects.enums.LocalizedObjectTypes;
import org.oscm.i18nservice.bean.LocalizerFacade;
import org.oscm.subscriptionservice.local.SubscriptionWithRoles;
import org.oscm.validator.BLValidator;
import org.oscm.internal.types.enumtypes.SettingType;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.ValidationException;

/**
 * @author weiser
 * 
 */
public class DataConverter {

    public static final String FIELD_NAME_USER_ID = "userId";
    public static final String FIELD_NAME_EMAIL = "email";
    public static final String FIELD_NAME_FIRST_NAME = "firstName";
    public static final String FIELD_NAME_LAST_NAME = "lastName";
    public static final String FIELD_NAME_LOCALE = "locale";

    public POUser toPOUser(PlatformUser user) {
        if (user == null) {
            return null;
        }
        POUser poUser = new POUser();
        updatePOUser(user, poUser);
        return poUser;
    }

    public POUserDetails toPOUserDetails(PlatformUser user,
            Set<UserRoleType> availableRoles) {
        if (user == null) {
            return null;
        }
        POUserDetails poUser = new POUserDetails();
        updatePOUserDetails(user, availableRoles, poUser);
        return poUser;
    }

    public PlatformUser toPlatformUser(POUserDetails user)
            throws ValidationException {
        if (user == null) {
            return null;
        }
        PlatformUser pu = new PlatformUser();
        return updatePlatformUser(user, pu);
    }

    public PlatformUser updatePlatformUser(POUserDetails user, PlatformUser pu)
            throws ValidationException {
        validate(user);
        pu.setEmail(user.getEmail());
        pu.setFirstName(user.getFirstName());
        pu.setLastName(user.getLastName());
        pu.setUserId(user.getUserId());
        pu.setLocale(user.getLocale());
        pu.setSalutation(user.getSalutation());
        // do not set account status
        return pu;
    }

    public void validate(POUserDetails user) throws ValidationException {
        BLValidator.isEmail(FIELD_NAME_EMAIL, user.getEmail(), true);
        BLValidator.isName(FIELD_NAME_FIRST_NAME, user.getFirstName(), false);
        BLValidator.isName(FIELD_NAME_LAST_NAME, user.getLastName(), false);
        BLValidator.isLocale(FIELD_NAME_LOCALE, user.getLocale(), true);
        BLValidator.isUserId(FIELD_NAME_USER_ID, user.getUserId(), true);
        // do not validate account status
    }

    public POUserAndSubscriptions toPOUserAndSubscriptionsNew(
            List<SubscriptionWithRoles> availableSubs,
            Set<UserRoleType> availableRoles, LocalizerFacade lf) {
        POUserAndSubscriptions user = new POUserAndSubscriptions();
        user.setAvailableRoles(availableRoles);
        user.setSubscriptions(getAvailableSubscriptions(availableSubs, lf));
        return user;
    }

    public POUserAndSubscriptions toPOUserAndSubscriptionsExisting(
            PlatformUser pu, List<SubscriptionWithRoles> availableSubs,
            Set<UserRoleType> availableRoles, List<UsageLicense> assignedSubs,
            LocalizerFacade lf, Set<SettingType> mappedLdapAttributes) {
        POUserAndSubscriptions user = new POUserAndSubscriptions();
        updatePOUserDetails(pu, availableRoles, user);
        List<POSubscription> subs = getAvailableSubscriptions(availableSubs, lf);
        // set assigned subscriptions
        user.setSubscriptions(matchAssigned(assignedSubs, subs, lf));
        user.setMappedAttributes(mappedLdapAttributes);
        return user;
    }

    void updatePOUser(PlatformUser user, POUser poUser) {
        poUser.setEmail(user.getEmail());
        poUser.setFirstName(user.getFirstName());
        poUser.setKey(user.getKey());
        poUser.setLastName(user.getLastName());
        poUser.setUserId(user.getUserId());
        poUser.setVersion(user.getVersion());
    }

    void updatePOUserDetails(PlatformUser user,
            Set<UserRoleType> availableRoles, POUserDetails poUser) {
        updatePOUser(user, poUser);
        poUser.setLocale(user.getLocale());
        poUser.setSalutation(user.getSalutation());
        poUser.setStatus(user.getStatus());
        if (availableRoles != null) {
            poUser.setAvailableRoles(availableRoles);
        }
        poUser.setAssignedRoles(user.getAssignedRoleTypes());
    }

    List<POSubscription> matchAssigned(List<UsageLicense> assignedSubs,
            List<POSubscription> subs, LocalizerFacade lf) {
        Map<String, UsageLicense> subIdToLic = new HashMap<String, UsageLicense>();
        for (UsageLicense lic : assignedSubs) {
            subIdToLic.put(lic.getSubscription().getSubscriptionId(), lic);
        }
        for (POSubscription sub : subs) {
            if (subIdToLic.containsKey(sub.getId())) {
                sub.setAssigned(true);
                UsageLicense lic = subIdToLic.get(sub.getId());
                sub.setUsageLicense(createPOUsageLicense(lic));
                sub.getUsageLicense().setPoServieRole(
                        toPOServiceRole(lf, lic.getRoleDefinition()));
            }
        }
        return subs;
    }

    POUsagelicense createPOUsageLicense(UsageLicense lic) {
        POUsagelicense poUsagelicense = new POUsagelicense();
        poUsagelicense.setKey(lic.getKey());
        poUsagelicense.setVersion(lic.getVersion());
        return poUsagelicense;
    }

    List<POSubscription> getAvailableSubscriptions(
            List<SubscriptionWithRoles> list, LocalizerFacade lf) {
        List<POSubscription> subs = new ArrayList<POSubscription>();
        if (list == null) {
            return subs;
        }
        for (SubscriptionWithRoles swr : list) {
            POSubscription sub = new POSubscription();
            Subscription s = swr.getSubscription();
            sub.setId(s.getSubscriptionId());
            sub.setKey(s.getKey());
            sub.setVersion(s.getVersion());

            List<RoleDefinition> roles = swr.getRoles();
            List<POServiceRole> rolesToSet = new ArrayList<POServiceRole>();
            for (RoleDefinition role : roles) {
                rolesToSet.add(toPOServiceRole(lf, role));
            }
            sub.setRoles(rolesToSet);
            subs.add(sub);
        }
        return subs;
    }

    POServiceRole toPOServiceRole(LocalizerFacade lf, RoleDefinition role) {
        if (role == null) {
            return null;
        }
        POServiceRole r = new POServiceRole();
        r.setId(role.getRoleId());
        r.setKey(role.getKey());
        r.setName(lf.getText(role.getKey(), LocalizedObjectTypes.ROLE_DEF_NAME));
        r.setVersion(role.getVersion());
        return r;
    }

    public boolean isUserInformationUpdated(POUserDetails user,
            PlatformUser userInDB) {
        if (userInDB.getVersion() > user.getVersion()) {
            return true;
        }
        return false;
    }

    public boolean isUserRoleUpdated(Set<UserRoleType> roles, PlatformUser pUser) {
        Iterator<RoleAssignment> roleIterator = pUser.getAssignedRoles()
                .iterator();
        int roleSizeInDB = 0;
        while (roleIterator.hasNext()) {
            RoleAssignment roleAssignment = roleIterator.next();
            if (!roles.contains(roleAssignment.getRole().getRoleName())) {
                return true;
            }
            roleSizeInDB++;
        }
        if (roles.size() != roleSizeInDB) {
            return true;
        }
        return false;
    }

}
