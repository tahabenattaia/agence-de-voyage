package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.Voyage;
import model.VoyageOrganise;
import model.VoyagePersonnalise;
import service.VoyageService;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

public class VoyageController {

    @FXML private TableView<Voyage> voyageTable;
    @FXML private TextField referenceField;
    @FXML private TextField destinationField;
    @FXML private TextField prixField;
    @FXML private DatePicker dateDepartPicker;
    @FXML private DatePicker dateRetourPicker;
    @FXML private TextArea descriptifArea;
    @FXML private ComboBox<String> typeVoyageCombo;

    private VoyageService voyageService;
    private ObservableList<Voyage> voyageList;

    public void initialize() {
        voyageService = new VoyageService();
        voyageList = FXCollections.observableArrayList();
        typeVoyageCombo.getItems().addAll("Organisé", "Personnalisé");
        loadVoyages();
    }

    private void loadVoyages() {
        List<Voyage> voyages = voyageService.getAllVoyages();
        voyageList.setAll(voyages);
        voyageTable.setItems(voyageList);
    }

    @FXML
    private void handleCreateVoyage() {
        // Vérification des champs obligatoires
        if (referenceField.getText().isEmpty() ||
                destinationField.getText().isEmpty() ||
                prixField.getText().isEmpty() ||
                dateDepartPicker.getValue() == null ||
                dateRetourPicker.getValue() == null ||
                typeVoyageCombo.getValue() == null) {

            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.", Alert.AlertType.ERROR);
            return;
        }

        String reference = referenceField.getText();
        String destination = destinationField.getText();
        int prix;
        try {
            prix = Integer.parseInt(prixField.getText());
        } catch (NumberFormatException ex) {
            showAlert("Erreur", "Le prix doit être un nombre valide.", Alert.AlertType.ERROR);
            return;
        }
        String descriptif = descriptifArea.getText();
        String type = typeVoyageCombo.getValue();

        Voyage newVoyage;
        if ("Organisé".equals(type)) {
            // Création d'un voyage organisé
            VoyageOrganise voyageOrganise = new VoyageOrganise();
            // Si vous disposez d'un DatePicker pour la date de validité (par exemple, dateValiditePicker),
            // décommentez et adaptez la ligne suivante pour renseigner ce champ :
            // voyageOrganise.setDateValidite(dateValiditePicker.getValue() != null ?
            //      java.sql.Date.valueOf(dateValiditePicker.getValue()) : null);
            newVoyage = voyageOrganise;
        } else {
            // Création d'un voyage personnalisé
            newVoyage = new VoyagePersonnalise();
        }

        newVoyage.setReference(reference);
        newVoyage.setDestination(destination);
        newVoyage.setPrixParPersonne(prix);
        newVoyage.setDescriptif(descriptif);
        newVoyage.setDateDepart(Date.valueOf(dateDepartPicker.getValue()));
        newVoyage.setDateRetour(Date.valueOf(dateRetourPicker.getValue()));

        // Appel du service pour créer le voyage
        voyageService.createVoyage(newVoyage);
        loadVoyages();
        clearFields();
    }

    // Méthode utilitaire pour afficher des messages d'alerte (à adapter selon vos besoins)
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleUpdateVoyage() {
        Voyage selectedVoyage = voyageTable.getSelectionModel().getSelectedItem();
        if (selectedVoyage != null) {
            selectedVoyage.setReference(referenceField.getText());
            selectedVoyage.setDestination(destinationField.getText());
            selectedVoyage.setPrixParPersonne(Integer.parseInt(prixField.getText()));
            selectedVoyage.setDescriptif(descriptifArea.getText());
            selectedVoyage.setDateDepart(Date.valueOf(dateDepartPicker.getValue()));
            selectedVoyage.setDateRetour(Date.valueOf(dateRetourPicker.getValue()));

            voyageService.updateVoyage(selectedVoyage);
            loadVoyages();
            clearFields();
        }
    }

    @FXML
    private void handleDeleteVoyage() {
        Voyage selectedVoyage = voyageTable.getSelectionModel().getSelectedItem();
        if (selectedVoyage != null) {
            voyageService.deleteVoyage(selectedVoyage.getId());
            loadVoyages();
            clearFields();
        }
    }

    @FXML
    private void handleSelectVoyage() {
        Voyage selectedVoyage = voyageTable.getSelectionModel().getSelectedItem();
        if (selectedVoyage != null) {
            referenceField.setText(selectedVoyage.getReference());
            destinationField.setText(selectedVoyage.getDestination());
            prixField.setText(String.valueOf(selectedVoyage.getPrixParPersonne()));
            descriptifArea.setText(selectedVoyage.getDescriptif());
            typeVoyageCombo.setValue(selectedVoyage instanceof VoyageOrganise ? "Organisé" : "Personnalisé");
        }
    }

    private void clearFields() {
        referenceField.clear();
        destinationField.clear();
        prixField.clear();
        descriptifArea.clear();
        dateDepartPicker.setValue(null);
        dateRetourPicker.setValue(null);
        typeVoyageCombo.getSelectionModel().clearSelection();
    }
    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            // Charger l'interface principale (Main)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer par la nouvelle
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleClearFields() {
        clearFields();
    }
}

