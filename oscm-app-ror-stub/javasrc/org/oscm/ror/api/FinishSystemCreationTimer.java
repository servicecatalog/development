/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                                                                                 
 *  Creation Date: 11.02.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.ror.api;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.oscm.ror.EntityCache;

/**
 * @author kulle
 * 
 */
@Stateless
public class FinishSystemCreationTimer {

    @Resource
    private TimerService timerService;

    @Inject
    private EntityCache cache;

    public void finishSystemCreation(CreateLPlatform lplatform) {
        TimerConfig config = new TimerConfig();
        config.setInfo(lplatform.getLplatformId());
        timerService.createSingleActionTimer(60000L, config);
    }

    @Timeout
    public void handleTimeout(Timer timer) {
        String lplatformId = (String) timer.getInfo();
        CreateLPlatform lplatform = cache.findLplatform(lplatformId);
        lplatform.setLplatformStatus("NORMAL");
    }

}
