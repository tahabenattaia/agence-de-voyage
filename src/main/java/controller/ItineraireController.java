package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Itineraire;
import model.Jour;
import model.Voyage;
import service.ItineraireService;
import service.VoyageService;

import java.util.List;

public class ItineraireController {

    @FXML private ComboBox<Voyage> voyageComboBox;
    @FXML private TableView<Jour> jourTable;
    @FXML private TableColumn<Jour, Integer> jourColumn;
    @FXML private TableColumn<Jour, String> descriptionColumn;
    @FXML private TextField jourField;
    @FXML private TextArea descriptionArea;
    @FXML private Button ajouterJourButton;
    @FXML private Button modifierJourButton;
    @FXML private Button supprimerJourButton;
    @FXML private Label messageLabel;

    private ItineraireService itineraireService;
    private VoyageService voyageService;
    private ObservableList<Jour> jourList;
    private Itineraire currentItineraire;

    public void initialize() {
        itineraireService = new ItineraireService();
        voyageService = new VoyageService();
        jourList = FXCollections.observableArrayList();

        setupComboBox();
        setupTableColumns();

        voyageComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadItineraire(newSelection);
            }
        });

        jourTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showJourDetails(newSelection);
            }
        });
    }

    private void setupComboBox() {
        List<Voyage> voyages = voyageService.getAllVoyages();
        voyageComboBox.setItems(FXCollections.observableArrayList(voyages));
    }

    private void setupTableColumns() {
        jourColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getJour()).asObject());
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescription()));
    }

    private void loadItineraire(Voyage voyage) {
        currentItineraire = itineraireService.getItineraireByVoyageId(voyage.getId())
                .orElseGet(() -> {
                    Itineraire newItineraire = new Itineraire();
                    itineraireService.createItineraire(newItineraire, voyage.getId());
                    return newItineraire;
                });
        jourList.setAll(currentItineraire.getJours());
        jourTable.setItems(jourList);
    }

    private void showJourDetails(Jour jour) {
        jourField.setText(String.valueOf(jour.getJour()));
        descriptionArea.setText(jour.getDescription());
    }

    @FXML
    private void handleAjouterJour() {
        if (currentItineraire == null) {
            messageLabel.setText("Veuillez sélectionner un voyage d'abord.");
            return;
        }

        try {
            int jourNumber = Integer.parseInt(jourField.getText());
            String description = descriptionArea.getText();

            Jour newJour = new Jour(jourNumber, description);
            currentItineraire.ajouterJour(newJour);
            itineraireService.updateItineraire(currentItineraire);

            jourList.add(newJour);
            clearFields();
            messageLabel.setText("Jour ajouté avec succès.");
        } catch (NumberFormatException e) {
            messageLabel.setText("Veuillez entrer un numéro de jour valide.");
        }
    }

    @FXML
    private void handleModifierJour() {
        Jour selectedJour = jourTable.getSelectionModel().getSelectedItem();
        if (selectedJour == null) {
            messageLabel.setText("Veuillez sélectionner un jour à modifier.");
            return;
        }

        try {
            int jourNumber = Integer.parseInt(jourField.getText());
            String description = descriptionArea.getText();

            selectedJour.setJour(jourNumber);
            selectedJour.setDescription(description);
            itineraireService.updateItineraire(currentItineraire);

            jourTable.refresh();
            clearFields();
            messageLabel.setText("Jour modifié avec succès.");
        } catch (NumberFormatException e) {
            messageLabel.setText("Veuillez entrer un numéro de jour valide.");
        }
    }

    @FXML
    private void handleSupprimerJour() {
        Jour selectedJour = jourTable.getSelectionModel().getSelectedItem();
        if (selectedJour == null) {
            messageLabel.setText("Veuillez sélectionner un jour à supprimer.");
            return;
        }

        currentItineraire.getJours().remove(selectedJour);
        itineraireService.updateItineraire(currentItineraire);

        jourList.remove(selectedJour);
        clearFields();
        messageLabel.setText("Jour supprimé avec succès.");
    }

    private void clearFields() {
        jourField.clear();
        descriptionArea.clear();
    }
}

