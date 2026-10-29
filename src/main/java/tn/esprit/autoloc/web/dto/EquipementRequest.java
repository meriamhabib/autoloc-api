package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de saisie pour la création et la modification d'un équipement.
 */
@Getter
@Setter
@NoArgsConstructor
public class EquipementRequest {

    @NotBlank(message = "Le libellé de l'équipement est obligatoire")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    private String libelle;
}
