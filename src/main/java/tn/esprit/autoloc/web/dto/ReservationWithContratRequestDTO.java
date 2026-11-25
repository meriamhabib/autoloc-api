package tn.esprit.autoloc.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.StatutReservation;

/**
 * DTO d'entrée permettant de créer simultanément une réservation et son contrat associé.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReservationWithContratRequestDTO {

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "Le statut de la réservation est obligatoire")
    private StatutReservation statut;

    /** Identifiant du véhicule réservé (facultatif). */
    private Long vehiculeId;

    /** Identifiant du client (facultatif). */
    private Long clientId;

    @NotNull(message = "La date de signature du contrat est obligatoire")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total du contrat est obligatoire")
    @DecimalMin(value = "0.00", message = "Le montant total ne peut pas être négatif")
    private BigDecimal montantTotal;

    private boolean valide;

    private List<@Valid PaiementRequest> paiements = new ArrayList<>();
}

