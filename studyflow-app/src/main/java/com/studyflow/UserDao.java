package com.studyflow;

import java.sql.*;

public final class UserDao {
    private UserDao() { }

    public static User authenticate(String email, String password) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, password_salt FROM users WHERE lower(email)=lower(?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String salt = rs.getString("password_salt");
                String expected = rs.getString("password_hash");
                String actual = PasswordUtil.hash(password, salt);
                if (!expected.equals(actual)) return null;
                return new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            }
        }
    }

    public static User create(String name, String email, String password) throws SQLException {
        String normalized = email.trim().toLowerCase();
        String salt = PasswordUtil.newSalt();
        String hash = PasswordUtil.hash(password, salt);
        String sql = "INSERT INTO users(name,email,password_hash,password_salt) VALUES(?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name.trim());
            ps.setString(2, normalized);
            ps.setString(3, hash);
            ps.setString(4, salt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new User(keys.getInt(1), name.trim(), normalized);
            }
        }
    }
}
