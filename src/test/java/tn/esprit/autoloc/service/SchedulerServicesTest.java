package tn.esprit.autoloc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

class SchedulerServicesTest {

    @Test
    @DisplayName("ContratSchedulerService identifie uniquement les contrats valides arrivant à échéance sous 48h")
    void testContratSchedulerService() throws NoSuchMethodException {
        LocalDate aujourdhui = LocalDate.now();

        Contrat contratEcheanceDemain = creerContratAvecReservation(1L, true, StatutReservation.CONFIRMEE, aujourdhui.plusDays(1));
        Contrat contratEcheanceDans2Jours = creerContratAvecReservation(2L, true, StatutReservation.CONFIRMEE, aujourdhui.plusDays(2));
        Contrat contratHorsFenetre = creerContratAvecReservation(3L, true, StatutReservation.CONFIRMEE, aujourdhui.plusDays(5));
        Contrat contratInvalide = creerContratAvecReservation(4L, false, StatutReservation.CONFIRMEE, aujourdhui.plusDays(1));

        List<Contrat> tous = List.of(
                contratEcheanceDemain,
                contratEcheanceDans2Jours,
                contratHorsFenetre,
                contratInvalide);

        IContratRepository contratRepo = proxyRepo(IContratRepository.class, (method, args) -> {
            if ("findContratsArrivantAEcheance".equals(method)) {
                StatutReservation statut = (StatutReservation) args[0];
                LocalDate debut = (LocalDate) args[1];
                LocalDate fin = (LocalDate) args[2];
                return tous.stream()
                        .filter(Contrat::isValide)
                        .filter(c -> c.getReservation().getStatut() == statut)
                        .filter(c -> !c.getReservation().getDateFin().isBefore(debut)
                                && !c.getReservation().getDateFin().isAfter(fin))
                        .toList();
            }
            throw new UnsupportedOperationException(method);
        });

        ContratSchedulerService scheduler = new ContratSchedulerService(contratRepo);
        List<Contrat> resultats = scheduler.identifierContratsArrivantAEcheance();
        scheduler.alerterContratsArrivantAEcheance();

        assertEquals(2, resultats.size());
        assertEquals(1L, resultats.get(0).getIdContrat());
        assertEquals(2L, resultats.get(1).getIdContrat());

        Method m = ContratSchedulerService.class.getMethod("alerterContratsArrivantAEcheance");
        Scheduled scheduled = m.getAnnotation(Scheduled.class);
        assertEquals("0 0 7 * * MON-FRI", scheduled.cron());
    }

    @Test
    @DisplayName("VehiculeSchedulerService clôture les réservations échues et libère les véhicules loués")
    void testVehiculeSchedulerService() throws NoSuchMethodException {
        LocalDate aujourdhui = LocalDate.now();

        Vehicule vehiculeLoue = new Vehicule();
        vehiculeLoue.setIdVehicule(10L);
        vehiculeLoue.setImmatriculation("200-TN-1234");
        vehiculeLoue.setStatut(StatutVehicule.LOUE);

        Vehicule vehiculeEnMaintenance = new Vehicule();
        vehiculeEnMaintenance.setIdVehicule(11L);
        vehiculeEnMaintenance.setImmatriculation("200-TN-9999");
        vehiculeEnMaintenance.setStatut(StatutVehicule.MAINTENANCE);

        Reservation resExpiree = new Reservation();
        resExpiree.setIdReservation(100L);
        resExpiree.setStatut(StatutReservation.CONFIRMEE);
        resExpiree.setDateDebut(aujourdhui.minusDays(4));
        resExpiree.setDateFin(aujourdhui.minusDays(1));
        resExpiree.setVehicule(vehiculeLoue);

        Reservation resMaintenance = new Reservation();
        resMaintenance.setIdReservation(101L);
        resMaintenance.setStatut(StatutReservation.CONFIRMEE);
        resMaintenance.setDateDebut(aujourdhui.minusDays(3));
        resMaintenance.setDateFin(aujourdhui.minusDays(1));
        resMaintenance.setVehicule(vehiculeEnMaintenance);

        Reservation resEnCours = new Reservation();
        resEnCours.setIdReservation(102L);
        resEnCours.setStatut(StatutReservation.CONFIRMEE);
        resEnCours.setDateDebut(aujourdhui.minusDays(1));
        resEnCours.setDateFin(aujourdhui.plusDays(2));
        resEnCours.setVehicule(vehiculeLoue);

        List<Reservation> allReservations = List.of(resExpiree, resMaintenance, resEnCours);
        List<Vehicule> savedVehicules = new ArrayList<>();
        List<Reservation> savedReservations = new ArrayList<>();

        IReservationRepository reservationRepo = proxyRepo(IReservationRepository.class, (method, args) -> switch (method) {
            case "findByStatutAndDateFinBefore" -> allReservations.stream()
                    .filter(r -> r.getStatut() == args[0] && r.getDateFin().isBefore((LocalDate) args[1]))
                    .toList();
            case "save" -> {
                Reservation r = (Reservation) args[0];
                savedReservations.add(r);
                yield r;
            }
            default -> throw new UnsupportedOperationException(method);
        });

        IVehiculeRepository vehiculeRepo = proxyRepo(IVehiculeRepository.class, (method, args) -> {
            if ("save".equals(method)) {
                Vehicule v = (Vehicule) args[0];
                savedVehicules.add(v);
                return v;
            }
            throw new UnsupportedOperationException(method);
        });

        VehiculeSchedulerService scheduler = new VehiculeSchedulerService(reservationRepo, vehiculeRepo);
        scheduler.libererVehiculesEnFinDeLocation();

        assertEquals(StatutReservation.TERMINEE, resExpiree.getStatut());
        assertEquals(StatutReservation.TERMINEE, resMaintenance.getStatut());
        assertEquals(StatutReservation.CONFIRMEE, resEnCours.getStatut());
        assertEquals(StatutVehicule.DISPONIBLE, vehiculeLoue.getStatut());
        assertEquals(StatutVehicule.MAINTENANCE, vehiculeEnMaintenance.getStatut());
        assertEquals(1, savedVehicules.size());
        assertEquals(2, savedReservations.size());

        Method m = VehiculeSchedulerService.class.getMethod("libererVehiculesEnFinDeLocation");
        Scheduled scheduled = m.getAnnotation(Scheduled.class);
        assertEquals("0 0 1 * * *", scheduled.cron());
    }

    private Contrat creerContratAvecReservation(
            Long id,
            boolean valide,
            StatutReservation statut,
            LocalDate dateFin) {
        Reservation reservation = new Reservation();
        reservation.setIdReservation(id + 50);
        reservation.setStatut(statut);
        reservation.setDateDebut(dateFin.minusDays(3));
        reservation.setDateFin(dateFin);

        Contrat contrat = new Contrat();
        contrat.setIdContrat(id);
        contrat.setValide(valide);
        contrat.setDateSignature(LocalDate.now().minusDays(3));
        contrat.setMontantTotal(new BigDecimal("300.00"));
        contrat.setReservation(reservation);
        return contrat;
    }

    @FunctionalInterface
    private interface RepoHandler {
        Object handle(String methodName, Object[] args);
    }

    @SuppressWarnings("unchecked")
    private <T> T proxyRepo(Class<T> iface, RepoHandler handler) {
        return (T) Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class<?>[]{iface},
                (proxy, method, args) -> handler.handle(method.getName(), args));
    }
}
