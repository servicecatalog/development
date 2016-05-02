/*******************************************************************************
 *  Copyright FUJITSU LIMITED 2016
 *******************************************************************************/

package org.oscm.ess.ws.v1_7;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oscm.ess.ws.v1_7.base.ServiceFactory;
import org.oscm.ess.ws.v1_7.base.WebserviceTestBase;
import org.oscm.ess.ws.v1_7.base.WebserviceTestSetup;

import com.fujitsu.bss.intf.AccountService;
import com.fujitsu.bss.intf.DiscountService;
import com.fujitsu.bss.types.exceptions.ObjectNotFoundException;
import com.fujitsu.bss.vo.VODiscount;
import com.fujitsu.bss.vo.VOOrganization;
import com.fujitsu.bss.vo.VOServiceDetails;

public class DiscountServiceWSTest {

    private static DiscountService discountService;
    private static AccountService accountService;

    WebserviceTestSetup setup;

    private VOServiceDetails chargeableService;
    private VOServiceDetails freeService;

    private VOOrganization customer;

    @Before
    public void setUp() throws Exception {
        WebserviceTestBase.getMailReader().deleteMails();
        WebserviceTestBase.getOperator().addCurrency("EUR");

        setup = new WebserviceTestSetup();
        setup.createSupplier("Supplier");
        setup.createTechnicalService();

        chargeableService = setup.createService("Service1");
        freeService = (VOServiceDetails) setup
                .createFreeService("FreeService1");

        customer = setup.createCustomer("Customer");

        VODiscount discount = new VODiscount();
        discount.setValue(new BigDecimal("33.24"));
        discount.setStartTime(Long.valueOf(1));
        discount.setEndTime(Long.valueOf(2));
        customer.setDiscount(discount);

        accountService = ServiceFactory.getDefault()
                .getAccountService(setup.getSupplierUserKey(),
                        WebserviceTestBase.DEFAULT_PASSWORD);

        accountService.updateCustomerDiscount(customer);
        // Get the Discount web service of the supplier.
        discountService = ServiceFactory.getDefault().getDiscountService(
                String.valueOf(setup.getCustomerUser().getKey()),
                WebserviceTestBase.DEFAULT_PASSWORD);
    }

    @Test
    public void testGetDiscountForService() throws Exception {
        VODiscount voDiscount = discountService
                .getDiscountForService(chargeableService.getKey());
        Assert.assertEquals(voDiscount.getValue(), new BigDecimal("33.24"));
        Assert.assertEquals(voDiscount.getStartTime(), Long.valueOf(1));
        Assert.assertEquals(voDiscount.getEndTime(), Long.valueOf(2));
    }

    /**
     * Tests the fetching of discount for a free service. Please note that the
     * discount is defined on a customer level, and not a service level. So the
     * returned discount for free of charge services is the discount defined by
     * the supplier (33.24%).
     * 
     * @throws Exception
     */
    @Test
    public void testGetDiscountFreeService() throws Exception {
        VODiscount voDiscount = discountService
                .getDiscountForService(freeService.getKey());
        Assert.assertEquals(voDiscount.getValue(), new BigDecimal("33.24"));
        Assert.assertEquals(voDiscount.getStartTime(), Long.valueOf(1));
        Assert.assertEquals(voDiscount.getEndTime(), Long.valueOf(2));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetCustomerDiscountServiceNotFound() throws Exception {
        long invalidProductKey = 0;
        discountService.getDiscountForService(invalidProductKey);
    }

    @Test
    public void testGetDiscountNotDefined() throws Exception {
        // Get the discount service for the customer for whom no discount has
        // been specified by the supplier
        // Create one customer for whom no discount will be specified
        setup.createCustomer("Customer1");

        discountService = ServiceFactory.getDefault().getDiscountService(
                String.valueOf(setup.getCustomerUser().getKey()),
                WebserviceTestBase.DEFAULT_PASSWORD);

        Assert.assertNull(discountService
                .getDiscountForService(chargeableService.getKey()));
    }

    @Test
    public void testGetDiscountForCustomer() throws Exception {
        // Get the discount service of the supplier.
        discountService = ServiceFactory.getDefault()
                .getDiscountService(setup.getSupplierUserKey(),
                        WebserviceTestBase.DEFAULT_PASSWORD);

        VODiscount voDiscount = discountService.getDiscountForCustomer(customer
                .getOrganizationId());
        Assert.assertEquals(voDiscount.getValue(), new BigDecimal("33.24"));
        Assert.assertEquals(voDiscount.getStartTime(), Long.valueOf(1));
        Assert.assertEquals(voDiscount.getEndTime(), Long.valueOf(2));
    }

    @Test
    public void testGetDiscountOfAnotherSupplier() throws Exception {

        // Create a customer of another supplier.
        setup.createSupplier("Supplier2");
        setup.createCustomer("Customer2");
        // Get the discount service for the customer of the second supplier.
        discountService = ServiceFactory.getDefault().getDiscountService(
                String.valueOf(setup.getCustomerUser().getKey()),
                WebserviceTestBase.DEFAULT_PASSWORD);

        // The chargeable service belongs to the first supplier, so
        // the association of the customer to the first supplier will not be
        // found and a null discount is returned in this case.
        Assert.assertNull(discountService
                .getDiscountForService(chargeableService.getKey()));
    }
}
