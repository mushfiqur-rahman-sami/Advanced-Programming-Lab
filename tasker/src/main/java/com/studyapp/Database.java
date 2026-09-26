package com.studyapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String URL =
            "jdbc:sqlite:study_manager.db";


    // =========================================================
    // DATABASE CONNECTION
    // =========================================================

    public static Connection connect()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }


    // =========================================================
    // INITIALIZE DATABASE
    // =========================================================

    public static void initializeDatabase() {

        String usersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
                """;


        String tasksTable = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    subject TEXT NOT NULL,
                    priority TEXT NOT NULL,
                    status TEXT NOT NULL,
                    due_date TEXT,
                    FOREIGN KEY(user_id)
                    REFERENCES users(id)
                )
                """;


        String sessionsTable = """
                CREATE TABLE IF NOT EXISTS study_sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    subject TEXT NOT NULL,
                    duration INTEGER NOT NULL,
                    study_date TEXT NOT NULL,
                    FOREIGN KEY(user_id)
                    REFERENCES users(id)
                )
                """;


        try (
                Connection connection = connect();
                Statement statement =
                        connection.createStatement()
        ) {

            statement.execute(usersTable);

            statement.execute(tasksTable);

            statement.execute(sessionsTable);


            // -------------------------------------------------
            // IMPORTANT
            //
            // If the old database already existed before
            // due_date was added, add the column now.
            // -------------------------------------------------

            try {

                statement.execute(
                        "ALTER TABLE tasks " +
                                "ADD COLUMN due_date TEXT"
                );

            } catch (SQLException ignored) {

                // Column probably already exists.
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // REGISTER USER
    // =========================================================

    public static boolean registerUser(
            String username,
            String password) {

        String sql =
                "INSERT INTO users(username, password) " +
                        "VALUES(?, ?)";


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            statement.setString(2, password);

            statement.executeUpdate();

            return true;

        } catch (SQLException e) {

            return false;
        }
    }


    // =========================================================
    // LOGIN
    // =========================================================

    public static int loginUser(
            String username,
            String password) {

        String sql =
                "SELECT id FROM users " +
                        "WHERE username = ? AND password = ?";


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            statement.setString(2, password);


            ResultSet result =
                    statement.executeQuery();


            if (result.next()) {

                return result.getInt("id");
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }


        return -1;
    }


    // =========================================================
    // ADD TASK
    // =========================================================

    public static boolean addTask(Task task) {

        String sql = """
                INSERT INTO tasks
                (user_id, title, subject, priority, status, due_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    task.getUserId()
            );


            statement.setString(
                    2,
                    task.getTitle()
            );


            statement.setString(
                    3,
                    task.getSubject()
            );


            statement.setString(
                    4,
                    task.getPriority()
            );


            statement.setString(
                    5,
                    task.getStatus()
            );


            statement.setString(
                    6,
                    task.getDueDate()
            );


            statement.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // GET TASKS
    // =========================================================

    public static List<Task> getTasks(
            int userId) {

        List<Task> tasks =
                new ArrayList<>();


        String sql =
                "SELECT * FROM tasks " +
                        "WHERE user_id = ? " +
                        "ORDER BY " +
                        "CASE WHEN status = 'Completed' " +
                        "THEN 1 ELSE 0 END, " +
                        "due_date ASC, id DESC";


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );


            ResultSet result =
                    statement.executeQuery();


            while (result.next()) {

                Task task =
                        new Task(
                                result.getInt("id"),
                                result.getInt("user_id"),
                                result.getString("title"),
                                result.getString("subject"),
                                result.getString("priority"),
                                result.getString("status"),
                                result.getString("due_date")
                        );


                tasks.add(task);
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }


        return tasks;
    }


    // =========================================================
    // UPDATE TASK
    // =========================================================

    public static boolean updateTask(
            int taskId,
            String title,
            String subject,
            String priority,
            String dueDate) {

        String sql = """
                UPDATE tasks
                SET title = ?,
                    subject = ?,
                    priority = ?,
                    due_date = ?
                WHERE id = ?
                """;


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    title
            );


            statement.setString(
                    2,
                    subject
            );


            statement.setString(
                    3,
                    priority
            );


            statement.setString(
                    4,
                    dueDate
            );


            statement.setInt(
                    5,
                    taskId
            );


            statement.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // DELETE TASK
    // =========================================================

    public static void deleteTask(
            int taskId) {

        String sql =
                "DELETE FROM tasks WHERE id = ?";


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    taskId
            );


            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // COMPLETE TASK
    // =========================================================

    public static void completeTask(
            int taskId) {

        String sql =
                "UPDATE tasks " +
                        "SET status = 'Completed' " +
                        "WHERE id = ?";


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    taskId
            );


            statement.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // TASK STATISTICS
    //
    // [0] = Total
    // [1] = Completed
    // [2] = Pending
    // [3] = Overdue
    // =========================================================

    public static int[] getTaskStatistics(
            int userId) {

        int[] stats =
                new int[4];


        String sql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(
                        CASE
                            WHEN status = 'Completed'
                            THEN 1
                            ELSE 0
                        END
                    ) AS completed,
                    SUM(
                        CASE
                            WHEN status != 'Completed'
                            THEN 1
                            ELSE 0
                        END
                    ) AS pending,
                    SUM(
                        CASE
                            WHEN status != 'Completed'
                            AND due_date IS NOT NULL
                            AND due_date != ''
                            AND date(due_date) < date('now')
                            THEN 1
                            ELSE 0
                        END
                    ) AS overdue
                FROM tasks
                WHERE user_id = ?
                """;


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );


            ResultSet result =
                    statement.executeQuery();


            if (result.next()) {

                stats[0] =
                        result.getInt("total");

                stats[1] =
                        result.getInt("completed");

                stats[2] =
                        result.getInt("pending");

                stats[3] =
                        result.getInt("overdue");
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }


        return stats;
    }


    // =========================================================
    // ADD STUDY SESSION
    // =========================================================

    public static boolean addStudySession(
            int userId,
            String subject,
            int duration) {

        String sql = """
                INSERT INTO study_sessions
                (user_id, subject, duration, study_date)
                VALUES (?, ?, ?, datetime('now', 'localtime'))
                """;


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );


            statement.setString(
                    2,
                    subject
            );


            statement.setInt(
                    3,
                    duration
            );


            statement.executeUpdate();

            return true;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // GET STUDY HISTORY
    // =========================================================

    public static List<StudySession>
    getStudyHistory(int userId) {

        List<StudySession> sessions =
                new ArrayList<>();


        String sql = """
                SELECT *
                FROM study_sessions
                WHERE user_id = ?
                ORDER BY id DESC
                """;


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );


            ResultSet result =
                    statement.executeQuery();


            while (result.next()) {

                StudySession session =
                        new StudySession(
                                result.getInt("id"),
                                result.getInt("user_id"),
                                result.getString("subject"),
                                result.getInt("duration"),
                                result.getString("study_date")
                        );


                sessions.add(session);
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }


        return sessions;
    }


    // =========================================================
    // TOTAL STUDY MINUTES
    // =========================================================

    public static int getTotalStudyMinutes(
            int userId) {

        String sql =
                "SELECT COALESCE(SUM(duration), 0) " +
                        "AS total FROM study_sessions " +
                        "WHERE user_id = ?";


        try (
                Connection connection = connect();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );


            ResultSet result =
                    statement.executeQuery();


            if (result.next()) {

                return result.getInt("total");
            }


        } catch (SQLException e) {

            e.printStackTrace();
        }


        return 0;
    }
}