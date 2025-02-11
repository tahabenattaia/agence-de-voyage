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
        prixColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrixParPersonne() + " €"));
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

        currentClient = clientService.getClientByCodeAndNom(codeClient, nom);
        if (currentClient != null) {
            messageLabel.setText("Client identifié : " + currentClient.getNom());
        } else {
            messageLabel.setText("Client non trouvé. Veuillez vérifier vos informations.");
        }
    }

    @FXML
    private void handleReservation() {
        if (currentClient == null) {
            messageLabel.setText("Veuillez vous identifier d'abord.");
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

}

