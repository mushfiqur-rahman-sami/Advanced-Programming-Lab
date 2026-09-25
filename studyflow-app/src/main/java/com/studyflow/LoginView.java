package com.studyflow;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLException;

public class LoginView extends BorderPane {
    private final java.util.function.Consumer<User> onLogin;
    private TextField email;
    private PasswordField password;

    public LoginView(java.util.function.Consumer<User> onLogin) {
        this.onLogin = onLogin;
        build();
    }

    private void build() {
        StackPane backdrop = new StackPane(card());
        backdrop.setPadding(new Insets(32));
        setCenter(backdrop);
    }

    private Node card() {
        VBox box = new VBox(14);
        box.getStyleClass().add("login-card");
        box.setMaxWidth(430);
        box.setPadding(new Insets(34));
        box.setAlignment(Pos.CENTER_LEFT);

        Label app = new Label("StudyFlow");
        app.getStyleClass().add("brand");
        Label title = new Label("Welcome back");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Plan your study. Finish what matters.");
        subtitle.getStyleClass().add("muted");

        email = new TextField();
        email.setPromptText("Email");
        password = new PasswordField();
        password.setPromptText("Password");

        Button login = new Button("Log In");
        login.getStyleClass().add("primary-button");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setOnAction(e -> doLogin());
        password.setOnAction(e -> doLogin());

        Button register = new Button("Create a new account");
        register.getStyleClass().add("link-button");
        register.setOnAction(e -> new RegisterDialog(getScene().getWindow()).showAndWait());

        box.getChildren().addAll(app, title, subtitle, new Region(), email, password, login, register);
        VBox.setMargin(box.getChildren().get(3), new Insets(6, 0, 2, 0));
        return box;
    }

    private void doLogin() {
        String e = email.getText().trim();
        String p = password.getText();
        if (e.isBlank() || p.isBlank()) {
            showError("Please enter both email and password.");
            return;
        }
        try {
            User user = UserDao.authenticate(e, p);
            if (user == null) {
                showError("Incorrect email or password.");
                return;
            }
            onLogin.accept(user);
        } catch (SQLException ex) {
            showError("Could not access the local database.");
        }
    }

    private void showError(String text) {
        Alert a = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
