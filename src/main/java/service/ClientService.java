package service;

import model.Client;
import model.Entreprise;
import model.Particulier;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service permettant de gérer les opérations CRUD sur les clients.
 */
public class ClientService {
    private Connection connection;

    /**
     * Constructeur qui initialise la connexion à la base de données.
     */
    public ClientService() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Ajoute un nouveau client dans la base de données.
     * @param client L'objet Client à insérer.
     */
    public void createClient(Client client) {
        String sql = "INSERT INTO client (code_cli, nom, telephone, adresse, type_client) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Affectation des valeurs aux paramètres de la requête SQL
            pstmt.setString(1, client.getCode_cli());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getTelephone());
            pstmt.setString(4, client.getAdresse());
            pstmt.setString(5, client instanceof Entreprise ? "ENTREPRISE" : "PARTICULIER");

            // Exécution de la requête et vérification du succès
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du client, aucune ligne affectée.");
            }

            // Récupération de l'ID généré automatiquement
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Échec de la création du client, aucun ID obtenu.");
                }
            }

            // Insertion des informations spécifiques en fonction du type de client
            if (client instanceof Entreprise) {
                createEntreprise((Entreprise) client);
            } else if (client instanceof Particulier) {
                createParticulier((Particulier) client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ajoute une entreprise associée à un client dans la base de données.
     * @param entreprise L'objet Entreprise à insérer.
     */
    private void createEntreprise(Entreprise entreprise) {
        String sql = "INSERT INTO entreprise (id, matricule_fiscale, registre_commerce) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, entreprise.getId());
            pstmt.setString(2, entreprise.getMatriculeFiscale());
            pstmt.setString(3, entreprise.getRegistreCommerce());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ajoute un particulier associé à un client dans la base de données.
     * @param particulier L'objet Particulier à insérer.
     */
    private void createParticulier(Particulier particulier) {
        String sql = "INSERT INTO particulier (id, cin) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, particulier.getId());
            pstmt.setString(2, particulier.getCin());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère un client par son ID.
     * @param id L'identifiant du client.
     * @return Un Optional contenant le client s'il est trouvé.
     */
    public Optional<Client> getClientById(Long id) {
        String sql = "SELECT c.*, e.matricule_fiscale, e.registre_commerce, p.cin " +
                "FROM client c " +
                "LEFT JOIN entreprise e ON c.id = e.id " +
                "LEFT JOIN particulier p ON c.id = p.id " +
                "WHERE c.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(createClientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Récupère tous les clients de la base de données.
     * @return Une liste contenant tous les clients.
     */
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT c.*, e.matricule_fiscale, e.registre_commerce, p.cin " +
                "FROM client c " +
                "LEFT JOIN entreprise e ON c.id = e.id " +
                "LEFT JOIN particulier p ON c.id = p.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clients.add(createClientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    /**
     * Met à jour un client existant.
     * @param client L'objet Client contenant les nouvelles informations.
     */
    public void updateClient(Client client) {
        String sql = "UPDATE client SET code_cli = ?, nom = ?, telephone = ?, adresse = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getCode_cli());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getTelephone());
            pstmt.setString(4, client.getAdresse());
            pstmt.setLong(5, client.getId());
            pstmt.executeUpdate();

            // Mise à jour spécifique en fonction du type de client
            if (client instanceof Entreprise) {
                updateEntreprise((Entreprise) client);
            } else if (client instanceof Particulier) {
                updateParticulier((Particulier) client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateEntreprise(Entreprise entreprise) {
        String sql = "UPDATE entreprise SET matricule_fiscale = ?, registre_commerce = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, entreprise.getMatriculeFiscale());
            pstmt.setString(2, entreprise.getRegistreCommerce());
            pstmt.setLong(3, entreprise.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateParticulier(Particulier particulier) {
        String sql = "UPDATE particulier SET cin = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, particulier.getCin());
            pstmt.setLong(2, particulier.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime un client par son ID.
     * @param id L'identifiant du client à supprimer.
     */
    public void deleteClient(Long id) {
        String sql = "DELETE FROM client WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crée un objet Client à partir des résultats d'une requête SQL.
     * @param rs Le ResultSet contenant les données.
     * @return Un objet Client (Entreprise ou Particulier).
     * @throws SQLException En cas d'erreur SQL.
     */
    private Client createClientFromResultSet(ResultSet rs) throws SQLException {
        Client client;
        if (rs.getString("type_client").equals("ENTREPRISE")) {
            Entreprise entreprise = new Entreprise();
            entreprise.setMatriculeFiscale(rs.getString("matricule_fiscale"));
            entreprise.setRegistreCommerce(rs.getString("registre_commerce"));
            client = entreprise;
        } else {
            Particulier particulier = new Particulier();
            particulier.setCin(rs.getString("cin"));
            client = particulier;
        }
        client.setId(rs.getLong("id"));
        client.setCode_cli(rs.getString("code_cli"));
        client.setNom(rs.getString("nom"));
        client.setTelephone(rs.getString("telephone"));
        client.setAdresse(rs.getString("adresse"));
        return client;
    }
}
