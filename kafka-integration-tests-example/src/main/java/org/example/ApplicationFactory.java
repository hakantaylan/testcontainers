package org.example;

public class ApplicationFactory {

    public Application createApplication(String bootstrapServers) {
        var kafkaClientFactory = new KafkaClientFactory();
        var kafkaProducer = kafkaClientFactory.createKafkaProducer(bootstrapServers);
        return new Application(kafkaProducer);
    }
}
