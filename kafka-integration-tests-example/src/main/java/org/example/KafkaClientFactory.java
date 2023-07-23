package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

public class KafkaClientFactory {

    public KafkaProducer<String, String> createKafkaProducer(String bootstrapServers)  {
        Map<String, Object> properties = new HashMap<>();

        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("request.timeout.ms", 3000);
        properties.put("delivery.timeout.ms", 3000);
        properties.put("retries", 0);
        properties.put("max.block.ms", 3000);
        properties.put("linger.ms", 0);
        properties.put("batch.size", 1);
        properties.put("max.metadata.age.ms", 1000);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer(properties);
    }
}
