package com.testcontainerspringboot.hero.universum;

import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@ContextConfiguration(initializers = {DatabaseBaseTest.Initializer.class})
public abstract class DatabaseBaseTest {
    static final MySQLContainer DATABASE = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.mysql")));

    static {
        DATABASE.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                            "spring.datasource.url=" + DATABASE.getJdbcUrl(),
                            "spring.datasource.username=" + DATABASE.getUsername(),
                            "spring.datasource.password=" + DATABASE.getPassword(),
                            "spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver",
                            "spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect")
                    .applyTo(applicationContext);
        }
    }
}
