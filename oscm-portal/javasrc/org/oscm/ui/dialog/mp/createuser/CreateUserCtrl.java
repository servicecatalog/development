/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 25.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.createuser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.oscm.string.Strings;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.dialog.classic.manageusers.UserRole;
import org.oscm.ui.dialog.state.TableState;
import org.oscm.ui.profile.FieldData;
import org.oscm.internal.components.response.Response;
import org.oscm.internal.types.constants.HiddenUIConstants;
import org.oscm.internal.types.enumtypes.Salutation;
import org.oscm.internal.types.enumtypes.UnitRoleType;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.usergroupmgmt.POUserGroup;
import org.oscm.internal.usergroupmgmt.UserGroupService;
import org.oscm.internal.usermanagement.POServiceRole;
import org.oscm.internal.usermanagement.POSubscription;
import org.oscm.internal.usermanagement.POUserAndSubscriptions;
import org.oscm.internal.usermanagement.POUserDetails;
import org.oscm.internal.usermanagement.UserService;

/**
 * @author weiser
 * 
 */
@ManagedBean
@ViewScoped
public class CreateUserCtrl {

    private static final String ROLE_NAME_TEMPLATE = "UserRoleType.%s.enum";
    private static final String UNIT_ROLE_NAME_TEMPLATE = "UnitRoleType.%s.enum";

    UiDelegate ui = new UiDelegate();

    @ManagedProperty(value = "#{createUserModel}")
    private CreateUserModel model;
    @ManagedProperty(value = "#{appBean}")
    private ApplicationBean applicationBean;
    @ManagedProperty(value = "#{tableState}")
    private TableState tableState;

    /**
     * EJB injected through setters.
     */
    private UserService userService;
    private UserGroupService userGroupService;

    transient Boolean rolesColumnVisible;

    @PostConstruct
    public void init() {
        POUserAndSubscriptions data = getUserService().getNewUserData();
        CreateUserModel m = getModel();
        m.setEmail(new FieldData<String>(null, false, true));
        m.setFirstName(new FieldData<String>(null, false));
        m.setLastName(new FieldData<String>(null, false));
        m.setLocale(new FieldData<>(data.getLocale(), false, true));
        m.setUserId(new FieldData<String>(null, false, true));
        m.setSalutation(new FieldData<String>(null, false));
        m.setRoles(initUserRoles(data));
        m.setUserGroups(initUserGroups());
        m.setSubscriptions(initSubscription(data));

    }

    List<UserGroup> initUserGroups() {
        List<POUserGroup> poUserGroups = getUserGroupService()
                .getGroupsForOrganization();
        List<UserGroup> userGroups = new ArrayList<>();
        for (POUserGroup poUserGroup : poUserGroups) {
            UserGroup userGroup = new UserGroup();
            userGroup.setPoUserGroup(poUserGroup);
            userGroup.setName(poUserGroup.getGroupName());
            userGroup.setDescription(poUserGroup.getGroupDescription());
            userGroup.setReferenceId(poUserGroup.getGroupReferenceId());
            List<SelectItem> roles = new ArrayList<SelectItem>();
            for (UnitRoleType unitRoleType : UnitRoleType.values()) {
                SelectItem unitRole = new SelectItem(unitRoleType.name(),
                        formatRoleName(UNIT_ROLE_NAME_TEMPLATE,
                                unitRoleType.name()));
                roles.add(unitRole);
            }
            userGroup.setRoles(roles);
            if (userGroup.isDefault()) {
                userGroup.setSelectedRole(UnitRoleType.USER.name());
            }
            userGroups.add(userGroup);
        }
        return userGroups;
    }

    private String formatRoleName(String template, String roleName) {
        return ui.getText(String.format(template, roleName));
    }

    List<UserRole> initUserRoles(POUserDetails u) {

        List<UserRole> result = new ArrayList<>();
        Set<UserRoleType> roles = u.getAvailableRoles();
        for (UserRoleType r : roles) {
            String name = formatRoleName(ROLE_NAME_TEMPLATE, r.name());
            UserRole role = new UserRole(r, name, u.getAssignedRoles()
                    .contains(r));
            result.add(role);
        }

        return result;
    }

    List<Subscription> initSubscription(POUserAndSubscriptions data) {

        List<Subscription> result = new ArrayList<>();
        List<POSubscription> list = data.getSubscriptions();
        for (POSubscription s : list) {
            Subscription sub = new Subscription();
            sub.setId(s.getId());
            List<POServiceRole> roles = s.getRoles();
            sub.setRolesRendered(!roles.isEmpty());
            List<SelectItem> items = new ArrayList<>();
            for (POServiceRole r : roles) {
                SelectItem si = new SelectItem(String.format("%s:%s",
                        Long.valueOf(r.getKey()), r.getId()), r.getName());
                items.add(si);
            }
            sub.setRoles(items);
            result.add(sub);
        }

        return result;
    }

