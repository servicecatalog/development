/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016
 *******************************************************************************/

package org.oscm.ess.ws.v1_7.billing;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oscm.ess.ws.v1_7.base.ServiceFactory;
import org.oscm.ess.ws.v1_7.base.WebserviceTestBase;
import org.oscm.ess.ws.v1_7.base.WebserviceTestSetup;

import com.fujitsu.bss.intf.BillingService;

/**
 * Integration tests for BillingServiceWS.
 * 
 * @author barzu
 */
public class BillingServiceWSTest {

    private static WebserviceTestSetup setup;

    @BeforeClass
    public static void setUpOnce() throws Exception {

        WebserviceTestBase.getMailReader().deleteMails();
        WebserviceTestBase.getOperator().addCurrency("EUR");

        setup = new WebserviceTestSetup();
        setup.createSupplier("Supplier");
    }

    @Test
    public void getCustomerBillingData() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        long start = calendar.getTimeInMillis();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        long end = calendar.getTimeInMillis();

        BillingService ws = ServiceFactory.getDefault()
                .getBillingService(setup.getSupplierUserKey(),
                        WebserviceTestBase.DEFAULT_PASSWORD);
        String result = new String(ws.getCustomerBillingData(
                Long.valueOf(start), Long.valueOf(end), null));

        // can only test that an empty result is returned, because billing
        // results are generated first at the end of the month
        assertTrue(result.contains("<Billingdata"));
    }

}
