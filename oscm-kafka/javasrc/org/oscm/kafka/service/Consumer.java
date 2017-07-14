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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import org.oscm.internal.intf.SubscriptionService;
import org.oscm.internal.types.exception.SaaSSystemException;
import org.oscm.internal.vo.VOInstanceInfo;
import org.oscm.kafka.records.ReleaseRecord;

import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

/**
 * @author stavreva
 *
 */
public class Consumer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Consumer.class);

    private final static String STRING_DESERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringDeserializer";
    private KafkaConsumer<String, String> consumer;
    private SubscriptionService subscriptionService;

    public Consumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                STRING_DESERIALIZER_CLASS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "oscm-group");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                STRING_DESERIALIZER_CLASS);
        consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList("releases"));
        LOGGER.debug("Kafka consumer subscribed to topic.");
    }

    @Override
    public void run() {
        LOGGER.debug("Kafka consumer started.");
        try {
            subscriptionService = findService(SubscriptionService.class);
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
            LOGGER.warn("Malformed JSON: "+ record.value());
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

        try {
            login();
            subscriptionService.completeAsyncSubscription(release.getId(),
                    instanceInfo);
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
            subscriptionService.abortAsyncSubscription(release.getId());
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

    <T> T findService(final Class<T> clazz) {

        try {
            Context context = new InitialContext();
            T service = clazz.cast(context.lookup(clazz.getName()));
            return service;
        } catch (NamingException e) {
            throw new SaaSSystemException("Service lookup failed!", e);
        }
    }

}
