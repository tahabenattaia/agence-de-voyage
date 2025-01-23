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

    public ReservationService() {
        this.connection = DatabaseConnection.getConnection();
        this.voyageService = new VoyageService();
        this.clientService = new ClientService();
    }

    public void createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (date_reservation, nb_place, id_voyage, id_client) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, new java.sql.Date(reservation.getDateReservation().getTime()));
            pstmt.setInt(2, reservation.getNbPlace());
            pstmt.setLong(3, reservation.getVoyage().getId());
            pstmt.setLong(4, reservation.getClient().getId());

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

    public Optional<Reservation> getReservationById(Long id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(createReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

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

    public void updateReservation(Reservation reservation) {
        String sql = "UPDATE reservation SET date_reservation = ?, nb_place = ?, id_voyage = ?, id_client = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(reservation.getDateReservation().getTime()));
            pstmt.setInt(2, reservation.getNbPlace());
            pstmt.setLong(3, reservation.getVoyage().getId());
            pstmt.setLong(4, reservation.getClient().getId());
            pstmt.setLong(5, reservation.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteReservation(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setDateReservation(rs.getDate("date_reservation"));
        reservation.setNbPlace(rs.getInt("nb_place"));

        Long voyageId = rs.getLong("id_voyage");
        Optional<Voyage> voyage = voyageService.getVoyageById(voyageId);
        voyage.ifPresent(reservation::setVoyage);

        Long clientId = rs.getLong("id_client");
        Optional<Client> client = clientService.getClientById(clientId);
        client.ifPresent(reservation::setClient);

        return reservation;
    }
}

