package com.example;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello JavaFX");
        stage.setScene(new Scene(label, 400, 200));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

