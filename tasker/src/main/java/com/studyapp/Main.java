package com.studyapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource(
                                "/dashboard.fxml"
                        )
                );

        Scene scene =
                new Scene(
                        loader.load(),
                        1200,
                        750
                );

        var css =
                getClass().getResource(
                        "/style.css"
                );

        if (css != null) {
            scene.getStylesheets().add(
                    css.toExternalForm()
            );
        }

        primaryStage.setTitle(
                "Study & Task Management System"
        );

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);
        primaryStage.setResizable(true);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}