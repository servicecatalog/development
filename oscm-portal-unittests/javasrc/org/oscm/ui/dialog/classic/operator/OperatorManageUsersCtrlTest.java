/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2015                                             
 *                                                                                                                                 
 *  Creation Date: Jun 1, 2012                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.classic.operator;

import static org.oscm.test.matchers.JavaMatchers.hasItems;
import static org.oscm.test.matchers.JavaMatchers.hasNoItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.junit.Before;
import org.junit.Test;

import org.oscm.types.constants.Configuration;
import org.oscm.ui.beans.ApplicationBean;
import org.oscm.ui.beans.BaseBean;
import org.oscm.ui.beans.MarketplaceBean;
import org.oscm.ui.common.UiDelegate;
import org.oscm.ui.delegates.ServiceLocator;
import org.oscm.ui.model.Marketplace;
import org.oscm.internal.intf.AccountService;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.IdentityService;
import org.oscm.internal.intf.OperatorService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.enumtypes.OrganizationRoleType;
import org.oscm.internal.types.exception.MailOperationException;
import org.oscm.internal.types.exception.ObjectNotFoundException;
import org.oscm.internal.types.exception.OperationNotPermittedException;
import org.oscm.internal.types.exception.OrganizationAuthoritiesException;
import org.oscm.internal.usermanagement.UserManagementService;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.internal.vo.VOUser;
import org.oscm.internal.vo.VOUserDetails;

/**
 * @author ZhouMin
 * 
 */
public class OperatorManageUsersCtrlTest {
    private VOUser user;
    private ServiceLocator sl;
    private OperatorManageUsersCtrl bean;
    private final ApplicationBean appBean = mock(ApplicationBean.class);
    private final UserManagementService userManageService = mock(UserManagementService.class);
    private final IdentityService idService = mock(IdentityService.class);
    private final AccountService accountingService = mock(AccountService.class);
    private final ConfigurationService configurationService = mock(ConfigurationService.class);
    private MarketplaceBean marketplaceBean;
    private UiDelegate ui;
    private final OperatorService operatorService = mock(OperatorService.class);

    @Before
    public void setup() throws Exception {
        bean = new OperatorManageUsersCtrl() {

            private static final long serialVersionUID = -9126265695343363133L;

            @Override
            protected IdentityService getIdService() {
                return idService;
            }

            @Override
            protected OperatorService getOperatorService() {
                return operatorService;
            }

            @Override
            protected void addMessage(final String clientId,
                    final FacesMessage.Severity severity, final String key) {
            }

            @Override
            protected AccountService getAccountingService() {
                return accountingService;
            }

            @Override
            protected ConfigurationService getConfigurationService() {
                return configurationService;
            }
        };

        bean.model = new OperatorManageUsersModel();

        bean.getModel().setUserId("userId");

        ui = mock(UiDelegate.class);
        bean.ui = ui;

        marketplaceBean = mock(MarketplaceBean.class);
        when(bean.ui.findBean("marketplaceBean")).thenReturn(marketplaceBean);

        sl = mock(ServiceLocator.class);
        bean.setServiceLocator(sl);
        user = new VOUser();
        user.setOrganizationId("organizationId");
        user.setUserId("userId");

        when(idService.getUser(any(VOUser.class))).thenReturn(user);
        when(bean.ui.findBean(eq(OperatorManageUsersCtrl.APPLICATION_BEAN)))
                .thenReturn(appBean);
        when(sl.findService(UserManagementService.class)).thenReturn(
                userManageService);
        when(bean.getApplicationBean()).thenReturn(appBean);
        when(Boolean.valueOf(appBean.isInternalAuthMode())).thenReturn(
                Boolean.TRUE);
        when(
                Boolean.valueOf(userManageService
                        .isOrganizationLDAPManaged(user.getOrganizationId())))
                .thenReturn(Boolean.FALSE);
        when(Long.valueOf(accountingService.countRegisteredUsers()))
                .thenReturn(Long.valueOf(10));

        doReturn(getConfigurationSetting()).when(configurationService)
                .getVOConfigurationSetting(any(ConfigurationKey.class),
                        anyString());
    }

    @Test
    public void getInitialize() throws Exception {
        // given
        currentUser(OrganizationRoleType.PLATFORM_OPERATOR);
        availableMarketplaces(123, 1234);
        bean.model.setInitialized(false);
        
        // when
        bean.getInitialize();

        // then
        verify(marketplaceBean, times(1)).getMarketplacesForOperator();
        assertThat(bean.getSelectableMarketplaces(), hasItems(2));
    }

