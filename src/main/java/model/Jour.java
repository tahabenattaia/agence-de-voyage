package model;

import java.util.*;

public class Jour {
    private Long id;
    private int jour;
    private String description;
    private List<String> activites;
    private Map<String, String> horaires;
    private Set<String> lieux;
    private Voyage voyage;
    public Jour() {
        this.activites = new ArrayList<>();
        this.horaires = new HashMap<>();
        this.lieux = new HashSet<>();
    }

    public Jour(int jour, String description) {
        this();
        this.jour = jour;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getJour() {
        return jour;
    }

    public void setJour(int jour) {
        this.jour = jour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getActivites() {
        return Collections.unmodifiableList(activites);
    }

    public void ajouterActivite(String activite) {
        this.activites.add(activite);
    }

    public Map<String, String> getHoraires() {
        return Collections.unmodifiableMap(horaires);
    }

    public void ajouterHoraire(String activite, String horaire) {
        this.horaires.put(activite, horaire);
    }

    public Set<String> getLieux() {
        return Collections.unmodifiableSet(lieux);
    }

    public void ajouterLieu(String lieu) {
        this.lieux.add(lieu);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jour)) return false;
        Jour jour1 = (Jour) o;
        return jour == jour1.jour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jour);
    }

    public Voyage getVoyage() {
        return voyage;
    }
}

