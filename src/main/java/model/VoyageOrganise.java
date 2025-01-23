package model;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoyageOrganise extends Voyage {
    private int nbPlaceMaxi;
    private Date dateValidite;
    private Map<Date, Integer> placesDisponibles;

    public VoyageOrganise() {
        super();
        this.placesDisponibles = new ConcurrentHashMap<>();
    }

    public VoyageOrganise(String reference, int prixParPersonne, String destination,
                          String descriptif, Date dateDepart, Date dateRetour,
                          int nbPlaceMaxi, Date dateValidite) {
        super(reference, prixParPersonne, destination, descriptif, dateDepart, dateRetour);
        this.nbPlaceMaxi = nbPlaceMaxi;
        this.dateValidite = dateValidite;
        this.placesDisponibles = new ConcurrentHashMap<>();
        this.placesDisponibles.put(dateDepart, nbPlaceMaxi);
    }

    public int getNbPlaceMaxi() {
        return nbPlaceMaxi;
    }

    public void setNbPlaceMaxi(int nbPlaceMaxi) {
        this.nbPlaceMaxi = nbPlaceMaxi;
    }

    public Date getDateValidite() {
        return dateValidite;
    }

    public void setDateValidite(Date dateValidite) {
        this.dateValidite = dateValidite;
    }

    public Map<Date, Integer> getPlacesDisponibles() {
        return Collections.unmodifiableMap(placesDisponibles);
    }

    public boolean reserverPlaces(Date date, int nombrePlaces) {
        return placesDisponibles.computeIfPresent(date, (key, places) -> {
            if (places >= nombrePlaces) {
                return places - nombrePlaces;
            }
            return places;
        }) != null;
    }

    public void ajouterDate(Date date) {
        placesDisponibles.putIfAbsent(date, nbPlaceMaxi);
    }
}