    @Test
    public void getInitialize_initialized() throws Exception {
        // given
        currentUser(OrganizationRoleType.PLATFORM_OPERATOR);
        bean.model.setInitialized(true);
        availableMarketplaces(123, 1234, 11);
        
        // when
        bean.getInitialize();

        // then
        verify(marketplaceBean, times(1)).getMarketplacesForOperator();
        assertThat(bean.getSelectableMarketplaces(), hasItems(3));
    }

    @Test
    public void reinitUser() throws Exception {
        // when
        bean.reinitUser();

        // then
        assertEquals(Boolean.valueOf(true),
                Boolean.valueOf(bean.isCheckResetPasswordSupported()));
    }

    @Test
    public void reinitUser_NotInternalAuthMode() throws Exception {
        // given
        when(Boolean.valueOf(appBean.isInternalAuthMode())).thenReturn(
                Boolean.FALSE);
        // when
        bean.reinitUser();
        // then
        assertEquals(Boolean.valueOf(false),
                Boolean.valueOf(bean.isCheckResetPasswordSupported()));
    }

    @Test
    public void reinitUser_UserIdNull() throws Exception {
        // given
        bean.getModel().setUserId(null);
        // when
        bean.reinitUser();
        // then
        verify(sl, never()).findService(UserManagementService.class);
        verify(bean.appBean, never()).isInternalAuthMode();
        assertEquals(Boolean.valueOf(true),
                Boolean.valueOf(bean.isCheckResetPasswordSupported()));
    }

    @Test
    public void isResetPasswordSupported() throws Exception {
        // when
        bean.isCheckResetPasswordSupported();
        // then
        assertEquals(Boolean.valueOf(true),
                Boolean.valueOf(bean.isCheckResetPasswordSupported()));
    }

    @Test
    public void isResetPasswordSupported_userIdChanged() throws Exception {
        // given
        bean.getModel().setUserId("userId1");
        // when
        bean.isCheckResetPasswordSupported();
        // then
        verify(bean.appBean, times(1)).isInternalAuthMode();
        assertEquals(Boolean.valueOf(true),
                Boolean.valueOf(bean.isCheckResetPasswordSupported()));
    }

    /**
     * Test the retrieving of available marketplaces. This operation is allowed
     * only to the PLATFORM_OPERATOR. All other organization roles will recieve
     * empty list.
     */
    @Test
    public void getSelectableMarketplaces() {
        // given
        currentUser(OrganizationRoleType.PLATFORM_OPERATOR);
        availableMarketplaces(123, 1234);

        // when
        List<Marketplace> result = bean.getSelectableMarketplaces();

        // than
        verify(marketplaceBean, times(1)).getMarketplacesForOperator();
        assertThat(result, hasItems(2));
    }

    /**
     * Test the retrieving of available marketplaces. This operation is allowed
     * only to the PLATFORM_OPERATOR. All other organization roles will recieve
     * empty list.
     */
    @Test
    public void getSelectableMarketplaces_Not_Platform_Operator() {
        // given not authorized for this operation
        currentUser(OrganizationRoleType.BROKER);
        availableMarketplaces(123, 1234);

        // when
        List<Marketplace> result = bean.getSelectableMarketplaces();

        // than
        verify(marketplaceBean, times(0)).getMarketplacesForOperator();
        assertThat(result, hasNoItems());
    }

    /**
     * Test the retrieving of available marketplaces. This operation is allowed
     * only to the PLATFORM_OPERATOR. All other organization roles will recieve
     * empty list.
     */
    @Test
    public void getSelectableMarketplaces_No_Marketplaces() {
        // given no existing marketplaces
        currentUser(OrganizationRoleType.PLATFORM_OPERATOR);
        availableMarketplaces();

        // when
        List<Marketplace> result = bean.getSelectableMarketplaces();

        // than
        verify(marketplaceBean, times(1)).getMarketplacesForOperator();
        assertThat(result, hasNoItems());
    }

    @Test
    public void resetPasswordForUser_userIdNull()
            throws ObjectNotFoundException, OperationNotPermittedException,
            MailOperationException, OrganizationAuthoritiesException {
        // given
        bean.getModel().setUser(null);

        // when
        String restult = bean.resetPasswordForUser();

        // then
        verify(operatorService, times(0)).resetPasswordForUser(
                bean.getModel().getUserId());
        assertEquals(BaseBean.OUTCOME_ERROR, restult);
    }

