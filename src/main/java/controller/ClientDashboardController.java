package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.Client;
import model.Avis;
import service.AvisService;

import java.io.IOException;
import java.sql.Date;

public class ClientDashboardController {

    @FXML private Label statusLabel;
    @FXML private TextArea noteTextArea;
    @FXML private TextArea avisTextArea;
    @FXML private Button submitAvisButton;

    private Client currentClient;
    private AvisService avisService;

    public void initialize() {
        avisService = new AvisService();
    }

    public void setCurrentClient(Client client) {
        this.currentClient = client;
        updateWelcomeMessage();
    }

    private void updateWelcomeMessage() {
        if (currentClient != null) {
            statusLabel.setText("Bienvenue, " + currentClient.getNom() + "!");
        }
    }

    @FXML
    private void handleReserverVoyage() {
        loadView("/client-reservation-view.fxml", "Réserver un Voyage");
    }

    @FXML
    private void handlePersonnaliserVoyage() {
        loadView("/personnaliser-voyage-view.fxml", "Personnaliser un Voyage");
    }

    @FXML
    private void handleAide() {
        loadView("/aide-view.fxml", "Aide");
    }

    @FXML
    private void handleSubmitAvis() {
        if (currentClient == null) {
            showAlert("Erreur", "Vous devez être connecté pour soumettre un avis.");
            return;
        }

        String note = noteTextArea.getText();
        String avisText = avisTextArea.getText();

        if (note.isEmpty() || avisText.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir à la fois la note et l'avis.");
            return;
        }

        Avis avis = new Avis();
        avis.setIdClient(currentClient.getId());
        avis.setNote(Integer.parseInt(note));
        avis.setCommentaire(avisText);
        avis.setDateAvis(new Date(System.currentTimeMillis()));

        avisService.createAvis(avis);

        showAlert("Succès", "Votre avis a été soumis avec succès. Merci pour votre retour !");
        noteTextArea.clear();
        avisTextArea.clear();
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginView = loader.load();
            Scene scene = new Scene(loginView);
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Agence de Voyage - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue de connexion");
        }
    }

    private void loadView(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent view = loader.load();
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(new Scene(view));
            stage.setTitle(title);

            // If the loaded view has a controller that needs the current client
            Object controller = loader.getController();
            if (controller instanceof ClientAwareController) {
                ((ClientAwareController) controller).setCurrentClient(currentClient);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue : " + title);
        }
    }

    private void showAlert(String title, String content) {
        // Implement this method to show alerts
    }

    // Interface for controllers that need to be aware of the current client
    public interface ClientAwareController {
        void setCurrentClient(Client client);
    }
}

