package com.examples.commons;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.zaxxer.hikari.HikariDataSource;

public class Jdbc {

    private static final Supplier<NamedParameterJdbcTemplate> jdbcTemplateSupplier =
            Suppliers.memoize(() -> new NamedParameterJdbcTemplate(initDataSource()));

    public static NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplateSupplier.get();
    }

    private static DataSource initDataSource() {
        return new HikariDataSource();
    }
}