    @Test
    public void resetPasswordForUser_ldapUser() throws ObjectNotFoundException,
            OperationNotPermittedException, MailOperationException,
            OrganizationAuthoritiesException {
        // given
        prepareLdapUser();

        // when
        String restult = bean.resetPasswordForUser();

        // then
        verify(operatorService, times(0)).resetPasswordForUser(
                bean.getModel().getUserId());
        assertEquals(BaseBean.OUTCOME_ERROR, restult);
    }

    @Test
    public void resetPasswordForUser() throws ObjectNotFoundException,
            OperationNotPermittedException, MailOperationException,
            OrganizationAuthoritiesException {
        // given
        VOUser voUser = new VOUser();
        voUser.setUserId("userId");
        bean.getModel().setUser(voUser);

        // when
        String restult = bean.resetPasswordForUser();

        // then
        verify(operatorService, times(1)).resetPasswordForUser("userId");
        assertEquals(BaseBean.OUTCOME_SUCCESS, restult);
    }

    @Test
    public void getMaxRegisteredUsersCount_configurationSetting() {
        // when
        long isExceed = bean.getMaxRegisteredUsersCount();

        // then
        assertEquals(5, isExceed);
    }

    @Test
    public void isExceedMaxNumberOfUsers_true() {
        // given
        bean.model.setNumberOfRegisteredUsers(15);
        bean.model.setMaxNumberOfRegisteredUsers(10);

        // when
        boolean isExceed = bean.isExceedMaxNumberOfUsers();

        // then
        assertEquals(Boolean.TRUE, Boolean.valueOf(isExceed));
    }

    @Test
    public void isExceedMaxNumberOfUsers_false() {
        // given
        bean.model.setNumberOfRegisteredUsers(5);
        bean.model.setMaxNumberOfRegisteredUsers(10);

        // when
        boolean isExceed = bean.isExceedMaxNumberOfUsers();

        // then
        assertEquals(Boolean.FALSE, Boolean.valueOf(isExceed));
    }

    @Test
    public void importUsers_tokenInvalid() {
        // given
        bean.model.resetToken();
        bean.model.setToken(bean.model.getToken());
        bean.model.resetToken();
        //when
        String result = bean.importUsers();
        // then
        assertEquals(BaseBean.OUTCOME_SUCCESS, result);
        verify(bean.ui, times(0)).handle(anyString(), anyString());
    }

    private void prepareLdapUser() throws ObjectNotFoundException {
        VOUser voUser = new VOUser();
        voUser.setOrganizationId("organizationId");
        bean.getModel().setUser(voUser);

        when(sl.findService(UserManagementService.class)).thenReturn(
                userManageService);
        when(
                Boolean.valueOf(userManageService
                        .isOrganizationLDAPManaged(user.getOrganizationId())))
                .thenReturn(Boolean.TRUE);
    }

    private void currentUser(OrganizationRoleType platformOperator) {
        VOUserDetails userDetails = new VOUserDetails();
        userDetails.setUserRoles(OrganizationRoleType
                .correspondingUserRoles(Arrays.asList(platformOperator)));

        when(ui.getUserFromSessionWithoutException()).thenReturn(userDetails);
    }

    private void availableMarketplaces(long... mIds) {
        ArrayList<Marketplace> marketplaces = new ArrayList<Marketplace>();
        for (int i = 0; i < mIds.length; i++) {
            Marketplace mp = new Marketplace();
            long mKey = mIds[i];
            mp.setKey(mKey);
            mp.setVersion(i + 1);
            mp.setMarketplaceId(String.valueOf(mKey) + "_id");
            mp.setName(String.valueOf(mKey) + "_name");
            mp.setOwningOrganizationId(String.valueOf(mKey) + "_org");

            marketplaces.add(mp);
        }

        if (marketplaces.isEmpty()) {
            when(marketplaceBean.getMarketplacesForOperator()).thenReturn(null);
        } else {
            when(marketplaceBean.getMarketplacesForOperator()).thenReturn(
                    marketplaces);
        }
    }

    private VOConfigurationSetting getConfigurationSetting() {
        VOConfigurationSetting configurationSetting = new VOConfigurationSetting();
        configurationSetting
                .setInformationId(ConfigurationKey.MAX_NUMBER_ALLOWED_USERS);
        configurationSetting.setContextId(Configuration.GLOBAL_CONTEXT);
        configurationSetting.setValue("5");
        configurationSetting.setKey(1000L);
        return configurationSetting;
    }
}
