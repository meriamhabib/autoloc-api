package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.RoleEmploye;

/**
 * DTO de saisie pour la création et la modification d'un employé.
 */
@Getter
@Setter
@NoArgsConstructor
public class EmployeRequest {

    @NotBlank(message = "Le nom de l'employé est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom de l'employé est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotNull(message = "Le rôle de l'employé est obligatoire")
    private RoleEmploye role;

    /** Identifiant de l'agence d'affectation (facultatif). */
    private Long agenceId;
}
