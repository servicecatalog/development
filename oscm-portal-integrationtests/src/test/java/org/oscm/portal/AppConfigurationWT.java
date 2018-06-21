/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2018                                           
 *                                                                                                                                 
 *  Creation Date: 20 6, 2018                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.portal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.oscm.webtest.AppConfigurationTester;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AppConfigurationWT {

    private static AppConfigurationTester tester;
    private static String TEST_CONTROLLER_ID = "ess."
            + System.currentTimeMillis();

    @BeforeClass
    public static void setup() throws Exception {
        tester = new AppConfigurationTester();
        String userid = tester
                .getPropertie(AppConfigurationTester.APP_ADMIN_USER_ID);
        String userpassword = tester
                .getPropertie(AppConfigurationTester.APP_ADMIN_USER_PWD);
        tester.loginAppConfig(userid, userpassword);
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        tester.logoutAppConfig();
        tester.close();

    }

    @Test
    public void test01setConfiguration() throws Exception {

        tester.setAppAdminMailAddress(PlaygroundSuiteTest.supplierOrgAdminMail);
        // BSS_USER_ID from app must be the administrator of platform operator
        tester.setBssUserId(
                tester.getPropertie(AppConfigurationTester.APP_ADMIN_USER_ID));
        tester.setBssUserPwd(
                tester.getPropertie(AppConfigurationTester.APP_ADMIN_USER_PWD));
    }

    @Test
    public void test02createNewControllerId() throws Exception {

        tester.registerController(TEST_CONTROLLER_ID,
                PlaygroundSuiteTest.supplierOrgId);
        PlaygroundSuiteTest.controllerId = TEST_CONTROLLER_ID;

    }

}
