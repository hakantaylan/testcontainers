package com.testcontainerspringboot.hero.universum;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class HeroClassicJpaRepositoryIT {

	@Container
	@ServiceConnection
	private static MySQLContainer database = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
			.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.mysql")));

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

//	@DynamicPropertySource
//	static void databaseProperties(DynamicPropertyRegistry registry) {
//		registry.add("spring.datasource.url", database::getJdbcUrl);
//		registry.add("spring.datasource.username", database::getUsername);
//		registry.add("spring.datasource.password", database::getPassword);
////		registry.add("spring.datasource.driverClassName", ()-> "com.mysql.cj.jdbc.Driver");
////		registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
//	}
}