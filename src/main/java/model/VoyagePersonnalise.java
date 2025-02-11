package model;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class VoyagePersonnalise extends Voyage {
    private String preference;
    private Set<String> optionsPersonnalisees;

    public VoyagePersonnalise() {
        super();
        this.optionsPersonnalisees = new HashSet<>();
    }

    public VoyagePersonnalise(String reference, int prixParPersonne, String destination,
                              String descriptif, Date dateDepart, Date dateRetour,
                              String preference) {
        super(reference, prixParPersonne, destination, descriptif, dateDepart, dateRetour);
        this.preference = preference;
        this.optionsPersonnalisees = new HashSet<>();
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public Set<String> getOptionsPersonnalisees() {
        return Collections.unmodifiableSet(optionsPersonnalisees);
    }

    public void ajouterOption(String option) {
        this.optionsPersonnalisees.add(option);
    }

    public void supprimerOption(String option) {
        this.optionsPersonnalisees.remove(option);
    }
}