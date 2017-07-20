/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017                                           
 *                                                                                                                                 
 *  Creation Date: 14.07.2017                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.kafka.service;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.oscm.domobjects.Subscription;
import org.oscm.internal.intf.ConfigurationService;
import org.oscm.internal.intf.SubscriptionService;
import org.oscm.internal.types.enumtypes.ConfigurationKey;
import org.oscm.internal.types.enumtypes.SubscriptionStatus;
import org.oscm.internal.vo.VOInstanceInfo;
import org.oscm.internal.vo.VOSubscription;
import org.oscm.kafka.records.ReleaseRecord;
import org.oscm.subscriptionservice.local.SubscriptionServiceLocal;

import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

/**
 * @author stavreva
 *
 */
public class Consumer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Consumer.class);

    private final static String STRING_DESERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringDeserializer";
    private final static String TOPIC = "releases";
    private final static String CONSUMER_GROUP = "oscm-group";
    private KafkaConsumer<String, String> consumer;
    private SubscriptionService subscriptionService;

    public Consumer() {
        ConfigurationService configService = ServiceLocator
                .findService(ConfigurationService.class);
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                configService.getVOConfigurationSetting(
                        ConfigurationKey.KAFKA_BOOTSTRAP_SERVERS, "global")
                        .getValue());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                STRING_DESERIALIZER_CLASS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                STRING_DESERIALIZER_CLASS);
        consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(TOPIC));
        LOGGER.debug("Kafka consumer subscribed to topic.");
    }

    @Override
    public void run() {
        LOGGER.debug("Kafka consumer started.");
        try {
            subscriptionService = ServiceLocator
                    .findService(SubscriptionService.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        while (true) {
            try {
                final ConsumerRecords<String, String> consumerRecords = consumer
                        .poll(1000);

                consumerRecords.forEach(record -> {
                    System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                            record.key(), record.value(), record.partition(),
                            record.offset());
                    processRecord(record);
                });

                consumer.commitAsync();
            } catch (Throwable t) {
                consumer.commitAsync();
                t.printStackTrace();
            }
        }
    }

    public void processRecord(ConsumerRecord<String, String> record) {

        ReleaseRecord release = ReleaseRecord
                .fromJson(record.value().toString());
        if (release == null) {
            LOGGER.warn("Malformed JSON: " + record.value());
            return;
        }

        ReleaseRecord.Status status = release.getStatus();
        switch (status) {
        case DEPLOYED:
            completeSubscription(release);
            break;
        case PENDING:
        case CREATING:
        case DELETING:
        case UPDATING:
            progressSubscription(release);
            break;
        case FAILED:
        case DELETED:
            abortSubscription(release);
            break;
        default:
            break;
        }
    }

    /**
     * @param release
     */
    void completeSubscription(ReleaseRecord release) {
        VOInstanceInfo instanceInfo = new VOInstanceInfo();
        instanceInfo.setInstanceId(release.getInstance().toString());
        instanceInfo.setAccessInfo(release.getServices().get("endpoint"));

        VOSubscription subscription = subscriptionService
                .getSubscription(release.getId());
        try {
            login();
            if (SubscriptionStatus.PENDING.equals(subscription.getStatus()))
                subscriptionService.completeAsyncSubscription(release.getId(),
                        instanceInfo);
            else if (SubscriptionStatus.PENDING_UPD
                    .equals(subscription.getStatus())) {
                subscriptionService.completeAsyncModifySubscription(
                        release.getId(), instanceInfo);
            } else {
                // TODO error
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param release
     */
    void abortSubscription(ReleaseRecord release) {
        try {
            login();
            VOSubscription subscription = subscriptionService
                    .getSubscription(release.getId());

            if (SubscriptionStatus.PENDING.equals(subscription.getStatus())) {
                subscriptionService.abortAsyncSubscription(release.getId());
            }  else if (SubscriptionStatus.PENDING_UPD
                    .equals(subscription.getStatus())) {
                //TODO get reason, failure...
                subscriptionService.abortAsyncModifySubscription(release.getId(), "some reason");
            } else {
                //TODO error
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param release
     */
    void progressSubscription(ReleaseRecord release) {
        try {
            login();
            subscriptionService.updateAsyncSubscriptionProgress(release.getId(),
                    release.getStatus().name());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @throws Exception
     */
    void login() throws Exception {
        ProgrammaticLogin prlogin = new ProgrammaticLogin();
        // TODO dedicated kafka user? how to solve password?
        boolean loginOutcome = prlogin
                .login("1000", ("admin123").toCharArray(), "bss-realm", false)
                .booleanValue();
        if (loginOutcome == false) {
            LOGGER.info("Consumer cannot login.");
        }
    }
}
