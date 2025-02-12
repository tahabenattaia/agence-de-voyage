package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Client;
import model.Entreprise;
import model.Particulier;
import service.ClientService;

import java.io.IOException;

public class RegisterController {

    @FXML private ComboBox<String> clientTypeCombo;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField telephoneField;
    @FXML private TextField adresseField;
    @FXML private VBox particulierFields;
    @FXML private TextField cinField;
    @FXML private VBox entrepriseFields;
    @FXML private TextField matriculeFiscaleField;
    @FXML private TextField registreCommerceField;
    @FXML private Button registerButton;
    @FXML private Button backToLoginButton;

    private ClientService clientService;

    @FXML
    public void initialize() {
        clientService = new ClientService();
        clientTypeCombo.getItems().addAll("Particulier", "Entreprise");
        clientTypeCombo.setValue("Particulier");

        clientTypeCombo.setOnAction(event -> {
            String selectedType = clientTypeCombo.getValue();
            particulierFields.setVisible(selectedType.equals("Particulier"));
            particulierFields.setManaged(selectedType.equals("Particulier"));
            entrepriseFields.setVisible(selectedType.equals("Entreprise"));
            entrepriseFields.setManaged(selectedType.equals("Entreprise"));
        });
    }

    @FXML
    private void handleRegister() {
        String clientType = clientTypeCombo.getValue();
        String nom = nomField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String telephone = telephoneField.getText();
        String adresse = adresseField.getText();

        if (nom.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty() || adresse.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        Client newClient;
        if (clientType.equals("Particulier")) {
            String cin = cinField.getText();
            if (cin.isEmpty()) {
                showAlert("Erreur", "Veuillez entrer le CIN.");
                return;
            }
            newClient = new Particulier(generateClientCode(), nom, telephone, adresse, cin ,email , password , false);
        } else {
            String matriculeFiscale = matriculeFiscaleField.getText();
            String registreCommerce = registreCommerceField.getText();
            if (matriculeFiscale.isEmpty() || registreCommerce.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs pour l'entreprise.");
                return;
            }
            newClient = new Entreprise(generateClientCode(), nom, telephone, adresse ,matriculeFiscale ,registreCommerce ,email ,password,false);
        }

        clientService.createClient(newClient);
        showAlert("Succès", "Inscription réussie. Vous pouvez maintenant vous connecter.");
        handleBackToLogin();
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent loginView = loader.load();
            Scene scene = new Scene(loginView);
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Agence de Voyage - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue de connexion.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String generateClientCode() {
        // This is a simple implementation. You might want to implement a more sophisticated
        // method to generate unique client codes.
        return "CLI" + System.currentTimeMillis();
    }
}

