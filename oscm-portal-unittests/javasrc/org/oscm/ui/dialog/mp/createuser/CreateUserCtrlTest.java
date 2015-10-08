/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: 26.09.2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.createuser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.faces.model.SelectItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.dialog.classic.manageusers.UserRole;
import org.oscm.ui.dialog.state.TableState;
import org.oscm.internal.components.response.Response;
import org.oscm.internal.components.response.ReturnCode;
import org.oscm.internal.components.response.ReturnType;
import org.oscm.internal.types.constants.HiddenUIConstants;
import org.oscm.internal.types.enumtypes.Salutation;
import org.oscm.internal.types.enumtypes.UserRoleType;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.usergroupmgmt.POUserGroup;
import org.oscm.internal.usergroupmgmt.UserGroupService;
import org.oscm.internal.usermanagement.POServiceRole;
import org.oscm.internal.usermanagement.POSubscription;
import org.oscm.internal.usermanagement.POUserAndSubscriptions;
import org.oscm.internal.usermanagement.UserService;

/**
 * @author weiser
 * 
 */
public class CreateUserCtrlTest {

    private static final String MP_ID = "mpid";
    private static final String NAME = "name";

    private CreateUserCtrl ctrl;

    private CreateUserModel model;
    private UserService us;
    private UserGroupService userGroupService;
    private POUserAndSubscriptions uas;
    private ApplicationBean applicationBean;
    private TableState ts;

    @Before
    public void setup() throws Exception {
        userGroupService = mock(UserGroupService.class);
        us = mock(UserService.class);
        ctrl = new CreateUserCtrl();
        ctrl.setUserService(us);
        ctrl.setUserGroupService(userGroupService);

        model = new CreateUserModel();
        applicationBean = mock(ApplicationBean.class);
        ctrl.ui = mock(UiDelegate.class);

        uas = new POUserAndSubscriptions();
        uas.setLocale("en");
        uas.setAvailableRoles(EnumSet.of(UserRoleType.ORGANIZATION_ADMIN));
        uas.setSubscriptions(prepareSubscriptions());

        when(ctrl.ui.getMarketplaceId()).thenReturn(MP_ID);
        ctrl.setModel(model);
        ctrl.setApplicationBean(applicationBean);
        when(ctrl.ui.getText(anyString())).thenReturn(NAME);

        when(us.getNewUserData()).thenReturn(uas);
        when(us.createNewUser(any(POUserAndSubscriptions.class), anyString()))
                .thenReturn(new Response());

        when(
                Boolean.valueOf(applicationBean
                        .isUIElementHidden(eq(HiddenUIConstants.PANEL_USER_LIST_SUBSCRIPTIONS))))
                .thenReturn(Boolean.FALSE);
        when(userGroupService.getGroupsForOrganization())
                .thenReturn(preparePOUserGroups(3));
        ts = mock(TableState.class);
        ctrl.setTableState(ts);
    }

    @Test
    public void init() {
        ctrl.init();

        verifyInitialization(model);
    }

    @Test
    public void init_NoSubs() {
        uas.getSubscriptions().clear();

        ctrl.init();

        assertEquals("en", model.getLocale().getValue());
        assertEquals(0, model.getSubscriptions().size());
        assertEquals(1, model.getRoles().size());
    }

    @Test
    public void initUserGroups() {
        // when
        List<UserGroup> result = ctrl.initUserGroups();

        // then
        verify(userGroupService, times(1))
                .getGroupsForOrganization();
        assertEquals(3, result.size());
    }

    @Test
    public void create() throws Exception {
        ctrl.init();
        setData(ctrl.getModel(), true);

        String outcome = ctrl.create();

        assertEquals(BaseBean.OUTCOME_SUCCESS, outcome);
        verify(ctrl.ui, times(1)).handle(any(Response.class),
                eq(BaseBean.INFO_USER_CREATED),
                eq(model.getUserId().getValue()));
        ArgumentCaptor<POUserAndSubscriptions> ac = ArgumentCaptor
                .forClass(POUserAndSubscriptions.class);
        verify(us, times(1)).createNewUser(ac.capture(), eq(MP_ID));
        verifyPassedValue(ac.getValue(), true);
        verify(ts).resetActivePages();
    }

