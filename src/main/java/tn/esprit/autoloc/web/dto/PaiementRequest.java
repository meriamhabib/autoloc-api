package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.ModePaiement;

/**
 * DTO de saisie pour un paiement.
 * Utilisé en saisie directe ou en objet imbriqué dans ContratRequest (cascade).
 */
@Getter
@Setter
@NoArgsConstructor
public class PaiementRequest {

    @NotNull(message = "Le montant du paiement est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant du paiement doit être strictement positif")
    private BigDecimal montant;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;
}
