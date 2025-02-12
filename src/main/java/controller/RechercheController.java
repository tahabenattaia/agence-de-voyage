package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Voyage;
import model.Client;
import model.Reservation;
import service.VoyageService;
import service.ClientService;
import service.ReservationService;

import java.util.List;
import java.util.stream.Collectors;

public class RechercheController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private TableView<Object> resultTable;
    @FXML private TableColumn<Object, String> column1;
    @FXML private TableColumn<Object, String> column2;
    @FXML private TableColumn<Object, String> column3;

    private VoyageService voyageService;
    private ClientService clientService;
    private ReservationService reservationService;

    private ObservableList<Object> searchResults;

    public void initialize() {
        voyageService = new VoyageService();
        clientService = new ClientService();
        reservationService = new ReservationService();

        searchResults = FXCollections.observableArrayList();
        resultTable.setItems(searchResults);

        searchTypeCombo.getItems().addAll("Voyages", "Clients", "Réservations");
        searchTypeCombo.setValue("Voyages");

        setupColumns();
    }

    private void setupColumns() {
        column1.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Voyage) {
                return new javafx.beans.property.SimpleStringProperty(((Voyage) cellData.getValue()).getReference());
            } else if (cellData.getValue() instanceof Client) {
                return new javafx.beans.property.SimpleStringProperty(((Client) cellData.getValue()).getCode_cli());
            } else if (cellData.getValue() instanceof Reservation) {
                return new javafx.beans.property.SimpleStringProperty(((Reservation) cellData.getValue()).getId().toString());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        column2.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Voyage) {
                return new javafx.beans.property.SimpleStringProperty(((Voyage) cellData.getValue()).getDestination());
            } else if (cellData.getValue() instanceof Client) {
                return new javafx.beans.property.SimpleStringProperty(((Client) cellData.getValue()).getNom());
            } else if (cellData.getValue() instanceof Reservation) {
                return new javafx.beans.property.SimpleStringProperty(((Reservation) cellData.getValue()).getClient().getNom());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        column3.setCellValueFactory(cellData -> {
            if (cellData.getValue() instanceof Voyage) {
                return new javafx.beans.property.SimpleStringProperty(((Voyage) cellData.getValue()).getPrixParPersonne() + " DT");
            } else if (cellData.getValue() instanceof Client) {
                return new javafx.beans.property.SimpleStringProperty(((Client) cellData.getValue()).getTelephone());
            } else if (cellData.getValue() instanceof Reservation) {
                return new javafx.beans.property.SimpleStringProperty(((Reservation) cellData.getValue()).getVoyage().getDestination());
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        String searchType = searchTypeCombo.getValue();

        searchResults.clear();

        switch (searchType) {
            case "Voyages":
                List<Voyage> voyages = voyageService.getAllVoyages();
                searchResults.addAll(voyages.stream()
                        .filter(v -> v.getReference().toLowerCase().contains(searchTerm) ||
                                v.getDestination().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList()));
                break;
            case "Clients":
                List<Client> clients = clientService.getAllClients();
                searchResults.addAll(clients.stream()
                        .filter(c -> c.getCode_cli().toLowerCase().contains(searchTerm) ||
                                c.getNom().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList()));
                break;
            case "Réservations":
                List<Reservation> reservations = reservationService.getAllReservations();
                searchResults.addAll(reservations.stream()
                        .filter(r -> r.getClient().getNom().toLowerCase().contains(searchTerm) ||
                                r.getVoyage().getDestination().toLowerCase().contains(searchTerm))
                        .collect(Collectors.toList()));
                break;
        }

        updateColumnHeaders(searchType);
    }

    private void updateColumnHeaders(String searchType) {
        switch (searchType) {
            case "Voyages":
                column1.setText("Référence");
                column2.setText("Destination");
                column3.setText("Prix");
                break;
            case "Clients":
                column1.setText("Code Client");
                column2.setText("Nom");
                column3.setText("Téléphone");
                break;
            case "Réservations":
                column1.setText("ID Réservation");
                column2.setText("Client");
                column3.setText("Destination");
                break;
        }
    }
}

