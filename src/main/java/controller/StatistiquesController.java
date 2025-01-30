package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Voyage;
import model.Client;
import model.Reservation;
import service.VoyageService;
import service.ClientService;
import service.ReservationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiquesController {

    @FXML private BarChart<String, Number> topDestinationsChart;
    @FXML private PieChart clientTypeChart;
    @FXML private LineChart<String, Number> revenueChart;

    private VoyageService voyageService;
    private ClientService clientService;
    private ReservationService reservationService;

    public void initialize() {
        voyageService = new VoyageService();
        clientService = new ClientService();
        reservationService = new ReservationService();

        loadTopDestinations();
        loadClientTypeDistribution();
        loadRevenueOverTime();
    }

    private void loadTopDestinations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        Map<String, Long> destinationCounts = reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getVoyage().getDestination(), Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de réservations");

        destinationCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        topDestinationsChart.getData().add(series);
    }

    private void loadClientTypeDistribution() {
        List<Client> clients = clientService.getAllClients();
        long entrepriseCount = clients.stream().filter(c -> c instanceof model.Entreprise).count();
        long particulierCount = clients.size() - entrepriseCount;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Entreprises", entrepriseCount),
                new PieChart.Data("Particuliers", particulierCount)
        );
        clientTypeChart.setData(pieChartData);
    }

    private void loadRevenueOverTime() {
        List<Reservation> reservations = reservationService.getAllReservations();
        Map<String, Double> revenueByMonth = reservations.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDateReservation().toString().substring(0, 7),
                        Collectors.summingDouble(r -> r.getVoyage().getPrixParPersonne() * r.getNbPlace())
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenus mensuels");

        revenueByMonth.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        revenueChart.getData().add(series);
    }
}