    public String create() throws SaaSApplicationException {

        CreateUserModel m = getModel();
        POUserAndSubscriptions user = toPOUserAndSubscriptions(m);
        String outcome = BaseBean.OUTCOME_SUCCESS;
        try {
            Response response = getUserService().createNewUser(user,
                    ui.getMarketplaceId());
            if (response.getReturnCodes().size() > 0) {
                outcome = BaseBean.OUTCOME_PENDING;
            } else {
                // reset user table paging if user was created
                TableState ts = getTableState();
                ts.resetActivePages();
            }
            ui.handle(response, BaseBean.INFO_USER_CREATED, user.getUserId());
        } catch (ObjectNotFoundException ex) {
            ui.handleException(ex);
            m.setUserGroups(initUserGroups());
            outcome = BaseBean.OUTCOME_ERROR;
        } catch (MailOperationException e) {
            if (getApplicationBean().isInternalAuthMode()) {
                ui.handleError(null, BaseBean.ERROR_USER_CREATE_MAIL);
            } else {
                ui.handleError(null,
                        BaseBean.ERROR_USER_CREATE_MAIL_NOT_INTERNAL);
            }
            outcome = BaseBean.OUTCOME_ERROR;
        }

        return outcome;
    }

    POUserAndSubscriptions toPOUserAndSubscriptions(CreateUserModel m) {

        POUserAndSubscriptions uas = new POUserAndSubscriptions();
        uas.setAssignedRoles(getSelectedUserRoles(m.getRoles()));
        uas.setEmail(m.getEmail().getValue());
        uas.setFirstName(m.getFirstName().getValue());
        uas.setLastName(m.getLastName().getValue());
        uas.setLocale(m.getLocale().getValue());
        String sal = m.getSalutation().getValue();
        if (!Strings.isEmpty(sal)) {
            uas.setSalutation(Salutation.valueOf(sal));
        }
        uas.setUserId(m.getUserId().getValue());

        uas.setSubscriptions(getSubscriptionAssignment(m.getSubscriptions()));

        uas.setGroupsToBeAssigned(getSelectedUserGroups(m.getUserGroups()));
        uas.setAllGroups(getAllUserGroups(m.getUserGroups()));
        return uas;
    }

    List<POSubscription> getSubscriptionAssignment(List<Subscription> subs) {

        List<POSubscription> result = new ArrayList<>();
        for (Subscription sub : subs) {
            if (!sub.isSelected()) {
                continue;
            }
            POSubscription s = new POSubscription();
            s.setId(sub.getId());

            String role = sub.getSelectedRole();
            if (!Strings.isEmpty(role)) {
                String[] selectedRole = role.split(":");
                long key = Long.parseLong(selectedRole[0]);
                POServiceRole r = new POServiceRole();
                r.setId(selectedRole[1]);
                r.setKey(key);
                s.getUsageLicense().setPoServieRole(r);
            }
            result.add(s);
        }

        return result;
    }

    Set<UserRoleType> getSelectedUserRoles(List<UserRole> roles) {

        Set<UserRoleType> result = new HashSet<>();
        for (UserRole r : roles) {
            if (r.isSelected()) {
                result.add(r.getType());
            }
        }

        return result;
    }

    List<POUserGroup> getSelectedUserGroups(List<UserGroup> groups) {
        List<POUserGroup> poUserGroups = new ArrayList<>();
        for (UserGroup g : groups) {
            if (g.isSelected()) {
                poUserGroups.add(g.getPoUserGroup());
            }
        }
        return poUserGroups;
    }

    List<POUserGroup> getAllUserGroups(List<UserGroup> groups) {
        List<POUserGroup> poUserGroups = new ArrayList<>();
        for (UserGroup group : groups) {
            poUserGroups.add(group.getPoUserGroup());
        }
        return poUserGroups;
    }

    public boolean isSubTableRendered() {
        return (!getModel().getSubscriptions().isEmpty())
                && (!getApplicationBean().isUIElementHidden(
                HiddenUIConstants.PANEL_USER_LIST_SUBSCRIPTIONS));
    }

    public boolean isRoleColumnRendered() {

        if (rolesColumnVisible == null) {
            boolean rolesDefined = false;
            List<Subscription> subs = getModel().getSubscriptions();
            for (int i = 0; i < subs.size() && !rolesDefined; i++) {
                Subscription subscription = subs.get(i);
                rolesDefined = subscription.isRolesRendered();
            }
            rolesColumnVisible = Boolean.valueOf(rolesDefined);
        }

        return rolesColumnVisible.booleanValue();
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = ui.findService(UserService.class);
        }
        return userService;
    }

    @EJB
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public UserGroupService getUserGroupService() {
        if (userGroupService == null) {
            userGroupService = ui.findService(UserGroupService.class);
        }
        return userGroupService;
    }

    @EJB
    public void setUserGroupService(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    public TableState getTableState() {
        return tableState;
    }

    public void setTableState(TableState tableState) {
        this.tableState = tableState;
    }

    public void setModel(CreateUserModel model) {
        this.model = model;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public CreateUserModel getModel() {
        return model;
    }

    public ApplicationBean getApplicationBean() {
        return applicationBean;
    }
}
