package iposca.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    private static Connection connection = null;

    static {
        try {
            Properties props = new Properties();
            InputStream input = DatabaseManager.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            if (input == null) {
                throw new RuntimeException("db.properties not found in resources");
            }

            props.load(input);

            URL = props.getProperty("db.url");
            USERNAME = props.getProperty("db.username");
            PASSWORD = props.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException("Could not load db.properties.", e);
        }
    }

    public static Connection getConnection() throws SQLException {

        try {
            if (connection == null || connection.isClosed()) {

                Class.forName("org.postgresql.Driver");

                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                // All CA tables live in the 'ca' schema on the shared Railway PostgreSQL DB
                connection.createStatement().execute("SET search_path TO ca, public");

                // Create CA schema + tables if they don't exist yet (runs on first connection)
                SchemaInitializer.run(connection);

                System.out.println("PostgreSQL connected successfully (schema: ca).");
            }

        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found. Add dependency.", e);
        }

        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
