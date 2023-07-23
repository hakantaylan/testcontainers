package org.example;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class ProxyAwareKafkaContainer extends KafkaContainer {

    public ProxyAwareKafkaContainer(DockerImageName imageName) {
       super(imageName);
    }

    @Override
    public String getBootstrapServers() {
        return String.format(
                "PLAINTEXT://%s:%s",
                ProxyAwareFailureScenarioTest.kafkaProxy.getContainerIpAddress(),
                ProxyAwareFailureScenarioTest.kafkaProxy.getProxyPort());
    }

}