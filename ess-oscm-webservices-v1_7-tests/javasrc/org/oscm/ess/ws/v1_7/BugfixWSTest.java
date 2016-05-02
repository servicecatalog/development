/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2016
 *                                                                              
 *  Creation Date: 15.04.2011                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ess.ws.v1_7;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Before;
import org.junit.Test;
import org.oscm.ess.ws.v1_7.base.ServiceFactory;
import org.oscm.ess.ws.v1_7.base.WebserviceTestBase;

import com.fujitsu.bss.vo.VOImageResource;
import com.fujitsu.bss.vo.VOServiceDetails;
import com.fujitsu.bss.intf.ServiceProvisioningService;

public class BugfixWSTest {

    @Before
    public void setUp() throws Exception {
        WebserviceTestBase.getMailReader().deleteMails();
        WebserviceTestBase.getOperator().addCurrency("EUR");
    }

    @Test
    public void testBug7461() throws Exception {

        ServiceProvisioningService serviceProvisioningSrv = ServiceFactory
                .getDefault().getServiceProvisioningService();

        VOServiceDetails sd = new VOServiceDetails();
        VOImageResource ir = new VOImageResource();
        try {
            serviceProvisioningSrv.updateService(sd, ir);
            fail("Call must not succeed!");
        } catch (Exception e) {
            if (e instanceof SOAPFaultException) {
                assertTrue(e.getMessage().contains("AccessException"));
            }
        }
    }

}
