package tn.esprit.autoloc.web.mapper;

import java.util.List;
import java.util.stream.Collectors;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.EquipementResponse;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
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
                mapEquipements(vehicule)
        );
    }

    /**
     * Convertit une entité Vehicule en VehiculeDTO,
     * incluant l'agence, la liste des équipements et la liste des maintenances.
     */
    public static VehiculeDTO toDTO(Vehicule vehicule) {
        Agence agence = vehicule.getAgence();

        List<MaintenanceResponse> maintenances = vehicule.getMaintenances() != null
                ? vehicule.getMaintenances().stream()
                        .map(MaintenanceMapper::toResponse)
                        .collect(Collectors.toList())
                : List.of();

        return new VehiculeDTO(
                vehicule.getIdVehicule(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getCategorie(),
                vehicule.getTarifJournalier(),
                vehicule.getStatut(),
                agence != null ? agence.getIdAgence() : null,
                agence != null ? agence.getNom() : null,
                mapEquipements(vehicule),
                maintenances
        );
    }

    private static List<EquipementResponse> mapEquipements(Vehicule vehicule) {
        return vehicule.getEquipements() != null
                ? vehicule.getEquipements().stream()
                        .map(EquipementMapper::toResponse)
                        .collect(Collectors.toList())
                : List.of();
    }
}
