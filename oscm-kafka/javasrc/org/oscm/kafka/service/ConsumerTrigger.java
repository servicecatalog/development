/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 03.07.17 14:51
 *
 ******************************************************************************/

package org.oscm.kafka.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;

@Singleton
@Startup
public class ConsumerTrigger {

    private static final Logger LOGGER = Logger
            .getLogger(ConsumerTrigger.class);

    ExecutorService executor = null;

    @PostConstruct
    public void regular() {
        if (isKafkaEnabled()) {
            executor = Executors.newSingleThreadExecutor();
            executor.execute(new Consumer());
            LOGGER.info("Kafka consumer job started");
        }
    }

    @PreDestroy
    public void stop() {
        if (executor == null) {
            return;
        }
        
        LOGGER.debug("Kafka consumer job canceling...");
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
            LOGGER.debug("Kafka consumer shutdown finished");
        }
    }

    private boolean isKafkaEnabled() {
        ConfigurationService configService = ServiceLocator
                .findService(ConfigurationService.class);
        String kafkaServer = configService
                .getVOConfigurationSetting(
                        ConfigurationKey.KAFKA_BOOTSTRAP_SERVERS, "global")
                .getValue();
        if (kafkaServer == null || kafkaServer.trim().isEmpty()) {
            return false;
        }

        return true;
    }
}
