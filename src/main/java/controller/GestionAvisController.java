package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.stage.Stage;
import model.Avis;
import model.Client;
import service.AvisService;
import service.ClientService;

import java.time.LocalDate;
import java.sql.Date;
import java.util.List;

public class GestionAvisController {

    @FXML private TableView<Avis> avisTable;
    @FXML private TableColumn<Avis, String> clientColumn;
    @FXML private TableColumn<Avis, Integer> noteColumn;
    @FXML private TableColumn<Avis, String> commentaireColumn;
    @FXML private TableColumn<Avis, Date> dateAvisColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterRatingCombo;
    @FXML private DatePicker filterDatePicker;

    @FXML private Label selectedClientLabel;
    @FXML private Label selectedNoteLabel;
    @FXML private Label selectedDateLabel;
    @FXML private TextArea selectedCommentaireArea;
    @FXML private Label statusLabel;

    private AvisService avisService;
    private ClientService clientService;
    private ObservableList<Avis> avisList;
    private FilteredList<Avis> filteredAvis;

    public void initialize() {
        avisService = new AvisService();
        clientService = new ClientService();
        avisList = FXCollections.observableArrayList();

        setupTableColumns();
        setupFilters();
        loadAvis();

        avisTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showAvisDetails(newSelection);
            }
        });
    }

    private void setupTableColumns() {
        clientColumn.setCellValueFactory(cellData -> {
            Client client = clientService.getClientById(cellData.getValue().getIdClient()).orElse(null);
            return new javafx.beans.property.SimpleStringProperty(
                    client != null ? client.getNom() : "Client inconnu"
            );
        });

        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        commentaireColumn.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        dateAvisColumn.setCellValueFactory(new PropertyValueFactory<>("dateAvis"));
    }

    private void setupFilters() {
        filterRatingCombo.getItems().addAll(
                "Toutes les notes",
                "5 étoiles",
                "4 étoiles",
                "3 étoiles",
                "2 étoiles",
                "1 étoile"
        );
        filterRatingCombo.setValue("Toutes les notes");
    }

    private void loadAvis() {
        List<Avis> avis = avisService.getAllAvis();
        avisList.setAll(avis);
        filteredAvis = new FilteredList<>(avisList, p -> true);
        avisTable.setItems(filteredAvis);
        updateStatus("Avis chargés : " + avis.size());
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        Integer filterRating = parseRatingFilter(filterRatingCombo.getValue());
        LocalDate filterDate = filterDatePicker.getValue();

        filteredAvis.setPredicate(avis -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    avis.getCommentaire().toLowerCase().contains(searchText);

            boolean matchesRating = filterRating == null || avis.getNote() == filterRating;

            boolean matchesDate = filterDate == null ||
                    avis.getDateAvis().toLocalDate().equals(filterDate);

            return matchesSearch && matchesRating && matchesDate;
        });

        updateStatus("Résultats filtrés : " + filteredAvis.size());
    }

    private Integer parseRatingFilter(String filter) {
        if (filter == null || filter.equals("Toutes les notes")) return null;
        return Integer.parseInt(filter.split(" ")[0]);
    }

    @FXML
    private void handleDeleteAvis() {
        Avis selectedAvis = avisTable.getSelectionModel().getSelectedItem();
        if (selectedAvis != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer l'avis");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cet avis ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    avisService.deleteAvis(selectedAvis.getId());
                    loadAvis();
                    clearDetails();
                    updateStatus("Avis supprimé avec succès");
                }
            });
        }
    }

    private void showAvisDetails(Avis avis) {
        Client client = clientService.getClientById(avis.getIdClient()).orElse(null);
        selectedClientLabel.setText(client != null ? client.getNom() : "Client inconnu");
        selectedNoteLabel.setText(avis.getNote() + " étoiles");
        selectedDateLabel.setText(avis.getDateAvis().toString());
        selectedCommentaireArea.setText(avis.getCommentaire());
    }

    private void clearDetails() {
        selectedClientLabel.setText("");
        selectedNoteLabel.setText("");
        selectedDateLabel.setText("");
        selectedCommentaireArea.setText("");
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Agence de Voyage - Menu Principal");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue principale.", Alert.AlertType.ERROR);
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

