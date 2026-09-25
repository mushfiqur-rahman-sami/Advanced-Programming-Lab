package com.studyflow;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private Stage stage;
    private static final String CSS = App.class.getResource("/app.css").toExternalForm();

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        Database.init();
        showLogin();
        primaryStage.setTitle("StudyFlow");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(620);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public void showLogin() {
        LoginView view = new LoginView(this::showDashboard);
        setScene(view);
    }

    public void showDashboard(User user) {
        DashboardView view = new DashboardView(user, this::showLogin);
        setScene(view);
    }

    private void setScene(javafx.scene.Parent root) {
        Scene scene = new Scene(root, 1280, 820);
        scene.getStylesheets().add(CSS);
        stage.setScene(scene);
    }

    @Override
    public void stop() {
        Database.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
