/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 03.07.17 14:51
 *
 ******************************************************************************/

package org.oscm.kafka.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;

@Singleton
@Startup
public class ConsumerTrigger {
    @Resource
    TimerService ts;

    @EJB
    KafkaConsumerService consumer;


    @PostConstruct
    public void schedule(){
        ts.createSingleActionTimer(10000, new TimerConfig());
        System.out.println("Setup one-time timer");
    }


    @Timeout
    public void trigger(){
        System.out.println("Timer triggered. invoking consumer....");
        consumer.consume();
    }
}
