package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AdminReservation;
import model.Client;
import model.Voyage;
import service.AdminReservationService;
import javafx.event.ActionEvent;

import java.awt.Label;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AdminReservationController {

    @FXML private TableView<AdminReservation> reservationTable;
    @FXML private TableColumn<AdminReservation, Long> idReservationColumn;
    @FXML private TableColumn<AdminReservation, String> clientColumn;
    @FXML private TableColumn<AdminReservation, String> voyageColumn;
    @FXML private TableColumn<AdminReservation, String> dateReservationColumn;
    @FXML private TableColumn<AdminReservation, Integer> nbPlaceColumn;
    @FXML private TableColumn<AdminReservation, String> statutColumn;
    @FXML
    private ComboBox<Client> clientCombo;
    @FXML
    private ComboBox<Voyage> voyageCombo;
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

        // 🔍 Ajout pour debug
        System.out.println("Données récupérées :");
        for (AdminReservation r : reservationList) {
            System.out.println("ID: " + r.getId() + ", Client: " + r.getClient().getNom() +
                    ", Voyage: " + r.getVoyage().getDestination());
        }

        reservationTable.setItems(reservationList);
    }





    @FXML
    private void handleUpdateReservation(ActionEvent event) {
        // Récupérer l'objet sélectionné dans la table
        AdminReservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            // Récupérer l'ID de la réservation
            String idReservation = idAdminReservationField.getText().trim();  // Supprimer les espaces blancs

            if (idReservation.isEmpty()) {
                // Gérer le cas où l'ID est vide
                System.out.println("L'ID de la réservation est vide.");
                // Vous pouvez afficher un message d'erreur à l'utilisateur ici
                return;  // Sortir de la méthode si l'ID est vide
            }

            // Convertir l'ID en long
            try {
                selectedReservation.setId(Long.parseLong(idReservation));  // Conversion de l'ID en long
            } catch (NumberFormatException e) {
                // Gérer l'erreur si l'ID n'est pas un nombre valide
                System.out.println("Erreur de format pour l'ID : " + idReservation);
                return;  // Sortir de la méthode si l'ID est invalide
            }

            // Récupérer les autres valeurs des champs
            Client selectedClient = clientCombo.getValue();  // L'objet Client sélectionné
            Voyage selectedVoyage = voyageCombo.getValue();  // L'objet Voyage sélectionné
            LocalDate selectedDate = dateReservationPicker.getValue();  // La date de réservation
            Integer nbPlaces = nbPlaceSpinner.getValue();  // Le nombre de places
            String statut = statutCombo.getValue();  // Le statut de la réservation

            // Mettre à jour l'objet sélectionné
            selectedReservation.setClient(selectedClient);
            selectedReservation.setVoyage(selectedVoyage);
            selectedReservation.setDateReservation(java.sql.Date.valueOf(selectedDate));
            selectedReservation.setNbPlace(nbPlaces);
            selectedReservation.setStatus(statut);

            // Sauvegarder ou mettre à jour la réservation dans le service ou la base de données
            reservationService.updateReservation(selectedReservation);
            reservationTable.refresh();

        }
        else {
            // Message d'erreur si aucune réservation n'est sélectionnée
            showAlert("Sélectionnez une réservation à mettre à jour", Alert.AlertType.WARNING);
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
            reservationService.deleteReservation(selectedReservation.getId());
            loadReservations();
        } else {
            showAlert("Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à supprimer.");
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        // Code pour retourner à l'écran précédent
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}