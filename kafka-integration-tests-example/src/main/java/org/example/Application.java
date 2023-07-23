package org.example;


import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.TimeUnit;

@Log4j2
public class Application {

    private KafkaProducer<String, String> kafkaProducer;

    public Application(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public void fireAndWaitForCommit(String value) {
        logger.info("Fire and waiting for commit: $value", value);
        var record = new ProducerRecord("topic", "anyKey", value);
        var future = kafkaProducer.send(record);
        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("Could not produce event", e);
            throw new SendingFailedException(e);
        }
    }

    public void close() {
        kafkaProducer.close();
    }
}
