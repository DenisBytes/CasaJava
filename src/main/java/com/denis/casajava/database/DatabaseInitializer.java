package com.denis.casajava.database;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void executeQueryOnStart() {

        String query1 = "insert ignore into roles (id,name) values (1,'ROLE_USER')";
        String query2 = "insert ignore into roles (id,name) values (2,'ROLE_ADMIN')";
        String query3 = "insert ignore into roles (id,name) values (3,'ROLE_SUPER_ADMIN')";

        jdbcTemplate.execute(query1);
        jdbcTemplate.execute(query2);
        jdbcTemplate.execute(query3);

    }
}
