package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Client;
import model.Voyage;
import model.Reservation;
import service.VoyageService;
import service.ReservationService;
import service.ClientService;

import java.util.Date;
import java.util.List;

public class ClientReservationController {

    @FXML
    private Label clientInfoLabel;
    @FXML
    private TableView<Voyage> voyageTable;
    @FXML
    private TableColumn<Voyage, String> referenceColumn;
    @FXML
    private TableColumn<Voyage, String> destinationColumn;
    @FXML
    private TableColumn<Voyage, String> dateDepartColumn;
    @FXML
    private TableColumn<Voyage, String> prixColumn;
    @FXML
    private Spinner<Integer> nbPlaceSpinner;
    @FXML
    private Label messageLabel;
    @FXML
    private TextField emailField;

    private VoyageService voyageService;
    private ReservationService reservationService;
    private ClientService clientService;
    private ObservableList<Voyage> voyageList;
    private Client currentClient;

    @FXML
    public void initialize() {
        clientService = new ClientService();
        voyageService = new VoyageService();
        reservationService = new ReservationService();
        voyageList = FXCollections.observableArrayList();

        setupTableColumns();

        nbPlaceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    public void setCurrentClient(Client client) {
        this.currentClient = client;
        if (client != null) {
            emailField.setText(client.getEmail());
            messageLabel.setText("Client connecté : " + client.getNom());
            loadVoyages(); // Load voyages when the client is set
        }
    }

    private void updateClientInfo() {
        if (currentClient != null) {
            clientInfoLabel.setText("Client: " + currentClient.getNom() + " (" + currentClient.getCode_cli() + ")");
        }
    }

    private void setupTableColumns() {
        referenceColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getReference()));
        destinationColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDestination()));
        dateDepartColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateDepart().toString()));
        prixColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrixParPersonne() + " €"));
    }

    private void loadVoyages() {
        List<Voyage> voyages = voyageService.getAllVoyages();
        voyageList.setAll(voyages);
        voyageTable.setItems(voyageList);
    }

    @FXML
    private void handleReservation() {
        if (currentClient == null) {
            messageLabel.setText("Erreur: Aucun client connecté.");
            return;
        }

        Voyage selectedVoyage = voyageTable.getSelectionModel().getSelectedItem();
        if (selectedVoyage == null) {
            messageLabel.setText("Veuillez sélectionner un voyage.");
            return;
        }

        int nbPlaces = nbPlaceSpinner.getValue();

        Reservation newReservation = new Reservation();
        newReservation.setClient(currentClient);
        newReservation.setVoyage(selectedVoyage);
        newReservation.setDateReservation(new Date());
        newReservation.setNbPlace(nbPlaces);

        reservationService.createReservation(newReservation);

        messageLabel.setText("Réservation effectuée avec succès !");
        loadVoyages(); // Refresh the voyage list after reservation
    }

}

