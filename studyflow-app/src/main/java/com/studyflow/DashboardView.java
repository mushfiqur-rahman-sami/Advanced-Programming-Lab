package com.studyflow;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.*;

public class DashboardView extends BorderPane {
    private final User user;
    private final Runnable onLogout;
    private final ExecutorService ioPool = Executors.newFixedThreadPool(2);
    private final ScheduledExecutorService timerExecutor = Executors.newSingleThreadScheduledExecutor();

    private final Label openCount = metricValue("0");
    private final Label doneCount = metricValue("0");
    private final Label focusCount = metricValue("0m");
    private final Label quoteText = new Label("Loading a small dose of motivation…");
    private final Label quoteAuthor = new Label("— StudyFlow");
    private final Label timerLabel = new Label("25:00");
    private final Label timerStatus = new Label("Ready for a focused session");
    private final ComboBox<String> subjectBox = new ComboBox<>(FXCollections.observableArrayList("Math", "Programming", "Database", "Networking", "Other"));
    private final ProgressBar timerProgress = new ProgressBar(0);
    private int remainingSeconds = 25 * 60;
    private boolean running;
    private ScheduledFuture<?> timerFuture;

    public DashboardView(User user, Runnable onLogout) {
        this.user = user;
        this.onLogout = onLogout;
        buildShell();
        loadStatsAsync();
        loadQuoteAsync();
    }

    private void buildShell() {
        setLeft(sidebar());
        setCenter(content());
    }

    private Node sidebar() {
        VBox side = new VBox(14);
        side.getStyleClass().add("sidebar");
        side.setPadding(new Insets(28, 18, 24, 18));
        side.setPrefWidth(225);

        Label brand = new Label("StudyFlow");
        brand.getStyleClass().add("brand");
        Label userLabel = new Label(user.name());
        userLabel.getStyleClass().add("sidebar-user");
        Label email = new Label(user.email());
        email.getStyleClass().add("sidebar-email");

        Button overview = navButton("Overview");
        Button tasks = navButton("Tasks");
        Button study = navButton("Study Timer");
        overview.setOnAction(e -> showOverview());
        tasks.setOnAction(e -> showTasks());
        study.setOnAction(e -> showStudy());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Button logout = navButton("Log Out");
        logout.getStyleClass().add("danger-button");
        logout.setOnAction(e -> {
            shutdownWorkers();
            onLogout.run();
        });

        side.getChildren().addAll(brand, userLabel, email, new Separator(), overview, tasks, study, spacer, logout);
        return side;
    }

