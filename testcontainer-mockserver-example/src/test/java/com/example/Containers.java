package com.example;

import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class Containers {

    public static PostgreSQLContainer POSTGRES;
    public static MockServerContainer EMAIL_SERVICE;
    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2");

    public static void ensureRunning() {
        postgresEnsureRunning();
        emailServiceEnsureRunning();
    }

    private static void postgresEnsureRunning() {
        if (POSTGRES == null) {
            POSTGRES = new PostgreSQLContainer<>("postgres:latest")
                    .withUsername("admin")
                    .withPassword("password")
                    .withDatabaseName("postgres");
        }
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
        System.setProperty("spring.datasource.url", POSTGRES.getJdbcUrl());
        System.setProperty("spring.datasource.username", "admin");
        System.setProperty("spring.datasource.password", "password");
    }

    private static void emailServiceEnsureRunning() {
        if (EMAIL_SERVICE == null) {
            EMAIL_SERVICE = new MockServerContainer(MOCKSERVER_IMAGE);
        }
        if (!EMAIL_SERVICE.isRunning()) {
            EMAIL_SERVICE.start();
        }
        System.setProperty("services.email.base-url", EMAIL_SERVICE.getEndpoint());
    }

}
