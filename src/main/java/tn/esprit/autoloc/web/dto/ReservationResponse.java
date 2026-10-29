package tn.esprit.autoloc.web.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

/**
 * DTO de réponse exposant les données d'une réservation.
 * Inclut des informations dénormalisées sur le véhicule et le client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long idReservation;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutReservation statut;
    private Long vehiculeId;
    private String vehiculeImmatriculation;
    private Long clientId;
    private String clientNomComplet;
    private Long contratId;
}
