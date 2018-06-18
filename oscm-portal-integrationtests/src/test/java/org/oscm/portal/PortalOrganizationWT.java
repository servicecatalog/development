/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
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
import org.oscm.webtest.PortalHtmlElements;
import org.oscm.webtest.PortalPathSegments;
import org.oscm.webtest.PortalTester;

/**
 * Integration web test to create an organization.

 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PortalOrganizationWT {


    private static final String ORG = ""+System.currentTimeMillis();
    private static final String ORG_ADMIN = ORG + "_admin";

    private static final int PASSWORD_LENGTH = 8;

    private static PortalTester tester;
    private static String passwordOrgAdmin = "";

    @BeforeClass
    public static void setup() throws Exception {
        tester = new PortalTester();
        String userid=tester.getPropertie(PortalTester.BES_ADMIN_USER_ID);
        String userpassword = tester.getPropertie(PortalTester.BES_ADMIN_USER_PWD);
        tester.loginPortal(userid, userpassword);
    }

    @AfterClass
    public static void cleanUp() {
        tester.logoutPortal();
        tester.close();
    }

    @Test
    public void test01createSupplierOrg() throws Exception {

        tester.visitPortal(PortalPathSegments.CREATE_ORGANIZATION);

        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_ADMINEMAIL,
                tester.getEmailAddress());
        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_DESIRED_USERID, ORG_ADMIN);
        tester.selectDropdown(PortalHtmlElements.CREATE_ORGANIZATION_DROPDOWN_LANGUAGE, "en");

        tester.clickElement(PortalHtmlElements.CREATE_ORGANIZATION_CHECKBOX_TPROVIDER);
        tester.waitForElement(PortalHtmlElements.CREATE_ORGANIZATION_FORM_UPLOADIMAGE, 10);
        tester.clickElement(PortalHtmlElements.CREATE_ORGANIZATION_CHECKBOX_SUPPLIER);
        tester.waitForElement(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_REVENUESHARE, 10);
        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_REVENUESHARE, "5");
        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_ORGNAME, ORG);
        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_ORGEMAIL,
                tester.getEmailAddress());
        tester.selectDropdown(PortalHtmlElements.CREATE_ORGANIZATION_DROPDOWN_ORGLOCALE, "en");
        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_ORGPHONE, "123");
        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_ORGURL, "http://abc.de");
        tester.writeValue(PortalHtmlElements.CREATE_ORGANIZATION_INPUT_ORGADDRESS, "ADDRESS");
        tester.selectDropdown(PortalHtmlElements.CREATE_ORGANIZATION_DROPDOWN_ORGCOUNTRY, "DE");

        tester.clickElement(PortalHtmlElements.CREATE_ORGANIZATION_BUTTON_SAVE);
       
        assertTrue(tester.getPortalExecutionResult());
        PlaygroundSuiteTest.supplierOrgId = tester.readInfoMessage().split(" ")[2];
    }

    @Test
    public void test02readEmailForPassword() throws Exception {

        Thread.sleep(30000);

        String body = tester.readLatestEmailWithSubject(tester.getPropertie("email.createaccount.head"));

        String phrase = tester.getPropertie("email.createaccount.phrase");

        assertNotNull(body);

        int index = body.indexOf(phrase);

        assertTrue(index > 0);

        passwordOrgAdmin = body.substring(index + phrase.length(),
                index + phrase.length() + PASSWORD_LENGTH);
        
        assertTrue(passwordOrgAdmin!="");
        tester.log("password from "+ tester.getEmailAddress() + " is: " + passwordOrgAdmin);
    }

    @Test
    public void test03ChangePassword() throws LoginException, InterruptedException {
//        String passwprd = tester.getPropertie(WebTester.BES_ADMIN_USER_PWD);        

        tester.logoutPortal();
        tester.loginPortal(ORG_ADMIN, passwordOrgAdmin);

        tester.writeValue(PortalHtmlElements.PORTAL_PASSWORD_INPUT_CURRENT, passwordOrgAdmin);
        tester.writeValue(PortalHtmlElements.PORTAL_PASSWORD_INPUT_CHANGE, tester.getPropertie(PortalTester.BES_ADMIN_USER_PWD));
        tester.writeValue(PortalHtmlElements.PORTAL_PASSWORD_INPUT_REPEAT, tester.getPropertie(PortalTester.BES_ADMIN_USER_PWD));

        tester.clickElement(PortalHtmlElements.PORTAL_PASSWORD_BUTTON_SAVE);

        tester.wait(WebTester.IMPLICIT_WAIT);        
        String currentURL = tester.getCurrentUrl();
        assertTrue(currentURL.contains(PortalPathSegments.IMPORT_TECHNICALSERVICE));
    }
}
