package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Avis;
import service.AvisService;

import java.sql.Date;
import java.time.LocalDate;

public class AvisController {

    @FXML private TextField idClientField;
    @FXML private Slider noteSlider;
    @FXML private TextArea commentaireArea;
    @FXML private DatePicker dateAvisPicker;
    @FXML private TableView<Avis> avisTable;
    @FXML private TableColumn<Avis, Long> idColumn;
    @FXML private TableColumn<Avis, Long> idClientColumn;
    @FXML private TableColumn<Avis, Integer> noteColumn;
    @FXML private TableColumn<Avis, String> commentaireColumn;
    @FXML private TableColumn<Avis, Date> dateAvisColumn;

    private AvisService avisService;
    private ObservableList<Avis> avisList;

    public void initialize() {
        avisService = new AvisService();
        avisList = FXCollections.observableArrayList();

        setupTableColumns();
        loadAvis();

        avisTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showAvisDetails(newSelection);
            }
        });
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleLongProperty(cellData.getValue().getId()).asObject());
        idClientColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleLongProperty(cellData.getValue().getIdClient()).asObject());
        noteColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getNote()).asObject());
        commentaireColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCommentaire()));
        dateAvisColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDateAvis()));
    }

    private void loadAvis() {
        avisList.setAll(avisService.getAllAvis());
        avisTable.setItems(avisList);
    }

    @FXML
    private void handleCreateAvis() {
        Avis newAvis = new Avis();
        newAvis.setIdClient(Long.parseLong(idClientField.getText()));
        newAvis.setNote((int) noteSlider.getValue());
        newAvis.setCommentaire(commentaireArea.getText());
        newAvis.setDateAvis(Date.valueOf(dateAvisPicker.getValue()));
        avisService.createAvis(newAvis);
        loadAvis();
        clearFields();
    }

    @FXML
    private void handleUpdateAvis() {
        Avis selectedAvis = avisTable.getSelectionModel().getSelectedItem();
        if (selectedAvis != null) {
            selectedAvis.setIdClient(Long.parseLong(idClientField.getText()));
            selectedAvis.setNote((int) noteSlider.getValue());
            selectedAvis.setCommentaire(commentaireArea.getText());
            selectedAvis.setDateAvis(Date.valueOf(dateAvisPicker.getValue()));

            avisService.updateAvis(selectedAvis);
            loadAvis();
            clearFields();
        }
    }

    @FXML
    private void handleDeleteAvis() {
        Avis selectedAvis = avisTable.getSelectionModel().getSelectedItem();
        if (selectedAvis != null) {
            avisService.deleteAvis(selectedAvis.getId());
            loadAvis();
            clearFields();
        }
    }

    private void showAvisDetails(Avis avis) {
        idClientField.setText(String.valueOf(avis.getIdClient()));
        noteSlider.setValue(avis.getNote());
        commentaireArea.setText(avis.getCommentaire());
        dateAvisPicker.setValue(avis.getDateAvis().toLocalDate());
    }

    private void clearFields() {
        idClientField.clear();
        noteSlider.setValue(0);
        commentaireArea.clear();
        dateAvisPicker.setValue(null);
    }
}

