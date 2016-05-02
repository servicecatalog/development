/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016
 *                                                                                                                                 
 *  Creation Date: 2013-1-10                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ess.ws.v1_7;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.oscm.ess.ws.v1_7.base.ServiceFactory;
import org.oscm.ess.ws.v1_7.base.WebserviceTestSetup;

import com.fujitsu.bss.intf.SamlService;

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
