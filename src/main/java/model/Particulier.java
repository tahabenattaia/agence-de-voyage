package model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Particulier extends Client {
    private String cin;
    private Set<String> documentsIdentite;

    public Particulier() {
        super();
        this.documentsIdentite = new HashSet<>();
    }

    public Particulier(String code_cli, String nom, String telephone, String adresse,
                       String cin, String email, String password ,boolean isAdmin) {
        super(code_cli, nom, telephone, adresse ,email , password ,isAdmin);
        this.cin = cin;
        this.documentsIdentite = new HashSet<>();
        this.documentsIdentite.add(cin);
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public Set<String> getDocumentsIdentite() {
        return Collections.unmodifiableSet(documentsIdentite);
    }

    public void ajouterDocument(String document) {
        this.documentsIdentite.add(document);
    }

    public void supprimerDocument(String document) {
        if (!document.equals(cin)) {
            this.documentsIdentite.remove(document);
        }
    }
}

