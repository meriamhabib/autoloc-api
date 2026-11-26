package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findFirstByDateDebutAndDateFin(LocalDate dateDebut, LocalDate dateFin);

    List<Reservation> findByStatutAndDateFinBefore(StatutReservation statut, LocalDate date);
}