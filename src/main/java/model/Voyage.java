package model;

import java.util.*;

public class Voyage {
    protected Long id;
    protected String reference;
    protected int prixParPersonne;
    protected String destination;
    protected String descriptif;
    protected Date dateDepart;
    protected Date dateRetour;
    protected List<Itineraire> itineraires;
    protected Set<Reservation> reservations;

    public Voyage() {
        this.itineraires = new ArrayList<>();
        this.reservations = new HashSet<>();
    }

    public Voyage(String reference, int prixParPersonne, String destination,
                  String descriptif, Date dateDepart, Date dateRetour) {
        this();
        this.reference = reference;
        this.prixParPersonne = prixParPersonne;
        this.destination = destination;
        this.descriptif = descriptif;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
    }

    public Voyage(long voyageId, String voyageDestination) {
        this.id = voyageId;
        this.destination = voyageDestination;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getPrixParPersonne() {
        return prixParPersonne;
    }

    public void setPrixParPersonne(int prixParPersonne) {
        this.prixParPersonne = prixParPersonne;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDescriptif() {
        return descriptif;
    }

    public void setDescriptif(String descriptif) {
        this.descriptif = descriptif;
    }

    public Date getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(Date dateDepart) {
        this.dateDepart = dateDepart;
    }

    public Date getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(Date dateRetour) {
        this.dateRetour = dateRetour;
    }

    public List<Itineraire> getItineraires() {
        return Collections.unmodifiableList(itineraires);
    }

    public void addItineraire(Itineraire itineraire) {
        this.itineraires.add(itineraire);
    }

    public Set<Reservation> getReservations() {
        return Collections.unmodifiableSet(reservations);
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voyage)) return false;
        Voyage voyage = (Voyage) o;
        return Objects.equals(reference, voyage.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }

    @Override
    public String toString() {
        return this.reference;
    }
}

