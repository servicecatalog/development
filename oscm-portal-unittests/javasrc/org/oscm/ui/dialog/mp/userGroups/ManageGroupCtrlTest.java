/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Jun 25, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.userGroups;

import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.SessionBean;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.dialog.state.TableState;
import org.oscm.ui.model.User;
import org.oscm.ui.stubs.FacesContextStub;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.intf.SearchServiceInternal;
import org.oscm.internal.types.enumtypes.PerformanceHint;
import org.oscm.internal.types.exception.*;
import org.oscm.internal.usergroupmgmt.POService;
import org.oscm.internal.usergroupmgmt.POUserGroup;
import org.oscm.internal.usergroupmgmt.UserGroupService;
import org.oscm.internal.usermanagement.POUserDetails;
import org.oscm.internal.vo.ListCriteria;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceListResult;
import org.oscm.internal.vo.VOUserDetails;
import org.junit.Before;
import org.junit.Test;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author mao
 * 
 */
public class ManageGroupCtrlTest {

    static final String APPLICATION_BEAN = "appBean";

    public static final String OUTCOME_ERROR = "error";
    private ManageGroupModel model;
    private ManageGroupCtrl ctrl;
    private UserGroupService userGroupService;
    private SessionBean sessionBean;
    private IdentityService identityService;
    private ExternalContext exContext;
    private TableState tableStatus;

    private POUserGroup selectedGroup;
    private final List<String> list = new ArrayList<String>();
    private SearchServiceInternal searchServiceInternal;

    @Before
    public void setup() throws Exception {
        UIViewRoot viewRoot = mock(UIViewRoot.class);
        tableStatus = mock(TableState.class);
        given(viewRoot.getLocale()).willReturn(Locale.ENGLISH);
        new FacesContextStub(Locale.ENGLISH).setViewRoot(viewRoot);

        List<VOService> servicesList = getServicesList();
        VOServiceListResult voServiceListResult = new VOServiceListResult();
        voServiceListResult.setServices(servicesList);
        userGroupService = mock(UserGroupService.class);
        sessionBean = mock(SessionBean.class);
        exContext = mock(ExternalContext.class);
        identityService = mock(IdentityService.class);
        searchServiceInternal = mock(SearchServiceInternal.class);
        ctrl = new ManageGroupCtrl() {
            @Override
            protected void redirectToGroupListPage() {
                return;
            }

            @Override
            public String getSelectedGroupId() {
                return "1000";
            }
        };
        ctrl.setUserGroupService(userGroupService);
        ctrl.setSearchServiceInternal(searchServiceInternal);
        ctrl.setIdService(identityService);
        model = new ManageGroupModel();
        ctrl.setManageGroupModel(model);
        ctrl.setUi(spy(new UiDelegate() {
            @Override
            public void handleException(SaaSApplicationException ex) {

            }

            @Override
            public void handleError(String clientId, String messageKey,
                                    Object... params) {

            }

            @Override
            public String getText(String key, Object... params) {
                return "";
            }

            @Override
            public SessionBean findSessionBean() {
                return sessionBean;
            }

            @Override
            public ExternalContext getExternalContext() {
                return exContext;
            }
        }));
        doReturn(new ArrayList<POUserGroup>()).when(userGroupService)
                .getGroupsForOrganization();
        doReturn(voServiceListResult).when(searchServiceInternal)
                .getServicesByCriteria(anyString(), anyString(),
                        any(ListCriteria.class), any(PerformanceHint.class));
        doReturn(tableStatus).when(ctrl.getUi()).findBean(eq(TableState.BEAN_NAME));

        initModelData();
    }

    @Test
    public void save_nullSelectedUserGroup() throws Exception {
        // given
        model.setSelectedGroupId(null);

        // when
        String result = ctrl.save();

        // then
        assertEquals(BaseBean.OUTCOME_ERROR, result);
    }

    @Test
    public void save() throws Exception {
        // given
        POUserGroup userGroup = new POUserGroup();
        doReturn(userGroup).when(userGroupService).updateGroup(
                eq(selectedGroup), anyString(),
                anyListOf(POUserDetails.class), anyListOf(POUserDetails.class));
        userGroup.setKey(1L);
        doReturn(selectedGroup).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();

        // when
        String result = ctrl.save();

        // then
        assertEquals(BaseBean.OUTCOME_SUCCESS, result);
        assertEquals(1, model.getSelectedGroup().getKey());
        verify(tableStatus, times(1)).resetActiveEditPage();
    }

