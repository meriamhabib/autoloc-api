package tn.esprit.autoloc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de réponse exposant les données d'un équipement.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipementResponse {

    private Long idEquipement;
    private String libelle;
}
