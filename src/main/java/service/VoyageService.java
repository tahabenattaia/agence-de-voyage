package service;

import model.Voyage;
import model.VoyageOrganise;
import model.VoyagePersonnalise;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoyageService {
    private Connection connection;

    // Constructeur : Initialise la connexion à la base de données
    public VoyageService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Crée un nouveau voyage et insère ses détails dans la base de données
    public void createVoyage(Voyage voyage) {
        String sql = "INSERT INTO voyage (reference, prix_par_personne, destination, descriptif, date_depart, date_retour, type_voyage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Remplissage des paramètres de la requête
            pstmt.setString(1, voyage.getReference());
            pstmt.setInt(2, voyage.getPrixParPersonne());
            pstmt.setString(3, voyage.getDestination());
            pstmt.setString(4, voyage.getDescriptif());
            pstmt.setDate(5, new java.sql.Date(voyage.getDateDepart().getTime()));
            pstmt.setDate(6, new java.sql.Date(voyage.getDateRetour().getTime()));
            pstmt.setString(7, voyage instanceof VoyageOrganise ? "ORGANISE" : "PERSONNALISE");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating voyage failed, no rows affected.");
            }

            // Récupération de l'ID généré
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    voyage.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating voyage failed, no ID obtained.");
                }
            }

            // Insérer les informations spécifiques à chaque type de voyage
            if (voyage instanceof VoyageOrganise) {
                createVoyageOrganise((VoyageOrganise) voyage);
            } else if (voyage instanceof VoyagePersonnalise) {
                createVoyagePersonnalise((VoyagePersonnalise) voyage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insère un voyage organisé dans la base de données
    private void createVoyageOrganise(VoyageOrganise voyage) {
        String sql = "INSERT INTO voyage_organise (id, nb_place_maxi, date_validite) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, voyage.getId());
            pstmt.setInt(2, voyage.getNbPlaceMaxi());
            pstmt.setDate(3, new java.sql.Date(voyage.getDateValidite().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insère un voyage personnalisé dans la base de données
    private void createVoyagePersonnalise(VoyagePersonnalise voyage) {
        String sql = "INSERT INTO voyage_personnalise (id, preference) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, voyage.getId());
            pstmt.setString(2, voyage.getPreference());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupère un voyage par son ID
    public Optional<Voyage> getVoyageById(Long id) {
        String sql = "SELECT v.*, vo.nb_place_maxi, vo.date_validite, vp.preference " +
                "FROM voyage v " +
                "LEFT JOIN voyage_organise vo ON v.id = vo.id " +
                "LEFT JOIN voyage_personnalise vp ON v.id = vp.id " +
                "WHERE v.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(createVoyageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Récupère tous les voyages de la base de données
    public List<Voyage> getAllVoyages() {
        List<Voyage> voyages = new ArrayList<>();
        String sql = "SELECT v.*, vo.nb_place_maxi, vo.date_validite, vp.preference " +
                "FROM voyage v " +
                "LEFT JOIN voyage_organise vo ON v.id = vo.id " +
                "LEFT JOIN voyage_personnalise vp ON v.id = vp.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                voyages.add(createVoyageFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voyages;
    }

    // Met à jour les informations d'un voyage
    public void updateVoyage(Voyage voyage) {
        String sql = "UPDATE voyage SET reference = ?, prix_par_personne = ?, destination = ?, " +
                "descriptif = ?, date_depart = ?, date_retour = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, voyage.getReference());
            pstmt.setInt(2, voyage.getPrixParPersonne());
            pstmt.setString(3, voyage.getDestination());
            pstmt.setString(4, voyage.getDescriptif());
            pstmt.setDate(5, new java.sql.Date(voyage.getDateDepart().getTime()));
            pstmt.setDate(6, new java.sql.Date(voyage.getDateRetour().getTime()));
            pstmt.setLong(7, voyage.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Supprime un voyage de la base de données
    public void deleteVoyage(Long id) {
        String sql = "DELETE FROM voyage WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Crée un objet Voyage à partir d'un ResultSet
    private Voyage createVoyageFromResultSet(ResultSet rs) throws SQLException {
        Voyage voyage;
        if (rs.getString("type_voyage").equals("ORGANISE")) {
            VoyageOrganise voyageOrganise = new VoyageOrganise();
            voyageOrganise.setNbPlaceMaxi(rs.getInt("nb_place_maxi"));
            voyageOrganise.setDateValidite(rs.getDate("date_validite"));
            voyage = voyageOrganise;
        } else {
            VoyagePersonnalise voyagePersonnalise = new VoyagePersonnalise();
            voyagePersonnalise.setPreference(rs.getString("preference"));
            voyage = voyagePersonnalise;
        }
        voyage.setId(rs.getLong("id"));
        voyage.setReference(rs.getString("reference"));
        voyage.setPrixParPersonne(rs.getInt("prix_par_personne"));
        voyage.setDestination(rs.getString("destination"));
        voyage.setDescriptif(rs.getString("descriptif"));
        voyage.setDateDepart(rs.getDate("date_depart"));
        voyage.setDateRetour(rs.getDate("date_retour"));
        return voyage;
    }
}
