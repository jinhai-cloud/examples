package com.examples.commons;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.JdbcClient;

import com.mysql.cj.conf.PropertyDefinitions;
import com.zaxxer.hikari.HikariDataSource;

public class Jdbc {
    private static final JdbcClient JDBC_CLIENT = JdbcClient.create(initDataSource());

    public static JdbcClient getClient() {
        return JDBC_CLIENT;
    }

    private static DataSource initDataSource() {
        System.setProperty(PropertyDefinitions.SYSP_disableAbandonedConnectionCleanup, "true");
        HikariDataSource dataSource = new HikariDataSource();
        // show variables like 'wait_timeout%'
        dataSource.setMaxLifetime(28800000);
        dataSource.setMaximumPoolSize(16);
        return dataSource;
    }
}
