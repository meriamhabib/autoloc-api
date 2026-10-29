package tn.esprit.autoloc.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tn.esprit.autoloc.domain.ModePaiement;

/**
 * DTO de réponse exposant les données d'un paiement.
 * Inclut l'identifiant du contrat parent pour la traçabilité.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaiementResponse {

    private Long idPaiement;
    private BigDecimal montant;
    private LocalDate datePaiement;
    private ModePaiement modePaiement;
    private Long contratId;
}
