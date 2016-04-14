/*******************************************************************************
 *                                                                              
 *  COPYRIGHT (C) FUJITSU LIMITED - ALL RIGHTS RESERVED.                  
 *       
 *  Creation Date: 2014-05-20                                                       
 *                                                                              
 *******************************************************************************/
package org.oscm.app.vmware.business.trigger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.oscm.vo.VOService;
import org.oscm.vo.VOTriggerProcess;

public class ServiceValidationTask {

    private static ExecutorService executor = Executors.newFixedThreadPool(1);

    public ServiceValidationTask() {
    }

    public void validate(VOTriggerProcess process, VOService product) {
        ServiceValidationThread thread = new ServiceValidationThread(process,
                product);
        executor.submit(thread);
    }

}
