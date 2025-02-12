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
import java.time.ZoneId;
import java.sql.Date;
import java.util.List;


public class VoyageController {

    @FXML private TableView<Voyage> voyageTable;
    @FXML private TableColumn<Voyage, String> referenceColumn;
    @FXML private TableColumn<Voyage, String> destinationColumn;
    @FXML private TableColumn<Voyage, Integer> prixColumn;
    @FXML private TableColumn<Voyage, java.sql.Date> dateDepartColumn;
    @FXML private TableColumn<Voyage, java.sql.Date> dateRetourColumn;
    @FXML private TableColumn<Voyage, String> typeColumn;
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

        setupTableColumns();
        loadVoyages();

        voyageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleSelectVoyage();
            }
        });
    }

    private void setupTableColumns() {
        referenceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReference()));
        destinationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDestination()));
        prixColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getPrixParPersonne()).asObject());
        dateDepartColumn.setCellValueFactory(cellData -> {
            java.util.Date utilDate = cellData.getValue().getDateDepart();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            return new javafx.beans.property.SimpleObjectProperty<>(sqlDate);
        });
        dateRetourColumn.setCellValueFactory(cellData -> {
            java.util.Date utilDate = cellData.getValue().getDateRetour();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            return new javafx.beans.property.SimpleObjectProperty<>(sqlDate);
        });
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue() instanceof VoyageOrganise ? "Organisé" : "Personnalisé"));
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
            newVoyage = new VoyageOrganise();
        } else {
            newVoyage = new VoyagePersonnalise();
        }

        newVoyage.setReference(reference);
        newVoyage.setDestination(destination);
        newVoyage.setPrixParPersonne(prix);
        newVoyage.setDescriptif(descriptif);
        newVoyage.setDateDepart(java.sql.Date.valueOf(dateDepartPicker.getValue()));
        newVoyage.setDateRetour(java.sql.Date.valueOf(dateRetourPicker.getValue()));

        voyageService.createVoyage(newVoyage);
        loadVoyages();
        clearFields();
        showAlert("Succès", "Le voyage a été créé avec succès.", Alert.AlertType.INFORMATION);
    }

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
            selectedVoyage.setDateDepart(java.sql.Date.valueOf(dateDepartPicker.getValue()));
            selectedVoyage.setDateRetour(java.sql.Date.valueOf(dateRetourPicker.getValue()));

            voyageService.updateVoyage(selectedVoyage);
            loadVoyages();
            clearFields();
            showAlert("Succès", "Le voyage a été mis à jour avec succès.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Veuillez sélectionner un voyage à mettre à jour.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDeleteVoyage() {
        Voyage selectedVoyage = voyageTable.getSelectionModel().getSelectedItem();
        if (selectedVoyage != null) {
            voyageService.deleteVoyage(selectedVoyage.getId());
            loadVoyages();
            clearFields();
            showAlert("Succès", "Le voyage a été supprimé avec succès.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Veuillez sélectionner un voyage à supprimer.", Alert.AlertType.ERROR);
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
            java.util.Date utilDateDepart = selectedVoyage.getDateDepart();
            java.util.Date utilDateRetour = selectedVoyage.getDateRetour();
            java.sql.Date sqlDateDepart = new java.sql.Date(utilDateDepart.getTime());
            java.sql.Date sqlDateRetour = new java.sql.Date(utilDateRetour.getTime());
            dateDepartPicker.setValue(sqlDateDepart.toLocalDate());
            dateRetourPicker.setValue(sqlDateRetour.toLocalDate());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Agence de Voyage - Menu Principal");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue principale.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleClearFields() {
        clearFields();
    }
}

