package com.examples.commons;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.JdbcClient;

import com.zaxxer.hikari.HikariDataSource;

public class Jdbc {
    private static final JdbcClient JDBC_CLIENT = JdbcClient.create(initDataSource());

    public static JdbcClient getClient() {
        return JDBC_CLIENT;
    }

    private static DataSource initDataSource() {
        return new HikariDataSource();
    }
}
