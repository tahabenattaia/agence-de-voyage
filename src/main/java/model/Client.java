package model;

import java.util.*;

public abstract class Client {
    protected Long id;
    protected String code_cli;
    protected String nom;
    protected String telephone;
    protected String adresse;
    protected Set<Reservation> reservations;
    protected Map<String, String> preferences;

    public Client() {
        this.reservations = new HashSet<>();
        this.preferences = new HashMap<>();
    }

    public Client(String code_cli, String nom, String telephone, String adresse) {
        this();
        this.code_cli = code_cli;
        this.nom = nom;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode_cli() {
        return code_cli;
    }

    public void setCode_cli(String code_cli) {
        this.code_cli = code_cli;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Set<Reservation> getReservations() {
        return Collections.unmodifiableSet(reservations);
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }

    public Map<String, String> getPreferences() {
        return Collections.unmodifiableMap(preferences);
    }

    public void ajouterPreference(String key, String value) {
        this.preferences.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return Objects.equals(code_cli, client.code_cli);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code_cli);
    }
}

