package model;

import java.util.Date;

public class AdminReservation {
    private Long id;
    private Client client;
    private Voyage voyage;
    private Date dateReservation;
    private int nbPlace;
    private String status; // Using 'status' instead of 'statut'

    public AdminReservation() {}

    public AdminReservation(Long id, Client client, Voyage voyage, Date dateReservation, int nbPlace, String status) {
        this.id = id;
        this.client = client;
        this.voyage = voyage;
        this.dateReservation = dateReservation;
        this.nbPlace = nbPlace;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Voyage getVoyage() { return voyage; }
    public void setVoyage(Voyage voyage) { this.voyage = voyage; }

    public Date getDateReservation() { return dateReservation; }
    public void setDateReservation(Date dateReservation) { this.dateReservation = dateReservation; }

    public int getNbPlace() { return nbPlace; }
    public void setNbPlace(int nbPlace) { this.nbPlace = nbPlace; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}