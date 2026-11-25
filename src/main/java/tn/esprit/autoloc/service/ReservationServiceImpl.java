package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IClientRepository clientRepository;
    private final IContratRepository contratRepository;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        validerReservation(request);
        Reservation reservation = ReservationMapper.toEntity(request);
        lierAssociations(reservation, request);
        return ReservationMapper.toResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse findById(Long id) {
        return reservationRepository.findById(id)
                .map(ReservationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @Override
    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse update(Long id, ReservationRequest request) {
        validerReservation(request);
        Reservation existante = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));

        existante.setDateDebut(request.getDateDebut());
        existante.setDateFin(request.getDateFin());
        existante.setStatut(request.getStatut());
        lierAssociations(existante, request);

        return ReservationMapper.toResponse(reservationRepository.save(existante));
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        if (nom == null || nom.trim().isEmpty() || prenom == null || prenom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom et le prénom du client sont obligatoires");
        }

        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", idReservation));

        Client client = clientRepository.findFirstByNomIgnoreCaseAndPrenomIgnoreCase(nom.trim(), prenom.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client introuvable avec le nom '" + nom + "' et le prénom '" + prenom + "'"));

        Client ancienClient = reservation.getClient();
        if (ancienClient != null && !ancienClient.equals(client) && ancienClient.getReservations() != null) {
            ancienClient.getReservations().remove(reservation);
        }

        reservation.setClient(client);
        if (client.getReservations() == null) {
            client.setReservations(new ArrayList<>());
        }
        if (!client.getReservations().contains(reservation)) {
            client.getReservations().add(reservation);
        }

        return ReservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    @Override
    @Transactional
    public ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO) {
        validerPeriode(requestDTO.getDateDebut(), requestDTO.getDateFin());
        if (requestDTO.getMontantTotal() != null && requestDTO.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total d'un contrat ne peut pas être négatif");
        }

        Contrat contrat = ContratMapper.toEntity(requestDTO);
        Contrat contratSauvegarde = contratRepository.save(contrat);

        Reservation reservation = ReservationMapper.toEntity(requestDTO);
        lierVehiculeEtClient(reservation, requestDTO.getVehiculeId(), requestDTO.getClientId());
        reservation.setContrat(contratSauvegarde);
        contratSauvegarde.setReservation(reservation);

        Reservation reservationSauvegardee = reservationRepository.save(reservation);
        return ReservationMapper.toResponseDTO(reservationSauvegardee);
    }

    private void lierAssociations(Reservation reservation, ReservationRequest request) {
        lierVehiculeEtClient(reservation, request.getVehiculeId(), request.getClientId());

        if (request.getContratId() != null) {
            Contrat contrat = contratRepository.findById(request.getContratId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contrat", request.getContratId()));
            reservation.setContrat(contrat);
        } else {
            reservation.setContrat(null);
        }
    }

    private void lierVehiculeEtClient(Reservation reservation, Long vehiculeId, Long clientId) {
        if (vehiculeId != null) {
            Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", vehiculeId));
            reservation.setVehicule(vehicule);
        } else {
            reservation.setVehicule(null);
        }

        if (clientId != null) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));
            reservation.setClient(client);
        } else {
            reservation.setClient(null);
        }
    }

    private void validerReservation(ReservationRequest request) {
        validerPeriode(request.getDateDebut(), request.getDateFin());
    }

    private void validerPeriode(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut != null && dateFin != null && dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
