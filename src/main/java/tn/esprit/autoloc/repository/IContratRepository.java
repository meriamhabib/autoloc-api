package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.StatutReservation;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
    Optional<Contrat> findFirstByDateSignature(LocalDate dateSignature);

    @Query("""
           SELECT c FROM Contrat c
           JOIN FETCH c.reservation r
           WHERE c.valide = true
             AND r.statut = :statut
             AND r.dateFin BETWEEN :debut AND :fin
           """)
    List<Contrat> findContratsArrivantAEcheance(
            @Param("statut") StatutReservation statut,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
}
