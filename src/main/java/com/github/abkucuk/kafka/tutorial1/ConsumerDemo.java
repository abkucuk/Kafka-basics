package com.github.abkucuk.kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.jvm.hotspot.oops.Array;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemo {
    public static void main(String[] args) {
        String bootstrapServer = "127.0.0.1:9092";
        Logger logger = LoggerFactory.getLogger(ConsumerDemo.class.getName());
        String groupId = "my-fourth-application";
        String AUTO_OFFSET_RESET_CONFIG = "earliest"; // or it can be "latest" or "none"
        String topic = "first_topic";

        // create consumer congfig
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_CONFIG);

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        //subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList(topic));

        while(true){
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : consumerRecords) {
                logger.info("Key: " + record.key() + ";" + "Value: " + record.value());
                logger.info("Partitions: " + record.partition() + " Offset: " + record.offset());
            }
        }
    }
}
