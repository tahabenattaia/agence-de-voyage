package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controller.MainController;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);

            primaryStage.setTitle("Gestion de Voyages");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

