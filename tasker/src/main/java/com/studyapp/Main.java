package com.studyapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(
                        getClass().getResource("/dashboard.fxml")
                );

        Parent root = loader.load();

        DashboardController controller =
                loader.getController();


        Scene scene =
                new Scene(
                        root,
                        1200,
                        750
                );


        scene.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );


        primaryStage.setTitle(
                "StudyFlow - Study Manager"
        );

        primaryStage.setScene(scene);

        primaryStage.setMinWidth(900);

        primaryStage.setMinHeight(650);

        primaryStage.setResizable(true);


        /*
         * Properly shut down the thread pool when
         * the JavaFX application is closed.
         */

        primaryStage.setOnCloseRequest(event -> {

            controller.shutdown();
        });


        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}