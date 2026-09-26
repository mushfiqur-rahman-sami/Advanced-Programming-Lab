package com.studyapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;

public class DashboardController {

    // =========================================================
    // ROOT / SCREENS
    // =========================================================

    @FXML
    private StackPane root;

    @FXML
    private VBox loginScreen;

    @FXML
    private VBox registerScreen;

    @FXML
    private BorderPane dashboardScreen;


    // =========================================================
    // LOGIN
    // =========================================================

    @FXML
    private TextField loginUsernameField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private Label loginMessage;


    // =========================================================
    // REGISTER
    // =========================================================

    @FXML
    private TextField registerUsernameField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label registerMessage;


    // =========================================================
    // HEADER
    // =========================================================

    @FXML
    private Label welcomeLabel;


    // =========================================================
    // DASHBOARD STATISTICS
    // =========================================================

    @FXML
    private Label totalTasksLabel;

    @FXML
    private Label completedTasksLabel;

    @FXML
    private Label pendingTasksLabel;

    @FXML
    private Label overdueTasksLabel;

    @FXML
    private Label studyMinutesLabel;


    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressPercentageLabel;

    @FXML
    private ListView<Task> recentTasksList;


    // =========================================================
    // MOTIVATION
    // =========================================================

    @FXML
    private Label quoteLabel;

    @FXML
    private Button quoteButton;


    // =========================================================
    // TASK MANAGEMENT
    // =========================================================

    @FXML
    private TextField taskTitleField;

    @FXML
    private TextField taskSubjectField;

    @FXML
    private ComboBox<String> priorityBox;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private ListView<Task> taskList;


    // =========================================================
    // STUDY TIMER
    // =========================================================

    @FXML
    private ComboBox<String> studySubjectBox;

    @FXML
    private TextField customSubjectField;

    @FXML
    private TextField minutesField;

    @FXML
    private Label timerLabel;


    // =========================================================
    // STUDY HISTORY
    // =========================================================

    @FXML
    private ListView<StudySession> historyList;


    // =========================================================
    // USER / TIMER VARIABLES
    // =========================================================

    private int currentUserId = -1;

    private String currentUsername = "";

    private int remainingSeconds = 0;

    private Thread timerThread;

    private int currentStudyMinutes = 0;

    private String currentStudySubject = "";


    // =========================================================
    // INITIALIZE
    // =========================================================

    @FXML
    public void initialize() {

        Database.initializeDatabase();

        priorityBox.getItems().addAll(
                "High",
                "Medium",
                "Low"
        );

        priorityBox.setValue("Medium");


        studySubjectBox.getItems().addAll(
                "Java",
                "Database",
                "Data Structures",
                "Algorithms",
                "Mathematics",
                "Computer Networks",
                "Operating Systems",
                "Other"
        );

        studySubjectBox.setValue("Java");


        timerLabel.setText("25:00");


        loginScreen.setVisible(true);
        loginScreen.setManaged(true);

        registerScreen.setVisible(false);
        registerScreen.setManaged(false);

        dashboardScreen.setVisible(false);
        dashboardScreen.setManaged(false);
    }


    // =========================================================
    // SHOW LOGIN
    // =========================================================

    private void showLogin() {

        loginScreen.setVisible(true);
        loginScreen.setManaged(true);

        registerScreen.setVisible(false);
        registerScreen.setManaged(false);

        dashboardScreen.setVisible(false);
        dashboardScreen.setManaged(false);

        loginUsernameField.clear();
        loginPasswordField.clear();

        loginMessage.setText("");
    }


    // =========================================================
    // SHOW REGISTER
    // =========================================================

    @FXML
    private void showRegister() {

        loginScreen.setVisible(false);
        loginScreen.setManaged(false);

        registerScreen.setVisible(true);
        registerScreen.setManaged(true);

        dashboardScreen.setVisible(false);
        dashboardScreen.setManaged(false);

        registerMessage.setText("");
    }


    // =========================================================
    // REGISTER USER
    // =========================================================

