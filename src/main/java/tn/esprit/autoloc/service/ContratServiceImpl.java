package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.mapper.ContratMapper;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;

    @Override
    public ContratResponse create(ContratRequest request) {
        // La validation @DecimalMin gère déjà le montant négatif au niveau DTO ;
        // on conserve la vérification applicative pour la cohérence métier.
        validerMontant(request.getMontantTotal());
        Contrat contrat = ContratMapper.toEntity(request);
        return ContratMapper.toResponse(contratRepository.save(contrat));
    }

    @Override
    public ContratResponse findById(Long id) {
        return contratRepository.findById(id)
                .map(ContratMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    public List<ContratResponse> findAll() {
        return contratRepository.findAll().stream()
                .map(ContratMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ContratResponse update(Long id, ContratRequest request) {
        validerMontant(request.getMontantTotal());
        Contrat existant = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        existant.setDateSignature(request.getDateSignature());
        existant.setMontantTotal(request.getMontantTotal());
        existant.setValide(request.isValide());
        return ContratMapper.toResponse(contratRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        if (dateSignature == null || dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de signature, de début et de fin sont obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        Contrat contrat = contratRepository.findFirstByDateSignature(dateSignature)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrat introuvable avec la date de signature " + dateSignature));

        Reservation reservation = reservationRepository.findFirstByDateDebutAndDateFin(dateDebut, dateFin)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Réservation introuvable pour la période du " + dateDebut + " au " + dateFin));

        Reservation ancienneReservation = contrat.getReservation();
        if (ancienneReservation != null && !ancienneReservation.equals(reservation)) {
            ancienneReservation.setContrat(null);
        }

        Contrat ancienContrat = reservation.getContrat();
        if (ancienContrat != null && !ancienContrat.equals(contrat)) {
            ancienContrat.setReservation(null);
        }

        reservation.setContrat(contrat);
        contrat.setReservation(reservation);

        reservationRepository.save(reservation);
        return ContratMapper.toDTO(contrat);
    }

    private void validerMontant(BigDecimal montant) {
        if (montant != null && montant.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total d'un contrat ne peut pas être négatif");
        }
    }
}
