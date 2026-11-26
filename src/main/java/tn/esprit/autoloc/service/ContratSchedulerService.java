package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.repository.IContratRepository;

/**
 * Service planifié de surveillance des contrats arrivant à échéance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContratSchedulerService {

    private final IContratRepository contratRepository;

    /**
     * Exécuté tous les jours de la semaine (lundi à vendredi) à 07h00 du matin.
     * Identifie et journalise les contrats valides dont la location se termine sous 48 heures.
     */
    @Scheduled(cron = "0 0 7 * * MON-FRI")
    @Transactional(readOnly = true)
    public void alerterContratsArrivantAEcheance() {
        List<Contrat> contrats = identifierContratsArrivantAEcheance();

        if (contrats.isEmpty()) {
            log.info("[Scheduler 07h] Aucun contrat n'arrive à échéance sous 48 heures.");
            return;
        }

        log.warn("[Scheduler 07h] {} contrat(s) arrivant à échéance sous 48 heures :", contrats.size());
        for (Contrat contrat : contrats) {
            log.warn(" - Contrat #{} | Date fin location : {} | Montant : {} | Réservation #{}",
                    contrat.getIdContrat(),
                    contrat.getReservation().getDateFin(),
                    contrat.getMontantTotal(),
                    contrat.getReservation().getIdReservation());
        }
    }

    /**
     * Retourne la liste des contrats actifs dont la réservation associée se termine dans les 48 prochaines heures.
     */
    @Transactional(readOnly = true)
    public List<Contrat> identifierContratsArrivantAEcheance() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans48Heures = aujourdhui.plusDays(2);
        return contratRepository.findContratsArrivantAEcheance(
                StatutReservation.CONFIRMEE,
                aujourdhui,
                dans48Heures);
    }
}
