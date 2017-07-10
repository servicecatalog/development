/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 03.07.17 14:39
 *
 ******************************************************************************/

package org.oscm.kafka.service;

import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

/**
 * Authored by dawidch
 */
@Singleton
@LocalBean
public class KafkaConsumerService {

    private KafkaConsumer<String, String> consumer;

    @PostConstruct
    public void init() {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.30.138:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "oscm-group");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("demo-topic"));
    }

    @PreDestroy
    public void close() {
        consumer.close();
    }

    @Asynchronous
    public void consume() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records)
                    System.out.println(record.offset() + ": " + record.value());
            }
        } finally {
            consumer.close();
        }

    }
}
