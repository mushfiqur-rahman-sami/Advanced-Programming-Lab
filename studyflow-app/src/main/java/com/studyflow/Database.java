package com.studyflow;

import java.io.File;
import java.sql.*;

public final class Database {
    private static Connection connection;

    private Database() { }

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String folder = System.getProperty("user.home") + File.separator + ".studyflow";
            new File(folder).mkdirs();
            connection = DriverManager.getConnection("jdbc:sqlite:" + folder + File.separator + "studyflow.db");
            try (Statement st = connection.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    public static synchronized void init() {
        try (Statement st = getConnection().createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    password_salt TEXT NOT NULL,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                )""");
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    subject TEXT NOT NULL DEFAULT '',
                    due_date TEXT NOT NULL DEFAULT '',
                    priority TEXT NOT NULL DEFAULT 'Normal',
                    completed INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )""");
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS study_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    subject TEXT NOT NULL,
                    minutes INTEGER NOT NULL,
                    started_at TEXT NOT NULL,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                )""");
        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize SQLite database", e);
        }
    }

    public static synchronized void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) { }
        }
    }
}
