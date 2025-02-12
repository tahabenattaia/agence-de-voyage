package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import model.Client;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label statusLabel;

    private Stage primaryStage;
    private Client currentClient;

    public void initialize() {
        // Initialization code if needed
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setCurrentClient(Client client) {
        this.currentClient = client;
        updateUIWithClientInfo();
    }

    private void updateUIWithClientInfo() {
        if (currentClient != null) {
            statusLabel.setText("Connecté : " + currentClient.getNom() + (currentClient.isAdmin() ? " (Admin)" : ""));
        }
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
    private void showStatistiquesView() {
        loadView("/statistiques-view.fxml", "Statistiques");
    }

    @FXML
    private void showRechercheView() {
        loadView("/recherche-view.fxml", "Recherche");
    }

    @FXML
    private void handleExit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginView = loader.load();
            Scene scene = new Scene(loginView);
            if (primaryStage != null) {
                primaryStage.setScene(scene);
                primaryStage.setTitle("Agence de Voyage - Login");
            } else {
                // If primaryStage is null, try to get the current stage
                Stage currentStage = (Stage) mainBorderPane.getScene().getWindow();
                currentStage.setScene(scene);
                currentStage.setTitle("Agence de Voyage - Login");
            }
            currentClient = null;
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Erreur lors du chargement de la vue de connexion");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent loginView = loader.load();
            Scene scene = new Scene(loginView);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Agence de Voyage - Login");
            currentClient = null;
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Erreur lors du chargement de la vue de connexion");
        }
    }

    private void loadView(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            mainBorderPane.setCenter(view);
            updateStatus("Vue chargée : " + title);
            if (primaryStage != null) {
                primaryStage.setTitle("Gestion de Voyages - " + title);
            }
        } catch (IOException e) {
            e.printStackTrace();
            updateStatus("Erreur lors du chargement de la vue : " + title);
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
}

