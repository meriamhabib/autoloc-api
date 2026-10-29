package tn.esprit.autoloc.web.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

/**
 * DTO de réponse exposant les données d'un véhicule.
 * Inclut l'agence propriétaire et la liste des équipements associés.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeResponse {

    private Long idVehicule;
    private String immatriculation;
    private String marque;
    private String modele;
    private CategorieVehicule categorie;
    private BigDecimal tarifJournalier;
    private StatutVehicule statut;
    private Long agenceId;
    private String agenceNom;
    private List<EquipementResponse> equipements;
}
