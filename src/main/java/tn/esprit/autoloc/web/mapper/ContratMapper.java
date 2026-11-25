package tn.esprit.autoloc.web.mapper;

import java.util.List;
import java.util.stream.Collectors;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.dto.PaiementRequest;
import tn.esprit.autoloc.web.dto.PaiementResponse;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

/**
 * Mapper manuel pour la conversion entre l'entité Contrat/Paiement et leurs DTOs.
 * Gère également la cascade des paiements imbriqués.
 */
public class ContratMapper {

    private ContratMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un ContratRequest en entité Contrat,
     * avec initialisation des paiements en cascade.
     */
    public static Contrat toEntity(ContratRequest request) {
        return buildContrat(
                request.getDateSignature(),
                request.getMontantTotal(),
                request.isValide(),
                request.getPaiements()
        );
    }

    /**
     * Convertit un ReservationWithContratRequestDTO en entité Contrat.
     */
    public static Contrat toEntity(ReservationWithContratRequestDTO request) {
        return buildContrat(
                request.getDateSignature(),
                request.getMontantTotal(),
                request.isValide(),
                request.getPaiements()
        );
    }

    private static Contrat buildContrat(
            java.time.LocalDate dateSignature,
            java.math.BigDecimal montantTotal,
            boolean valide,
            List<PaiementRequest> paiementsRequest) {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(dateSignature);
        contrat.setMontantTotal(montantTotal);
        contrat.setValide(valide);

        if (paiementsRequest != null) {
            List<Paiement> paiements = paiementsRequest.stream()
                    .map(req -> paiementToEntity(req, contrat))
                    .collect(Collectors.toList());
            contrat.getPaiements().addAll(paiements);
        }
        return contrat;
    }

    /**
     * Convertit une entité Contrat en ContratResponse (avec ses paiements).
     */
    public static ContratResponse toResponse(Contrat contrat) {
        return new ContratResponse(
                contrat.getIdContrat(),
                contrat.getDateSignature(),
                contrat.getMontantTotal(),
                contrat.isValide(),
                mapPaiements(contrat)
        );
    }

    /**
     * Convertit une entité Contrat en ContratDTO (avec réservation liée et paiements).
     */
    public static ContratDTO toDTO(Contrat contrat) {
        Long reservationId = contrat.getReservation() != null
                ? contrat.getReservation().getIdReservation()
                : null;

        return new ContratDTO(
                contrat.getIdContrat(),
                contrat.getDateSignature(),
                contrat.getMontantTotal(),
                contrat.isValide(),
                reservationId,
                mapPaiements(contrat)
        );
    }

    private static List<PaiementResponse> mapPaiements(Contrat contrat) {
        return contrat.getPaiements() != null
                ? contrat.getPaiements().stream()
                        .map(ContratMapper::paiementToResponse)
                        .collect(Collectors.toList())
                : List.of();
    }

    // ----------------------------------------------------------- Paiements

    /**
     * Convertit un PaiementRequest en entité Paiement, en liant le contrat parent.
     */
    public static Paiement paiementToEntity(PaiementRequest request, Contrat contratParent) {
        Paiement paiement = new Paiement();
        paiement.setMontant(request.getMontant());
        paiement.setDatePaiement(request.getDatePaiement());
        paiement.setModePaiement(request.getModePaiement());
        paiement.setContrat(contratParent);
        return paiement;
    }

    /**
     * Convertit une entité Paiement en PaiementResponse.
     */
    public static PaiementResponse paiementToResponse(Paiement paiement) {
        Long contratId = paiement.getContrat() != null ? paiement.getContrat().getIdContrat() : null;
        return new PaiementResponse(
                paiement.getIdPaiement(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getModePaiement(),
                contratId
        );
    }
}
