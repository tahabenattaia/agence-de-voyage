package service;

import model.Avis;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService {
    private Connection connection;

    public AvisService() {
        this.connection = DatabaseConnection.getConnection();
    }

    public void createAvis(Avis avis) {
        String sql = "INSERT INTO avis (id_client,  note, commentaire, date_avis) VALUES (?,  ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, avis.getIdClient());
            pstmt.setInt(2, avis.getNote());
            pstmt.setString(3, avis.getCommentaire());
            pstmt.setDate(4, avis.getDateAvis());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating avis failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    avis.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating avis failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Avis> getAllAvis() {
        List<Avis> avisList = new ArrayList<>();
        String sql = "SELECT * FROM avis";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Avis avis = new Avis();
                avis.setId(rs.getLong("id"));
                avis.setIdClient(rs.getLong("id_client"));
                avis.setNote(rs.getInt("note"));
                avis.setCommentaire(rs.getString("commentaire"));
                avis.setDateAvis(rs.getDate("date_avis"));
                avisList.add(avis);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avisList;
    }

    public void updateAvis(Avis avis) {
        String sql = "UPDATE avis SET id_client = ?, note = ?, commentaire = ?, date_avis = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, avis.getIdClient());
            pstmt.setInt(3, avis.getNote());
            pstmt.setString(4, avis.getCommentaire());
            pstmt.setDate(5, avis.getDateAvis());
            pstmt.setLong(6, avis.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAvis(Long id) {
        String sql = "DELETE FROM avis WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

