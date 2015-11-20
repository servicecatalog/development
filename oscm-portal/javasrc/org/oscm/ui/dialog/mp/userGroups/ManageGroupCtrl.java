/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Jun 25, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.userGroups;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.enumtypes.UnitRoleType;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.usergroupmgmt.POService;
import org.oscm.internal.usergroupmgmt.POUserGroup;
import org.oscm.internal.usergroupmgmt.UserGroupService;
import org.oscm.internal.usermanagement.POUserInUnit;
import org.oscm.internal.vo.VOUserDetails;
import org.oscm.logging.Log4jLogger;
import org.oscm.logging.LoggerFactory;
import org.oscm.string.Strings;
import org.oscm.types.enumtypes.LogMessageIdentifier;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.SessionBean;
import org.oscm.ui.common.Constants;
import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.dialog.state.TableState;

/**
 * @author mao
 * 
 */
@ManagedBean
@ViewScoped
public class ManageGroupCtrl extends UserGroupBaseCtrl {

    protected static final String ASSIGN_USERS_MODAL_TITLE = "marketplace.group.assign.user.modal.title";
    private static final String UNIT_ROLE_NAME_TEMPLATE = "UnitRoleType.%s.enum";
    private static final Log4jLogger logger = LoggerFactory
            .getLogger(ManageGroupCtrl.class);

    @ManagedProperty(value = "#{manageGroupModel}")
    private ManageGroupModel manageGroupModel;
    
    @ManagedProperty(value = "#{usersLazyDataModel}")
    private UsersLazyDataModel usersLazyDataModel;

    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;

    UiDelegate ui = new UiDelegate();
    /**
     * EJB injected through setters.
     */
    private UserGroupService userGroupService;
    private IdentityService idService;

    @PostConstruct
    public void getInitialize() {
        TableState ts = getUi().findBean(TableState.BEAN_NAME);
        ts.resetActiveUnitPage();
        manageGroupModel.setSelectedGroupId(getSelectedGroupId());

        if (!Strings.isEmpty(manageGroupModel.getSelectedGroupId())) {
            initSelectedGroup();
        } else {
            redirectToGroupListPage();
        }
        for (UnitRoleType unitRoleType : UnitRoleType.values()) {
            SelectItem unitRole = new SelectItem(unitRoleType.name(),
                    formatRoleName(UNIT_ROLE_NAME_TEMPLATE,
                            unitRoleType.name()));
            manageGroupModel.getRoles().add(unitRole);
        }
    }
    
    private String formatRoleName(String template, String roleName) {
        return ui.getText(String.format(template, roleName));
    }

    public String cancel() {
        if (getIsLoggedAndUnitAdmin()) {
            return BaseBean.OUTCOME_SUCCESS_UNIT_ADMIN;
        }
        return BaseBean.OUTCOME_SUCCESS;
    }
    
    private void determineAssignmentsToUpdate() throws SaaSApplicationException {
        POUserGroup poUserGroup = userGroupService.getUserGroupDetailsWithUsers(manageGroupModel.getSelectedGroup().getKey());
        Map<String, String> userAndRole = new HashMap<String, String>();
        
        for (POUserInUnit poUserInUnit : poUserGroup.getUsersAssignedToUnit()) {
            userAndRole.put(poUserInUnit.getUserId(), poUserInUnit.getRoleInUnit());
        }
        
        for (POUserInUnit poUserInUnit : manageGroupModel.getCurrentResultUsers()) {
            if (poUserInUnit.isSelected() && !userAndRole.containsKey(poUserInUnit.getUserId())) {
                manageGroupModel.getUsersToAssign().add(poUserInUnit);
                continue;
            }
            if (poUserInUnit.isSelected() && userAndRole.containsKey(poUserInUnit.getUserId())) {
                if (!poUserInUnit.getRoleInUnit().equals(userAndRole.get(poUserInUnit.getUserId()))) {
                    manageGroupModel.getUsersToUpdate().add(poUserInUnit);
                }
                continue;
            }
            if (!poUserInUnit.isSelected() && userAndRole.containsKey(poUserInUnit.getUserId())) {
                manageGroupModel.getUsersToUnassign().add(poUserInUnit);
            }
        }
    }

