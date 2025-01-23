package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Entreprise extends Client {
    private String matriculeFiscale;
    private String registreCommerce;
    private List<String> contactsSecondaires;

    public Entreprise() {
        super();
        this.contactsSecondaires = new ArrayList<>();
    }

    public Entreprise(String code_cli, String nom, String telephone, String adresse,
                      String matriculeFiscale, String registreCommerce) {
        super(code_cli, nom, telephone, adresse);
        this.matriculeFiscale = matriculeFiscale;
        this.registreCommerce = registreCommerce;
        this.contactsSecondaires = new ArrayList<>();
    }

    public String getMatriculeFiscale() {
        return matriculeFiscale;
    }

    public void setMatriculeFiscale(String matriculeFiscale) {
        this.matriculeFiscale = matriculeFiscale;
    }

    public String getRegistreCommerce() {
        return registreCommerce;
    }

    public void setRegistreCommerce(String registreCommerce) {
        this.registreCommerce = registreCommerce;
    }

    public List<String> getContactsSecondaires() {
        return Collections.unmodifiableList(contactsSecondaires);
    }

    public void ajouterContact(String contact) {
        this.contactsSecondaires.add(contact);
    }

    public void supprimerContact(String contact) {
        this.contactsSecondaires.remove(contact);
    }
}

