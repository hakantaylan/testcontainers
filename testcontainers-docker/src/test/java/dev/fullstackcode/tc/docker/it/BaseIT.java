package dev.fullstackcode.tc.docker.it;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
public class BaseIT {

    @Autowired
    protected TestRestTemplate testRestTemplate;

    private static int POSTGRES_PORT = 5432;

    static final DockerComposeContainer environment;

    static {
        environment =
                new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yaml"))
                        .withExposedService("postgres", POSTGRES_PORT, Wait.forListeningPort())
                        .withLogConsumer("postgres", new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.postgres")));
//                        .waitingFor("postgres", Wait.forLogMessage("started", 1))
//                        .withLocalCompose(true);

        environment.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopContainer()));

    }

    private static void stopContainer() {
        environment.stop();
    }


    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {

        String postgresUrl = environment.getServiceHost("postgres", POSTGRES_PORT)
                + ":" +
                environment.getServicePort("postgres", POSTGRES_PORT);

        registry.add("spring.datasource.url", () -> "jdbc:postgresql://" + postgresUrl + "/eis");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");

    }
}