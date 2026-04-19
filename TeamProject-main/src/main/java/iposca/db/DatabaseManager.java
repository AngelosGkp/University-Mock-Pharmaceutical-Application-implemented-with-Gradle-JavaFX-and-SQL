package iposca.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private static String URL;
    private static final String User;
    private static final String Password;

    private static Connection connection = null;

    static {
        try {
            Properties props = new Properties();
            InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(input);
            URL = props.getProperty("db.url");
            User = props.getProperty("db.user");
            Password = props.getProperty("db.password");
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load db.properties.", e);
        }
    }

    public static Connection getConnection() throws SQLException {

        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, User, Password );
                System.out.println("Database connected successfully.");
            }
            catch (ClassNotFoundException e) {
                throw new SQLException("MySQL Driver not found, check lib folder.", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}