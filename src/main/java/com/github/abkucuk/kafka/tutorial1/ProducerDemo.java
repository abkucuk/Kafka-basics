package com.github.abkucuk.kafka.tutorial1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(ProducerDemo.class);

        // create new producer properties
        // go to kafka.apache.org/documentation/#producerconfigs
        // old school => properties.setProperty("bootstrap.servers","localhost:9092");
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        // old school way => properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // old school way => properties.setProperty("value.serializer", StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);

        for (int i=0; i < 10; i++) {
            // create a producer record
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<String, String>("first_topic", "hello world" + Integer.toString(i));

            // send data
            producer.send(producerRecord, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes every time a record successfully sent or exepciton a thrown

                    if (e == null) { //the record was successfully send
                        logger.info(
                                "Received metadata . \n" +
                                        "Topic : " + recordMetadata.topic() + "\n" +
                                        "partition : " + recordMetadata.partition() + "\n" +
                                        "offset : " + recordMetadata.offset() + "\n" +
                                        "Timestamp :" + recordMetadata.timestamp() + "\n");
                    } else {
                        logger.error("Error while producing : ", e);
                    }
                }
            });
        }
        producer.flush();
        producer.close();

    }
}
