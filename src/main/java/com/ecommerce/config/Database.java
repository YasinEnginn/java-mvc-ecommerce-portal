package com.ecommerce.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Database {
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/ecommerce_portal?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            }
            Class.forName(get("db.driver", "DB_DRIVER", DEFAULT_DRIVER));
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                get("db.url", "DB_URL", DEFAULT_URL),
                get("db.username", "DB_USERNAME", "root"),
                get("db.password", "DB_PASSWORD", "")
        );
    }

    private static String get(String propertyName, String environmentName, String defaultValue) {
        String systemValue = System.getProperty(propertyName);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return PROPERTIES.getProperty(propertyName, defaultValue);
    }
}
