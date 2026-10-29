package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Equipement et ses DTOs.
 */
public class EquipementMapper {

    private EquipementMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un EquipementRequest en entité Equipement.
     */
    public static Equipement toEntity(EquipementRequest request) {
        Equipement equipement = new Equipement();
        equipement.setLibelle(request.getLibelle());
        return equipement;
    }

    /**
     * Convertit une entité Equipement en EquipementResponse.
     */
    public static EquipementResponse toResponse(Equipement equipement) {
        return new EquipementResponse(
                equipement.getIdEquipement(),
                equipement.getLibelle()
        );
    }
}
