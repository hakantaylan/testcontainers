package com.testcontainerspringboot.hero.universum;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(initializers = {HeroRestControllerIT.Initializer.class})
class HeroRestControllerIT {

    @Container
    private static MySQLContainer database = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.mysql")));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HeroSpringDataJpaRepository heroRepository;

    @Test
    void allHeros() throws Exception {
        heroRepository.save(new Hero("Batman", "Gotham City", ComicUniversum.DC_COMICS));
        heroRepository.save(new Hero("Superman", "Metropolis", ComicUniversum.DC_COMICS));

        mockMvc.perform(get("/heros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Batman", "Superman")));
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