package service;

import model.Reservation;
import model.Voyage;
import model.Client;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationService {
    private Connection connection;
    private VoyageService voyageService;
    private ClientService clientService;

    // Constructeur : Initialise la connexion à la base de données et les services associés
    public ReservationService() {
        this.connection = DatabaseConnection.getConnection();
        this.voyageService = new VoyageService();
        this.clientService = new ClientService();
    }

    // Méthode pour créer une réservation et insérer ses données dans la base
    public void createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (date_reservation, nb_place, id_voyage, id_client, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, new java.sql.Date(reservation.getDateReservation().getTime()));
            pstmt.setInt(2, reservation.getNbPlace());
            pstmt.setLong(3, reservation.getVoyage().getId());
            pstmt.setLong(4, reservation.getClient().getId());
            pstmt.setString(5, reservation.getStatus()); // Ajout du statut
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating reservation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer une réservation par son ID
    public Optional<Reservation> getReservationById(Long id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(createReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Méthode pour récupérer toutes les réservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(createReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    // Méthode pour mettre à jour une réservation existante
    public void updateReservation(Reservation reservation) {
        String sql = "UPDATE reservation SET date_reservation = ?, nb_place = ?, id_voyage = ?, id_client = ?, status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(reservation.getDateReservation().getTime()));
            pstmt.setInt(2, reservation.getNbPlace());
            pstmt.setLong(3, reservation.getVoyage().getId());
            pstmt.setLong(4, reservation.getClient().getId());
            pstmt.setString(5, reservation.getStatus());
            pstmt.setLong(6, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour supprimer une réservation par son ID
    public void deleteReservation(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour créer un objet Reservation à partir d'un ResultSet
    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setDateReservation(rs.getDate("date_reservation"));
        reservation.setNbPlace(rs.getInt("nb_place"));
        reservation.setStatus(rs.getString("status")); // Récupération du statut

        // Récupération de l'objet Voyage associé
        Long voyageId = rs.getLong("id_voyage");
        Optional<Voyage> voyage = voyageService.getVoyageById(voyageId);
        voyage.ifPresent(reservation::setVoyage);

        // Récupération de l'objet Client associé
        Long clientId = rs.getLong("id_client");
        Optional<Client> client = clientService.getClientById(clientId);
        client.ifPresent(reservation::setClient);

        return reservation;
    }
}