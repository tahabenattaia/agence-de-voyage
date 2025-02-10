package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import model.Client;
import model.Entreprise;
import model.Particulier;
import service.ClientService;

import java.util.List;

public class ClientController {

    @FXML private TextField codeClientField;
    @FXML private TextField nomField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private ComboBox<String> typeClientCombo;
    @FXML private TextField detailsField;
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> codeClientColumn;
    @FXML private TableColumn<Client, String> nomColumn;
    @FXML private TableColumn<Client, String> telephoneColumn;
    @FXML private TableColumn<Client, String> adresseColumn;
    @FXML private TableColumn<Client, String> typeColumn;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TableColumn<Client, String> emailColumn;


    private ClientService clientService;
    private ObservableList<Client> clientList;

    public void initialize() {
        clientService = new ClientService();
        clientList = FXCollections.observableArrayList();
        typeClientCombo.getItems().addAll("Entreprise", "Particulier");
        setupTableColumns();
        loadClients();

        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showClientDetails(newSelection);
            }
        });
    }

    private void setupTableColumns() {
        codeClientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode_cli()));
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        telephoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelephone()));
        adresseColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAdresse()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue() instanceof Entreprise ? "Entreprise" : "Particulier"));


    }

    private void loadClients() {
        List<Client> clients = clientService.getAllClients();
        clientList.setAll(clients);
        clientTable.setItems(clientList);
    }

    @FXML
    private void handleCreateClient() {
        String codeClient = codeClientField.getText();
        String nom = nomField.getText();
        String telephone = telephoneField.getText();
        String adresse = adresseField.getText();
        String type = typeClientCombo.getValue();
        String details = detailsField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();



        Client newClient;
        if ("Entreprise".equals(type)) {
            Entreprise entreprise = new Entreprise();
            entreprise.setMatriculeFiscale(details);
            newClient = entreprise;
        } else {
            Particulier particulier = new Particulier();
            particulier.setCin(details);
            newClient = particulier;
        }

        newClient.setCode_cli(codeClient);
        newClient.setNom(nom);
        newClient.setTelephone(telephone);
        newClient.setAdresse(adresse);
        newClient.setEmail(email);
        newClient.setPassword(password);

        clientService.createClient(newClient);
        loadClients();
        clearFields();
    }

    @FXML
    private void handleUpdateClient() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            selectedClient.setCode_cli(codeClientField.getText());
            selectedClient.setNom(nomField.getText());
            selectedClient.setTelephone(telephoneField.getText());
            selectedClient.setAdresse(adresseField.getText());
            selectedClient.setEmail(emailField.getText());
            selectedClient.setPassword(passwordField.getText());


            String type = typeClientCombo.getValue();
            String details = detailsField.getText();

            if (selectedClient instanceof Entreprise && "Entreprise".equals(type)) {
                ((Entreprise) selectedClient).setMatriculeFiscale(details);
            } else if (selectedClient instanceof Particulier && "Particulier".equals(type)) {
                ((Particulier) selectedClient).setCin(details);
            } else {

                Client newClient;
                if ("Entreprise".equals(type)) {
                    Entreprise entreprise = new Entreprise();
                    entreprise.setMatriculeFiscale(details);
                    newClient = entreprise;
                } else {
                    Particulier particulier = new Particulier();
                    particulier.setCin(details);
                    newClient = particulier;
                }
                newClient.setId(selectedClient.getId());
                newClient.setCode_cli(selectedClient.getCode_cli());
                newClient.setNom(selectedClient.getNom());
                newClient.setTelephone(selectedClient.getTelephone());
                newClient.setAdresse(selectedClient.getAdresse());
                newClient.setEmail(selectedClient.getEmail());
                newClient.setPassword(selectedClient.getPassword());
                selectedClient = newClient;
            }

            clientService.updateClient(selectedClient);
            loadClients();
            clearFields();
        }
    }

    @FXML
    private void handleDeleteClient() {
        Client selectedClient = clientTable.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            clientService.deleteClient(selectedClient.getId());
            loadClients();
            clearFields();
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
    }

    private void showClientDetails(Client client) {
        codeClientField.setText(client.getCode_cli());
        nomField.setText(client.getNom());
        telephoneField.setText(client.getTelephone());
        adresseField.setText(client.getAdresse());
        emailField.setText(client.getEmail());
        passwordField.setText(client.getPassword());


        if (client instanceof Entreprise) {
            typeClientCombo.setValue("Entreprise");
            detailsField.setText(((Entreprise) client).getMatriculeFiscale());
        } else if (client instanceof Particulier) {
            typeClientCombo.setValue("Particulier");
            detailsField.setText(((Particulier) client).getCin());
        }
    }

    private void clearFields() {
        codeClientField.clear();
        nomField.clear();
        telephoneField.clear();
        adresseField.clear();
        typeClientCombo.getSelectionModel().clearSelection();
        detailsField.clear();
        emailField.clear();
        passwordField.clear();

    }
}