    @Test
    public void create_MailOperationException() throws Exception {
        ctrl.init();
        // given
        when(us.createNewUser(any(POUserAndSubscriptions.class), anyString()))
                .thenThrow(new MailOperationException());
        setData(ctrl.getModel(), true);
        when(Boolean.valueOf(applicationBean.isInternalAuthMode())).thenReturn(
                Boolean.TRUE);

        // when
        String outcome = ctrl.create();

        // then
        assertEquals(BaseBean.OUTCOME_ERROR, outcome);
        verify(ctrl.ui, times(1)).handleError((String) isNull(),
                eq(BaseBean.ERROR_USER_CREATE_MAIL));
    }

    @Test
    public void create_MailOperationException_NotInternal() throws Exception {
        ctrl.init();
        // given
        when(us.createNewUser(any(POUserAndSubscriptions.class), anyString()))
                .thenThrow(new MailOperationException());
        setData(ctrl.getModel(), true);
        when(Boolean.valueOf(applicationBean.isInternalAuthMode())).thenReturn(
                Boolean.FALSE);

        // when
        String outcome = ctrl.create();

        // then
        assertEquals(BaseBean.OUTCOME_ERROR, outcome);
        verify(ctrl.ui, times(1)).handleError((String) isNull(),
                eq(BaseBean.ERROR_USER_CREATE_MAIL_NOT_INTERNAL));
    }

    @Test
    public void create_Pending() throws Exception {
        ctrl.init();
        Response r = new Response();
        r.getReturnCodes().add(new ReturnCode(ReturnType.INFO, "messageKey"));
        when(us.createNewUser(any(POUserAndSubscriptions.class), anyString()))
                .thenReturn(r);
        setData(ctrl.getModel(), false);

        String outcome = ctrl.create();

        assertEquals(BaseBean.OUTCOME_PENDING, outcome);
        verify(ctrl.ui, times(1)).handle(any(Response.class),
                eq(BaseBean.INFO_USER_CREATED),
                eq(model.getUserId().getValue()));
        ArgumentCaptor<POUserAndSubscriptions> ac = ArgumentCaptor
                .forClass(POUserAndSubscriptions.class);
        verify(us, times(1)).createNewUser(ac.capture(), eq(MP_ID));
        verifyPassedValue(ac.getValue(), false);
    }

    @Test
    public void isSubTableRendered_NoSubs() {
        assertFalse(ctrl.isSubTableRendered());
    }

    @Test
    public void isSubTableRendered_TableHidden() {
        when(
                Boolean.valueOf(applicationBean
                        .isUIElementHidden(eq(HiddenUIConstants.PANEL_USER_LIST_SUBSCRIPTIONS))))
                .thenReturn(Boolean.TRUE);

        assertFalse(ctrl.isSubTableRendered());
    }

    @Test
    public void isSubTableRendered() {
        ctrl.init();
        assertTrue(ctrl.isSubTableRendered());
    }

    @Test
    public void isRoleColumnRendered_NoSubs() {
        assertFalse(ctrl.isRoleColumnRendered());
    }

    @Test
    public void isRoleColumnRendered_NoRoles() {
        model.setSubscriptions(Arrays.asList(new Subscription()));

        assertFalse(ctrl.isRoleColumnRendered());
    }

    @Test
    public void isRoleColumnRendered_Cached() {
        ctrl.rolesColumnVisible = Boolean.TRUE;

        assertTrue(ctrl.isRoleColumnRendered());

        verifyZeroInteractions(ctrl.ui);
    }

    @Test
    public void isRoleColumnRendered() {
        ctrl.init();
        assertTrue(ctrl.isRoleColumnRendered());
    }

    @Test
    public void getSelectedUserGroups() {
        List<UserGroup> groups = new ArrayList<UserGroup>();
        groups.add(prepareUserGroup(true));
        groups.add(prepareUserGroup(false));
        List<POUserGroup> poUserGroups = ctrl.getSelectedUserGroups(groups);

        assertEquals(2, poUserGroups.size());
    }

    private UserGroup prepareUserGroup(boolean selected) {
        UserGroup group = new UserGroup();
        group.setSelected(selected);
        POUserGroup poUserGroup = new POUserGroup();
        poUserGroup.setDefault(true);
        group.setPoUserGroup(poUserGroup);
        return group;
    }

