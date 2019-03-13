package org.oscm.ws;

import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;
import org.oscm.intf.ServiceProvisioningService;
import org.oscm.intf.SubscriptionService;
import org.oscm.types.exceptions.ObjectNotFoundException;
import org.oscm.vo.VOParameter;
import org.oscm.vo.VOService;
import org.oscm.vo.VOSubscription;
import org.oscm.vo.VOSubscriptionDetails;
import org.oscm.vo.VOUda;
import org.oscm.ws.base.ServiceFactory;
import org.oscm.ws.base.WebserviceTestBase;



public class SubscriptionServiceWSLoadTest {

  private static ServiceProvisioningService serviceProvisioningService;
  private static SubscriptionService subscriptionService;

  private static final String SUBSCRIPTION_ID_PREFIX = "TestLoadSub_";
  private static final String MARKETPLACE_ID = "ef95bfbc";
  private static final String SERVICE_ID = "AppSampleId";

  private static final long NO_OF_SUBS = 200;
  private static final long USER_KEY = 10000;

  @BeforeClass
  public static void setup() throws Exception {

    serviceProvisioningService =
        ServiceFactory.getDefault()
            .getServiceProvisioningService(
                String.valueOf(USER_KEY), WebserviceTestBase.DEFAULT_PASSWORD);

    subscriptionService =
        ServiceFactory.getDefault()
            .getSubscriptionService(String.valueOf(USER_KEY), WebserviceTestBase.DEFAULT_PASSWORD);
  }

  @Test
  public void testCreateAndModifySubscriptions() throws Exception {

    for (int i = 0; i < NO_OF_SUBS; i++) {
      createSubscription(SUBSCRIPTION_ID_PREFIX + i);
    }

    System.out.println("Creation of " + NO_OF_SUBS + " subscriptions finished");
    System.out.println("Waiting for background asynchronous task to be completed");
    Thread.sleep(120000);

    Map<String, Exception> failedMap = new HashMap<String, Exception>();  
    for (int i = 0; i < NO_OF_SUBS; i++) { 
        try {
            modifySubscription(SUBSCRIPTION_ID_PREFIX + i);        
        } catch (Exception ex) {
            failedMap.put(SUBSCRIPTION_ID_PREFIX + i, ex);
        }
      
    }
    if (!failedMap.isEmpty()) {
       
        StringBuffer msg = new StringBuffer();
        failedMap.forEach((key, e) -> {
            msg.append("\n" + key );
            msg.append(" : ");
            msg.append(e.getMessage());
        });
        
        fail(msg.toString());
    }
    System.out.println("Modification of " + NO_OF_SUBS + " subscriptions finished");
  }

  public VOSubscription modifySubscription(String subscriptionId) throws Exception {

    VOSubscriptionDetails subscription = subscriptionService.getSubscriptionDetails(subscriptionId);
    List<VOParameter> parameters = subscription.getSubscribedService().getParameters();

    List<VOParameter> updatedParams =
        parameters
            .stream()
            .filter(param -> param.getParameterDefinition().getParameterId().equals("PARAM_PWD"))
            .map(
                param -> {
                  param.setValue("changedpasswd");
                  return param;
                })
            .collect(Collectors.toList());

    
    VOSubscriptionDetails modifiedSubscription =
        subscriptionService.modifySubscription(subscription, updatedParams, new ArrayList<VOUda>());

    return modifiedSubscription;
  }

  public VOSubscription createSubscription(String subscriptionId) throws Exception {

    VOSubscription subscription = prepareSubscription(subscriptionId, false);
    VOService sampleService = getSampleService(MARKETPLACE_ID, SERVICE_ID);
    VOSubscription createdSubscription =
        subscriptionService.subscribeToService(
            subscription, sampleService, null, null, null, new ArrayList<VOUda>());
    return createdSubscription;
  }

  private static VOService getSampleService(String mpId, String serviceId)
      throws ObjectNotFoundException {

    List<VOService> services = serviceProvisioningService.getServicesForMarketplace(mpId);

    for (VOService service : services) {
      if (serviceId.equals(service.getServiceId())) {
        return service;
      }
    }

    throw new ObjectNotFoundException(
        "Service[" + serviceId + "] not found in marketplace[" + mpId + "]");
  }

  private static VOSubscription prepareSubscription(String subscriptionId, boolean dateTimeSuffix) {

    VOSubscription subscription = new VOSubscription();

    LocalDateTime localDateTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
    String dateTime = localDateTime.format(formatter);

    if (dateTimeSuffix) {
      subscriptionId += "_" + dateTime;
    }
    subscription.setSubscriptionId(subscriptionId);

    return subscription;
  }
}
