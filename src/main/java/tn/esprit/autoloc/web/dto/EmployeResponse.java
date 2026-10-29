package tn.esprit.autoloc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.RoleEmploye;

/**
 * DTO de réponse exposant les données d'un employé.
 * Inclut l'identifiant et le nom de l'agence d'affectation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeResponse {

    private Long idEmploye;
    private String nom;
    private String prenom;
    private RoleEmploye role;
    private Long agenceId;
    private String agenceNom;
}
