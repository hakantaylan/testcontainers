package org.example;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SunnyDayScenarioTest {

    private KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withKraft()
            .withExposedPorts(9093)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.kafka")));

    private Application application = null;

    @BeforeEach
    public void setup() {
        kafka.start();
    }

    @AfterEach
    public void cleanup() {
        if (application != null)
            application.close();
        kafka.stop();
    }

    @Test
    public void should_send_events_to_kafka_topic() throws ExecutionException, InterruptedException {
        // given
        application = createApplication(kafka.getBootstrapServers());
        createTopic(kafka.getBootstrapServers());

        String event = "anyEventValue";
        KafkaConsumer consumer = createKafkaConsumer(kafka.getBootstrapServers());

        application.fireAndWaitForCommit(event);

        // when
        ConsumerRecords<String, String> events = consumer.poll(Duration.ofSeconds(3));

        // then
        String value = events.iterator().next().value();
        assertEquals(value, event);
    }

    private Application createApplication(String bootstrapServers) {
        var applicationFactory = new ApplicationFactory();
        return applicationFactory.createApplication(bootstrapServers);
    }

    private KafkaConsumer<String, String> createKafkaConsumer(String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("max.poll.size", 1);
        properties.put("group.id", "sunnyDayScenarioGroup");
        properties.put("max.metadata.age.ms", 1000);
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        var consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(List.of("topic"));
        return consumer;
    }

    private void createTopic(String bootstrapServers) throws ExecutionException, InterruptedException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", bootstrapServers);

        var adminClient = AdminClient.create(properties);
        adminClient.createTopics(List.of(new NewTopic("topic", 1, (short) 1))).all().get();
    }
}
