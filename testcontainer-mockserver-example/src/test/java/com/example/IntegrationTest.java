package com.example;

import com.example.repository.CustomerRepository;
import com.example.service.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int localServerPort;
    private RestTemplate restTemplate;
    private MockServerClient emailService;

    @Autowired
    private CustomerRepository repository;

    @BeforeAll
    public static void beforeAll() {
        Containers.ensureRunning();
    }

    @BeforeEach
    public void before() {
        restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localServerPort)
                .build();
        emailService = new MockServerClient(Containers.EMAIL_SERVICE.getHost(), Containers.EMAIL_SERVICE.getServerPort());
        emailService.when(HttpRequest.request("/send"))
                .respond(HttpResponse.response().withStatusCode(204));
    }

    @Test
    public void integrationTest() {
        // Given
        var email = "jessy@example.com";
        var name = "Jessy";
        // When
        var request = Map.ofEntries(
                Map.entry("email", email),
                Map.entry("name", name)
        );
        restTemplate.postForEntity("/customers", request, Void.class);
        // Then
        emailService.verify(HttpRequest.request()
                .withPath("/send")
                .withMethod("POST")
                .withHeader(Header.header("Content-Type", "application/json"))
                .withBody(JsonBody.json(new EmailService.SendEmailRequest("WELCOME", email)))
        );
        var customer = repository.findByEmail(email).orElseThrow();
        assertThat(customer.getEmail()).isEqualTo(email);
        assertThat(customer.getName()).isEqualTo(name);
    }

    @AfterEach
    public void after() {
        repository.deleteAll();
        emailService.stop();
    }

}
