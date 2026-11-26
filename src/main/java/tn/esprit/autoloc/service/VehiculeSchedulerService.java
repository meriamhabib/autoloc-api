package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;

/**
 * Service planifié de clôture automatique des locations terminées et libération des véhicules.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehiculeSchedulerService {

    private final IReservationRepository reservationRepository;
    private final IVehiculeRepository vehiculeRepository;

    /**
     * Exécuté chaque jour à 01h00 du matin.
     * Clôture les réservations confirmées échues (TERMINEE) et remet les véhicules loués à DISPONIBLE.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void libererVehiculesEnFinDeLocation() {
        LocalDate aujourdhui = LocalDate.now();
        List<Reservation> reservationsExpirees = reservationRepository
                .findByStatutAndDateFinBefore(StatutReservation.CONFIRMEE, aujourdhui);

        if (reservationsExpirees.isEmpty()) {
            log.info("[Scheduler 01h] Aucune location terminée à clôturer.");
            return;
        }

        for (Reservation reservation : reservationsExpirees) {
            reservation.setStatut(StatutReservation.TERMINEE);

            Vehicule vehicule = reservation.getVehicule();
            if (vehicule != null && vehicule.getStatut() == StatutVehicule.LOUE) {
                vehicule.setStatut(StatutVehicule.DISPONIBLE);
                vehiculeRepository.save(vehicule);
                log.info("[Scheduler 01h] Véhicule {} remis à DISPONIBLE.", vehicule.getImmatriculation());
            }

            reservationRepository.save(reservation);
            log.info("[Scheduler 01h] Réservation #{} passée à TERMINEE.", reservation.getIdReservation());
        }
    }
}
