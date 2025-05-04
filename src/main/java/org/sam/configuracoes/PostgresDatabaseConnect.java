package org.sam.configuracoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresDatabaseConnect {
    private static final Logger LOGGER = Logger.getLogger(PostgresDatabaseConnect.class.getName());

    public static Connection connect() {
        String url = "jdbc:postgresql://localhost:5432/saam_db";
        String user = "postgres";
        String password = "1234";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            LOGGER.log(Level.INFO, "Connected to PostgreSQL database!");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Connection to PostgreSQL failed!", e);
        }
        return connection;
    }
}