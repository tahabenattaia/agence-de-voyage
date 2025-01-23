package model;

import java.util.*;

public class Itineraire {
    private Long id;
    private List<Jour> jours = new ArrayList<>();
    private Map<Integer, String> pointsInteret;
    private Set<String> activites;

    public Itineraire() {
        this.jours = new ArrayList<>();
        this.pointsInteret = new HashMap<>();
        this.activites = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Jour> getJours() {
        return Collections.unmodifiableList(jours);
    }

    public void ajouterJour(Jour jour) {
        this.jours.add(jour);
    }

    public void setJours(List<Jour> jours) {
        this.jours = new ArrayList<>(jours);
    }

    public Map<Integer, String> getPointsInteret() {
        return Collections.unmodifiableMap(pointsInteret);
    }

    public void ajouterPointInteret(Integer ordre, String pointInteret) {
        this.pointsInteret.put(ordre, pointInteret);
    }

    public Set<String> getActivites() {
        return Collections.unmodifiableSet(activites);
    }

    public void ajouterActivite(String activite) {
        this.activites.add(activite);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Itineraire)) return false;
        Itineraire that = (Itineraire) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

