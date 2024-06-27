package com.examples.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query {

    private DataSource dataSource;

    public Query(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection prepareConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public <T> T queryForObject(String sql) throws SQLException {
        try (Connection conn = prepareConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {
            return resultSet.next() ? (T) resultSet.getObject(1) : null;
        }
    }

    public <T> T query(String sql, ResultSetExtractor<T> extractor) throws SQLException {
        try (Connection conn = prepareConnection();
             Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {
            return resultSet.next() ? extractor.extract(resultSet) : null;
        }
    }
}
