package com.testcontainerspringboot.hero.universum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class TableClearer {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DataSource dataSource;

	private Connection connection;

	public void clearTables() {
		try {
			connection = dataSource.getConnection();
			tryToClearTables();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void tryToClearTables() throws SQLException {
		List<String> tableNames = getTableNames();
		clear(tableNames);
	}

	private List<String> getTableNames() throws SQLException {
		List<String> tableNames = new ArrayList<>();

		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet rs = metaData.getTables(
			connection.getCatalog(), null, null, new String[]{"TABLE"});

		while (rs.next()) {
			String table_name = rs.getString("TABLE_NAME");
			if(!table_name.toUpperCase().contains("SEQUENCE"))
				tableNames.add(table_name);
		}

		return tableNames;
	}

	private void clear(List<String> tableNames) throws SQLException {
		Statement statement = buildSqlStatement(tableNames);

		logger.debug("Executing SQL");
		statement.executeBatch();
	}

	private Statement buildSqlStatement(List<String> tableNames) throws SQLException {
		Statement statement = connection.createStatement();

		statement.addBatch(sql("SET FOREIGN_KEY_CHECKS = 0"));
		addDeleteSatements(tableNames, statement);
		statement.addBatch(sql("SET FOREIGN_KEY_CHECKS = 1"));

		return statement;
	}

	private void addDeleteSatements(List<String> tableNames, Statement statement) {
		tableNames.forEach(tableName -> {
			try {
				statement.addBatch(sql("DELETE FROM " + tableName));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private String sql(String sql) {
		logger.debug("Adding SQL: {}", sql);
		return sql;
	}
}