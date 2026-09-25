package com.studyflow;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.sql.SQLException;

public class RegisterDialog extends Dialog<Void> {
    private final TextField name = new TextField();
    private final TextField email = new TextField();
    private final PasswordField password = new PasswordField();
    private final PasswordField confirm = new PasswordField();

    public RegisterDialog(Window owner) {
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        setTitle("Create Account");

        VBox root = new VBox(12);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("dialog-root");
        name.setPromptText("Full name");
        email.setPromptText("Email");
        password.setPromptText("Password");
        confirm.setPromptText("Confirm password");
        root.getChildren().addAll(new Label("Create your StudyFlow account"), name, email, password, confirm);

        ButtonType create = new ButtonType("Create account", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().setContent(root);
        getDialogPane().getButtonTypes().addAll(create, ButtonType.CANCEL);
        getDialogPane().getStylesheets().add(App.class.getResource("/app.css").toExternalForm());
        NodeStyle.apply(getDialogPane());
        getDialogPane().lookupButton(create).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!createAccount()) event.consume();
        });
    }

    private boolean createAccount() {
        if (name.getText().isBlank() || email.getText().isBlank() || password.getText().isBlank()) {
            showError("Please fill in all fields.");
            return false;
        }
        if (!password.getText().equals(confirm.getText())) {
            showError("Passwords do not match.");
            return false;
        }
        if (password.getText().length() < 4) {
            showError("Use a password with at least 4 characters.");
            return false;
        }
        try {
            UserDao.create(name.getText(), email.getText(), password.getText());
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Account created. You can now log in.", ButtonType.OK);
            a.setHeaderText(null);
            a.showAndWait();
            return true;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
                showError("That email is already registered.");
            } else {
                showError("Could not create the account.");
            }
            return false;
        }
    }

    private void showError(String text) {
        Alert a = new Alert(Alert.AlertType.ERROR, text, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private static final class NodeStyle {
        static void apply(javafx.scene.Node n) { n.getStyleClass().add("dark-dialog"); }
    }
}
