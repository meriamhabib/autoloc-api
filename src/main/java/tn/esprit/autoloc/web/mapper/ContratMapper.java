package tn.esprit.autoloc.web.mapper;

import java.util.List;
import java.util.stream.Collectors;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.dto.PaiementRequest;
import tn.esprit.autoloc.web.dto.PaiementResponse;

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
        Contrat contrat = new Contrat();
        contrat.setDateSignature(request.getDateSignature());
        contrat.setMontantTotal(request.getMontantTotal());
        contrat.setValide(request.isValide());

        if (request.getPaiements() != null) {
            List<Paiement> paiements = request.getPaiements().stream()
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
        List<PaiementResponse> paiementsDto = contrat.getPaiements() != null
                ? contrat.getPaiements().stream()
                        .map(ContratMapper::paiementToResponse)
                        .collect(Collectors.toList())
                : List.of();

        return new ContratResponse(
                contrat.getIdContrat(),
                contrat.getDateSignature(),
                contrat.getMontantTotal(),
                contrat.isValide(),
                paiementsDto
        );
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
