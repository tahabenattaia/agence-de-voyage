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

    private VoyageService voyageService;
    private ReservationService reservationService;
    private ObservableList<Voyage> voyageList;
    private Client currentClient;

    public void initialize() {
        voyageService = new VoyageService();
        reservationService = new ReservationService();
        voyageList = FXCollections.observableArrayList();

        setupTableColumns();
        loadVoyages();

        nbPlaceSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    public void setCurrentClient(Client client) {
        this.currentClient = client;
        updateClientInfo();
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
        prixColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrixParPersonne() + " DT"));
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
    }
}

