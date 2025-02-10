package org.example;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
        Parent root = fxmlLoader.load();

        // ✅ Pass the primary stage to the controller
        MainController controller = fxmlLoader.getController();
        controller.setPrimaryStage(stage);

        Scene scene = new Scene(root, 800, 600);


        stage.setTitle("Agence de Voyage");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
