package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Maintenance et ses DTOs.
 */
public class MaintenanceMapper {

    private MaintenanceMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un MaintenanceRequest en entité Maintenance.
     * Le véhicule concerné est résolu par la couche service.
     */
    public static Maintenance toEntity(MaintenanceRequest request) {
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(request.getDateDebut());
        maintenance.setDateFin(request.getDateFin());
        maintenance.setDescription(request.getDescription());
        return maintenance;
    }

    /**
     * Convertit une entité Maintenance en MaintenanceResponse,
     * incluant l'immatriculation du véhicule concerné.
     */
    public static MaintenanceResponse toResponse(Maintenance maintenance) {
        Vehicule vehicule = maintenance.getVehicule();
        return new MaintenanceResponse(
                maintenance.getIdMaintenance(),
                maintenance.getDateDebut(),
                maintenance.getDateFin(),
                maintenance.getDescription(),
                vehicule != null ? vehicule.getIdVehicule() : null,
                vehicule != null ? vehicule.getImmatriculation() : null
        );
    }
}
