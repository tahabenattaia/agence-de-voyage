package service;

import model.Client;
import model.Entreprise;
import model.Particulier;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private Connection connection;

    public ClientService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void createClient(Client client) {
        String sql = "INSERT INTO client (code_cli, nom, telephone, adresse, email, password, is_admin, type_client) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, client.getCode_cli());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getTelephone());
            pstmt.setString(4, client.getAdresse());
            pstmt.setString(5, client.getEmail());
            pstmt.setString(6, client.getPassword());
            pstmt.setBoolean(7, client.isAdmin());
            pstmt.setString(8, client instanceof Entreprise ? "ENTREPRISE" : "PARTICULIER");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating client failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating client failed, no ID obtained.");
                }
            }

            if (client instanceof Entreprise) {
                createEntreprise((Entreprise) client);
            } else if (client instanceof Particulier) {
                createParticulier((Particulier) client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public void updateClient(Client client) {
        String sql = "UPDATE client SET code_cli = ?, nom = ?, telephone = ?, adresse = ?, email = ?, password = ?, is_admin = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, client.getCode_cli());
            pstmt.setString(2, client.getNom());
            pstmt.setString(3, client.getTelephone());
            pstmt.setString(4, client.getAdresse());
            pstmt.setString(5, client.getEmail());
            pstmt.setString(6, client.getPassword());
            pstmt.setBoolean(7, client.isAdmin());
            pstmt.setLong(8, client.getId());
            pstmt.executeUpdate();

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

    public void deleteClient(Long id) {
        String sql = "DELETE FROM client WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Client> authenticate(String email, String password) {
        String sql = "SELECT c.*, e.matricule_fiscale, e.registre_commerce, p.cin " +
                "FROM client c " +
                "LEFT JOIN entreprise e ON c.id = e.id " +
                "LEFT JOIN particulier p ON c.id = p.id " +
                "WHERE c.email = ? AND c.password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(createClientFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Client getClientByCodeAndNom(String code, String nom) {
        String sql = "SELECT c.*, e.matricule_fiscale, e.registre_commerce, p.cin " +
                "FROM client c " +
                "LEFT JOIN entreprise e ON c.id = e.id " +
                "LEFT JOIN particulier p ON c.id = p.id " +
                "WHERE c.code_cli = ? AND c.nom = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, nom);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return createClientFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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
        client.setEmail(rs.getString("email"));
        client.setPassword(rs.getString("password"));
        client.setAdmin(rs.getBoolean("is_admin"));
        return client;
    }
}

