package tn.esprit.autoloc.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de réponse exposant les données d'un contrat.
 * Inclut la liste des paiements en composition (relation forte).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContratResponse {

    private Long idContrat;
    private LocalDate dateSignature;
    private BigDecimal montantTotal;
    private boolean valide;
    private List<PaiementResponse> paiements;
}
