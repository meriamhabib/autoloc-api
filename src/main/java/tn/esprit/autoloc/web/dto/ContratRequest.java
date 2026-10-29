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

/**
 * DTO de saisie pour la création et la modification d'un contrat.
 * Supporte la création en cascade des paiements associés.
 */
@Getter
@Setter
@NoArgsConstructor
public class ContratRequest {

    @NotNull(message = "La date de signature est obligatoire")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.00", message = "Le montant total ne peut pas être négatif")
    private BigDecimal montantTotal;

    private boolean valide;

    @Valid
    private List<PaiementRequest> paiements = new ArrayList<>();
}
