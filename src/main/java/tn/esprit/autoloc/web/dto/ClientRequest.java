package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de saisie pour la création et la modification d'un client.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClientRequest {

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom du client est obligatoire")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotBlank(message = "L'adresse e-mail du client est obligatoire")
    @Email(message = "L'adresse e-mail n'est pas valide")
    @Size(max = 100, message = "L'e-mail ne doit pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le téléphone du client est obligatoire")
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String telephone;

    @NotBlank(message = "Le numéro de permis du client est obligatoire")
    @Size(max = 50, message = "Le numéro de permis ne doit pas dépasser 50 caractères")
    private String numPermis;

    @NotNull(message = "La date d'inscription est obligatoire")
    @PastOrPresent(message = "La date d'inscription ne peut pas être dans le futur")
    private LocalDate dateInscription;
}
