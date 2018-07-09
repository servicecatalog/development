/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2018                                           
 *                                                                                                                                 
 *  Creation Date: Feb 8, 2017                                                      
 *                                                                              
 *******************************************************************************/

    package org.oscm.portal;

    import static org.junit.Assert.assertNotNull;
    import static org.junit.Assert.assertTrue;

    import javax.security.auth.login.LoginException;

    import org.junit.AfterClass;
    import org.junit.BeforeClass;
    import org.junit.FixMethodOrder;
    import org.junit.Test;
    import org.junit.runners.MethodSorters;
    import org.openqa.selenium.By;
    import org.oscm.webtest.PortalHtmlElements;
    import org.oscm.webtest.PortalPathSegments;
    import org.oscm.webtest.PortalTester;
    import org.oscm.webtest.WebTester;

    /**
     * Integration web test to create an organization.

     */
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public class PortalCustomerWT {


        private static final String customerId = "customer_"+System.currentTimeMillis();

        private static final int PASSWORD_LENGTH = 8;
        private static String passwordCustomer = "";

        private static PortalTester tester;

        @BeforeClass
        public static void setup() throws Exception {
            
            tester = new PortalTester();
            String userid=PlaygroundSuiteTest.supplierOrgAdminId;
            String userpassword = PlaygroundSuiteTest.supplierOrgAdminPwd;
            tester.loginPortal(userid, userpassword);
        }

        @AfterClass
        public static void cleanUp() {
            
            tester.logoutPortal();
            tester.close();
        }

        @Test
        public void test01createCustomer() throws Exception {
            tester.visitPortal(PortalPathSegments.REGISTER_CUSTOMER);

            tester.writeValue(PortalHtmlElements.REGISTER_CUSTOMER_INPUT_EMAIL,
                    tester.getEmailAddress());
            tester.writeValue(PortalHtmlElements.REGISTER_CUSTOMER_INPUT_USERID, customerId);
            tester.selectDropdown(PortalHtmlElements.REGISTER_CUSTOMER_DROPDOWN_COUNTRY, "US");
            tester.selectDropdown(PortalHtmlElements.REGISTER_CUSTOMER_DROPDOWN_MARKETPLACE, PlaygroundSuiteTest.supplierOrgId);
            tester.clickElement(PortalHtmlElements.REGISTER_CUSTOMER_BUTTONLINK_SAVE);
           
            assertTrue(tester.getExecutionResult());
            PlaygroundSuiteTest.customerId = customerId;
        }

        @Test
        public void test02readEmailForPassword() throws Exception {
            Thread.sleep(30000);

            String body = tester.readLatestEmailWithSubject(tester.getPropertie("email.createaccount.head"));

            String phrase = tester.getPropertie("email.createaccount.phrase")+ " ";

            assertNotNull(body);

            int index = body.indexOf(phrase);

            assertTrue(index > 0);

            passwordCustomer = body.substring(index + phrase.length(),
                    index + phrase.length() + PASSWORD_LENGTH);
            
            assertTrue(passwordCustomer!="");
            tester.log("password from "+ tester.getEmailAddress() + " is: " + passwordCustomer);
        }

        @Test
        public void test03ChangePassword() throws LoginException, InterruptedException {
            tester.logoutPortal();
            tester.loginPortal(PlaygroundSuiteTest.customerId, passwordCustomer);
            tester.writeValue(PortalHtmlElements.MARKETPLACE_PASSWORD_INPUT_CURRENT, passwordCustomer);
            tester.writeValue(PortalHtmlElements.MARKETPLACE_PASSWORD_INPUT_CHANGE, tester.getPropertie(WebTester.BES_ADMIN_USER_PWD));
            tester.writeValue(PortalHtmlElements.MARKETPLACE_PASSWORD_INPUT_REPEAT, tester.getPropertie(WebTester.BES_ADMIN_USER_PWD));
            tester.clickElement(PortalHtmlElements.MARKETPLACE_PASSWORD_BUTTONLINK_SAVE);
            tester.wait(WebTester.IMPLICIT_WAIT);        
            assertTrue(tester.verifyFoundElement(By.id(PortalHtmlElements.MARKETPLACE_SPAN_WELCOME)));
            PlaygroundSuiteTest.customerPwd = tester.getPropertie(WebTester.BES_ADMIN_USER_PWD);   
        }
}
