package tn.esprit.autoloc.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de réponse exposant les données d'une agence.
 * Ne contient pas les listes d'employés ni de véhicules pour éviter la surcharge.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgenceResponse {

    private Long idAgence;
    private String nom;
    private String ville;
    private String adresse;
    private String telephone;
}
