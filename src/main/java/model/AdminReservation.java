package model;

import java.util.*;

public class AdminReservation {
    private Long id;
    private Date dateReservation;
    private int nbPlace;
    private Voyage voyage;
    private Client client;
    private String status;
    private Map<String, String> detailsReservation;
    private List<String> commentaires;

    public AdminReservation() {
        this.detailsReservation = new HashMap<>();
        this.commentaires = new ArrayList<>();
        this.status = "EN_ATTENTE";
    }

    public AdminReservation(Date dateReservation, int nbPlace, Voyage voyage, Client client) {
        this();
        this.dateReservation = dateReservation;
        this.nbPlace = nbPlace;
        this.voyage = voyage;
        this.client = client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getNbPlace() {
        return nbPlace;
    }

    public void setNbPlace(int nbPlace) {
        this.nbPlace = nbPlace;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Map<String, String> getDetailsReservation() {
        return Collections.unmodifiableMap(detailsReservation);
    }

    public void ajouterDetail(String key, String value) {
        this.detailsReservation.put(key, value);
    }

    public List<String> getCommentaires() {
        return Collections.unmodifiableList(commentaires);
    }

    public void ajouterCommentaire(String commentaire) {
        this.commentaires.add(commentaire);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminReservation)) return false;
        AdminReservation that = (AdminReservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}