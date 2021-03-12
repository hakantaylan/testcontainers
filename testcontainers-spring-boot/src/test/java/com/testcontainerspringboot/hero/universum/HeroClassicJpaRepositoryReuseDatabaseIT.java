package com.testcontainerspringboot.hero.universum;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class HeroClassicJpaRepositoryReuseDatabaseIT extends DatabaseBaseTest {

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
}