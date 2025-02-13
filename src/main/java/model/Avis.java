package model;

import java.sql.Date;

public class Avis {
    private Long id;
    private Long idClient;
    private int note;
    private String commentaire;
    private Date dateAvis;

    public Avis() {}

    public Avis(Long idClient , int note, String commentaire, Date dateAvis) {
        this.idClient = idClient;
        this.note = note;
        this.commentaire = commentaire;
        this.dateAvis = dateAvis;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdClient() { return idClient; }
    public void setIdClient(Long idClient) { this.idClient = idClient; }



    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Date getDateAvis() { return dateAvis; }
    public void setDateAvis(Date dateAvis) { this.dateAvis = dateAvis; }

    @Override
    public String toString() {
        return "Avis{" +
                "id=" + id +
                ", idClient=" + idClient +
                ", note=" + note +
                ", commentaire='" + commentaire + '\'' +
                ", dateAvis=" + dateAvis +
                '}';
    }
}

