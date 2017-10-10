/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) FUJITSU LIMITED - ALL RIGHTS RESERVED.                  
 *       
 *  Creation Date: 2017-10-10                                                      
 *                                                                              
 *******************************************************************************/
package org.oscm.app.openstack.billing;


import java.util.Collection;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.oscm.app.openstack.controller.OpenStackController;
import org.oscm.app.openstack.controller.PropertyHandler;
import org.oscm.app.v2_0.APPlatformServiceFactory;
import org.oscm.app.v2_0.data.PasswordAuthentication;
import org.oscm.app.v2_0.data.ProvisioningSettings;
import org.oscm.app.v2_0.data.Setting;
import org.oscm.app.v2_0.intf.APPlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a timer which regularly invokes the billing handler.
 */
@Singleton
@Startup
public class BillingTimerServiceBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BillingTimerServiceBean.class);

    private static final long DEFAULT_TIMER_INTERVAL = 60000;

    /**
     * Used to identify the timer service
     * */
    private static final String TIMER_INFO = "OSCM_OPENSTACK_BILLING_EVENT_GENERATION_TIMER";

    @Resource
    private TimerService timerService;

    private long timerInterval;

    @PostConstruct
    public void initializeTimer() {
        Collection<?> timers = timerService.getTimers();
        if (timers.size() == 0) {
            timerInterval = getTimerInterval();
            timerService.createTimer(0, timerInterval, TIMER_INFO);
        }

        LOGGER.info("Timer is up and running with an interval of "
                + timerInterval);
    }

    /**
     * Handles the timer event.
     */
    @Timeout
    public void handleTimer(Timer timer) {
        adaptTimerToNewInterval();

        try {
            APPDataAccessService das = new APPDataAccessService();
            PasswordAuthentication authentication = das.loadTechnologyProviderCredentials().toPasswordAuthentication();
            APPlatformService platformService = APPlatformServiceFactory
                    .getInstance();
            Collection<String> instanceIds = platformService
                    .listServiceInstances(OpenStackController.ID,
                            authentication);
            for (String id : instanceIds) {
                ProvisioningSettings settings = platformService
                        .getServiceInstanceDetails(
                                OpenStackController.ID, id,
                                authentication);
                
                boolean isTenant = "OS::Keystone::Project".equals(settings.getAttributes().get(PropertyHandler.RESOURCE_TYPE).getValue());
                if(isTenant) {
                    PropertyHandler paramHandler = new PropertyHandler(settings);
                    Setting setting = new Setting(OpenstackBilling.INSTANCE_ID,id);
                    paramHandler.getSettings().getParameters().put(OpenstackBilling.INSTANCE_ID, setting);
                    if (paramHandler.isCharging()) {
                        OpenstackBilling billing = new OpenstackBilling(paramHandler);
                        billing.chargeMonthlyFees();
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("handleTimer", e);
        }
    }

    private void adaptTimerToNewInterval() {
        long newInterval = getTimerInterval();
        if (timerInterval != newInterval) {
            cancelTimer();
            timerInterval = newInterval;
            timerService.createTimer(0, timerInterval, TIMER_INFO);
        }
    }

    private void cancelTimer() {
        Collection<Timer> timers = timerService.getTimers();
        for (Timer th : timers) {
            if (TIMER_INFO.equals(th.getInfo())) {
                th.cancel();
                return;
            }
        }
    }

    /**
     * Returns the timer interval which is stored in the configuration settings.
     * If the setting does not exist, e.g. bootstrapping case, then the default
     * value is returned.
     */
    private long getTimerInterval() {
        long interval = DEFAULT_TIMER_INTERVAL;
        try {
            APPDataAccessService das = new APPDataAccessService();
            HashMap<String,String> controllerSettings = das.getControllerSettings();
            String timerIntervalSetting = controllerSettings.get(PropertyHandler.TIMER_INTERVAL);
            interval = (timerIntervalSetting != null) ? Long
                    .parseLong(timerIntervalSetting) * 1000
                    : DEFAULT_TIMER_INTERVAL;
        } catch (Exception e) {
            // bootstrap case, settings cannot be read yet
            // ignore and use default timer interval instead
        }
        return interval;
    }
}
