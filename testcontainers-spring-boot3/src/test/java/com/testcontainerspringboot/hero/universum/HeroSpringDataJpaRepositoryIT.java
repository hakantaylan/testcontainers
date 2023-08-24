package com.testcontainerspringboot.hero.universum;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class HeroSpringDataJpaRepositoryIT {

	@Container
	@ServiceConnection
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

//	@DynamicPropertySource
//	static void databaseProperties(DynamicPropertyRegistry registry) {
//		registry.add("spring.datasource.url", database::getJdbcUrl);
//		registry.add("spring.datasource.username", database::getUsername);
//		registry.add("spring.datasource.password", database::getPassword);
////		registry.add("spring.datasource.driverClassName", ()-> "com.mysql.cj.jdbc.Driver");
////		registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
//	}
}