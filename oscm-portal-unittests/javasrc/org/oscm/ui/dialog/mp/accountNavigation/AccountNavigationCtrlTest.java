/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2013-8-22                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ui.dialog.mp.accountNavigation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.oscm.ui.beans.ApplicationBean;
import org.oscm.domobjects.ConfigurationSetting;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.types.constants.HiddenUIConstants;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.vo.VOConfigurationSetting;
import org.oscm.types.constants.Configuration;

/**
 * @author Yuyin
 * 
 */
public class AccountNavigationCtrlTest {

    private AccountNavigationCtrl ctrl;
    private ApplicationBean abMock;
    private ConfigurationService cnfgSrv;

    @Before
    public void setup() throws Exception {
        abMock = mock(ApplicationBean.class);
        cnfgSrv = mock(ConfigurationService.class);

        when(Boolean.valueOf(abMock.isReportingAvailable()))
                .thenReturn(Boolean.TRUE);
        ctrl = new AccountNavigationCtrl() {
            @Override
            protected ConfigurationService getConfigurationService() {
                return cnfgSrv;
            }
        };
        ctrl = spy(ctrl);
        doReturn(Boolean.TRUE).when(ctrl).isLoggedInAndAdmin();
        doReturn(getDefaultHidePaymentConfigurationSetting()).when(cnfgSrv)
                .getVOConfigurationSetting(
                        ConfigurationKey.HIDE_PAYMENT_INFORMATION,
                        Configuration.GLOBAL_CONTEXT);
        ctrl.setApplicationBean(abMock);
        ctrl.setModel(new AccountNavigationModel());
    }

    @Test
    public void getModel() {
        AccountNavigationModel model = ctrl.getModel();
        assertEquals(8, model.getHiddenElement().size());
        assertEquals(9, model.getLink().size());
        assertEquals(9, model.getTitle().size());
        assertEquals("account/index.jsf", model.getLink().get(0));
        assertEquals(AccountNavigationModel.MARKETPLACE_ACCOUNT_TITLE,
                model.getTitle().get(0));
    }

    @Test
    public void getLink() {
        List<String> result = ctrl.getLink();
        assertEquals(9, result.size());
        assertEquals("account/index.jsf", result.get(0));
    }

    @Test
    public void getTitle() {
        List<String> result = ctrl.getTitle();
        assertEquals(9, result.size());
        assertEquals(AccountNavigationModel.MARKETPLACE_ACCOUNT_TITLE,
                result.get(0));
    }

    @Test
    public void getHiddenElement() {
        List<String> result = ctrl.getHiddenElement();
        assertEquals(8, result.size());
        assertEquals(HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_PROFILE,
                result.get(0));
    }

    @Test
    public void isReportingAvailable_visible() {
        doReturn(Boolean.FALSE).when(abMock).isUIElementHidden(
                eq(HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_REPORTS));
        boolean result = ctrl.isReportingAvailable();
        assertEquals(Boolean.TRUE, Boolean.valueOf(result));
    }

    @Test
    public void isReportingAvailable_inVisible() {
        doReturn(Boolean.TRUE).when(abMock).isUIElementHidden(
                eq(HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_REPORTS));
        boolean result = ctrl.isReportingAvailable();
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void isLinkVisible_ProfileInVisible() {
        ctrl.getModel();
        doReturn(Boolean.TRUE).when(abMock).isUIElementHidden(
                eq(HiddenUIConstants.MARKETPLACE_MENU_ITEM_ACCOUNT_PROFILE));
        boolean result = ctrl.isLinkVisible(1);
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void isLinkVisible_Title() {
        ctrl.getModel();
        boolean result = ctrl.isLinkVisible(0);
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void isLinkVisible_SubscriptionMenu() {
        doReturn(Boolean.TRUE).when(ctrl).isLoggedInAndUnitAdmin();
        ctrl.getModel();
        boolean result = ctrl.isLinkVisible(3);
        assertEquals(Boolean.TRUE, Boolean.valueOf(result));
    }

    @Test
    public void isLinkHidden_SubscriptionMenu() {
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndAdmin();
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndSubscriptionManager();
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndUnitAdmin();
        ctrl.getModel();
        boolean result = ctrl.isLinkVisible(3);
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void isLinkVisible_OrgUnits() {
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndAdmin();
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndSubscriptionManager();
        doReturn(Boolean.TRUE).when(ctrl).isLoggedInAndUnitAdmin();
        ctrl.getModel();
        boolean result = ctrl.isLinkVisible(5);
        assertEquals(Boolean.TRUE, Boolean.valueOf(result));
    }

    @Test
    public void isLinkHidden_OrgUnits() {
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndAdmin();
        doReturn(Boolean.TRUE).when(ctrl).isLoggedInAndSubscriptionManager();
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndUnitAdmin();
        ctrl.getModel();
        boolean result = ctrl.isLinkVisible(5);
        assertEquals(Boolean.FALSE, Boolean.valueOf(result));
    }

    @Test
    public void menuSizeForUnitAdmin() {
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndAdmin();
        doReturn(Boolean.FALSE).when(ctrl).isLoggedInAndSubscriptionManager();
        doReturn(Boolean.TRUE).when(ctrl).isLoggedInAndUnitAdmin();
        ctrl.getModel();
        int visibleLinks = 0;
        for (int i = 0; i < ctrl.getLink().size(); i++) {
            if (ctrl.isLinkVisible(i)) {
                visibleLinks++;
            }
        }
        assertEquals(6, visibleLinks);
    }

    @Test
    public void isPaymentAvailable_inVisible() {

        // given
        VOConfigurationSetting setting = new VOConfigurationSetting(
                ConfigurationKey.HIDE_PAYMENT_INFORMATION,
                Configuration.GLOBAL_CONTEXT, "TRUE");
        doReturn(setting).when(cnfgSrv).getVOConfigurationSetting(
                ConfigurationKey.HIDE_PAYMENT_INFORMATION,
                Configuration.GLOBAL_CONTEXT);
        
        // when
        boolean isPaymentAvailable = ctrl.isPaymentAvailable();

        // then
        assertFalse(isPaymentAvailable);
    }

    private VOConfigurationSetting getDefaultHidePaymentConfigurationSetting() {

        return new VOConfigurationSetting(
                ConfigurationKey.HIDE_PAYMENT_INFORMATION,
                Configuration.GLOBAL_CONTEXT, "FALSE");
    }

}
