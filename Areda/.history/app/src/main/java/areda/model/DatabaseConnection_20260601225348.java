package areda.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:areda_database.db";

    /**
     * ✅ FIX: Return koneksi BARU setiap kali dipanggil.
     * SQLite mendukung multiple connections, jadi ini aman.
     * Dengan ini, try-with-resources di DatabaseManager jadi aman.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // Aktifkan foreign key untuk setiap koneksi baru
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    // Method opsional untuk shutdown (tidak wajib)
    public static void shutdown() {
        // SQLite auto-closes when JVM exits
    }
}