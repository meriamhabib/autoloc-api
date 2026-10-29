package tn.esprit.autoloc.web.mapper;

import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;

/**
 * Mapper manuel pour la conversion entre l'entité Employe et ses DTOs.
 */
public class EmployeMapper {

    private EmployeMapper() {
        // Utilitaire statique — instanciation interdite
    }

    /**
     * Convertit un EmployeRequest en entité Employe.
     * L'association Agence doit être résolue séparément dans la couche service.
     */
    public static Employe toEntity(EmployeRequest request) {
        Employe employe = new Employe();
        employe.setNom(request.getNom());
        employe.setPrenom(request.getPrenom());
        employe.setRole(request.getRole());
        // L'agence est positionnée par le service via agenceId
        return employe;
    }

    /**
     * Convertit une entité Employe en EmployeResponse.
     * Inclut l'identifiant et le nom de l'agence d'affectation.
     */
    public static EmployeResponse toResponse(Employe employe) {
        Agence agence = employe.getAgence();
        return new EmployeResponse(
                employe.getIdEmploye(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getRole(),
                agence != null ? agence.getIdAgence() : null,
                agence != null ? agence.getNom() : null
        );
    }
}
