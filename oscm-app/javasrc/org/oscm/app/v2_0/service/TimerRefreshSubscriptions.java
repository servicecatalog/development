/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 2017-05-05
 *
 *******************************************************************************/
package org.oscm.app.v2_0.service;

import org.oscm.app.business.APPlatformControllerFactory;
import org.oscm.app.domain.PlatformConfigurationKey;
import org.oscm.app.domain.ServiceInstance;
import org.oscm.app.v2_0.exceptions.APPlatformException;
import org.oscm.app.v2_0.exceptions.ConfigurationException;
import org.oscm.app.v2_0.intf.APPlatformController;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;


/**
 * Created by BadziakP on 2017-05-04.
 *
 * This class is a timer which gets number of VMs from the cloud platform and
 * updates respective subscription parameters
 */

@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Singleton
@LocalBean
@Startup
public class TimerRefreshSubscriptions {

    @Inject
    protected ServiceInstanceServiceBean serviceInstanceService;

    @Resource
    protected TimerService timerService;

    @EJB
    protected APPConfigurationServiceBean configService;

    @Inject
    protected transient Logger logger;

    @PostConstruct
    public void setTimer() {
        try {
            String timerIntervalSetting = configService.getProxyConfigurationSetting(
                    PlatformConfigurationKey.APP_TIMER_REFRESH_SUBSCRIPTIONS);
            long interval = Long.parseLong(timerIntervalSetting);
            timerService.createIntervalTimer(new Date(), interval,
                    new TimerConfig());
        } catch (ConfigurationException e) {
            logger.info("Timer for refreshing subscriptions not set");
        }
    }

    @Timeout
    public void execute() {
        List<ServiceInstance> instances = serviceInstanceService.getInstances();
        try {
            for (ServiceInstance serviceInstance : instances) {
                final APPlatformController controller = APPlatformControllerFactory
                        .getInstance(serviceInstance.getControllerId());

                Integer vmsNumber = controller.getServersNumber(serviceInstance.getInstanceId(),
                        serviceInstance.getSubscriptionId(),
                        serviceInstance.getOrganizationId());
                if (vmsNumber == null) {
                    continue;
                }
                ServiceInstance updatedServiceInstance = serviceInstanceService.updateVmsNumber(serviceInstance,
                    vmsNumber);
                serviceInstanceService.notifySubscriptionAboutVmsNumber(updatedServiceInstance);
            }
        } catch (APPlatformException e) {
            e.printStackTrace();
        }
    }

}