    private Button navButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.getStyleClass().add("nav-button");
        return b;
    }

    private Node content() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(28, 34, 30, 34));
        root.getStyleClass().add("content-root");
        showOverviewInto(root);
        return root;
    }

    private void showOverview() { showOverviewInto((BorderPane) getCenter()); }

    private void showOverviewInto(BorderPane root) {
        VBox page = new VBox(20);
        Label title = pageTitle("Today");
        Label subtitle = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        subtitle.getStyleClass().add("muted");

        HBox metrics = new HBox(14, metricCard("Open tasks", openCount), metricCard("Completed", doneCount), metricCard("Study today", focusCount));
        for (Node n : metrics.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        VBox quote = quoteCard();
        VBox next = new VBox(10);
        Label nextTitle = sectionTitle("Keep it moving");
        Label helper = new Label("Use Tasks to plan work and Study Timer for a focused 25-minute session.");
        helper.setWrapText(true);
        helper.getStyleClass().add("muted");
        next.getChildren().addAll(nextTitle, helper);
        page.getChildren().addAll(title, subtitle, metrics, quote, next);

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("clean-scroll");
        root.setCenter(scroll);
    }

    private VBox quoteCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        quoteText.setWrapText(true);
        quoteText.getStyleClass().add("quote");
        quoteAuthor.getStyleClass().add("muted");
        HBox head = new HBox(new Label("Daily focus"));
        head.getChildren().get(0).getStyleClass().add("card-title");
        Button refresh = new Button("Refresh");
        refresh.getStyleClass().add("subtle-button");
        refresh.setOnAction(e -> loadQuoteAsync());
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        head.getChildren().addAll(spacer, refresh);
        card.getChildren().addAll(head, quoteText, quoteAuthor);
        return card;
    }

    private void showTasks() {
        BorderPane root = (BorderPane) getCenter();
        VBox page = new VBox(18);
        Label title = pageTitle("Tasks");
        Label sub = new Label("Simple, local, and synced to your SQLite database.");
        sub.getStyleClass().add("muted");

        HBox form = new HBox(10);
        TextField titleField = new TextField(); titleField.setPromptText("Task title"); HBox.setHgrow(titleField, Priority.ALWAYS);
        TextField subjectField = new TextField(); subjectField.setPromptText("Subject"); subjectField.setPrefWidth(170);
        TextField dueField = new TextField(); dueField.setPromptText("Due date (YYYY-MM-DD)"); dueField.setPrefWidth(170);
        ComboBox<String> priority = new ComboBox<>(FXCollections.observableArrayList("High", "Normal", "Low")); priority.setValue("Normal"); priority.setPrefWidth(120);
        Button add = new Button("Add task"); add.getStyleClass().add("primary-button");
        add.setOnAction(e -> {
            if (titleField.getText().isBlank()) return;
            String due = dueField.getText().isBlank() ? LocalDate.now().toString() : dueField.getText().trim();
            ioPool.submit(() -> {
                try {
                    TaskDao.insert(user.id(), titleField.getText().trim(), subjectField.getText().trim(), due, priority.getValue());
                    Platform.runLater(() -> { titleField.clear(); subjectField.clear(); dueField.clear(); refreshStatsAndTasks(root); });
                } catch (Exception ex) { Platform.runLater(() -> showError("Could not add task.")); }
            });
        });
        form.getChildren().addAll(titleField, subjectField, dueField, priority, add);

        VBox listBox = new VBox(8);
        listBox.getStyleClass().add("list-box");
        ScrollPane listScroll = new ScrollPane(listBox); listScroll.setFitToWidth(true); listScroll.getStyleClass().add("clean-scroll");
        VBox.setVgrow(listScroll, Priority.ALWAYS);
        page.getChildren().addAll(title, sub, form, listScroll);
        root.setCenter(page);
        refreshStatsAndTasks(root, listBox);
    }

    private void refreshStatsAndTasks(BorderPane root) {
        showTasks();
    }

    private void refreshStatsAndTasks(BorderPane ignored, VBox listBox) {
        ioPool.submit(() -> {
            try {
                List<Task> tasks = TaskDao.findByUser(user.id());
                Platform.runLater(() -> {
                    listBox.getChildren().clear();
                    if (tasks.isEmpty()) {
                        Label empty = new Label("No tasks yet. Add your first one above.");
                        empty.getStyleClass().add("muted");
                        listBox.getChildren().add(empty);
                    } else {
                        for (Task task : tasks) listBox.getChildren().add(taskRow(task));
                    }
                });
            } catch (Exception ex) { Platform.runLater(() -> showError("Could not load tasks.")); }
        });
    }

    private Node taskRow(Task task) {
        HBox row = new HBox(12);
        row.getStyleClass().add("task-row");
        row.setAlignment(Pos.CENTER_LEFT);
        CheckBox done = new CheckBox(); done.setSelected(task.completed());
        done.setOnAction(e -> ioPool.submit(() -> {
            try { TaskDao.toggle(task.id(), done.isSelected()); Platform.runLater(this::loadStatsAsync); }
            catch (Exception ex) { Platform.runLater(() -> showError("Could not update task.")); }
        }));
        VBox text = new VBox(3); HBox.setHgrow(text, Priority.ALWAYS);
        Label title = new Label(task.title()); title.getStyleClass().add("task-title");
        Label meta = new Label((task.subject().isBlank() ? "General" : task.subject()) + "  •  " + (task.dueDate().isBlank() ? "No due date" : task.dueDate())); meta.getStyleClass().add("muted");
        if (task.completed()) { title.getStyleClass().add("completed"); }
        text.getChildren().addAll(title, meta);
        Label p = new Label(task.priority()); p.getStyleClass().add("priority-pill");
        Button delete = new Button("Delete"); delete.getStyleClass().add("subtle-button");
        delete.setOnAction(e -> {
            ioPool.submit(() -> {
                try { TaskDao.delete(task.id()); Platform.runLater(() -> showTasks()); }
                catch (Exception ex) { Platform.runLater(() -> showError("Could not delete task.")); }
            });
        });
        row.getChildren().addAll(done, text, p, delete);
        return row;
    }

    private void showStudy() {
        BorderPane root = (BorderPane) getCenter();
        VBox page = new VBox(18);
        Label title = pageTitle("Study Timer");
        Label sub = new Label("A distraction-light 25 minute focus cycle."); sub.getStyleClass().add("muted");

        VBox timerCard = new VBox(12); timerCard.getStyleClass().add("card"); timerCard.setAlignment(Pos.CENTER);
        timerLabel.getStyleClass().add("timer-label");
        timerStatus.getStyleClass().add("muted");
        timerProgress.setMaxWidth(380);
        timerProgress.setPrefWidth(380);

        subjectBox.setValue("Programming"); subjectBox.setPrefWidth(220);
        Button start = new Button("Start"); start.getStyleClass().add("primary-button");
        Button reset = new Button("Reset"); reset.getStyleClass().add("subtle-button");
        start.setOnAction(e -> toggleTimer(start));
        reset.setOnAction(e -> resetTimer(start));
        HBox actions = new HBox(10, start, reset); actions.setAlignment(Pos.CENTER);

        timerCard.getChildren().addAll(new Label("Focus subject"), subjectBox, timerLabel, timerProgress, actions, timerStatus);

        VBox details = new VBox(8); details.getStyleClass().add("card");
        details.getChildren().addAll(sectionTitle("How it works"), new Label("Start the timer, stay focused, and when the cycle ends your study minutes are stored in SQLite automatically."));
        details.getStyleClass().add("helper-card");

        page.getChildren().addAll(title, sub, timerCard, details);
        root.setCenter(page);
    }

    private void toggleTimer(Button button) {
        if (running) {
            running = false;
            if (timerFuture != null) timerFuture.cancel(false);
            button.setText("Start");
            timerStatus.setText("Paused");
            return;
        }
        running = true; button.setText("Pause"); timerStatus.setText("Focus mode is running");
        timerFuture = timerExecutor.scheduleAtFixedRate(() -> {
            if (!running) return;
            remainingSeconds--;
            Platform.runLater(() -> {
                updateTimerUi();
                if (remainingSeconds <= 0) {
                    running = false;
                    if (timerFuture != null) timerFuture.cancel(false);
                    button.setText("Start");
                    timerStatus.setText("Session complete — nice work");
                    saveCompletedSession();
                    remainingSeconds = 25 * 60;
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void saveCompletedSession() {
        String subject = subjectBox.getValue() == null ? "Other" : subjectBox.getValue();
        ioPool.submit(() -> {
            try {
                TaskDao.saveSession(user.id(), subject, 25, LocalDateTime.now().toString());
                loadStatsAsync();
            } catch (Exception ex) { Platform.runLater(() -> showError("Session finished, but could not be saved.")); }
        });
    }

    private void resetTimer(Button start) {
        running = false;
        if (timerFuture != null) timerFuture.cancel(false);
        remainingSeconds = 25 * 60;
        start.setText("Start");
        timerStatus.setText("Ready for a focused session");
        updateTimerUi();
    }

    private void updateTimerUi() {
        int mins = remainingSeconds / 60;
        int secs = remainingSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", mins, secs));
        timerProgress.setProgress(1.0 - (remainingSeconds / (25.0 * 60.0)));
    }

    private void loadStatsAsync() {
        ioPool.submit(() -> {
            try {
                int open = TaskDao.countOpen(user.id());
                int done = TaskDao.countDone(user.id());
                int minutes = TaskDao.countTodaySessions(user.id());
                Platform.runLater(() -> {
                    openCount.setText(String.valueOf(open));
                    doneCount.setText(String.valueOf(done));
                    focusCount.setText(minutes + "m");
                });
            } catch (Exception ignored) { }
        });
    }

    private void loadQuoteAsync() {
        quoteText.setText("Loading a small dose of motivation…");
        ApiService.fetchQuoteAsync().whenComplete((quote, error) -> Platform.runLater(() -> {
            if (error != null) {
                quoteText.setText("Focus on the next useful step, not the whole mountain.");
                quoteAuthor.setText("— StudyFlow");
            } else {
                quoteText.setText("“" + quote.text() + "”");
                quoteAuthor.setText("— " + quote.author());
            }
        }));
    }

    private VBox metricCard(String label, Label value) {
        VBox box = new VBox(5); box.getStyleClass().add("metric-card");
        Label l = new Label(label); l.getStyleClass().add("muted");
        box.getChildren().addAll(l, value); return box;
    }

    private Label metricValue(String text) { Label l = new Label(text); l.getStyleClass().add("metric-value"); return l; }
    private Label pageTitle(String text) { Label l = new Label(text); l.getStyleClass().add("page-title"); return l; }
    private Label sectionTitle(String text) { Label l = new Label(text); l.getStyleClass().add("card-title"); return l; }

    private void showError(String text) { Alert a = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK); a.setHeaderText(null); a.showAndWait(); }

    private void shutdownWorkers() { ioPool.shutdownNow(); timerExecutor.shutdownNow(); }
}
