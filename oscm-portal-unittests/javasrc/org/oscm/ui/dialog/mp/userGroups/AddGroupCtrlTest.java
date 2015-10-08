/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Jun 25, 2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.userGroups;

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
import org.oscm.internal.vo.ListCriteria;
import org.oscm.internal.vo.VOService;
import org.oscm.internal.vo.VOServiceListResult;
import org.oscm.internal.vo.VOUserDetails;
import org.junit.Before;
import org.junit.Test;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author mao
 * 
 */
public class AddGroupCtrlTest {

    static final String APPLICATION_BEAN = "appBean";
    public static final String OUTCOME_NEWGROUP = "addNewGroup";
    public static final String OUTCOME_SUCCESS = "success";
    public static final String OUTCOME_ERROR = "error";
    private ManageGroupModel model;
    private AddGroupCtrl ctrl;
    private UserGroupService userGroupService;
    private SearchServiceInternal searchServiceInternal;
    private ExternalContext exContext;
    private IdentityService idServiceMock;
    private TableState tableStatus;

    @Before
    public void setup() throws Exception {
        UIViewRoot viewRoot = mock(UIViewRoot.class);
        tableStatus = mock(TableState.class);
        given(viewRoot.getLocale()).willReturn(Locale.ENGLISH);
        new FacesContextStub(Locale.ENGLISH).setViewRoot(viewRoot);
        List<POUserGroup> poUserGroupList = getPOUserGroupList();
        List<VOService> servicesList = getServicesList();
        VOServiceListResult voServiceListResult = new VOServiceListResult();
        voServiceListResult.setServices(servicesList);
        searchServiceInternal = mock(SearchServiceInternal.class);
        final List<VOUserDetails> voUserDetailsList = getVOUserDetailsList();
        idServiceMock = mock(IdentityService.class);
        userGroupService = mock(UserGroupService.class);
        exContext = mock(ExternalContext.class);
        model = spy(new ManageGroupModel());
        ctrl = spy(new AddGroupCtrl());
        ctrl.setUserGroupService(userGroupService);
        ctrl.setSearchServiceInternal(searchServiceInternal);
        ctrl.setIdService(idServiceMock);
        ctrl.setManageGroupModel(model);
        ctrl.setTableState(tableStatus);
        ctrl.setUi(spy(new UiDelegate() {
            @Override
            public void handleException(SaaSApplicationException ex) {

            }

            @Override
            public String getText(String key, Object... params) {
                return "modalName";
            }

            @Override
            public ExternalContext getExternalContext() {
                return exContext;
            }
        }));

        doReturn(voUserDetailsList).when(idServiceMock)
                .getUsersForOrganization();

        doReturn(poUserGroupList).when(userGroupService)
                .getGroupsForOrganization();

        doReturn(voServiceListResult).when(searchServiceInternal)
                .getServicesByCriteria(anyString(), anyString(),
                        any(ListCriteria.class), any(PerformanceHint.class));

    }

    @Test
    public void getInitListCriteria_CorrectParam() {
        // when
        ListCriteria result = ctrl.getInitListCriteria();
        // then
        assertEquals("", result.getFilter());
        assertEquals(0, result.getOffset());
        assertEquals(-1, result.getLimit());
    }

    @Test
    public void initUnassignUsers() {
        // when
        ctrl.initUnassignUsers();

        // then
        assertEquals("firstUserId", model.getUsersToDeassign().get(0)
                .getUserId());
        assertEquals("secondUserId", model.getUsersToDeassign().get(1)
                .getUserId());
    }

    @Test
    public void initServiceRows() throws ObjectNotFoundException {
        // when
        List<ServiceRow> serviceRowList = ctrl.initServiceRows();

        // then
        assertEquals("testNameA", serviceRowList.get(0).getService()
                .getProviderName());
        assertEquals("testNameB", serviceRowList.get(1).getService()
                .getProviderName());
    }

