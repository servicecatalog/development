/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Jun 25, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.userGroups;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.oscm.ui.beans.BaseModel;
import org.oscm.ui.model.User;
import org.oscm.internal.usergroupmgmt.POUserGroup;

/**
 * @author mao
 * 
 */
@ViewScoped
@ManagedBean(name="manageGroupModel")
public class ManageGroupModel extends BaseModel {

    private static final long serialVersionUID = 7803451754585858737L;

    private boolean dirty;

    private String selectedGroupId = "";

    private POUserGroup selectedGroup;

    private List<ServiceRow> serviceRows = new ArrayList<ServiceRow>();

    private List<User> assignedUsers = new ArrayList<User>();

    private List<User> unAssignedUsers = new ArrayList<User>();

    private String modalTitle = "";

    private String deassignMessage = "";

    private List<User> usersToAssign = new ArrayList<User>();

    private boolean isUserGroupNotFoundException = false;

    private boolean allServicesSelected = false;

    private List<User> usersToDeassign = new ArrayList<User>();

    private String deassignUserId = "";

    private List<POUserGroup> groups = new ArrayList<>();

    public List<User> getUsersToDeassign() {
        return usersToDeassign;
    }

    public void setUsersToDeassign(List<User> usersToDeassign) {
        this.usersToDeassign = usersToDeassign;
    }

    public String getDeassignUserId() {
        return deassignUserId;
    }

    public void setDeassignUserId(String deassignUserId) {
        this.deassignUserId = deassignUserId;
    }

    public List<ServiceRow> getServiceRows() {
        return serviceRows;
    }

    public void setServiceRows(List<ServiceRow> serviceRows) {
        this.serviceRows = serviceRows;
    }

    public POUserGroup getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(POUserGroup selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public String getSelectedGroupId() {
        return selectedGroupId;
    }

    public void setSelectedGroupId(String selectedGroupId) {
        this.selectedGroupId = selectedGroupId;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public List<User> getUnAssignedUsers() {
        return unAssignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setUnAssignedUsers(List<User> unAssignedUsers) {
        this.unAssignedUsers = unAssignedUsers;
    }

    public String getModalTitle() {
        return modalTitle;
    }

    public void setModalTitle(String modalTitle) {
        this.modalTitle = modalTitle;
    }

    public String getDeassignMessage() {
        return deassignMessage;
    }

    public void setDeassignMessage(String deassignMessage) {
        this.deassignMessage = deassignMessage;
    }

    public List<User> getUsersToAssign() {
        return usersToAssign;
    }

    public void setUsersToAssign(List<User> usersToAssign) {
        this.usersToAssign = usersToAssign;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isUserGroupNotFoundException() {
        return isUserGroupNotFoundException;
    }

    public void setUserGroupNotFoundException(
            boolean isUserGroupNotFoundException) {
        this.isUserGroupNotFoundException = isUserGroupNotFoundException;
    }

    public boolean isAllServicesSelected() {
        return allServicesSelected;
    }

    public void setAllServicesSelected(boolean allServicesSelected) {
        this.allServicesSelected = allServicesSelected;
    }

    public int getServicesNumber() {
        return serviceRows.size();
    }

    public void setGroups(List<POUserGroup> groups) {
        this.groups = groups;
    }

    public List<POUserGroup> getGroups() {
        return groups;
    }
}
