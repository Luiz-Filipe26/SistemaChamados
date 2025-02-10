package com.ticketsolutions.ticket_manager.core.initialization;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class TableCreator implements CommandLineRunner {

    @SuppressWarnings("unused")
	private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public TableCreator(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource("db/init-db.sql"));

        databasePopulator.setContinueOnError(false);
        databasePopulator.populate(dataSource.getConnection());
    }
}