    @FXML
    private void handleRegister() {

        String username =
                registerUsernameField
                        .getText()
                        .trim();

        String password =
                registerPasswordField
                        .getText();

        String confirm =
                confirmPasswordField
                        .getText();


        if (username.isEmpty() ||
                password.isEmpty() ||
                confirm.isEmpty()) {

            registerMessage.setText(
                    "Please fill all fields."
            );

            return;
        }


        if (!password.equals(confirm)) {

            registerMessage.setText(
                    "Passwords do not match."
            );

            return;
        }


        boolean success =
                Database.registerUser(
                        username,
                        password
                );


        if (success) {

            registerMessage.setText(
                    "Account created successfully!"
            );

            registerUsernameField.clear();
            registerPasswordField.clear();
            confirmPasswordField.clear();

        } else {

            registerMessage.setText(
                    "Username already exists."
            );
        }
    }


    // =========================================================
    // BACK TO LOGIN
    // =========================================================

    @FXML
    private void backToLogin() {

        showLogin();
    }


    // =========================================================
    // LOGIN
    // =========================================================

    @FXML
    private void handleLogin() {

        String username =
                loginUsernameField
                        .getText()
                        .trim();

        String password =
                loginPasswordField
                        .getText();


        if (username.isEmpty() ||
                password.isEmpty()) {

            loginMessage.setText(
                    "Please enter username and password."
            );

            return;
        }


        int userId =
                Database.loginUser(
                        username,
                        password
                );


        if (userId != -1) {

            currentUserId = userId;

            currentUsername = username;

            welcomeLabel.setText(
                    "Welcome back, " +
                            currentUsername
            );


            loginScreen.setVisible(false);
            loginScreen.setManaged(false);

            registerScreen.setVisible(false);
            registerScreen.setManaged(false);

            dashboardScreen.setVisible(true);
            dashboardScreen.setManaged(true);


            refreshAll();
        } else {

            loginMessage.setText(
                    "Invalid username or password."
            );
        }
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    @FXML
    private void handleLogout() {

        stopTimer();

        currentUserId = -1;

        currentUsername = "";

        showLogin();
    }


    // =========================================================
    // REFRESH EVERYTHING
    // =========================================================

    private void refreshAll() {

        refreshDashboardStatistics();

        refreshRecentTasks();

        refreshTasks();

        refreshHistory();
    }


    // =========================================================
    // DASHBOARD STATISTICS
    // =========================================================

    private void refreshDashboardStatistics() {

        if (currentUserId == -1) {
            return;
        }


        int[] stats =
                Database.getTaskStatistics(
                        currentUserId
                );


        totalTasksLabel.setText(
                String.valueOf(stats[0])
        );


        completedTasksLabel.setText(
                String.valueOf(stats[1])
        );


        pendingTasksLabel.setText(
                String.valueOf(stats[2])
        );


        overdueTasksLabel.setText(
                String.valueOf(stats[3])
        );


        int studyMinutes =
                Database.getTotalStudyMinutes(
                        currentUserId
                );


        studyMinutesLabel.setText(
                String.valueOf(studyMinutes)
        );


        double progress = 0.0;


        if (stats[0] > 0) {

            progress =
                    (double) stats[1]
                            / stats[0];
        }


        progress =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                progress
                        )
                );


        progressBar.setProgress(
                progress
        );


        int percentage =
                (int) Math.round(
                        progress * 100
                );


