package org.example;

import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FailureScenarioTest {
    private Network network = Network.newNetwork();

    KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
            .withKraft()
            .withExposedPorts(9093)
            .withNetwork(network)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.kafka")));

    ToxiproxyContainer toxiproxy = new ToxiproxyContainer()
            .withNetwork(network)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.toxiproxy")));

    ToxiproxyContainer.ContainerProxy kafkaProxy;

    Application application;

    @BeforeAll
    void setupContainers() {
        toxiproxy.start();
        kafkaProxy = toxiproxy.getProxy(kafka, 9093);
        kafka.start();
    }

    @BeforeEach
    public void init(){
        application = createApplication();
    }

    @AfterAll
    void cleanUp() {
        application.close();
    }

    private Application createApplication() {
        var applicationFactory = new ApplicationFactory();
        var kafkaProxyIp = kafkaProxy.getContainerIpAddress();
        var kafkaProxyPort = kafkaProxy.getProxyPort();
        return applicationFactory.createApplication(kafkaProxyIp + ":" + kafkaProxyPort);
    }

    @Test
    public void should_throw_exception_when_committing_failed() {
        // given
        String event = "anyEventValue";
        assertDoesNotThrow(() -> application.fireAndWaitForCommit(event));
        kafkaProxy.setConnectionCut(true);
        assertThrows(SendingFailedException.class, () -> application.fireAndWaitForCommit(event));
    }

    @AfterEach
    public void resetToxiProxy() {
        kafkaProxy.setConnectionCut(false);
    }
}
