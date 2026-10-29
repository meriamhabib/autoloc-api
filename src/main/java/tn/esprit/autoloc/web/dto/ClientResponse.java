package tn.esprit.autoloc.web.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de réponse exposant les données d'un client.
 * Exclut la liste des réservations pour éviter la surcharge JSON.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {

    private Long idClient;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String numPermis;
    private LocalDate dateInscription;
}
