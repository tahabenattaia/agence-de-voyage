package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.AdminReservation;
import model.Client;
import model.Voyage;
import service.AdminReservationService;
import service.VoyageService;
import service.ClientService;
import javafx.event.ActionEvent;

import java.awt.Label;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class AdminReservationController {

    @FXML private TableView<AdminReservation> reservationTable;
    @FXML private TableColumn<AdminReservation, String> idReservationColumn;
    @FXML private TableColumn<AdminReservation, String> clientColumn;
    @FXML private TableColumn<AdminReservation, String> voyageColumn;
    @FXML private TableColumn<AdminReservation, String> dateReservationColumn;
    @FXML private TableColumn<AdminReservation, Integer> nbPlaceColumn;
    @FXML private TableColumn<AdminReservation, String> statutColumn;
    @FXML
    private TextField client;
    @FXML
    private TextField voyage;
    @FXML
    private DatePicker dateReservationPicker;
    @FXML
    private Spinner<Integer> nbPlaceSpinner;
    @FXML
    private ComboBox<String> statutCombo;
    @FXML
    private TextField idAdminReservationField;

    private AdminReservationService reservationService;
    private ObservableList<AdminReservation> reservationList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void initialize() {
        reservationService = new AdminReservationService();
        reservationList = FXCollections.observableArrayList();

        setupTableColumns();
        loadReservations();

        // Initialise les ComboBox
        initializeClientField();
        initializeVoyageField();
        statutCombo.getItems().addAll("EN_ATTENTE", "ACCEPTE", "REFUSE");

        // Initialise le Spinner pour le nombre de places
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,10000, 1);
        nbPlaceSpinner.setValueFactory(valueFactory);

        // Ajoute un écouteur sur la sélection de la table
        reservationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleSelectReservation(newSelection);
            }
        });
    }

    private void setupTableColumns() {
        idReservationColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClient().getNom()));
        voyageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVoyage().getDestination()));
        dateReservationColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(dateFormat.format(cellData.getValue().getDateReservation())));
        nbPlaceColumn.setCellValueFactory(new PropertyValueFactory<>("nbPlace"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("status")); // Changé de "statut" à "status"
    }

    private void loadReservations() {
        reservationList.setAll(reservationService.getAllReservations());


        System.out.println("Données récupérées :");
        for (AdminReservation r : reservationList) {
            System.out.println("ID: " + r.getId() + ", Client: " + r.getClient().getNom() +
                    ", Voyage: " + r.getVoyage().getDestination());
        }

        reservationTable.setItems(reservationList);
    }

    private void handleSelectReservation(AdminReservation reservation) {
        client.setText(reservation.getClient().getNom());
        voyage.setText(reservation.getVoyage().getDestination());
        java.util.Date utilDateReservation = reservation.getDateReservation();
        java.sql.Date sqlDateReservation = new java.sql.Date(utilDateReservation.getTime());
        dateReservationPicker.setValue(sqlDateReservation.toLocalDate());
        nbPlaceSpinner.getValueFactory().setValue(reservation.getNbPlace());
        statutCombo.setValue(reservation.getStatus());
        idAdminReservationField.setText(reservation.getId().toString());
    }
    private void initializeClientField() {
        ClientService cl = new ClientService();
        List<Client> clients = cl.getAllClients();
        if (!clients.isEmpty()) {
            // Set the TextField to the name of the first client (or any other logic you prefer)
            client.setText(clients.get(0).getNom());
        }
    }

    private void initializeVoyageField() {
        VoyageService vs = new VoyageService();
        List<Voyage> voyages = vs.getAllVoyages();
        if (!voyages.isEmpty()) {
            // Set the TextField to the destination of the first voyage
            voyage.setText(voyages.get(0).getDestination());
        }
    }
    @FXML
    private void handleUpdateReservation(ActionEvent event) {
        AdminReservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            // Récupérer les valeurs des champs
            String clientName = client.getText();       // Changed from clientCombo.getValue()
            String voyageDestination = voyage.getText();
            LocalDate selectedDate = dateReservationPicker.getValue();
            Integer nbPlaces = nbPlaceSpinner.getValue();
            String statut = statutCombo.getValue();

            // Convert text into objects using your services
            Client selectedClient = ClientService.findClientByName(clientName);
            Voyage selectedVoyage = VoyageService.findVoyageByDestination(voyageDestination);

            // Check if the lookups were successful
            if (selectedClient == null) {
                showAlert("Client non trouvé", Alert.AlertType.ERROR);
                return;
            }
            if (selectedVoyage == null) {
                showAlert("Voyage non trouvé", Alert.AlertType.ERROR);
                return;
            }


            // Mettre à jour l'objet sélectionné
            selectedReservation.setClient(selectedClient);
            selectedReservation.setVoyage(selectedVoyage);
            selectedReservation.setDateReservation(java.sql.Date.valueOf(selectedDate));
            selectedReservation.setNbPlace(nbPlaces);
            selectedReservation.setStatus(statut);

            // Sauvegarder ou mettre à jour la réservation
            reservationService.updateReservation(selectedReservation);
            reservationTable.refresh(); // Rafraîchir la table pour afficher les modifications
        } else {
            showAlert("Erreur", "Veuillez sélectionner une réservation à mettre à jour.");
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Attention");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDeleteReservation(ActionEvent event) {
        AdminReservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            reservationService.deleteReservation(selectedReservation.getId().toString());
            loadReservations();
        } else {
            showAlert("Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à supprimer.");
        }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        idAdminReservationField.clear();
        client.clear();
        voyage.clear();
        dateReservationPicker.setValue(null);
        nbPlaceSpinner.getValueFactory().setValue(1);
        statutCombo.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleClearFields(ActionEvent event) {
        clearFields();
    }
}