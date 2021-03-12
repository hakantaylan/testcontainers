package com.testcontainerspringboot.hero.universum;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
class HeroRestControllerIT {

	@Container
	private static MySQLContainer database = new MySQLContainer("mysql:latest");

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

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", database::getJdbcUrl);
		registry.add("spring.datasource.username", database::getUsername);
		registry.add("spring.datasource.password", database::getPassword);
	}
}