    public String save() {
        if (Strings.isEmpty(manageGroupModel.getSelectedGroupId())) {
            return BaseBean.OUTCOME_ERROR;
        }
        try {
            determineAssignmentsToUpdate();
            setSelectedServices();
            POUserGroup group = getUserGroupService().updateGroup(
                    manageGroupModel.getSelectedGroup(),
                    BaseBean.getMarketplaceIdStatic(),
                    manageGroupModel.getUsersToAssign(),
                    manageGroupModel.getUsersToUnassign(),
                    manageGroupModel.getUsersToUpdate());
            manageGroupModel.setSelectedGroup(group);
            if (getUserGroupService().handleRemovingCurrentUserFromGroup()) {
                updateUserInSession();
                JSFUtils.addMessage(null, FacesMessage.SEVERITY_INFO, BaseBean.INFO_USER_SAVED_ITSELF,
                        new Object[]{});
                return BaseBean.OUTCOME_SUCCESS_UNIT_ADMIN;
            }
            JSFUtils.addMessage(null, FacesMessage.SEVERITY_INFO, BaseBean.INFO_GROUP_SAVED,
                    new Object[]{manageGroupModel.getSelectedGroup().getGroupName()});
            TableState ts = getUi().findBean(TableState.BEAN_NAME);
            ts.resetActiveEditPage();
        } catch (OperationNotPermittedException ex) {
            if (ex.getMessageKey().equals(BaseBean.ERROR_NOT_AVALIABLE_SERVICE)) {
                try {
                    manageGroupModel.setServiceRows(initServiceRows());
                } catch (ObjectNotFoundException e) {
                    getUi().handleException(ex);
                    return BaseBean.OUTCOME_ERROR;
                }
            }
            getUi().handleException(ex);
            return BaseBean.OUTCOME_ERROR;
        } catch (ObjectNotFoundException ex) {
            String outcome = BaseBean.OUTCOME_ERROR;
            if (ex.getMessageKey().equals(BaseBean.ERROR_USERGROUP_NOT_FOUND)) {
                ex.setMessageParams(new String[]{manageGroupModel.getSelectedGroup()
                        .getGroupName()});

                if(getIsLoggedAndUnitAdmin()) {
                    outcome = BaseBean.ERROR_USERGROUP_NOT_FOUND_EXCEPTION_UNIT_ADMIN;
                } else {
                    outcome = BaseBean.ERROR_USERGROUP_NOT_FOUND_EXCEPTION;
                }

            }
            getUi().handleException(ex);
            return outcome;
        } catch (SaaSApplicationException e) {
            getUi().handleException(e);
            return BaseBean.OUTCOME_ERROR;
        }
        if(getIsLoggedAndUnitAdmin()) {
            return BaseBean.OUTCOME_SUCCESS_UNIT_ADMIN;
        } else {
            return BaseBean.OUTCOME_SUCCESS;
        }

    }

    private void updateUserInSession() {
        VOUserDetails user = BaseBean
                .getUserFromSessionWithoutException(FacesContext
                        .getCurrentInstance());
        user.getUserRoles().remove(UserRoleType.UNIT_ADMINISTRATOR);
        HttpServletRequest request = JSFUtils.getRequest();
        request.getSession().setAttribute(Constants.SESS_ATTR_USER,
                user);
    }

    void initSelectedGroup() {
        try {
            POUserGroup userGroup = getUserGroupService()
                    .getUserGroupDetailsForList(
                            Long.valueOf(manageGroupModel.getSelectedGroupId())
                                    .longValue());
            manageGroupModel.setSelectedGroup(userGroup);
            manageGroupModel.setServiceRows(initServiceRows());
        } catch (ObjectNotFoundException e) {
            manageGroupModel.setSelectedGroup(null);
            manageGroupModel.setSelectedGroupId(null);
            JSFUtils.addMessage(null, FacesMessage.SEVERITY_ERROR, BaseBean.ERROR_INVALID_GROUP, null);
        }
    }

    public String selectGroup() {
        getUi().findSessionBean().setSelectedGroupId(manageGroupModel.getSelectedGroupId());
        if (manageGroupModel.getSelectedGroupId() == null) {
            JSFUtils.addMessage(null, FacesMessage.SEVERITY_ERROR, BaseBean.ERROR_INVALID_GROUP, null);
            return BaseBean.OUTCOME_REFRESH;
        }
        try {
            getUserGroupService().getUserGroupDetails(
                    Long.valueOf(manageGroupModel.getSelectedGroupId()).longValue());
        } catch (ObjectNotFoundException e) {
            JSFUtils.addMessage(null, FacesMessage.SEVERITY_ERROR, BaseBean.ERROR_INVALID_GROUP, null);
            return BaseBean.OUTCOME_REFRESH;
        }
        return BaseBean.OUTCOME_EDIT_GROUP;
    }