    @Test
    public void getChangedUsers() {
        // given
        List<User> existingUsers = new ArrayList<User>();
        VOUserDetails voUser1 = new VOUserDetails();
        voUser1.setKey(1000L);
        User user1 = new User(voUser1);
        user1.setUserId("u1");
        existingUsers.add(user1);

        List<User> newUsers = new ArrayList<User>();
        VOUserDetails voUser2 = new VOUserDetails();
        voUser2.setKey(2000L);
        User user2 = new User(voUser2);
        user2.setUserId("u2");
        newUsers.add(user1);
        newUsers.add(user2);

        // when
        List<POUserDetails> result = ctrl.getChangedUsers(existingUsers,
                newUsers);

        // then
        assertEquals(1, result.size());
        assertEquals(user2.getKey(), result.get(0).getKey());
        assertEquals(user2.getUserId(), result.get(0).getUserId());
    }

    @Test
    public void save_ObjectNotFound() throws Exception {
        // given
        ObjectNotFoundException e = new ObjectNotFoundException();
        doThrow(e).when(userGroupService).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        doReturn(model.getSelectedGroup()).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();
        // when
        String result = ctrl.save();

        // then
        assertEquals(BaseBean.OUTCOME_ERROR, result);
        verify(ctrl.getUi(), times(1)).handleException(e);
    }

    @Test
    public void save_NotAvailableService() throws Exception {
        // given
        OperationNotPermittedException e = new OperationNotPermittedException();
        e.setMessageKey(BaseBean.ERROR_NOT_AVALIABLE_SERVICE);
        doThrow(e).when(userGroupService).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        doReturn(model.getSelectedGroup()).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();
        // when
        String result = ctrl.save();

        // then
        assertEquals(BaseBean.OUTCOME_ERROR, result);
        verify(ctrl.getUi(), times(1)).handleException(e);
    }

    @Test
    public void save_UserGroupNotFound() throws Exception {
        // given
        ObjectNotFoundException e = new ObjectNotFoundException();
        e.setMessageKey(BaseBean.ERROR_USERGROUP_NOT_FOUND);
        doThrow(e).when(userGroupService).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        doReturn(model.getSelectedGroup()).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();

        // when
        String result = ctrl.save();

        // then
        assertEquals(BaseBean.ERROR_USERGROUP_NOT_FOUND_EXCEPTION, result);
        verify(ctrl.getUi(), times(1)).handleException(e);
    }

    @Test
    public void initAssignedUnassignedUsers() {
        // given
        prepareAssignedUnassignedUsers();
        doReturn(Arrays.asList("assignedUser")).when(userGroupService)
                .getAssignedUserIdsForUserGroup(
                        model.getSelectedGroup().getKey());

        // when
        ctrl.initAssignedUnassignedUsers(model.getSelectedGroup());

        // then
        assertEquals("assignedUser", model.getAssignedUsers().get(0)
                .getUserId());
        assertEquals(1, model.getAssignedUsers().size());
        assertEquals("unAssignedUser", model.getUnAssignedUsers().get(0)
                .getUserId());
        assertEquals(1, model.getUnAssignedUsers().size());
    }

    @Test
    public void setPopupTargetAssignUsers_noAssignedUser() {
        // when
        String result = ctrl.setPopupTargetAssignUsers();

        // then
        assertEquals("", result);
        assertEquals("", model.getModalTitle());
    }

    @Test
    public void setPopupTargetAssignUsers() {
        // given
        User user = new User(new VOUserDetails());
        model.setUsersToDeassign(Arrays.asList(user));

        // when
        String result = ctrl.setPopupTargetAssignUsers();

        // then
        assertEquals("", result);
    }

    @Test
    public void assignUsers() throws Exception {
        // given
        prepareUsersToDeassign(true);
        prepareUsersToAssign();

        List<User> usersToDeassign = new ArrayList<>();
        VOUserDetails voUser = new VOUserDetails();
        User user = new User(voUser);
        user.setSelected(true);
        user.setUserId("test1");
        user.setEmail("sh@fujitsu.com");
        user.setLocale("en");
        usersToDeassign.add(user);
        model.setUsersToDeassign(usersToDeassign);

        // when
        ctrl.assignUsers();

        // then
        assertEquals(2, model.getUsersToAssign().size());
        assertEquals(0, model.getUsersToDeassign().size());
    }

    @Test
    public void assignUsersCancel_isAssignUserException() {
        // given
        model.setUserGroupNotFoundException(true);

        // when
        String result = ctrl.assignUsersCancel();

        // then
        assertEquals(BaseBean.OUTCOME_ERROR, result);
    }

