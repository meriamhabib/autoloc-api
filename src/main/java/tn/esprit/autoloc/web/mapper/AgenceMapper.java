package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.AgenceResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Agence et ses DTOs.
 */
public class AgenceMapper {

    private AgenceMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un AgenceRequest en entité Agence (pour création ou mise à jour).
     */
    public static Agence toEntity(AgenceRequest request) {
        Agence agence = new Agence();
        agence.setNom(request.getNom());
        agence.setVille(request.getVille());
        agence.setAdresse(request.getAdresse());
        agence.setTelephone(request.getTelephone());
        return agence;
    }

    /**
     * Convertit une entité Agence en AgenceResponse.
     */
    public static AgenceResponse toResponse(Agence agence) {
        return new AgenceResponse(
                agence.getIdAgence(),
                agence.getNom(),
                agence.getVille(),
                agence.getAdresse(),
                agence.getTelephone()
        );
    }
}
