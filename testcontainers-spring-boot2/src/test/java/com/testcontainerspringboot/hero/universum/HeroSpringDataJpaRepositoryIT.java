package com.testcontainerspringboot.hero.universum;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ContextConfiguration(initializers = {HeroSpringDataJpaRepositoryIT.Initializer.class})
class HeroSpringDataJpaRepositoryIT {

    @Container
    private static MySQLContainer database = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.mysql")));

    @Autowired
    private HeroSpringDataJpaRepository repositoryUnderTest;

    @Test
    void findHerosBySearchCriteria() {
        repositoryUnderTest.save(new Hero("Batman", "Gotham City", ComicUniversum.DC_COMICS));

        Collection<Hero> heros = repositoryUnderTest.findHerosBySearchCriteria("Batman");

        assertThat(heros).hasSize(1).contains(new Hero("Batman", "Gotham City", ComicUniversum.DC_COMICS));
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(
                            "spring.datasource.url=" + database.getJdbcUrl(),
                            "spring.datasource.username=" + database.getUsername(),
                            "spring.datasource.password=" + database.getPassword(),
                            "spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver",
                            "spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect")
                    .applyTo(applicationContext);
        }
    }
}