    private List<POUserGroup> preparePOUserGroups(int num) {
        List<POUserGroup> userGroups = new ArrayList<POUserGroup>();
        for (int i = 0; i < num; i++) {
            POUserGroup userGroup = new POUserGroup();
            userGroup.setKey(i);
            userGroup.setGroupName("groupName");
            userGroup.setGroupDescription("description");
            userGroup.setGroupReferenceId("reference id" + i);
            userGroups.add(userGroup);
        }
        return userGroups;
    }

    private static void verifyPassedValue(POUserAndSubscriptions uas,
            boolean extended) {
        assertEquals("email", uas.getEmail());
        assertEquals("firstname", uas.getFirstName());
        assertEquals("lastname", uas.getLastName());
        assertEquals("ja", uas.getLocale());
        assertEquals("userid", uas.getUserId());
        List<POSubscription> subs = uas.getSubscriptions();
        if (extended) {
            assertEquals(Salutation.MR, uas.getSalutation());
            assertEquals(EnumSet.of(UserRoleType.ORGANIZATION_ADMIN),
                    uas.getAssignedRoles());
            assertEquals(2, subs.size());
            POSubscription s = subs.get(0);
            assertEquals("sub1", s.getId());
            assertNull(s.getUsageLicense().getPoServieRole());

            s = subs.get(1);
            assertEquals("sub2", s.getId());
            POServiceRole r = s.getUsageLicense().getPoServieRole();
            assertEquals("role2", r.getId());
            assertEquals(15, r.getKey());
        } else {
            assertNull(uas.getSalutation());
            assertTrue(uas.getAssignedRoles().isEmpty());
            assertTrue(subs.isEmpty());
        }
    }

    private static void setData(CreateUserModel m, boolean extended) {
        m.getEmail().setValue("email");
        m.getFirstName().setValue("firstname");
        m.getLastName().setValue("lastname");
        m.getLocale().setValue("ja");
        m.getUserId().setValue("userid");
        if (extended) {
            m.getRoles().get(0).setSelected(true);
            m.getSalutation().setValue(Salutation.MR.name());
            m.getSubscriptions().get(0).setSelected(true);
            m.getSubscriptions().get(1).setSelected(true);
            m.getSubscriptions().get(1).setSelectedRole("15:role2");
        }
    }

    private static List<POSubscription> prepareSubscriptions() {
        List<POSubscription> result = new ArrayList<POSubscription>();
        POSubscription sub = new POSubscription();
        sub.setId("sub1");
        sub.setKey(10);
        result.add(sub);

        sub = new POSubscription();
        sub.setId("sub2");
        sub.setKey(20);
        sub.getUsageLicense().setKey(0);
        sub.getUsageLicense().setVersion(0);

        POServiceRole role = new POServiceRole();
        role.setId("role1");
        role.setKey(5);
        role.setName("role1name");
        sub.getRoles().add(role);
        role = new POServiceRole();
        role.setId("role2");
        role.setName("role2name");
        role.setKey(15);
        sub.getRoles().add(role);
        result.add(sub);
        return result;
    }

    private static void verifyInitialization(CreateUserModel model) {
        assertEquals("en", model.getLocale().getValue());
        assertEquals(1, model.getRoles().size());
        UserRole r = model.getRoles().get(0);
        assertEquals(NAME, r.getName());
        assertEquals(UserRoleType.ORGANIZATION_ADMIN, r.getType());

        assertEquals(2, model.getSubscriptions().size());
        Subscription s = model.getSubscriptions().get(0);
        assertEquals("sub1", s.getId());
        assertEquals(0, s.getLicKey());
        assertEquals(0, s.getLicVersion());
        assertTrue(s.getRoles().isEmpty());
        assertFalse(s.isRolesRendered());
        assertFalse(s.isSelected());
        assertNull(s.getSelectedRole());

        s = model.getSubscriptions().get(1);
        assertEquals("sub2", s.getId());
        assertTrue(s.isRolesRendered());
        assertFalse(s.isSelected());
        assertNull(s.getSelectedRole());
        assertEquals(2, s.getRoles().size());

        SelectItem si = s.getRoles().get(0);
        assertEquals("role1name", si.getLabel());
        assertEquals("5:role1", si.getValue());
        si = s.getRoles().get(1);
        assertEquals("role2name", si.getLabel());
        assertEquals("15:role2", si.getValue());
    }

}
