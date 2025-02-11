package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AdminReservation;
import service.AdminReservationService;
import javafx.event.ActionEvent;
import java.text.SimpleDateFormat;

public class AdminReservationController {

    @FXML private TableView<AdminReservation> reservationTable;
    @FXML private TableColumn<AdminReservation, Long> idReservationColumn;
    @FXML private TableColumn<AdminReservation, String> clientColumn;
    @FXML private TableColumn<AdminReservation, String> voyageColumn;
    @FXML private TableColumn<AdminReservation, String> dateReservationColumn;
    @FXML private TableColumn<AdminReservation, Integer> nbPlaceColumn;
    @FXML private TableColumn<AdminReservation, String> statutColumn;

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
        AdminReservation selectedReservation = reservationTable.getSelectionModel().getSelectedItem();
        if (selectedReservation != null) {
            // Ouvrir une boîte de dialogue pour mettre à jour la réservation
            // Mettre à jour la réservation dans la base de données
            reservationService.updateReservation(selectedReservation);
            loadReservations();
        } else {
            showAlert("Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à mettre à jour.");
        }
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