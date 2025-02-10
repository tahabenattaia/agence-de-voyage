package service;

import model.Itineraire;
import model.Jour;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItineraireService {
    private Connection connection;

    public ItineraireService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void createItineraire(Itineraire itineraire, Long voyageId) {
        String sql = "INSERT INTO itineraire (id_voyage) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, voyageId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating itineraire failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    itineraire.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating itineraire failed, no ID obtained.");
                }
            }

            for (Jour jour : itineraire.getJours()) {
                createJour(jour, itineraire.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createJour(Jour jour, Long itineraireId) {
        String sql = "INSERT INTO jour (jour, description, id_itineraire) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, jour.getJour());
            pstmt.setString(2, jour.getDescription());
            pstmt.setLong(3, itineraireId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating jour failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    jour.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating jour failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Itineraire> getItineraireById(Long id) {
        String sql = "SELECT * FROM itineraire WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Itineraire itineraire = new Itineraire();
                itineraire.setId(rs.getLong("id"));
                List<Jour> jours = getJoursForItineraire(itineraire.getId());
                itineraire.setJours(jours);
                return Optional.of(itineraire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Itineraire> getItineraireByVoyageId(Long voyageId) {
        String sql = "SELECT * FROM itineraire WHERE id_voyage = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, voyageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Itineraire itineraire = new Itineraire();
                itineraire.setId(rs.getLong("id"));
                List<Jour> jours = getJoursForItineraire(itineraire.getId());
                itineraire.setJours(jours);
                return Optional.of(itineraire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private List<Jour> getJoursForItineraire(Long itineraireId) {
        List<Jour> jours = new ArrayList<>();
        String sql = "SELECT * FROM jour WHERE id_itineraire = ? ORDER BY jour";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, itineraireId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Jour jour = new Jour();
                jour.setId(rs.getLong("id"));
                jour.setJour(rs.getInt("jour"));
                jour.setDescription(rs.getString("description"));
                jours.add(jour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jours;
    }

    public List<Itineraire> getAllItineraires() {
        List<Itineraire> itineraires = new ArrayList<>();
        String sql = "SELECT * FROM itineraire";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Itineraire itineraire = new Itineraire();
                itineraire.setId(rs.getLong("id"));
                List<Jour> jours = getJoursForItineraire(itineraire.getId());
                itineraire.setJours(jours);
                itineraires.add(itineraire);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itineraires;
    }

    public void updateItineraire(Itineraire itineraire) {
        for (Jour jour : itineraire.getJours()) {
            if (jour.getId() == null) {
                createJour(jour, itineraire.getId());
            } else {
                updateJour(jour);
            }
        }

        deleteRemovedJours(itineraire);
    }

    private void updateJour(Jour jour) {
        String sql = "UPDATE jour SET jour = ?, description = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, jour.getJour());
            pstmt.setString(2, jour.getDescription());
            pstmt.setLong(3, jour.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteRemovedJours(Itineraire itineraire) {
        List<Jour> existingJours = getJoursForItineraire(itineraire.getId());
        List<Long> updatedJourIds = itineraire.getJours().stream()
                .map(Jour::getId)
                .filter(id -> id != null)
                .toList();

        for (Jour existingJour : existingJours) {
            if (!updatedJourIds.contains(existingJour.getId())) {
                deleteJour(existingJour.getId());
            }
        }
    }


    private void deleteJour(Long id) {
        String sql = "DELETE FROM jour WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteItineraire(Long id) {
        String sql = "DELETE FROM itineraire WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

