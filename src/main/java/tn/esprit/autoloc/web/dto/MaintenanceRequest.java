package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de saisie pour la création et la modification d'une maintenance.
 * Le véhicule concerné est référencé par son identifiant.
 */
@Getter
@Setter
@NoArgsConstructor
public class MaintenanceRequest {

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    /** Identifiant du véhicule concerné par la maintenance. */
    private Long vehiculeId;
}