        progressPercentageLabel.setText(
                percentage + "%"
        );
    }


    // =========================================================
    // RECENT TASKS
    // =========================================================

    private void refreshRecentTasks() {

        if (currentUserId == -1) {
            return;
        }


        recentTasksList.getItems().clear();


        List<Task> tasks =
                Database.getTasks(
                        currentUserId
                );


        int count =
                Math.min(
                        5,
                        tasks.size()
                );


        for (int i = 0; i < count; i++) {

            recentTasksList
                    .getItems()
                    .add(tasks.get(i));
        }
    }


    // =========================================================
    // TASK MANAGEMENT
    // =========================================================

    @FXML
    private void handleAddTask() {

        String title =
                taskTitleField
                        .getText()
                        .trim();

        String subject =
                taskSubjectField
                        .getText()
                        .trim();

        String priority =
                priorityBox.getValue();

        String dueDate =
                dueDatePicker.getValue() == null
                        ? ""
                        : dueDatePicker
                        .getValue()
                        .toString();


        if (title.isEmpty() ||
                subject.isEmpty()) {

            showAlert(
                    "Please enter task title and subject."
            );

            return;
        }


        Task task =
                new Task(
                        currentUserId,
                        title,
                        subject,
                        priority,
                        dueDate
                );


        if (Database.addTask(task)) {

            clearTaskFields();

            refreshTasks();

            refreshRecentTasks();

            refreshDashboardStatistics();
        }
    }


    // =========================================================
    // SELECT TASK
    // =========================================================

    @FXML
    private void handleTaskSelection() {

        Task selected =
                taskList
                        .getSelectionModel()
                        .getSelectedItem();


        if (selected == null) {
            return;
        }


        taskTitleField.setText(
                selected.getTitle()
        );


        taskSubjectField.setText(
                selected.getSubject()
        );


        priorityBox.setValue(
                selected.getPriority()
        );


        if (selected.getDueDate() != null &&
                !selected.getDueDate().isEmpty()) {

            try {

                dueDatePicker.setValue(
                        LocalDate.parse(
                                selected.getDueDate()
                        )
                );

            } catch (Exception ignored) {

            }

        } else {

            dueDatePicker.setValue(null);
        }
    }


    // =========================================================
    // EDIT TASK
    // =========================================================

    @FXML
    private void handleEditTask() {

        Task selected =
                taskList
                        .getSelectionModel()
                        .getSelectedItem();


        if (selected == null) {

            showAlert(
                    "Please select a task to edit."
            );

            return;
        }


        String title =
                taskTitleField
                        .getText()
                        .trim();

        String subject =
                taskSubjectField
                        .getText()
                        .trim();

        String priority =
                priorityBox.getValue();

        String dueDate =
                dueDatePicker.getValue() == null
                        ? ""
                        : dueDatePicker
                        .getValue()
                        .toString();


        if (title.isEmpty() ||
                subject.isEmpty()) {

            showAlert(
                    "Please enter task title and subject."
            );

            return;
        }


        boolean updated =
                Database.updateTask(
                        selected.getId(),
                        title,
                        subject,
                        priority,
                        dueDate
                );


        if (updated) {

            clearTaskFields();

            refreshTasks();

            refreshRecentTasks();

            refreshDashboardStatistics();

            showAlert(
                    "Task updated successfully."
            );
        }
    }


    // =========================================================
    // COMPLETE TASK
    // =========================================================

    @FXML
    private void handleCompleteTask() {

        Task selected =
                taskList
                        .getSelectionModel()
                        .getSelectedItem();


        if (selected == null) {

            showAlert(
                    "Please select a task."
            );

            return;
        }


        Database.completeTask(
                selected.getId()
        );


        refreshTasks();

        refreshRecentTasks();

        refreshDashboardStatistics();
    }


    // =========================================================
    // DELETE TASK
    // =========================================================

    @FXML
    private void handleDeleteTask() {

        Task selected =
                taskList
                        .getSelectionModel()
                        .getSelectedItem();


        if (selected == null) {

            showAlert(
                    "Please select a task."
            );

            return;
        }


        Database.deleteTask(
                selected.getId()
        );


        clearTaskFields();

        refreshTasks();

        refreshRecentTasks();

        refreshDashboardStatistics();
    }


    // =========================================================
    // REFRESH TASKS
    // =========================================================

    @FXML
    private void refreshTasks() {

        if (currentUserId == -1) {
            return;
        }


        taskList.getItems().clear();


        List<Task> tasks =
                Database.getTasks(
                        currentUserId
                );


        taskList
                .getItems()
                .addAll(tasks);
    }


    // =========================================================
    // CLEAR TASK FIELDS
    // =========================================================

    private void clearTaskFields() {

        taskTitleField.clear();

        taskSubjectField.clear();

        priorityBox.setValue("Medium");

        dueDatePicker.setValue(null);

        taskList
                .getSelectionModel()
                .clearSelection();
    }


    // =========================================================
    // STUDY TIMER
    // =========================================================

    @FXML
    private void handleStartStudy() {

        try {

            int minutes =
                    Integer.parseInt(
                            minutesField
                                    .getText()
                                    .trim()
                    );


            if (minutes <= 0) {

                showAlert(
                        "Enter a duration greater than 0."
                );

                return;
            }


            String customSubject =
                    customSubjectField
                            .getText()
                            .trim();


            String subject;


            if (!customSubject.isEmpty()) {

                subject = customSubject;

            } else {

                subject =
                        studySubjectBox
                                .getValue();
            }


            startTimer(
                    minutes,
                    subject
            );


        } catch (NumberFormatException e) {

            showAlert(
                    "Please enter a valid number."
            );
        }
    }


    // =========================================================
    // START TIMER
    // =========================================================

    private void startTimer(
            int minutes,
            String subject) {

        stopTimer();


        currentStudyMinutes = minutes;

        currentStudySubject = subject;

        remainingSeconds =
                minutes * 60;


        updateTimerLabel();


        timerThread =
                new Thread(() -> {

                    try {

                        while (remainingSeconds > 0) {

                            Thread.sleep(1000);

                            remainingSeconds--;


                            Platform.runLater(
                                    this::updateTimerLabel
                            );
                        }


                        Platform.runLater(() -> {

                            Database.addStudySession(
                                    currentUserId,
                                    currentStudySubject,
                                    currentStudyMinutes
                            );


                            showAlert(
                                    "Study session completed!\n\n"
                                            + currentStudyMinutes
                                            + " minutes of "
                                            + currentStudySubject
                                            + " saved to your history."
                            );


                            refreshDashboardStatistics();

                            refreshHistory();
                        });


                    } catch (InterruptedException e) {

                        Thread.currentThread()
                                .interrupt();
                    }

                });


        timerThread.setDaemon(true);

        timerThread.start();
    }


    // =========================================================
    // STOP TIMER
    // =========================================================

    @FXML
    private void handleStopTimer() {

        stopTimer();
    }


    private void stopTimer() {

        if (timerThread != null) {

            timerThread.interrupt();

            timerThread = null;
        }
    }


    // =========================================================
    // RESET TIMER
    // =========================================================

    @FXML
    private void handleResetTimer() {

        stopTimer();

        remainingSeconds =
                25 * 60;

        updateTimerLabel();
    }


    // =========================================================
    // UPDATE TIMER LABEL
    // =========================================================

    private void updateTimerLabel() {

        if (timerLabel == null) {
            return;
        }


        int minutes =
                remainingSeconds / 60;


        int seconds =
                remainingSeconds % 60;


        timerLabel.setText(
                String.format(
                        "%02d:%02d",
                        minutes,
                        seconds
                )
        );
    }


    // =========================================================
    // STUDY HISTORY
    // =========================================================

    private void refreshHistory() {

        if (currentUserId == -1) {
            return;
        }


        historyList.getItems().clear();


        historyList
                .getItems()
                .addAll(
                        Database.getStudyHistory(
                                currentUserId
                        )
                );
    }


    @FXML
    private void handleRefreshHistory() {

        refreshHistory();
    }


    // =========================================================
    // API MOTIVATION
    // =========================================================

    @FXML
    private void loadQuote() {

        quoteButton.setDisable(true);

        quoteLabel.setText(
                "Loading motivation..."
        );


        javafx.concurrent.Task<String> apiTask =
                new javafx.concurrent.Task<>() {

                    @Override
                    protected String call()
                            throws Exception {

                        return ApiService
                                .getMotivationalQuote();
                    }
                };


        apiTask.setOnSucceeded(event -> {

            quoteLabel.setText(
                    apiTask.getValue()
            );


            quoteButton.setDisable(false);
        });


        apiTask.setOnFailed(event -> {

            quoteLabel.setText(
                    "Could not load motivation."
            );


            quoteButton.setDisable(false);

            apiTask.getException()
                    .printStackTrace();
        });


        Thread apiThread =
                new Thread(apiTask);

        apiThread.setDaemon(true);

        apiThread.start();
    }


    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle("StudyFlow");

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }
}