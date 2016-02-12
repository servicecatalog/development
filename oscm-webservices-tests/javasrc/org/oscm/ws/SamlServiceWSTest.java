/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016                                             
 *                                                                                                                                 
 *  Creation Date: 2013-1-10                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ws;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import org.oscm.ws.base.ServiceFactory;
import org.oscm.ws.base.WebserviceTestSetup;
import org.oscm.intf.SamlService;

/**
 * @author Wenxin Gao
 * 
 */
public class SamlServiceWSTest {
    private static SamlService samlService;
    private WebserviceTestSetup setup;

    @Before
    public void setUp() throws Exception {
        setup = new WebserviceTestSetup();
        setup.createSupplier("Supplier1");
        samlService = ServiceFactory.getDefault().getSamlService();
    }

    @Test
    public void createSamlResponse() {
        String result = samlService.createSamlResponse("test");
        assertNotNull(result);
    }
}
