package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

/**
 * DTO de saisie pour la création et la modification d'un véhicule.
 */
@Getter
@Setter
@NoArgsConstructor
public class VehiculeRequest {

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(max = 20, message = "L'immatriculation ne doit pas dépasser 20 caractères")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 50, message = "La marque ne doit pas dépasser 50 caractères")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 50, message = "Le modèle ne doit pas dépasser 50 caractères")
    private String modele;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieVehicule categorie;

    @NotNull(message = "Le tarif journalier est obligatoire")
    @DecimalMin(value = "0.00", message = "Le tarif journalier ne peut pas être négatif")
    private BigDecimal tarifJournalier;

    @NotNull(message = "Le statut du véhicule est obligatoire")
    private StatutVehicule statut;

    /** Identifiant de l'agence propriétaire (facultatif). */
    private Long agenceId;

    /** Liste des identifiants d'équipements à associer (facultatif). */
    private List<Long> equipementIds = new ArrayList<>();
}
