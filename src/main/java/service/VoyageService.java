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

    public VoyageService() {
        this.connection = DatabaseConnection.getConnection();
        if (this.connection == null) {
            throw new RuntimeException("Impossible d'établir une connexion à la base de données.");
        }
    }

    public void createVoyage(Voyage voyage) {
        String sql = "INSERT INTO voyage (reference, prix_par_personne, destination, descriptif, date_depart, date_retour, type_voyage) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, voyage.getReference());
            pstmt.setInt(2, voyage.getPrixParPersonne());
            pstmt.setString(3, voyage.getDestination());
            pstmt.setString(4, voyage.getDescriptif());
            pstmt.setDate(5, new java.sql.Date(voyage.getDateDepart().getTime()));
            pstmt.setDate(6, new java.sql.Date(voyage.getDateRetour().getTime()));
            pstmt.setString(7, voyage instanceof VoyageOrganise ? "ORGANISE" : "PERSONNALISE");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La création du voyage a échoué, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    voyage.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("La création du voyage a échoué, aucun ID obtenu.");
                }
            }

            if (voyage instanceof VoyageOrganise) {
                createVoyageOrganise((VoyageOrganise) voyage);
            } else if (voyage instanceof VoyagePersonnalise) {
                createVoyagePersonnalise((VoyagePersonnalise) voyage);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du voyage : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Insère un voyage organisé dans la base de données
    private void createVoyageOrganise(VoyageOrganise voyage) {
        String sql = "INSERT INTO voyage_organise (id, nb_place_maxi, date_validite) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, voyage.getId());
            pstmt.setInt(2, voyage.getNbPlaceMaxi());
            // Vérifier si la date de validité est renseignée
            if (voyage.getDateValidite() != null) {
                pstmt.setDate(3, new java.sql.Date(voyage.getDateValidite().getTime()));
            } else {
                // Si elle est nulle, on insère une valeur SQL NULL
                pstmt.setNull(3, java.sql.Types.DATE);
            }
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
            if (voyage.getPreference() != null) {
                pstmt.setString(2, voyage.getPreference());
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }
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
        // Met à jour les informations de base dans la table voyage
        String updateVoyageSql = "UPDATE voyage SET reference = ?, prix_par_personne = ?, destination = ?, " +
                "descriptif = ?, date_depart = ?, date_retour = ? WHERE id = ?";

        // Met à jour les informations spécifiques pour les voyages organisés
        String updateOrganiseSql = "UPDATE voyage_organise SET nb_place_maxi = ?, date_validite = ? WHERE id = ?";

        // Met à jour les informations spécifiques pour les voyages personnalisés
        String updatePersonnaliseSql = "UPDATE voyage_personnalise SET preference = ? WHERE id = ?";

        try {
            // Désactive l'autocommit pour démarrer une transaction
            connection.setAutoCommit(false);

            try (PreparedStatement pstmtVoyage = connection.prepareStatement(updateVoyageSql);
                 PreparedStatement pstmtOrganise = connection.prepareStatement(updateOrganiseSql);
                 PreparedStatement pstmtPersonnalise = connection.prepareStatement(updatePersonnaliseSql)) {

                // Met à jour les informations de base dans la table voyage
                pstmtVoyage.setString(1, voyage.getReference());
                pstmtVoyage.setInt(2, voyage.getPrixParPersonne());
                pstmtVoyage.setString(3, voyage.getDestination());
                pstmtVoyage.setString(4, voyage.getDescriptif());
                pstmtVoyage.setDate(5, new java.sql.Date(voyage.getDateDepart().getTime()));
                pstmtVoyage.setDate(6, new java.sql.Date(voyage.getDateRetour().getTime()));
                pstmtVoyage.setLong(7, voyage.getId());
                pstmtVoyage.executeUpdate();

                // Met à jour les informations spécifiques pour les voyages organisés
                if (voyage instanceof VoyageOrganise) {
                    VoyageOrganise voyageOrganise = (VoyageOrganise) voyage;
                    pstmtOrganise.setInt(1, voyageOrganise.getNbPlaceMaxi());
                    pstmtOrganise.setDate(2, new java.sql.Date(voyageOrganise.getDateValidite().getTime()));
                    pstmtOrganise.setLong(3, voyageOrganise.getId());
                    pstmtOrganise.executeUpdate();
                }

                // Met à jour les informations spécifiques pour les voyages personnalisés
                if (voyage instanceof VoyagePersonnalise) {
                    VoyagePersonnalise voyagePersonnalise = (VoyagePersonnalise) voyage;
                    pstmtPersonnalise.setString(1, voyagePersonnalise.getPreference());
                    pstmtPersonnalise.setLong(2, voyagePersonnalise.getId());
                    pstmtPersonnalise.executeUpdate();
                }

                // Valide la transaction
                connection.commit();
            } catch (SQLException e) {
                // Annule la transaction en cas d'erreur
                connection.rollback();
                System.err.println("Erreur lors de la mise à jour du voyage : " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Réactive l'autocommit
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la gestion de la transaction : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Supprime un voyage de la base de données
    public void deleteVoyage(Long id) {
        try {
            // Désactive l'autocommit pour démarrer une transaction
            connection.setAutoCommit(false);

            // Supprime d'abord les enregistrements dans les tables dépendantes
            String deleteReservationSql = "DELETE FROM reservation WHERE id_voyage = ?";
            String deleteItineraireSql = "DELETE FROM itineraire WHERE id_voyage = ?";
            String deleteOrganiseSql = "DELETE FROM voyage_organise WHERE id = ?";
            String deletePersonnaliseSql = "DELETE FROM voyage_personnalise WHERE id = ?";
            String deleteVoyageSql = "DELETE FROM voyage WHERE id = ?";

            try (PreparedStatement pstmtReservation = connection.prepareStatement(deleteReservationSql);
                 PreparedStatement pstmtItineraire = connection.prepareStatement(deleteItineraireSql);
                 PreparedStatement pstmtOrganise = connection.prepareStatement(deleteOrganiseSql);
                 PreparedStatement pstmtPersonnalise = connection.prepareStatement(deletePersonnaliseSql);
                 PreparedStatement pstmtVoyage = connection.prepareStatement(deleteVoyageSql)) {

                // Supprime les enregistrements dans reservation
                pstmtReservation.setLong(1, id);
                pstmtReservation.executeUpdate();

                // Supprime les enregistrements dans itineraire
                pstmtItineraire.setLong(1, id);
                pstmtItineraire.executeUpdate();

                // Supprime les enregistrements dans voyage_organise
                pstmtOrganise.setLong(1, id);
                pstmtOrganise.executeUpdate();

                // Supprime les enregistrements dans voyage_personnalise
                pstmtPersonnalise.setLong(1, id);
                pstmtPersonnalise.executeUpdate();

                // Supprime l'enregistrement dans voyage
                pstmtVoyage.setLong(1, id);
                pstmtVoyage.executeUpdate();

                // Valide la transaction
                connection.commit();
            } catch (SQLException e) {
                // Annule la transaction en cas d'erreur
                connection.rollback();
                System.err.println("Erreur lors de la suppression du voyage : " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Réactive l'autocommit
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la gestion de la transaction : " + e.getMessage());
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
