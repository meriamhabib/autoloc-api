package tn.esprit.autoloc.web.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de réponse exposant les données d'une maintenance.
 * Inclut l'immatriculation du véhicule concerné.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceResponse {

    private Long idMaintenance;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private Long vehiculeId;
    private String vehiculeImmatriculation;
}