    @Test
    public void assignUsersCancel_unSelectusersToDeassign() throws Exception {
        // given
        model.setUserGroupNotFoundException(false);
        prepareUsersToDeassign(false);

        // when
        String result = ctrl.assignUsersCancel();

        // then
        assertEquals("", result);
        assertEquals(Boolean.FALSE,
                Boolean.valueOf(model.getUsersToDeassign().get(0).isSelected()));
    }

    @Test
    public void assignUsersCancel_notIsAssignUserException() {
        // given
        model.setUserGroupNotFoundException(false);

        // when
        String result = ctrl.assignUsersCancel();

        // then
        assertEquals("", result);
    }

    @Test
    public void deassignUser() throws Exception {
        // given
        prepareUsersToDeassign(true);
        prepareUsersToAssign();

        model.setDeassignUserId("test2");

        // when
        ctrl.deassignUser();

        // then
        assertEquals(0, model.getUsersToAssign().size());
        assertEquals(2, model.getUsersToDeassign().size());
    }

    @Test
    public void assemblePOService_emptyName() {
        // given
        List<VOService> voServices = createVoServices(null);

        // when
        List<POService> result = ctrl.assemblePOService(voServices);

        // then
        assertEquals(1, result.size());
        assertEquals("", result.get(0).getServiceName());
    }

    @Test
    public void assemblePOService() {
        // given
        List<VOService> voServices = createVoServices("test");

        // when
        List<POService> result = ctrl.assemblePOService(voServices);

        // then
        assertEquals(1, result.size());
        assertEquals("test", result.get(0).getServiceName());
    }

    @Test
    public void toSortedServiceRows_B11124() throws Exception {
        // given

        // when
        List<ServiceRow> result = ctrl.initServiceRows();

        // then
        assertEquals(2, result.size());
        assertListIsSorted(result);
    }

    @Test
    public void initServiceRows() throws Exception {
        // given
        doReturn(Arrays.asList(Long.valueOf(2))).when(userGroupService)
                .getInvisibleProductKeysForGroup(
                        model.getSelectedGroup().getKey());

        // when
        List<ServiceRow> result = ctrl.initServiceRows();

        // then
        assertEquals(2, result.size());
        assertEquals(Boolean.TRUE, Boolean.valueOf(result.get(0).isSelected()));
        assertEquals(Boolean.FALSE, Boolean.valueOf(result.get(1).isSelected()));
    }

    @Test
    public void setSelectedServices() {
        // given
        ServiceRow serviceRow1 = new ServiceRow(new POService(), false);
        ServiceRow serviceRow2 = new ServiceRow(new POService(), true);
        model.setServiceRows(Arrays.asList(serviceRow1, serviceRow2));

        // when
        ctrl.setSelectedServices();

        // then
        assertEquals(1, model.getSelectedGroup().getInvisibleServices().size());
    }

    @Test
    public void update() throws Exception {
        POUserGroup group = new POUserGroup();
        group.setGroupName("NAME");
        doReturn(group).when(userGroupService).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        model.setSelectedGroup(new POUserGroup());
        doReturn(model.getSelectedGroup()).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();
        String result = ctrl.save();
        verify(userGroupService, times(1)).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        assertEquals(BaseBean.OUTCOME_SUCCESS, result);
        verify(tableStatus, times(1)).resetActiveEditPage();
    }

    @Test
    public void update_ValidationException() throws Exception {
        model.setSelectedGroup(new POUserGroup());
        doThrow(new ValidationException()).when(userGroupService).updateGroup(
                eq(selectedGroup), anyString(),
                anyListOf(POUserDetails.class), anyListOf(POUserDetails.class));
        doReturn(model.getSelectedGroup()).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();
        String result = ctrl.save();
        verify(userGroupService, times(1)).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        assertEquals(OUTCOME_ERROR, result);
    }

    @Test
    public void update_ConcurrentModificationException() throws Exception {
        model.setSelectedGroup(new POUserGroup());
        doReturn(model.getSelectedGroup()).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();
        doThrow(new ConcurrentModificationException()).when(userGroupService)
                .updateGroup(eq(selectedGroup), anyString(),
                        anyListOf(POUserDetails.class),
                        anyListOf(POUserDetails.class));
        String result = ctrl.save();
        verify(userGroupService, times(1)).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        assertEquals(OUTCOME_ERROR, result);
    }

    @Test
    public void update_OperationNotPermittedException() throws Exception {
        model.setSelectedGroup(new POUserGroup());
        doThrow(new OperationNotPermittedException()).when(userGroupService)
                .updateGroup(eq(selectedGroup), anyString(),
                        anyListOf(POUserDetails.class),
                        anyListOf(POUserDetails.class));
        doReturn(model.getSelectedGroup()).when(userGroupService).getUserGroupDetailsForList(anyLong());
        ctrl.getInitialize();
        String result = ctrl.save();
        verify(userGroupService, times(1)).updateGroup(eq(selectedGroup),
                anyString(), anyListOf(POUserDetails.class),
                anyListOf(POUserDetails.class));
        assertEquals(OUTCOME_ERROR, result);
    }

