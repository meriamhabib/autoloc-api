package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de saisie pour la création et la modification d'une agence.
 * Contient les contraintes de validation Bean Validation.
 */
@Getter
@Setter
@NoArgsConstructor
public class AgenceRequest {

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    @Size(max = 50, message = "Le nom de l'agence ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "La ville de l'agence est obligatoire")
    @Size(max = 50, message = "La ville ne doit pas dépasser 50 caractères")
    private String ville;

    @NotBlank(message = "L'adresse de l'agence est obligatoire")
    @Size(max = 150, message = "L'adresse ne doit pas dépasser 150 caractères")
    private String adresse;

    @NotBlank(message = "Le téléphone de l'agence est obligatoire")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String telephone;
}
