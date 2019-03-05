package org.oscm.ws;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oscm.intf.ServiceProvisioningService;
import org.oscm.intf.SubscriptionService;
import org.oscm.types.exceptions.OrganizationAuthoritiesException;
import org.oscm.vo.VOService;
import org.oscm.vo.VOSubscription;
import org.oscm.ws.base.ServiceFactory;
import org.oscm.ws.base.WebserviceTestBase;

public class SubscriptionServiceWSLoadTest {
    
    private ServiceProvisioningService serviceProvisioningService;
    private SubscriptionService subscriptionService;
    
    @BeforeClass
    public void setup() throws Exception{
        
        long userKey = 10000;
        
        serviceProvisioningService = ServiceFactory.getDefault()
                .getServiceProvisioningService(String.valueOf(userKey), WebserviceTestBase.DEFAULT_PASSWORD);
        
        subscriptionService = ServiceFactory.getDefault()
                .getSubscriptionService(String.valueOf(userKey), WebserviceTestBase.DEFAULT_PASSWORD);
        
        //VOSubscription subscription = createSubscription();
        //subscriptionService.subscribeToService(subscription, service, users, paymentInfo, billingContact, udas)
    }
    
    @Test
    public void testSubscriptionIdentifiers() throws OrganizationAuthoritiesException{
        
        List<String> identifiers = subscriptionService.getSubscriptionIdentifiers();
        System.out.println(identifiers);
    }
    
    @Test
    public void testProvisioningService(){
        
        String mpId = "d50e0266";
        List<VOService> services = serviceProvisioningService.getServicesForMarketplace(mpId);
        int size = services.size();
        assertEquals("Wrong number of services:"+ size,2,size);  
        
        for(VOService service:services){
            System.out.println(service.getKey());
            System.out.println(service.getServiceId());
            System.out.println(service.getName());
            System.out.println(service.getTechnicalId());
        }
    }
    
    private VOSubscription createSubscription() {
   
        VOSubscription subscription = new VOSubscription();
        
        String subscriptionId = "LoadSub_"+Long.toHexString(System.currentTimeMillis());
        subscription.setSubscriptionId(subscriptionId);

        return subscription;
    }

}