    void setSelectedServices() {
        manageGroupModel.getSelectedGroup().getInvisibleServices().clear();
        manageGroupModel.getSelectedGroup().getVisibleServices().clear();
        for (ServiceRow serviceRow : manageGroupModel.getServiceRows()) {
            serviceRow.getService().setProductId(
                    serviceRow.getService().getServiceName());
            if (!serviceRow.isSelected()) {
                manageGroupModel.getSelectedGroup().getInvisibleServices()
                        .add(serviceRow.getService());
            } else {
                manageGroupModel.getSelectedGroup().getVisibleServices()
                        .add(serviceRow.getService());
            }
        }
    }

    void redirectToGroupListPage() {
        HttpServletRequest request = JSFUtils.getRequest();
        HttpServletResponse response = JSFUtils.getResponse();

        String relativePath = null;
        if(getIsLoggedAndUnitAdmin()) {
            relativePath = BaseBean.MARKETPLACE_UNITS_PAGE;
        } else {
            relativePath = BaseBean.MARKETPLACE_USERS_PAGE;
        }
        
        try {
            JSFUtils.sendRedirect(response, request.getContextPath()
                    + relativePath);
        } catch (Exception e) {
            logger.logError(Log4jLogger.SYSTEM_LOG, e,
                    LogMessageIdentifier.ERROR);
        }
    }

    List<ServiceRow> initServiceRows() throws ObjectNotFoundException {
        List<ServiceRow> serviceRows = new ArrayList<>();
        List<Long> invisibleServiceKeys = getUserGroupService()
                .getInvisibleProductKeysForGroup(
                        getManageGroupModel().getSelectedGroup().getKey());
        for (POService service : initServiceList()) {
            ServiceRow serviceRow = new ServiceRow(service, true);
            if (invisibleServiceKeys.contains(Long.valueOf(service.getKey()))) {
                serviceRow.setSelected(false);
            }
            serviceRows.add(serviceRow);
        }
        sortServiceRows(serviceRows);
        return serviceRows;
    }

    public UserGroupService getUserGroupService() {
        if (userGroupService == null) {
            userGroupService = getUi().findService(UserGroupService.class);
        }
        return userGroupService;
    }

    @EJB
    public void setUserGroupService(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    public IdentityService getIdService() {
        if (idService == null) {
            idService = getUi().findService(IdentityService.class);
        }
        return idService;
    }

    @EJB
    public void setIdService(IdentityService idService) {
        this.idService = idService;
    }

    public String getSelectedGroupId() {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String groupId = req.getParameter("groupId");
        
        // directly after creating user group
        if (groupId == null) {
            groupId = (String) req.getAttribute(ManageGroupModel.ATTRIBUTE_GROUP_ID);
        }
        if (groupId == null || groupId.equals("")) {
            groupId = getSessionBean().getSelectedGroupId();
        } else {
            getSessionBean().setSelectedGroupId(groupId);
        }
        return groupId;
    }

    @Override
    public ManageGroupModel getManageGroupModel() {
        return manageGroupModel;
    }

    @Override
    public void setManageGroupModel(ManageGroupModel model) {
        this.manageGroupModel = model;
    }

    /**
     * Returns true if current user is logged in as Unit Administrator, but not
     * as a Organization Administrator.
     *
     * @return true if current user is logged in as Unit Administrator, but not
     * as a Organization Administrator
     */
    public boolean getIsLoggedAndUnitAdmin() {
        VOUserDetails user = BaseBean
                .getUserFromSessionWithoutException(FacesContext
                        .getCurrentInstance());
        return user != null
                && user.getUserRoles()
                        .contains(UserRoleType.UNIT_ADMINISTRATOR)
                && !user.getUserRoles().contains(
                        UserRoleType.ORGANIZATION_ADMIN);
    }

    public UsersLazyDataModel getUsersLazyDataModel() {
        return usersLazyDataModel;
    }

    public void setUsersLazyDataModel(UsersLazyDataModel usersLazyDataModel) {
        this.usersLazyDataModel = usersLazyDataModel;
    }

    public void selectUser(final String userId, final boolean selected) {
        manageGroupModel.getSelectedUsersIds().put(userId, new Boolean(selected));
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

}
