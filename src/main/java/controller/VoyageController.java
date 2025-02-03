package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        String reference = referenceField.getText();
        String destination = destinationField.getText();
        int prix = Integer.parseInt(prixField.getText());
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
        newVoyage.setDateDepart(Date.valueOf(dateDepartPicker.getValue()));
        newVoyage.setDateRetour(Date.valueOf(dateRetourPicker.getValue()));

        voyageService.createVoyage(newVoyage);
        loadVoyages();
        clearFields();
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

    public void handleClearFields() {
        clearFields();
    }
}

