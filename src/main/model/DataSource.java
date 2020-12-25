package model;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

// Creates a connection pool using HikariCP
public class DataSource {

    private static HikariConfig config = new HikariConfig(PropertyManager.getProperties("datasource.properties"));
    private static HikariDataSource ds = new HikariDataSource( config );

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}