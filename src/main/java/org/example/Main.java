package org.example;

import model.Voyage;
import model.VoyageOrganise;
import model.VoyagePersonnalise;
import service.VoyageService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        VoyageService voyageService = new VoyageService();

        // Test de création d'un voyage organisé
        VoyageOrganise voyageOrganise = new VoyageOrganise();
        voyageOrganise.setReference("VO001");
        voyageOrganise.setPrixParPersonne(100);
        voyageOrganise.setDestination("Paris");
        voyageOrganise.setDescriptif("Découverte de Paris");
        voyageOrganise.setDateDepart(new Date());
        voyageOrganise.setDateRetour(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)); // 7 jours plus tard
        voyageOrganise.setNbPlaceMaxi(20);
        voyageOrganise.setDateValidite(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000)); // 30 jours plus tard

        voyageService.createVoyage(voyageOrganise);
        System.out.println("Voyage organisé créé avec l'ID : " + voyageOrganise.getId());

        // Test de création d'un voyage personnalisé
        VoyagePersonnalise voyagePersonnalise = new VoyagePersonnalise();
        voyagePersonnalise.setReference("VP001");
        voyagePersonnalise.setPrixParPersonne(150);
        voyagePersonnalise.setDestination("Tokyo");
        voyagePersonnalise.setDescriptif("Découverte de Tokyo");
        voyagePersonnalise.setDateDepart(new Date());
        voyagePersonnalise.setDateRetour(new Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000)); // 10 jours plus tard
        voyagePersonnalise.setPreference("Gastronomie");

        voyageService.createVoyage(voyagePersonnalise);
        System.out.println("Voyage personnalisé créé avec l'ID : " + voyagePersonnalise.getId());

        // Test de récupération d'un voyage
        Optional<VoyageOrganise> voyageRecupere = voyageService.getVoyageById(voyageOrganise.getId()).map(v -> (VoyageOrganise) v);
        if (voyageRecupere.isPresent()) {
            System.out.println("Voyage récupéré : " + voyageRecupere.get().getReference() + " - " + voyageRecupere.get().getDestination());
        } else {
            System.out.println("Aucun voyage trouvé avec cet ID.");
        }

        System.out.println("\nAffichage de tous les voyages :");
        List<Voyage> voyages = voyageService.getAllVoyages();
        for (Voyage voyage : voyages) {
            System.out.println("ID : " + voyage.getId() + ", Référence : " + voyage.getReference() +
                    ", Destination : " + voyage.getDestination() +
                    ", Prix : " + voyage.getPrixParPersonne() + " dt");
        }

    }
}

