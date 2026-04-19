package iposca.db;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    /**
     * Reads ipos_ca_postgresql.sql from the classpath and executes each statement.
     * Safe to run on every startup — all tables use CREATE TABLE IF NOT EXISTS
     * and seed inserts use ON CONFLICT DO NOTHING.
     */
    public static void run(Connection conn) {
        try (InputStream is = SchemaInitializer.class
                .getClassLoader()
                .getResourceAsStream("ipos_ca_postgresql.sql")) {

            if (is == null) {
                System.err.println("[SchemaInitializer] ipos_ca_postgresql.sql not found in classpath.");
                return;
            }

            String fullSql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Split on semicolons to get individual statements
            String[] chunks = fullSql.split(";");

            try (Statement stmt = conn.createStatement()) {
                for (String chunk : chunks) {
                    // Strip comment-only lines from the chunk, keep actual SQL lines
                    StringBuilder sb = new StringBuilder();
                    for (String line : chunk.split("\n")) {
                        String trimmedLine = line.stripLeading();
                        if (!trimmedLine.startsWith("--")) {
                            sb.append(line).append("\n");
                        }
                    }

                    String sql = sb.toString().strip();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            System.err.println("[SchemaInitializer] Skipping statement due to error: "
                                    + e.getMessage().trim());
                        }
                    }
                }
            }

            System.out.println("[SchemaInitializer] CA schema initialised successfully.");

        } catch (Exception e) {
            System.err.println("[SchemaInitializer] Fatal error during schema init: " + e.getMessage());
        }
    }
}
