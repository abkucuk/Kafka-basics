package com.github.abkucuk.kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoThread {
    public static void main(String[] args) {

        new ConsumerDemoThread().run();




    }
    private ConsumerDemoThread(){

    }
    private   void run (){
        String bootstrapServer = "127.0.0.1:9092";
        Logger logger = LoggerFactory.getLogger(ConsumerDemoThread.class.getName());
        String groupId = "my-sixth-application";
        String AUTO_OFFSET_RESET_CONFIG = "earliest"; // or it can be "latest" or "none"
        String topic = "first_topic";
        CountDownLatch latch = new CountDownLatch(1);
        logger.info("creating consumer thread");
        Runnable myConsumerRunnable = new ConsumerRunnable(
                latch,
                bootstrapServer,
                groupId,
                topic
        );
        Thread myThread = new Thread(myConsumerRunnable);
        myThread.start();

        // add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    logger.info("cought shutdown hook ");
                    ((ConsumerRunnable)myConsumerRunnable).shutdown();
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("application has excited");
                }
        ));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("App igot nterrupt", e);
        }finally {
            logger.info("Application is closing ! ");
        }
    }
    public class ConsumerRunnable implements Runnable {
        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;
        private Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class.getName());

        public ConsumerRunnable(CountDownLatch latch, String bootstrapServer, String groupId, String topic) {
            this.latch = latch;
            // create consumer congfig
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            // create consumer
            consumer = new KafkaConsumer<String, String>(properties);

            //subscribe consumer to our topic(s)
            consumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run() {

           try{
               while(true){
                   ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

                   for (ConsumerRecord<String, String> record : consumerRecords) {
                       logger.info("Key: " + record.key() + ";" + "Value: " + record.value());
                       logger.info("Partitions: " + record.partition() + " Offset: " + record.offset());
                   }
               }
           }catch(WakeupException e){
               logger.info("received shutdown signal !");
           }finally {
               consumer.close();
               latch.countDown();
           }
        }

        public void shutdown(){
            // the wakeup method is a special method to interrupt consumer.poll()
            consumer.wakeup();
        }
    }
}
