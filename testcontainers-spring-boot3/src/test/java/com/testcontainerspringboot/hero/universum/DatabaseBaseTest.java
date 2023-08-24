package com.testcontainerspringboot.hero.universum;

import org.slf4j.LoggerFactory;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;


public abstract class DatabaseBaseTest {
    @ServiceConnection
    static final MySQLContainer DATABASE = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.mysql")));

    static {
        DATABASE.start();
    }

//    @DynamicPropertySource
//    static void databaseProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
//        registry.add("spring.datasource.username", DATABASE::getUsername);
//        registry.add("spring.datasource.password", DATABASE::getPassword);
////        registry.add("spring.datasource.driverClassName", ()-> "com.mysql.cj.jdbc.Driver");
////        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
//    }
}
