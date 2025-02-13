package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Client;
import service.ClientService;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    private ClientService clientService;

    @FXML
    public void initialize() {
        clientService = new ClientService();

        if (loginButton != null) {
            loginButton.setOnAction(event -> handleLogin());
        } else {
            System.err.println("Login button not found in FXML");
        }

        if (registerButton != null) {
            registerButton.setOnAction(event -> handleRegister());
        } else {
            System.err.println("Register button not found in FXML");
        }
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Optional<Client> clientOpt = clientService.authenticate(email, password);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            try {
                if (client.isAdmin()) {
                    loadMainView(client);
                } else {
                    loadClientReservationView(client);
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement de la vue.");
            }
        } else {
            showAlert("Erreur", "Utilisateur non trouvé. Veuillez vous inscrire d'abord.");
        }
    }

    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue d'inscription.");
        }
    }

    private void loadMainView(Client client) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.setCurrentClient(client);

        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Agence de Voyage - Administration");
        stage.show();
    }

    private void loadClientReservationView(Client client) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.fxml"));
        Parent root = loader.load();
        ClientDashboardController controller = loader.getController();
        controller.setCurrentClient(client);

        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Agence de Voyage - Réservation");
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