    @Test
    public void assemblePOService() {
        // when
        List<POService> pos = ctrl.assemblePOService(getServicesList());

        // then
        assertEquals("voServiceName", pos.get(0).getServiceName());
        assertEquals("testNameB", pos.get(0).getProviderName());
        assertEquals("modalName", pos.get(1).getServiceName());
        assertEquals("testNameA", pos.get(1).getProviderName());
    }

    @Test
    public void create_Null() {
        model.setSelectedGroup(null);
        String result = ctrl.create();
        assertEquals(OUTCOME_ERROR, result);
    }

    @Test
    public void create() throws Exception {
        model.setSelectedGroup(new POUserGroup());
        String result = ctrl.create();
        verify(userGroupService, times(1)).createGroup(any(POUserGroup.class),
                anyString());
        verify(tableStatus, times(1)).resetActiveAddPage();
        assertEquals(OUTCOME_SUCCESS, result);
    }

    @Test
    public void create_ValidationException() throws Exception {
        model.setSelectedGroup(new POUserGroup());
        doThrow(new ValidationException()).when(userGroupService).createGroup(
                any(POUserGroup.class), anyString());
        String result = ctrl.create();
        verify(userGroupService, times(1)).createGroup(any(POUserGroup.class),
                anyString());
        assertEquals(OUTCOME_ERROR, result);
    }

    @Test
    public void create_NonUniqueBusinessKeyException() throws Exception {
        // given
        model.setSelectedGroup(new POUserGroup());
        determineUser();
        doThrow(new NonUniqueBusinessKeyException()).when(userGroupService)
                .createGroup(any(POUserGroup.class), anyString());
        model.setServiceRows(ctrl.initServiceRows());
        model.getServiceRows().get(0).setSelected(true);
        ctrl.setSelectedServices();
        // when
        String result = ctrl.create();
        // then
        verify(userGroupService, times(1)).createGroup(any(POUserGroup.class),
                anyString());
        assertEquals(0, model.getSelectedGroup().getUsers().size());
        assertEquals(0, model.getSelectedGroup().getInvisibleServices().size());
        assertEquals(0, model.getSelectedGroup().getVisibleServices().size());
        assertEquals(OUTCOME_ERROR, result);
    }

    @Test
    public void create_OperationNotPermittedException() throws Exception {
        // given
        model.setSelectedGroup(getPOUserGroupList().get(0));
        determineUser();
        doThrow(new OperationNotPermittedException()).when(userGroupService)
                .createGroup(any(POUserGroup.class), anyString());
        model.setServiceRows(ctrl.initServiceRows());
        model.getServiceRows().get(0).setSelected(true);
        ctrl.setSelectedServices();
        // when
        String result = ctrl.create();
        // then
        verify(userGroupService, times(1)).createGroup(any(POUserGroup.class),
                anyString());
        assertEquals(0, model.getSelectedGroup().getUsers().size());
        assertEquals(0, model.getSelectedGroup().getInvisibleServices().size());
        assertEquals(0, model.getSelectedGroup().getVisibleServices().size());
        assertEquals(OUTCOME_ERROR, result);
    }

    @Test
    public void setSelectedServices() throws ObjectNotFoundException {
        // given
        model.setServiceRows(ctrl.initServiceRows());
        model.setSelectedGroup(getPOUserGroupList().get(0));
        model.getServiceRows().get(0).setSelected(true);

        // when
        ctrl.setSelectedServices();

        // then
        assertEquals(0, model.getSelectedGroup().getInvisibleServices().size());
    }

    @Test
    public void setSelectedUsers() {
        // given
        ctrl.initUnassignUsers();
        model.setSelectedGroup(getPOUserGroupList().get(0));
        User user = model.getUsersToDeassign().get(0);
        model.getUsersToAssign().add(user);
        model.getUsersToAssign().get(0).setSelected(true);

        // when
        ctrl.setSelectedUsers();

        // then
        assertEquals(1, model.getSelectedGroup().getUsers().size());
        assertEquals(
                Long.valueOf(1),
                Long.valueOf(model.getSelectedGroup().getUsers().get(0)
                        .getKey()));
        assertEquals("firstUserId", model.getSelectedGroup().getUsers().get(0)
                .getUserId());
    }

