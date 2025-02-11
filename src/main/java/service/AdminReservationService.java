package service;

import model.AdminReservation;
import model.Client;
import model.Voyage;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminReservationService {
    private Connection connection;

    public AdminReservationService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<AdminReservation> getAllReservations() {
        List<AdminReservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, c.id AS client_id, c.nom as client_nom, v.id AS voyage_id, v.destination as voyage_destination  " +
                "FROM reservation r " +
                "JOIN client c ON r.id_client = c.id " +  // Assurez-vous que 'id_client' est le bon nom de colonne
                "JOIN voyage v ON r.id_voyage = v.id";   // Assurez-vous que 'id_voyage' est le bon nom de colonne
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(createReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reservations: " + e.getMessage());
            e.printStackTrace();
        }
        return reservations;
    }


    public void updateReservation(AdminReservation reservation) {
        String sql = "UPDATE reservation SET id_client = ?, voyage_id = ?, date_reservation = ?, nb_place = ?, status = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, reservation.getClient().getId());
            pstmt.setLong(2, reservation.getVoyage().getId());
            pstmt.setDate(3, new java.sql.Date(reservation.getDateReservation().getTime()));
            pstmt.setInt(4, reservation.getNbPlace());
            pstmt.setString(5, reservation.getStatus());
            pstmt.setLong(6, reservation.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating reservation failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean deleteReservation(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting reservation with id " + id + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private AdminReservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        AdminReservation reservation = new AdminReservation();
        reservation.setId(rs.getLong("id"));
        reservation.setClient(new Client(rs.getLong("client_id"), rs.getString("client_nom")));
        reservation.setVoyage(new Voyage(rs.getLong("voyage_id"), rs.getString("voyage_destination")));
        reservation.setDateReservation(rs.getDate("date_reservation"));
        reservation.setNbPlace(rs.getInt("nb_place"));
        reservation.setStatus(rs.getString("status"));
        return reservation;
    }
}