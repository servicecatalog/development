package org.oscm.portal;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.oscm.webtest.AppTester;
import org.oscm.webtest.PortalTester;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class AppConfigurationWT {

    private static AppTester tester;
    
    @BeforeClass
    public static void setup() throws Exception {
        tester = new AppTester();
        String userid=tester.getPropertie(AppTester.APP_ADMIN_USER_ID);
        String userpassword = tester.getPropertie(AppTester.APP_ADMIN_USER_PWD);
        tester.loginApp(userid, userpassword);
    }

    @AfterClass
    public static void cleanUp() throws Exception {
        tester.logoutApp();
        tester.close();
    }

    @Test
    public void test01createNewControllerId() throws Exception {
        
    }    
    
    
}