    @Test
    public void determineUserToDeassign() {
        // given
        determineUser();

        // when
        String result = ctrl.determineUserToDeassign();

        // then
        assertEquals("", result);
        assertEquals("test", model.getDeassignUserId());
    }

    @Test
    public void assignUsers() {
        // given
        reset(model);
        prepareUsersToDeassign();
        model.getUsersToDeassign().get(0).setSelected(true);
        // when
        String result = ctrl.assignUsers();

        // then
        assertEquals("", result);
        assertEquals(Boolean.TRUE,
                Boolean.valueOf(model.getUsersToDeassign().isEmpty()));
    }

    private void determineUser() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userToDeassign", "test");
        doReturn(map).when(exContext).getRequestParameterMap();
        VOUserDetails voUser = new VOUserDetails();
        User user = new User(voUser);
        user.setSelected(true);
        user.setUserId("test");
        user.setEmail("sh@fujitsu.com");
        user.setLocale("en");
        model.getUsersToAssign().add(user);
    }

    private void prepareUsersToDeassign() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userToDeassign", "test");
        doReturn(map).when(exContext).getRequestParameterMap();
        VOUserDetails voUser = new VOUserDetails();
        User user = new User(voUser);
        user.setSelected(true);
        user.setUserId("test");
        user.setEmail("sh@fujitsu.com");
        user.setLocale("en");
        model.getUsersToDeassign().add(user);
    }

    private List<POUserGroup> getPOUserGroupList() {
        List<POUserGroup> poUserGroupList = new ArrayList<POUserGroup>();
        POUserGroup poUserFirst = new POUserGroup();
        poUserFirst.setKey(1L);
        poUserFirst.setVersion(1001);
        poUserFirst.setGroupName("groupNameOne");
        POUserGroup poUserSecond = new POUserGroup();
        poUserSecond.setKey(2L);
        poUserSecond.setVersion(1001);
        poUserSecond.setGroupName("groupNameTwo");
        poUserGroupList.add(poUserFirst);
        poUserGroupList.add(poUserSecond);
        return poUserGroupList;
    }

    List<VOUserDetails> getVOUserDetailsList() {
        List<VOUserDetails> voUserDetails = new ArrayList<VOUserDetails>();
        VOUserDetails voUserFirst = new VOUserDetails();
        voUserFirst.setKey(1L);
        voUserFirst.setUserId("firstUserId");
        voUserFirst.setVersion(1000);
        voUserFirst.setEMail("test@fujitsu.com");
        voUserFirst.setLocale("en");
        VOUserDetails voUserSecond = new VOUserDetails();
        voUserSecond.setKey(1L);
        voUserSecond.setUserId("secondUserId");
        voUserSecond.setVersion(1000);
        voUserSecond.setEMail("test@fujitsu.com");
        voUserSecond.setLocale("en");
        voUserDetails.add(voUserFirst);
        voUserDetails.add(voUserSecond);
        return voUserDetails;
    }

    private List<VOService> getServicesList() {
        List<VOService> servicesList = new ArrayList<VOService>();
        VOService serviceFirst = new VOService();
        serviceFirst.setKey(1L);
        serviceFirst.setSellerName("testNameB");
        serviceFirst.setVersion(1000);
        serviceFirst.setName("voServiceName");
        VOService serviceSecond = new VOService();
        serviceSecond.setKey(2L);
        serviceSecond.setSellerName("testNameA");
        serviceSecond.setVersion(1000);
        servicesList.add(serviceFirst);
        servicesList.add(serviceSecond);
        return servicesList;
    }

}
