/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 10.07.17 07:42
 *
 ******************************************************************************/

package org.oscm.kafka.service;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.oscm.kafka.common.PublishingResult;

/**
 * Authored by dawidch
 */
@Stateless
@LocalBean
public class KafkaProducerServiceBean {
    private static final Logger LOGGER = Logger
            .getLogger(KafkaProducerService.class);
    private KafkaProducer kafkaProducer;

    @PostConstruct
    public void init() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "192.168.30.138:9092");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");

        this.kafkaProducer = new KafkaProducer<>(kafkaProps);
        LOGGER.debug("Kafka producer finished, init()");
    }

    public PublishingResult publishSubscription(String subscriptionJSON) {
        produce(subscriptionJSON);
        return PublishingResult.SUCCESS;
    }

    public void produce(String subscriptionJSON) {
        try {
            kafkaProducer.send(record("demo-topic", subscriptionJSON));
        } catch (Exception e) {
            LOGGER.error("Producer closed");
            e.printStackTrace();
        } finally {
            kafkaProducer.close();
            LOGGER.debug("Producer closed");
        }

    }

    @PreDestroy
    public void close() {
        kafkaProducer.close();
    }

    private ProducerRecord<String, String> record(String topic, String subscriptionJSON) {

        String key = "Action" + "DEPLOYMENT";
        String value = "SubscriptionJSON-" + subscriptionJSON;
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key,
                value);
        return record;
    }
}
