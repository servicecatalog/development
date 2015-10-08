/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Jun 25, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.userGroups;

import static org.oscm.ui.beans.BaseBean.ERROR_GROUP_EXIST;
import static org.oscm.ui.beans.BaseBean.INFO_GROUP_CREATED;
import static org.oscm.ui.beans.BaseBean.INFO_NO_MORE_USERS;
import static org.oscm.ui.beans.BaseBean.OUTCOME_ERROR;
import static org.oscm.ui.beans.BaseBean.OUTCOME_SUCCESS;
import static org.oscm.ui.beans.BaseBean.getMarketplaceIdStatic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.oscm.ui.common.JSFUtils;
import org.oscm.ui.dialog.state.TableState;
import org.oscm.ui.model.User;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.types.exception.NonUniqueBusinessKeyException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.SaaSApplicationException;
import org.oscm.internal.usergroupmgmt.POService;
import org.oscm.internal.usergroupmgmt.POUserGroup;
import org.oscm.internal.usergroupmgmt.UserGroupService;
import org.oscm.internal.usermanagement.POUserDetails;
import org.oscm.internal.vo.VOUserDetails;

/**
 * @author mao
 * 
 */
@ManagedBean
@ViewScoped
public class AddGroupCtrl extends UserGroupBaseCtrl implements Serializable {
    private static final String NEW_GROUP = "New Group";
    private static final long serialVersionUID = 3961718802529537580L;

    /**
     * EJB injected through setters.
     */
    private UserGroupService userGroupService;
    private IdentityService idService;

    @ManagedProperty(value = "#{tableState}")
    private TableState tableState;

    @ManagedProperty(value = "#{manageGroupModel}")
    private ManageGroupModel manageGroupModel;

    @PostConstruct
    public void getInitialize() {
        manageGroupModel.setSelectedGroup(new POUserGroup());
        try {
            manageGroupModel.setServiceRows(initServiceRows());
        } catch (ObjectNotFoundException e) {
            getUi().handleException(e);
        }
        manageGroupModel.setModalTitle(getUi().getText(ManageGroupCtrl.ASSIGN_USERS_MODAL_TITLE, NEW_GROUP));
        initUnassignUsers();
    }

    public String create() {
        if (manageGroupModel.getSelectedGroup() == null) {
            return OUTCOME_ERROR;
        }
        try {
            setSelectedServices();
            setSelectedUsers();
            getUserGroupService().createGroup(manageGroupModel.getSelectedGroup(),
                    getMarketplaceIdStatic());
            JSFUtils.addMessage(null, FacesMessage.SEVERITY_INFO, INFO_GROUP_CREATED,
                    new Object[]{manageGroupModel.getSelectedGroup().getGroupName()});
            TableState ts = getTableState();
            ts.resetActiveAddPage();
        } catch (NonUniqueBusinessKeyException e) {
            JSFUtils.addMessage(null, FacesMessage.SEVERITY_ERROR, ERROR_GROUP_EXIST,
                    new Object[]{manageGroupModel.getSelectedGroup().getGroupName()});
            refreshSelectedGroup();
            return OUTCOME_ERROR;
        } catch (SaaSApplicationException e) {
            getUi().handleException(e);
            refreshSelectedGroup();
            return OUTCOME_ERROR;
        }

        return OUTCOME_SUCCESS;
    }

    public String setPopupTargetAssignUsers() {
        if (manageGroupModel.getUsersToDeassign().size() < 1) {
            getUi().handleError(null, INFO_NO_MORE_USERS);
        }
        return "";
    }

    List<ServiceRow> initServiceRows() throws ObjectNotFoundException {
        List<ServiceRow> serviceRows = new ArrayList<>();
        for (POService service : initServiceList()) {
            ServiceRow serviceRow = new ServiceRow(service, true);
            serviceRows.add(serviceRow);
        }
        sortServiceRows(serviceRows);
        return serviceRows;
    }

    void initUnassignUsers() {
        List<VOUserDetails> users = getIdService().getUsersForOrganization();
        for (VOUserDetails voUserDetails : users) {
            User user = new User(voUserDetails);
            manageGroupModel.getUsersToDeassign().add(user);
        }
    }

    private void refreshSelectedGroup() {
        manageGroupModel.getSelectedGroup().getUsers().clear();
        manageGroupModel.getSelectedGroup().getInvisibleServices().clear();
        manageGroupModel.getSelectedGroup().getVisibleServices().clear();
    }

    void setSelectedServices() {
        for (ServiceRow serviceRow : manageGroupModel.getServiceRows()) {
            if (!serviceRow.isSelected()) {
                manageGroupModel.getSelectedGroup().getInvisibleServices()
                        .add(serviceRow.getService());
            } else {
                manageGroupModel.getSelectedGroup().getVisibleServices()
                        .add(serviceRow.getService());
            }
        }
    }

    void setSelectedUsers() {
        for (User user : manageGroupModel.getUsersToAssign()) {
            POUserDetails poUser = new POUserDetails();
            poUser.setKey(user.getKey());
            poUser.setUserId(user.getUserId());
            poUser.setEmail(user.getEmail());
            poUser.setLocale(user.getLocale());
            manageGroupModel.getSelectedGroup().getUsers().add(poUser);
        }
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
        return req.getParameter("groupId");
    }

    @Override
    public ManageGroupModel getManageGroupModel() {
        return manageGroupModel;
    }

    @Override
    public void setManageGroupModel(ManageGroupModel model) {
        this.manageGroupModel = model;
    }

    public void setTableState(TableState tableState) {
        this.tableState = tableState;
    }

    public TableState getTableState() {
        if (tableState == null) {
            tableState = getUi().findBean(TableState.BEAN_NAME);
        }
        return tableState;
    }
}
