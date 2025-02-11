package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label statusLabel;

    private Stage primaryStage;

    public void initialize() {
        // Initialisation du contrôleur
        updateStatus("Prêt");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void showVoyagesView() {
        loadView("/voyage-view.fxml", "Gestion des Voyages");
    }

    @FXML
    private void showClientsView() {
        loadView("/client-view.fxml", "Gestion des Clients");
    }

    @FXML
    private void showReservationsView() {
        loadView("/reservation-view.fxml", "Gestion des Réservations");
    }

    @FXML
    private void showItinerairesView() {
        loadView("/itineraire-view.fxml", "Gestion des Itinéraires");
    }

    @FXML
    private void showClientReservationView() {
        loadView("/client-reservation-view.fxml", "Réservation Client");
    }

    private void loadView(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            updateStatus("Vue chargée : " + title);
            primaryStage.setTitle("Gestion de Voyages - " + title);
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Erreur lors du chargement de la vue : " + title);
        }
    }
    @FXML
    private void showClientReservationsView() {
        loadView( "/client-reservation-view.fxml", "Gestion des Réservations");
    }
    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        // Vous pouvez implémenter ici l'affichage d'une boîte de dialogue "À propos"
        updateStatus("À propos de l'application");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}

