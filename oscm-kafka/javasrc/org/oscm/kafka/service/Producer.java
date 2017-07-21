/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2017
 *
 *  Creation Date: 10.07.17 07:42
 *
 ******************************************************************************/

package org.oscm.kafka.service;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.oscm.domobjects.Subscription;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.kafka.records.Operation;
import org.oscm.kafka.records.SubscriptionRecord;
import org.oscm.kafka.result.PublishingResult;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Authored by dawidch
 */
public class Producer {
    private static final Logger LOGGER = Logger.getLogger(Producer.class);
    private final static String STRING_SERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringSerializer";
    private final static String TOPIC = "core-subscription";

    private KafkaProducer<String, String> producer;

    public PublishingResult publish(Subscription subscription,
            Operation operation) {
        String subscriptionJson = new SubscriptionRecord(subscription,
                operation).toJson();
        produce(subscriptionJson);
        return PublishingResult.SUCCESS;
    }

    public void produce(String subscriptionJSON) {
        ConfigurationService configService = ServiceLocator
                .findService(ConfigurationService.class);
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                configService.getVOConfigurationSetting(
                        ConfigurationKey.KAFKA_BOOTSTRAP_SERVERS, "global")
                        .getValue());
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                STRING_SERIALIZER_CLASS);
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                STRING_SERIALIZER_CLASS);

        this.producer = new KafkaProducer<>(kafkaProps);
        try {
            producer.send(record(TOPIC, subscriptionJSON));
        } catch (Exception e) {
            LOGGER.error("Producer closed");
            e.printStackTrace();
        } finally {
            producer.close();
            LOGGER.debug("Producer closed");
        }
    }

    private ProducerRecord<String, String> record(String topic, String json) {

        Gson gson = new Gson();
        JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
        String key = jsonObj.get("id").getAsString();
        String value = json;
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key,
                value);
        return record;
    }
}
