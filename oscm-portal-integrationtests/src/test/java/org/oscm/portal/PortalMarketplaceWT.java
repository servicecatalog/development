/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: Feb 8, 2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.portal;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.oscm.webtest.PortalHtmlElements;
import org.oscm.webtest.PortalPathSegments;
import org.oscm.webtest.WebTester;

/**
 * Integration web test to create an new marketplace.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PortalMarketplaceWT {
    

    private static final String MARKETPLACE = "mp_test";
    
    private String marketplaceId;
    
    private static WebTester tester;
    
    @BeforeClass
    public static void setup() throws Exception {
        tester = new WebTester();
        String userid=tester.getPropertie(WebTester.BES_ADMIN_USER_ID);
        String userpassword = tester.getPropertie(WebTester.BES_ADMIN_USER_PWD);
        tester.loginPortal(userid, userpassword);
    }

    @AfterClass
    public static void cleanUp() {
        tester.logoutPortal();
        tester.close();
    }

    @Test
    public void create() throws Exception {
        marketplaceId = "";
      
        tester.visitPortal(PortalPathSegments.CREATE_MARKETPLACE);

        tester.writeValue(PortalHtmlElements.CREATE_MARKETPLACE_INPUT_NAME, MARKETPLACE);
        tester.writeValue(PortalHtmlElements.CREATE_MARKETPLACE_INPUT_ORG_ID,
                PlaygroundSuiteTest.supplierOrgId);

        tester.clickElement(PortalHtmlElements.CREATE_MARKETPLACE__BUTTON_SAVE);

        assertTrue(tester.getPortalExecutionResult());
        
    }
    
//    @Test
    public void remove() throws Exception {
        
        marketplaceId = tester.getCreatedId(tester.readInfoMessage());
        
        if(marketplaceId == null || marketplaceId == "")
            throw new Exception("Marketplace " + MARKETPLACE + " doesn't exists!");

        
        tester.visitPortal(PortalPathSegments.DELETE_MARKETPLACE);
        tester.selectDropdown(PortalHtmlElements.DELETE_MARKETPLACE_DROPDOWN_IDLIST, marketplaceId);
        tester.waitForElement(PortalHtmlElements.DELETE_MARKETPLACE_BUTTON_DELETE, 10);        
        tester.clickElement(PortalHtmlElements.DELETE_MARKETPLACE_BUTTON_DELETE);
        tester.waitForElement(PortalHtmlElements.DELETE_MARKETPLACE_BUTTON_CONFIRM, 10);        
        tester.clickElement(PortalHtmlElements.DELETE_MARKETPLACE_BUTTON_CONFIRM);

        assertTrue(tester.getPortalExecutionResult());
    }

}