    @Test
    public void selectGroup() {

        String result = ctrl.selectGroup();

        assertEquals(BaseBean.OUTCOME_EDIT_GROUP, result);
    }

    @Test
    public void initSelectedGroup() throws Exception {
        // given
        POUserGroup group = new POUserGroup();
        doReturn(group).when(userGroupService).getUserGroupDetailsForList(
                anyLong());

        // when
        ctrl.initSelectedGroup();

        // then
        assertEquals(group, model.getSelectedGroup());
    }

    @Test
    public void initSelectedGroup_ObjectNotFoundException() throws Exception {
        doThrow(new ObjectNotFoundException()).when(userGroupService)
                .getUserGroupDetailsForList(anyLong());
        ctrl.initSelectedGroup();

        assertEquals(null, model.getSelectedGroup());
        assertEquals(null, model.getSelectedGroupId());
    }

    private void initModelData() {
        selectedGroup = new POUserGroup();
        model.setSelectedGroupId("1000");
        model.setSelectedGroup(selectedGroup);
        POUserDetails poUser = new POUserDetails();
        poUser.setUserId("assignedUser");
        List<POService> poServices = new ArrayList<POService>();
        POService poService = new POService();
        poService.setKey(1L);
        poServices.add(poService);
        selectedGroup.setUsers(Arrays.asList(poUser));
        selectedGroup.setGroupName("selectedGroup");
        selectedGroup.setInvisibleServices(poServices);

        model.setAssignedUsers(new ArrayList<User>());
        model.setUnAssignedUsers(new ArrayList<User>());
    }

    private void prepareAssignedUnassignedUsers() {
        List<VOUserDetails> users = new ArrayList<VOUserDetails>();
        VOUserDetails assignedUser = new VOUserDetails();
        assignedUser.setUserId("assignedUser");
        users.add(assignedUser);

        VOUserDetails unAssignedUser = new VOUserDetails();
        unAssignedUser.setUserId("unAssignedUser");
        users.add(unAssignedUser);

        doReturn(users).when(identityService).getUsersForOrganization();
    }

    private void prepareUsersToDeassign(boolean isSelected) throws Exception {
        List<User> assignedUsers = new ArrayList<User>();
        VOUserDetails voUser = new VOUserDetails();
        User user = new User(voUser);
        user.setSelected(isSelected);
        user.setUserId("test1");
        user.setEmail("sh@fujitsu.com");
        user.setLocale("en");
        assignedUsers.add(user);
        model.setUsersToDeassign(assignedUsers);
    }

    private void prepareUsersToAssign() throws Exception {
        List<User> assignedUsers = new ArrayList<User>();
        VOUserDetails voUser = new VOUserDetails();
        User user = new User(voUser);
        user.setSelected(true);
        user.setUserId("test2");
        user.setEmail("sh@fujitsu.com");
        user.setLocale("en");
        assignedUsers.add(user);
        model.setUsersToAssign(assignedUsers);
    }

    private List<VOService> createVoServices(String name) {
        List<VOService> voServices = new ArrayList<VOService>();
        VOService voService = new VOService();
        voService.setKey(1L);
        voService.setVersion(1);
        voService.setSellerName("sellerName");
        voService.setName(name);
        voServices.add(voService);
        return voServices;
    }

    private void assertListIsSorted(List<ServiceRow> rows) {
        String prev = null;
        for (ServiceRow row : rows) {
            if (prev != null) {
                assertEquals(Boolean.TRUE, Boolean.valueOf(prev.compareTo(row
                        .getService().getServiceName()) <= 0));
            } else {
                prev = row.getService().getServiceName();
            }
        }

    }

    private List<VOService> getServicesList() {
        List<VOService> servicesList = new ArrayList<VOService>();
        VOService serviceFirst = new VOService();
        serviceFirst.setKey(1L);
        serviceFirst.setSellerName("chen");
        serviceFirst.setVersion(1000);
        serviceFirst.setName("voServiceName");
        VOService serviceSecond = new VOService();
        serviceSecond.setKey(2L);
        serviceSecond.setSellerName("zhang");
        serviceSecond.setVersion(1000);
        serviceSecond.setName("voServiceNameSecond");
        servicesList.add(serviceFirst);
        servicesList.add(serviceSecond);
        return servicesList;
    }
}
