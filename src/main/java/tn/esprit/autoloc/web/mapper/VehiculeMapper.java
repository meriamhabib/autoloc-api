package tn.esprit.autoloc.web.mapper;

import java.util.List;
import java.util.stream.Collectors;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.EquipementResponse;
import tn.esprit.autoloc.web.dto.VehiculeRequest;
import tn.esprit.autoloc.web.dto.VehiculeResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Vehicule et ses DTOs.
 */
public class VehiculeMapper {

    private VehiculeMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un VehiculeRequest en entité Vehicule.
     * L'agence et les équipements sont résolus séparément par la couche service.
     */
    public static Vehicule toEntity(VehiculeRequest request) {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(request.getImmatriculation());
        vehicule.setMarque(request.getMarque());
        vehicule.setModele(request.getModele());
        vehicule.setCategorie(request.getCategorie());
        vehicule.setTarifJournalier(request.getTarifJournalier());
        vehicule.setStatut(request.getStatut());
        return vehicule;
    }

    /**
     * Convertit une entité Vehicule en VehiculeResponse,
     * incluant l'agence et la liste des équipements.
     */
    public static VehiculeResponse toResponse(Vehicule vehicule) {
        Agence agence = vehicule.getAgence();

        List<EquipementResponse> equipements = vehicule.getEquipements() != null
                ? vehicule.getEquipements().stream()
                        .map(EquipementMapper::toResponse)
                        .collect(Collectors.toList())
                : List.of();

        return new VehiculeResponse(
                vehicule.getIdVehicule(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getCategorie(),
                vehicule.getTarifJournalier(),
                vehicule.getStatut(),
                agence != null ? agence.getIdAgence() : null,
                agence != null ? agence.getNom() : null,
                equipements
        );
    }
}
