package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

/**
 * DTO de saisie pour la création et la modification d'une réservation.
 * Les associations véhicule/client/contrat sont référencées par identifiant.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReservationRequest {

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "Le statut de la réservation est obligatoire")
    private StatutReservation statut;

    /** Identifiant du véhicule réservé. */
    private Long vehiculeId;

    /** Identifiant du client. */
    private Long clientId;

    /** Identifiant du contrat lié (facultatif à la création). */
    private Long contratId;
}
