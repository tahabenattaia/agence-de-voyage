package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Client;
import model.VoyagePersonnalise;
import service.VoyageService;

import java.time.LocalDate;

public class PersonnaliserVoyageController implements ClientDashboardController.ClientAwareController {

    @FXML private TextField destinationField;
    @FXML private DatePicker dateDepartPicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private TextField budgetField;
    @FXML private TextArea preferencesArea;
    @FXML private ComboBox<String> typeVoyageCombo;
    @FXML private Spinner<Integer> nombrePersonnesSpinner;
    @FXML private Label statusLabel;

    private Client currentClient;
    private VoyageService voyageService;

    public void initialize() {
        voyageService = new VoyageService();
        typeVoyageCombo.getItems().addAll("Culturel", "Aventure", "Détente", "Gastronomique");
        nombrePersonnesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    @Override
    public void setCurrentClient(Client client) {
        this.currentClient = client;
    }

    @FXML
    private void handleSubmitRequest() {
        if (currentClient == null) {
            showAlert("Erreur", "Vous devez être connecté pour soumettre une demande.");
            return;
        }

        String destination = destinationField.getText();
        LocalDate dateDepart = dateDepartPicker.getValue();
        LocalDate dateRetour = dateRetourPicker.getValue();
        String budget = budgetField.getText();
        String preferences = preferencesArea.getText();
        String typeVoyage = typeVoyageCombo.getValue();
        int nombrePersonnes = nombrePersonnesSpinner.getValue();

        if (destination.isEmpty() || dateDepart == null || dateRetour == null || budget.isEmpty() ||
                preferences.isEmpty() || typeVoyage == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        VoyagePersonnalise voyagePersonnalise = new VoyagePersonnalise();
        voyagePersonnalise.setReference("VP" + System.currentTimeMillis());
        voyagePersonnalise.setDestination(destination);
        voyagePersonnalise.setDateDepart(java.sql.Date.valueOf(dateDepart));
        voyagePersonnalise.setDateRetour(java.sql.Date.valueOf(dateRetour));
        voyagePersonnalise.setPrixParPersonne(Integer.parseInt(budget));
        voyagePersonnalise.setPreference(preferences);
        voyagePersonnalise.setDescriptif("Type: " + typeVoyage + ", Nombre de personnes: " + nombrePersonnes);

        voyageService.createVoyage(voyagePersonnalise);

        showAlert("Succès", "Votre demande de voyage personnalisé a été soumise avec succès!");
        clearFields();
        updateStatus("Demande soumise avec succès");
    }

    @FXML
    private void handleClearFields() {
        clearFields();
        updateStatus("Champs effacés");
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

    private void clearFields() {
        destinationField.clear();
        dateDepartPicker.setValue(null);
        dateRetourPicker.setValue(null);
        budgetField.clear();
        preferencesArea.clear();
        typeVoyageCombo.getSelectionModel().clearSelection();
        nombrePersonnesSpinner.getValueFactory().setValue(1);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}

