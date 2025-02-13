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
import model.Itineraire;
import model.Jour;
import model.Voyage;
import service.ItineraireService;
import service.VoyageService;

import java.util.List;

public class ItineraireController {

    @FXML private ComboBox<Voyage> voyageComboBox; // Modification pour contenir l'objet Voyage
    @FXML private TableView<Jour> jourTable;
    @FXML private TableColumn<Jour, Integer> jourColumn;
    @FXML private TableColumn<Jour, String> descriptionColumn;
    @FXML private TextField jourField;
    @FXML private TextArea descriptionArea;
    @FXML private Button ajouterJourButton;
    @FXML private Button modifierJourButton;
    @FXML private Button supprimerJourButton;
    @FXML private Label messageLabel;
    @FXML private TableColumn<Jour, String> voyageColumn;

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
                System.out.println("Voyage sélectionné : " + newSelection.getDestination() + " - " + newSelection.getDateDepart());
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

            // Vérifie combien de voyages sont retournés
            System.out.println("Nombre de voyages récupérés : " + voyages.size());
            for (Voyage v : voyages) {
                System.out.println("Voyage : " + v.getDestination() + " - " + v.getDateDepart());
            }

            voyageComboBox.setItems(FXCollections.observableArrayList(voyages));


        // Utilisation d'un CellFactory pour afficher le nom du voyage et la date dans le ComboBox
        voyageComboBox.setCellFactory(param -> new ListCell<Voyage>() {
            @Override
            protected void updateItem(Voyage voyage, boolean empty) {
                super.updateItem(voyage, empty);
                if (empty || voyage == null) {
                    setText(null);
                } else {
                    // Affichage du voyage sans texte supplémentaire indésirable
                    String displayText = voyage.getDestination() + " - " + voyage.getDateDepart().toString(); // Modifie ici selon tes besoins
                    setText(displayText);
                }
            }
        });

        // Utilisation du même format d'affichage pour les éléments de la liste dans le ComboBox
        voyageComboBox.setButtonCell(new ListCell<Voyage>() {
            @Override
            protected void updateItem(Voyage voyage, boolean empty) {
                super.updateItem(voyage, empty);
                if (empty || voyage == null) {
                    setText(null);
                } else {
                    // Format d'affichage pour le bouton
                    String displayText = voyage.getDestination() + " - " + voyage.getDateDepart().toString(); // Modifie ici selon tes besoins
                    setText(displayText);
                }
            }
        });

        // Charger les voyages dans la ComboBox
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

        // Remove the jour from the currentItineraire
        currentItineraire.removeJour(selectedJour);

        // Update the itineraire in the database
        itineraireService.updateItineraire(currentItineraire);

        // Remove the jour from the observable list
        jourList.remove(selectedJour);

        clearFields();
        messageLabel.setText("Jour supprimé avec succès.");
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

    private void clearFields() {
        jourField.clear();
        descriptionArea.clear();
    }
}
