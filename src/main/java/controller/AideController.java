package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Client;

public class AideController implements ClientDashboardController.ClientAwareController {

    @FXML
    private Label statusLabel;

    private Client currentClient;

    @Override
    public void setCurrentClient(Client client) {
        this.currentClient = client;
        updateStatus(client != null ? "Bienvenue, " + client.getNom() + "!" : "Bienvenue !");
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            // Charger l'interface principale (Main)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la nouvelle
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}

