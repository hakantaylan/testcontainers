package com.testcontainerspringboot.hero.universum;

import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestPropertySource(locations = "classpath:application-test.properties")
class HeroClassicJDBCRepositoryIT {

	@Container
	private MySQLContainer database = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.33"))
			.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("testcontainers.mysql")));

	private HeroClassicJDBCRepository repositoryUnderTest;

	@Test
	void testInteractionWithDatabase() {
		ScriptUtils.runInitScript(new JdbcDatabaseDelegate(database, ""), "addl.sql");
		repositoryUnderTest = new HeroClassicJDBCRepository(dataSource());

		repositoryUnderTest.addHero(new Hero("Batman", "Gotham City", ComicUniversum.DC_COMICS));

		Collection<Hero> heroes = repositoryUnderTest.allHeros();

		assertThat(heroes).hasSize(1);
	}

	@NotNull
	private DataSource dataSource() {
		MysqlDataSource dataSource = new MysqlDataSource();
		dataSource.setUrl(database.getJdbcUrl());
		dataSource.setUser(database.getUsername());
		dataSource.setPassword(database.getPassword());
		return dataSource;
	}
}