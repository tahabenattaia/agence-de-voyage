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
import model.Client;
import model.Voyage;
import model.Reservation;
import service.ClientService;
import service.VoyageService;
import service.ReservationService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class ClientReservationController {

    @FXML private TextField codeClientField;
    @FXML private TextField nomField;
    @FXML private TableView<Voyage> voyageTable;
    @FXML private TableColumn<Voyage, String> referenceColumn;
    @FXML private TableColumn<Voyage, String> destinationColumn;
    @FXML private TableColumn<Voyage, String> dateDepartColumn;
    @FXML private TableColumn<Voyage, String> prixColumn;
    @FXML private Spinner<Integer> nbPlaceSpinner;
    @FXML private Label messageLabel;

    private ClientService clientService;
    private VoyageService voyageService;
    private ReservationService reservationService;
    private ObservableList<Voyage> voyageList;
    private Client currentClient;

    public void initialize() {
        clientService = new ClientService();
        voyageService = new VoyageService();
        reservationService = new ReservationService();
        voyageList = FXCollections.observableArrayList();

        setupTableColumns();
        loadVoyages();

        nbPlaceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    private void setupTableColumns() {
        referenceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReference()));
        destinationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDestination()));
        dateDepartColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateDepart().toString()));
        prixColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrixParPersonne() + " DT"));
    }

    private void loadVoyages() {
        List<Voyage> voyages = voyageService.getAllVoyages();
        voyageList.setAll(voyages);
        voyageTable.setItems(voyageList);
    }

    @FXML
    private void handleIdentifyClient() {
        String codeClient = codeClientField.getText();
        String nom = nomField.getText();

        if (codeClient.isEmpty() || nom.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        currentClient = clientService.getClientByCodeAndNom(codeClient, nom);
        if (currentClient != null) {
            messageLabel.setText("Client identifié : " + currentClient.getNom());
        } else {
            showAlert("Erreur", "Client non trouvé. Veuillez vérifier vos informations.");
        }
    }


    @FXML
    private void handleReservation() {
        if (currentClient == null) {
            showAlert("Erreur", "Veuillez vous identifier d'abord.");
            return;
        }

        Voyage selectedVoyage = voyageTable.getSelectionModel().getSelectedItem();
        if (selectedVoyage == null) {
            showAlert("Erreur", "Veuillez sélectionner un voyage.");
            return;
        }

        int nbPlaces = nbPlaceSpinner.getValue();

        Reservation newReservation = new Reservation();
        newReservation.setClient(currentClient);
        newReservation.setVoyage(selectedVoyage);
        newReservation.setDateReservation(new Date());
        newReservation.setNbPlace(nbPlaces);
        newReservation.setStatus("EN_ATTENTE"); // Définit le statut par défaut

        reservationService.createReservation(newReservation);

        showAlert("Succès", "Réservation effectuée avec succès !");
        loadVoyages();
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentClient(Client client) {
        this.currentClient = client;
        if (client != null) {
            codeClientField.setText(client.getCode_cli());
            nomField.setText(client.getNom());
            messageLabel.setText("Client connecté : " + client.getNom());
        }
    }
}

