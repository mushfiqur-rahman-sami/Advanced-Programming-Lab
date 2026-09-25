package com.studyflow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class TaskDao {
    private TaskDao() { }

    public static List<Task> findByUser(int userId) throws SQLException {
        List<Task> list = new ArrayList<>();
        String sql = "SELECT id,user_id,title,subject,due_date,priority,completed FROM tasks WHERE user_id=? ORDER BY completed ASC, CASE priority WHEN 'High' THEN 1 WHEN 'Normal' THEN 2 ELSE 3 END, due_date, id DESC";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Task(rs.getInt("id"), rs.getInt("user_id"), rs.getString("title"),
                            rs.getString("subject"), rs.getString("due_date"), rs.getString("priority"),
                            rs.getInt("completed") == 1));
                }
            }
        }
        return list;
    }

    public static void insert(int userId, String title, String subject, String dueDate, String priority) throws SQLException {
        String sql = "INSERT INTO tasks(user_id,title,subject,due_date,priority) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, subject);
            ps.setString(4, dueDate);
            ps.setString(5, priority);
            ps.executeUpdate();
        }
    }

    public static void toggle(int taskId, boolean completed) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE tasks SET completed=? WHERE id=?")) {
            ps.setInt(1, completed ? 1 : 0);
            ps.setInt(2, taskId);
            ps.executeUpdate();
        }
    }

    public static void delete(int taskId) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM tasks WHERE id=?")) {
            ps.setInt(1, taskId);
            ps.executeUpdate();
        }
    }

    public static int countOpen(int userId) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT COUNT(*) FROM tasks WHERE user_id=? AND completed=0")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public static int countDone(int userId) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT COUNT(*) FROM tasks WHERE user_id=? AND completed=1")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public static int countTodaySessions(int userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(minutes),0) FROM study_sessions WHERE user_id=? AND date(started_at)=date('now','localtime')";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public static void saveSession(int userId, String subject, int minutes, String startedAt) throws SQLException {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("INSERT INTO study_sessions(user_id,subject,minutes,started_at) VALUES(?,?,?,?)")) {
            ps.setInt(1, userId);
            ps.setString(2, subject);
            ps.setInt(3, minutes);
            ps.setString(4, startedAt);
            ps.executeUpdate();
        }
    }
}
