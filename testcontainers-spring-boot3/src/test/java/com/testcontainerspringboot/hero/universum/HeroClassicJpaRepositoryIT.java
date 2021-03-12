package com.testcontainerspringboot.hero.universum;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class HeroClassicJpaRepositoryIT {

	@Container
	private static MySQLContainer database = new MySQLContainer("mysql:latest");

	@Autowired
	private HeroClassicJpaRepository repositoryUnderTest;

	@Test
	void findAllHero() {
		int numberHeros = repositoryUnderTest.allHeros().size();

		repositoryUnderTest.addHero(new Hero("Batman", "Gotham City", ComicUniversum.DC_COMICS));
		repositoryUnderTest.addHero(new Hero("Superman", "Metropolis", ComicUniversum.DC_COMICS));

		Collection<Hero> heros = repositoryUnderTest.allHeros();

		assertThat(heros).hasSize(numberHeros + 2);
	}

	@Test
	void findHeroByCriteria() {
		repositoryUnderTest.addHero(new Hero("Batman", "Gotham City", ComicUniversum.DC_COMICS));

		Collection<Hero> heros = repositoryUnderTest.findHerosBySearchCriteria("Batman");

		assertThat(heros).contains(new Hero("Batman", "Gotham City", ComicUniversum.DC_COMICS));
	}

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", database::getJdbcUrl);
		registry.add("spring.datasource.username", database::getUsername);
		registry.add("spring.datasource.password", database::getPassword);
//		registry.add("spring.datasource.driverClassName", ()-> "com.mysql.cj.jdbc.Driver");
//		registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
	}
}