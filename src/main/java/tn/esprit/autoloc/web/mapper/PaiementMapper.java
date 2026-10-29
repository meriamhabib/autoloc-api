package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.PaiementRequest;
import tn.esprit.autoloc.web.dto.PaiementResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Paiement et ses DTOs.
 */
public class PaiementMapper {

    private PaiementMapper() {
        // Utilitaire statique — instanciation interdite
    }

    public static Paiement toEntity(PaiementRequest request, Contrat contratParent) {
        Paiement paiement = new Paiement();
        paiement.setMontant(request.getMontant());
        paiement.setDatePaiement(request.getDatePaiement());
        paiement.setModePaiement(request.getModePaiement());
        paiement.setContrat(contratParent);
        return paiement;
    }

    public static PaiementResponse toResponse(Paiement paiement) {
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
